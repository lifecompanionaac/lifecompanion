/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.lifecompanion.config.data.mode;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.mode.AppMode;
import org.lifecompanion.api.mode.LCModeI;
import org.lifecompanion.api.mode.ModeListenerI;
import org.lifecompanion.api.ui.ViewProviderI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.config.TranslationManager;
import org.lifecompanion.base.data.config.UserBaseConfiguration;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.data.control.stats.SessionStatsController;
import org.lifecompanion.base.data.control.update.InstallationController;
import org.lifecompanion.base.data.plugins.PluginManager;
import org.lifecompanion.config.data.action.impl.GlobalActions;
import org.lifecompanion.config.data.action.impl.LCConfigurationActions;
import org.lifecompanion.config.data.action.impl.LCConfigurationActions.OpenConfigurationAction;
import org.lifecompanion.config.data.component.configerror.ConfigurationErrorController;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.ErrorHandlingController;
import org.lifecompanion.config.data.control.LCStateController;
import org.lifecompanion.config.data.control.usercomp.UserCompController;
import org.lifecompanion.config.data.notif.LCNotification;
import org.lifecompanion.config.data.ui.ConfigViewProvider;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.common.SystemVirtualKeyboardHelper;
import org.lifecompanion.config.view.pane.main.notification2.LCNotificationController;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.lifecompanion.base.data.config.LCConstant.URL_PATH_CHANGELOG;

/**
 * The configuration mode description of LifeCompanion.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigMode implements LCModeI {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigMode.class);

    private final ViewProviderI configViewProvider;
    private final Scene configScene;
    private final ObjectProperty<LCConfigurationI> currentConfiguration;
    private final ObjectProperty<LCConfigurationI> configurationBeforeChange;

    public ConfigMode(final Scene configSceneP) {
        this.configScene = configSceneP;
        this.configViewProvider = new ConfigViewProvider();
        this.currentConfiguration = new SimpleObjectProperty<>();
        this.configurationBeforeChange = new SimpleObjectProperty<>();

        //Add the config only state listener to AppController
        AppController.INSTANCE.getStateListeners().addAll(Arrays.asList(
                LCStateController.INSTANCE, PluginManager.INSTANCE, UserCompController.INSTANCE, LCNotificationController.INSTANCE, ErrorHandlingController.INSTANCE
        ));

        //Custom confirmation in configuration mode
        AppController.INSTANCE.setConfirmConfigurationModeFunction((source, configuration) -> {
            if (configuration.securedConfigurationModeProperty().get()) {
                // Issue #180 - Secure dialog should automatically be closed (can be the user error)
                IntegerProperty timeLeft = new SimpleIntegerProperty(LCConstant.GO_TO_CONFIG_MODE_DELAY);
                Timeline timeLineAutoHide = new Timeline(new KeyFrame(Duration.seconds(1), (e) -> timeLeft.set(timeLeft.get() - 1)));
                timeLineAutoHide.setCycleCount(LCConstant.GO_TO_CONFIG_MODE_DELAY);
                //Generate a 1000 - 9999 code
                Random random = new Random();
                String number = "" + (random.nextInt(8999) + 1000);
                TextInputDialog dialog = ConfigUIUtils.createInputDialog(AppController.INSTANCE.getMainStageRoot(), null);
                dialog.headerTextProperty().bind(TranslationFX.getTextBinding("action.confirm.go.config.header", timeLeft));
                dialog.setContentText(Translation.getText("action.confirm.go.config.message", number));
                timeLineAutoHide.setOnFinished(e -> dialog.hide());
                timeLineAutoHide.play();
                SystemVirtualKeyboardHelper.INSTANCE.showIfEnabled();
                Optional<String> enteredString = dialog.showAndWait();
                timeLineAutoHide.stop();
                //Check code
                if (enteredString.isEmpty() || StringUtils.isDifferent(enteredString.get(), number)) {
                    if (enteredString.isPresent()) {
                        Alert warning = ConfigUIUtils.createDialog(source, AlertType.ERROR);
                        warning.setContentText(Translation.getText("action.confirm.go.config.error"));
                        warning.show();
                    }
                    return false;
                }
            }
            return true;
        });

        // When configuration and description are different : load the correct configuration
        AppController.INSTANCE.setCallbackIfDescriptionIsDifferent((configDesc) -> {
            // TODO : fix dirty getRoot()
            OpenConfigurationAction openConfigAction = new OpenConfigurationAction(AppController.INSTANCE.getMainStage().getScene().getRoot(), configDesc, false);
            ConfigActionController.INSTANCE.executeAction(openConfigAction);
        });

        // Update callback : show notifications when update downloaded/finished
        InstallationController.INSTANCE.setUpdateDownloadFinishedCallback(() -> {
            Platform.runLater(() -> {
                if (AppController.INSTANCE.currentModeProperty().get() != AppMode.USE) {
                    LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("notification.info.update.download.finished.title", false,
                            "notification.info.update.download.finish.restart.button", () -> ConfigActionController.INSTANCE.executeAction(new GlobalActions.RestartAction(AppController.INSTANCE.getMainStage().getScene().getRoot())))
                    );
                }
            });
        });
        InstallationController.INSTANCE.setUpdateFinishedCallback(() -> {
            Platform.runLater(() -> {
                if (AppController.INSTANCE.currentModeProperty().get() != AppMode.USE) {
                    LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("notification.info.update.done.title", InstallationController.INSTANCE.getBuildProperties().getVersionLabel()), false));
                    UIUtils.openUrlInDefaultBrowser(InstallationController.INSTANCE.getBuildProperties().getAppServerUrl() + URL_PATH_CHANGELOG);
                }
            });
        });
        InstallationController.INSTANCE.setPluginUpdateCallback(pluginInfo -> {
            Platform.runLater(() -> {
                if (AppController.INSTANCE.currentModeProperty().get() != AppMode.USE) {
                    LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("notification.info.plugin.update.done.title", pluginInfo.getPluginName(), pluginInfo.getPluginVersion()), true));
                }
            });
        });
    }

    // Class part : "Base"
    //========================================================================
    @Override
    public ViewProviderI getViewProvider() {
        return this.configViewProvider;
    }

    @Override
    public Scene initializeAndGetScene() {
        return this.configScene;
    }

    @Override
    public AppMode getMode() {
        return AppMode.CONFIG;
    }

    @Override
    public ObjectProperty<LCConfigurationI> currentConfigurationProperty() {
        return this.currentConfiguration;
    }

    @Override
    public ObjectProperty<LCConfigurationI> configurationBeforeChangeProperty() {
        return this.configurationBeforeChange;
    }
    //========================================================================

    // Class part : "LifeCompanion Start/stop"
    //========================================================================

    /**
     * Custom loading methods : this methods is called before {@link #lcStart()} to allow essential resources to be loaded before config mode init.<br>
     * This allow main loading to be faster.
     */
    public void preLcStart() {
        //Configuration
        try {
            UserBaseConfiguration.INSTANCE.load();
            ConfigMode.LOGGER.info("User configuration fully loaded");
        } catch (Exception e) {
            ConfigMode.LOGGER.warn("The user configuration can't be loaded", e);
        }

        //Load text
        String language = UserBaseConfiguration.INSTANCE.userLanguageProperty().get();
        for (String languageFile : LCConstant.INT_PATH_TEXT_FILES) {
            TranslationManager.INSTANCE.loadLanguageResource(language, languageFile);
        }
    }

    @Override
    public void lcStart() {
        // Binding : on configuration unsave count threshold, fire a warning notification
        ChangeListener<Number> unsavedNotificationListener = (obs, ov, nv) -> {
            int threshold = UserBaseConfiguration.INSTANCE.unsavedChangeInConfigurationThresholdProperty().get();
            // Value become larger than threshold : show a warning notification that suggest to save
            if (LCUtils.nullToZeroInt(ov) < threshold && LCUtils.nullToZeroInt(nv) >= threshold) {
                LCNotificationController.INSTANCE.showNotification(LCNotification.createWarning(Translation.getText("notification.warning.unsaved.changes.configuration.title", nv),
                        "notification.warning.unsaved.changes.action.name", () -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.SaveAction())));
            }
        };
        AppController.INSTANCE.currentConfigConfigurationProperty().addListener((obs, ov, nv) -> {
            if (ov != null)
                ov.unsavedActionProperty().removeListener(unsavedNotificationListener);
            if (nv != null) {
                unsavedNotificationListener.changed(null, Integer.MAX_VALUE, nv.unsavedActionProperty().get());
                nv.unsavedActionProperty().addListener(unsavedNotificationListener);
            }
        });
    }

    @Override
    public void lcExit() {
    }
    //========================================================================

    // Class part : "Mode start/stop"
    //========================================================================
    private static final List<ModeListenerI> configModeListeners = Arrays.asList(ConfigurationErrorController.INSTANCE);

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        ConfigMode.LOGGER.info("Config mode start");
        //Listeners
        for (ModeListenerI configModeListenerI : ConfigMode.configModeListeners) {
            configModeListenerI.modeStart(configuration);
        }
        // Frame parameters
        LCUtils.runOnFXThread(() -> {
            if (!AppController.INSTANCE.isOnEmbeddedDevice()) {
                Stage mainStage = AppController.INSTANCE.getMainStage();
                mainStage.setTitle(AppController.INSTANCE.getMainStageDefaultTitle());
                mainStage.setIconified(false);
                mainStage.setFullScreen(false);
                mainStage.setMaximized(UserBaseConfiguration.INSTANCE.launchMaximizedProperty().get());
                if (!UserBaseConfiguration.INSTANCE.launchMaximizedProperty().get()) {
                    mainStage.setWidth(UserBaseConfiguration.INSTANCE.mainFrameWidthProperty().get());
                    mainStage.setHeight(UserBaseConfiguration.INSTANCE.mainFrameHeightProperty().get());
                    mainStage.centerOnScreen();
                }
                mainStage.opacityProperty().unbind();
                mainStage.setOpacity(1.0);
            }
        });
        SessionStatsController.INSTANCE.modeStarted(AppMode.CONFIG, configuration);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        //Listeners
        for (ModeListenerI configModeListenerI : ConfigMode.configModeListeners) {
            configModeListenerI.modeStop(configuration);
        }
        SessionStatsController.INSTANCE.modeStopped(AppMode.CONFIG);
    }

    @Override
    public boolean isSkipNextModeStartAndReset() {
        return false;
    }
    //========================================================================

}
