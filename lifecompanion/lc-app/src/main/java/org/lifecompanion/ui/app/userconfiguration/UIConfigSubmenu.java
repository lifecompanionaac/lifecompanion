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
package org.lifecompanion.ui.app.userconfiguration;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.lifecompanion.controller.appinstallation.OptionalResourceController;
import org.lifecompanion.controller.appinstallation.task.InstallOptionalResourceTask;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editaction.GlobalActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.io.task.CleanupTempFileTask;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.model.impl.appinstallation.OptionalResourceEnum;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.ui.common.pane.specific.cell.SimpleTextListCell;
import org.lifecompanion.ui.common.pane.specific.cell.TextPositionListCell;
import org.lifecompanion.ui.common.pane.specific.cell.TitleAndDescriptionListCell;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.javafx.*;

/**
 * Stage configuration
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UIConfigSubmenu extends ScrollPane implements UserConfigSubmenuI, LCViewInitHelper {
    private static final double SPINNER_WIDTH = 150.0;

    /**
     * Spinner to set the frame width/height
     */
    private Spinner<Integer> spinnerFrameWidth, spinnerFrameHeight;

    /**
     * Spinner unsaved modification
     */
    private Spinner<Integer> spinnerUnsavedModification;

    /**
     * Spinner to set config selection size
     */
    private Spinner<Double> spinnerStrokeSize, spinnerDashSize;

    private ComboBox<TextPosition> comboBoxDefaultTextPositionOnImageSelection;

    /**
     * To enable/disable fullscreen
     */
    private ToggleSwitch toggleEnableFullScreen;

    private ToggleSwitch toggleAutoSelectImages;

    private ToggleSwitch toggleEnableSpeechOptimization;

    /**
     * To enable/disable tips on startup
     */
    private ToggleSwitch toggleEnableTipsStartup;
    private ToggleSwitch toggleEnableLaunchLCSystemStartup;
    private ToggleSwitch toggleEnableRecordAndSendSessionStats;
    private ToggleSwitch toggleEnableAutoShowVirtualKeyboard;
    private ToggleSwitch toggleDisabledExitInUseMode;
    private ToggleSwitch toggleSecureGoToEditModeProperty;
    private ToggleSwitch toggleAutoConfigurationProfileBackup;
    private ToggleSwitch toggleDisableFullscreenShortcut;
    private ToggleSwitch toggleEnablePreviousConfigurationShortcut;

    private Button buttonCleanupFiles, buttonAddMakaton;

    private ComboBox<String> comboBoxLanguage;

    private Label labelMakatonEnabled;

    public UIConfigSubmenu() {
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        Label labelConfigGeneral = FXControlUtils.createTitleLabel("user.config.part.ui.general");
        toggleEnableLaunchLCSystemStartup = FXControlUtils.createToggleSwitch("user.config.launch.lc.startup", null);
        toggleEnableRecordAndSendSessionStats = FXControlUtils.createToggleSwitch("user.config.enable.session.stats", null);
        toggleEnableAutoShowVirtualKeyboard = FXControlUtils.createToggleSwitch("user.config.auto.show.virtual.keyboard", null);

        // Optional resource
        Label labelOptionalResource = FXControlUtils.createTitleLabel("user.config.part.ui.optional.resource");
        Label labelExplainOptionalResource = new Label(Translation.getText("optional.resource.explain.label"));
        labelExplainOptionalResource.getStyleClass().addAll("text-wrap-enabled", "text-font-italic", "text-fill-gray");

        labelMakatonEnabled = new Label(Translation.getText("optional.resource.makaton.enabled"));
        labelMakatonEnabled.getStyleClass().add("text-weight-bold");
        buttonAddMakaton = FXControlUtils.createLeftTextButton(Translation.getText("button.add.optional.resource.makaton"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        TilePane paneMakaton = new TilePane(buttonAddMakaton, labelMakatonEnabled);

        // Use mode
        Label labelUseMode = FXControlUtils.createTitleLabel("user.config.part.ui.use.mode");
        toggleDisabledExitInUseMode = FXControlUtils.createToggleSwitch("user.config.disable.exit.in.use.mode", null);
        Label labelExplainExitUseMode = new Label(Translation.getText("tooltip.explain.disable.exit.use.mode"));
        labelExplainExitUseMode.getStyleClass().addAll("text-wrap-enabled", "text-font-italic", "text-fill-gray");
        toggleSecureGoToEditModeProperty = FXControlUtils.createToggleSwitch("configuration.secured.config.mode", null);
        Label labelExplainSecuredConfigMode = new Label(Translation.getText("tooltip.explain.use.param.secured.config.mode"));
        labelExplainSecuredConfigMode.getStyleClass().addAll("text-wrap-enabled", "text-font-italic", "text-fill-gray");

        toggleDisableFullscreenShortcut = FXControlUtils.createToggleSwitch("user.config.disable.fullscreen.shortcut", null);
        Label labelExplainDisableFullscreenShortcut = new Label(Translation.getText("tooltip.user.config.disable.fullscreen.shortcut"));
        labelExplainDisableFullscreenShortcut.getStyleClass().addAll("text-wrap-enabled", "text-font-italic", "text-fill-gray");

        toggleEnablePreviousConfigurationShortcut = FXControlUtils.createToggleSwitch("user.config.enable.previous.configuration.shortcut", null);
        Label labelExplainEnablePreviousConfigurationShortcut = new Label(Translation.getText("tooltip.user.config.enable.previous.configuration.shortcut"));
        labelExplainEnablePreviousConfigurationShortcut.getStyleClass().addAll("text-wrap-enabled", "text-font-italic", "text-fill-gray");

        //Selection parameter
        this.spinnerStrokeSize = FXControlUtils.createDoubleSpinner(1.0, 20.0, 3.0, 1.0, SPINNER_WIDTH);
        Label labelStrokeSize = new Label(Translation.getText("user.config.selection.stroke.size"));
        GridPane.setHgrow(labelStrokeSize, Priority.ALWAYS);
        Label labelDashSize = new Label(Translation.getText("user.config.selection.dash.size"));
        this.spinnerDashSize = FXControlUtils.createDoubleSpinner(1.0, 20.0, 3.0, 1.0, SPINNER_WIDTH);
        GridPane gridPaneStyleParam = createConfigPane();
        int rowStyle = 0;
        gridPaneStyleParam.add(labelStrokeSize, 0, rowStyle);
        gridPaneStyleParam.add(this.spinnerStrokeSize, 1, rowStyle++);
        gridPaneStyleParam.add(labelDashSize, 0, rowStyle);
        gridPaneStyleParam.add(this.spinnerDashSize, 1, rowStyle++);
        Label labelConfigStylePart = FXControlUtils.createTitleLabel("user.config.part.ui.config");

        //Frame parameter
        this.spinnerFrameWidth = FXControlUtils.createIntSpinner(10, Integer.MAX_VALUE, 50, 100, SPINNER_WIDTH);
        GridPane.setHalignment(spinnerFrameWidth, HPos.RIGHT);
        this.spinnerFrameHeight = FXControlUtils.createIntSpinner(10, Integer.MAX_VALUE, 50, 100, SPINNER_WIDTH);
        GridPane.setHalignment(spinnerFrameHeight, HPos.RIGHT);
        Label labelWidth = new Label(Translation.getText("user.config.stage.width"));
        GridPane.setHgrow(labelWidth, Priority.ALWAYS);
        Label labelHeight = new Label(Translation.getText("user.config.stage.height"));
        this.toggleEnableFullScreen = FXControlUtils.createToggleSwitch("user.config.stage.fullscreen", null);
        GridPane.setMargin(this.toggleEnableFullScreen, new Insets(5.0, 0.0, 5.0, 0.0));
        GridPane gridPaneStageParam = createConfigPane();
        int rowStage = 0;
        gridPaneStageParam.add(this.toggleEnableFullScreen, 0, rowStage++, 2, 1);
        gridPaneStageParam.add(labelWidth, 0, rowStage);
        gridPaneStageParam.add(this.spinnerFrameWidth, 1, rowStage++);
        gridPaneStageParam.add(labelHeight, 0, rowStage);
        gridPaneStageParam.add(this.spinnerFrameHeight, 1, rowStage++);
        Label labelStagePart = FXControlUtils.createTitleLabel("user.config.stage.title");

        comboBoxLanguage = new ComboBox<>(FXCollections.observableArrayList(LCConstant.DEFAULT_LANGUAGE, "en"));
        comboBoxLanguage.setButtonCell(new SimpleTextListCell<>(this::getLanguageTitle));
        comboBoxLanguage.setCellFactory(lv -> new TitleAndDescriptionListCell<>(this::getLanguageTitle, this::getLanguageDescription));
        comboBoxLanguage.setPrefWidth(250.0);
        GridPane.setHalignment(comboBoxLanguage, HPos.RIGHT);
        Label labelLanguage = new Label(Translation.getText("user.config.language"));
        gridPaneStageParam.add(labelLanguage, 0, rowStage);
        gridPaneStageParam.add(this.comboBoxLanguage, 1, rowStage++);

        Label labelConfigFiles = FXControlUtils.createTitleLabel("user.config.part.file.config");
        buttonCleanupFiles = FXControlUtils.createLeftTextButton(Translation.getText("button.cleanup.temp.file"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH_ALT).size(16).color(LCGraphicStyle.MAIN_DARK), "button.cleanup.temp.file.tooltip");
        buttonCleanupFiles.setMaxWidth(Double.MAX_VALUE);
        buttonCleanupFiles.setAlignment(Pos.CENTER);
        toggleAutoConfigurationProfileBackup = FXControlUtils.createToggleSwitch("user.config.enable.profile.configuration.auto.backup", null);

        //Unsaved modification
        Label labelConfigTitle = FXControlUtils.createTitleLabel("user.config.configuration.title");
        this.spinnerUnsavedModification = FXControlUtils.createIntSpinner(1, 5000, 5, 10, SPINNER_WIDTH);
        GridPane.setMargin(spinnerUnsavedModification, new Insets(0, 0, 10, 0));

        Label labelUnsavedThreshold = new Label(Translation.getText("user.config.unsaved.modification.threshold"));
        toggleAutoSelectImages = FXControlUtils.createToggleSwitch("auto.select.key.image.config", null);
        Label labelExplainAutoSelectImages = new Label(Translation.getText("auto.select.key.image.config.description"));
        labelExplainAutoSelectImages.getStyleClass().addAll("text-wrap-enabled", "text-font-italic", "text-fill-gray");
        GridPane.setMargin(labelExplainAutoSelectImages, new Insets(0, 0, 10, 0));

        toggleEnableSpeechOptimization = FXControlUtils.createToggleSwitch("enable.speech.optimization.control", null);
        Label labelExplainSpeechOptimization = new Label(Translation.getText("enable.speech.optimization.control.description"));
        labelExplainSpeechOptimization.getStyleClass().addAll("text-wrap-enabled", "text-font-italic", "text-fill-gray");
        GridPane.setMargin(labelExplainSpeechOptimization, new Insets(0, 0, 10, 0));

        Label labelDefaultTextPositionOnImageSelection = new Label(Translation.getText("default.text.position.image.selection.field"));
        Label labelDefaultTextPositionOnImageSelectionExplain = new Label(Translation.getText("default.text.position.image.selection.explain"));
        labelDefaultTextPositionOnImageSelectionExplain.getStyleClass().addAll("text-wrap-enabled", "text-font-italic", "text-fill-gray");
        GridPane.setMargin(labelDefaultTextPositionOnImageSelectionExplain, new Insets(0, 0, 10, 0));
        this.comboBoxDefaultTextPositionOnImageSelection = new ComboBox<>(FXCollections.observableArrayList(TextPosition.values()));
        this.comboBoxDefaultTextPositionOnImageSelection.setButtonCell(new TextPositionListCell(false));
        this.comboBoxDefaultTextPositionOnImageSelection.setCellFactory(lv -> new TextPositionListCell(true));
        this.comboBoxDefaultTextPositionOnImageSelection.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHgrow(labelUnsavedThreshold, Priority.ALWAYS);
        GridPane gridPaneConfiguration = createConfigPane();
        int rowConfig = 0;
        gridPaneConfiguration.add(labelUnsavedThreshold, 0, rowConfig);
        gridPaneConfiguration.add(this.spinnerUnsavedModification, 1, rowConfig++);

        gridPaneConfiguration.add(labelDefaultTextPositionOnImageSelection, 0, rowConfig);
        gridPaneConfiguration.add(comboBoxDefaultTextPositionOnImageSelection, 1, rowConfig++);
        gridPaneConfiguration.add(labelDefaultTextPositionOnImageSelectionExplain, 0, rowConfig++, 2, 1);

        gridPaneConfiguration.add(toggleAutoSelectImages, 0, rowConfig++, 2, 1);
        gridPaneConfiguration.add(labelExplainAutoSelectImages, 0, rowConfig++, 2, 1);
        gridPaneConfiguration.add(toggleEnableSpeechOptimization, 0, rowConfig++, 2, 1);
        gridPaneConfiguration.add(labelExplainSpeechOptimization, 0, rowConfig++, 2, 1);

        //Tips
        Label labelConfigTips = FXControlUtils.createTitleLabel("user.config.tips.title");
        toggleEnableTipsStartup = FXControlUtils.createToggleSwitch("user.config.tips.show.startup", null);
        GridPane.setHgrow(toggleEnableTipsStartup, Priority.ALWAYS);
        GridPane.setMargin(this.toggleEnableTipsStartup, new Insets(5.0, 0.0, 5.0, 0.0));

        //Add
        VBox totalBox = new VBox(10.0,
                labelConfigGeneral, toggleEnableAutoShowVirtualKeyboard, toggleEnableLaunchLCSystemStartup, toggleEnableRecordAndSendSessionStats,
                labelUseMode, toggleSecureGoToEditModeProperty, labelExplainSecuredConfigMode, toggleDisabledExitInUseMode, labelExplainExitUseMode,
                toggleDisableFullscreenShortcut, labelExplainDisableFullscreenShortcut, toggleEnablePreviousConfigurationShortcut, labelExplainEnablePreviousConfigurationShortcut,
                labelOptionalResource, labelExplainOptionalResource, paneMakaton,
                labelConfigStylePart, gridPaneStyleParam,
                labelConfigTitle, gridPaneConfiguration,
                labelConfigFiles,
                toggleAutoConfigurationProfileBackup,
                buttonCleanupFiles,
                labelStagePart, gridPaneStageParam
        );
        totalBox.setPadding(new Insets(10.0));
        this.setFitToWidth(true);
        this.setContent(totalBox);
    }
    //========================================================================

    private String getLanguageTitle(String id) {
        return Translation.getText("language.translation.title." + id);
    }

    private String getLanguageDescription(String id) {
        return Translation.getText("language.translation.description." + id);
    }

    private GridPane createConfigPane() {
        GridPane gridPaneStageParam = new GridPane();
        gridPaneStageParam.setVgap(5.0);
        gridPaneStageParam.setHgap(5.0);
        return gridPaneStageParam;
    }

    @Override
    public void updateFields() {
        this.spinnerFrameWidth.getValueFactory().setValue(UserConfigurationController.INSTANCE.mainFrameWidthProperty().get());
        this.spinnerFrameHeight.getValueFactory().setValue(UserConfigurationController.INSTANCE.mainFrameHeightProperty().get());
        this.spinnerStrokeSize.getValueFactory().setValue(UserConfigurationController.INSTANCE.selectionStrokeSizeProperty().get());
        this.spinnerDashSize.getValueFactory().setValue(UserConfigurationController.INSTANCE.selectionDashSizeProperty().get());
        this.toggleEnableFullScreen.setSelected(UserConfigurationController.INSTANCE.launchMaximizedProperty().get());
        this.toggleEnableTipsStartup.setSelected(UserConfigurationController.INSTANCE.showTipsOnStartupProperty().get());
        this.spinnerUnsavedModification.getValueFactory()
                .setValue(UserConfigurationController.INSTANCE.unsavedChangeInConfigurationThresholdProperty().get());
        this.toggleEnableLaunchLCSystemStartup.setSelected(UserConfigurationController.INSTANCE.launchLCSystemStartupProperty().get());
        this.toggleEnableRecordAndSendSessionStats.setSelected(UserConfigurationController.INSTANCE.recordAndSendSessionStatsProperty().get());
        this.toggleEnableAutoShowVirtualKeyboard.setSelected(UserConfigurationController.INSTANCE.autoVirtualKeyboardShowProperty().get());
        this.toggleDisabledExitInUseMode.setSelected(UserConfigurationController.INSTANCE.disableExitInUseModeProperty().get());
        this.toggleSecureGoToEditModeProperty.setSelected(UserConfigurationController.INSTANCE.secureGoToEditModeProperty().get());
        this.toggleAutoConfigurationProfileBackup.setSelected(UserConfigurationController.INSTANCE.autoConfigurationProfileBackupProperty().get());
        this.toggleAutoSelectImages.setSelected(UserConfigurationController.INSTANCE.autoSelectImagesProperty().get());
        this.comboBoxLanguage.getSelectionModel().select(UserConfigurationController.INSTANCE.userLanguageProperty().get());
        this.toggleEnableSpeechOptimization.setSelected(UserConfigurationController.INSTANCE.enableSpeechOptimizationProperty().get());
        this.comboBoxDefaultTextPositionOnImageSelection.getSelectionModel().select(UserConfigurationController.INSTANCE.defaultTextPositionOnImageSelectionProperty().get());
        this.toggleDisableFullscreenShortcut.setSelected(UserConfigurationController.INSTANCE.disableFullscreenShortcutProperty().get());
        this.toggleEnablePreviousConfigurationShortcut.setSelected(UserConfigurationController.INSTANCE.enablePreviousConfigurationShortcutProperty().get());
        updateInstalledOptions();
    }

    private void updateInstalledOptions() {
        boolean makatonInstalled = OptionalResourceController.INSTANCE.getInstalledResource().contains(OptionalResourceEnum.MAKATON);
        buttonAddMakaton.visibleProperty().set(!makatonInstalled);
        labelMakatonEnabled.visibleProperty().set(makatonInstalled);
    }

    @Override
    public void updateModel() {
        UserConfigurationController.INSTANCE.mainFrameWidthProperty().set(this.spinnerFrameWidth.getValue());
        UserConfigurationController.INSTANCE.mainFrameHeightProperty().set(this.spinnerFrameHeight.getValue());
        UserConfigurationController.INSTANCE.launchMaximizedProperty().set(this.toggleEnableFullScreen.isSelected());
        UserConfigurationController.INSTANCE.selectionStrokeSizeProperty().set(this.spinnerStrokeSize.getValue());
        UserConfigurationController.INSTANCE.selectionDashSizeProperty().set(this.spinnerDashSize.getValue());
        UserConfigurationController.INSTANCE.showTipsOnStartupProperty().set(this.toggleEnableTipsStartup.isSelected());
        UserConfigurationController.INSTANCE.unsavedChangeInConfigurationThresholdProperty().set(this.spinnerUnsavedModification.getValue());
        UserConfigurationController.INSTANCE.launchLCSystemStartupProperty().set(toggleEnableLaunchLCSystemStartup.isSelected());
        UserConfigurationController.INSTANCE.recordAndSendSessionStatsProperty().set(toggleEnableRecordAndSendSessionStats.isSelected());
        UserConfigurationController.INSTANCE.autoVirtualKeyboardShowProperty().set(this.toggleEnableAutoShowVirtualKeyboard.isSelected());
        UserConfigurationController.INSTANCE.disableExitInUseModeProperty().set(this.toggleDisabledExitInUseMode.isSelected());
        UserConfigurationController.INSTANCE.secureGoToEditModeProperty().set(this.toggleSecureGoToEditModeProperty.isSelected());
        UserConfigurationController.INSTANCE.autoConfigurationProfileBackupProperty().set(this.toggleAutoConfigurationProfileBackup.isSelected());
        UserConfigurationController.INSTANCE.autoSelectImagesProperty().set(this.toggleAutoSelectImages.isSelected());
        UserConfigurationController.INSTANCE.userLanguageProperty().set(this.comboBoxLanguage.getValue());
        UserConfigurationController.INSTANCE.enableSpeechOptimizationProperty().set(this.toggleEnableSpeechOptimization.isSelected());
        UserConfigurationController.INSTANCE.defaultTextPositionOnImageSelectionProperty().set(comboBoxDefaultTextPositionOnImageSelection.getValue());
        UserConfigurationController.INSTANCE.disableFullscreenShortcutProperty().set(this.toggleDisableFullscreenShortcut.isSelected());
        UserConfigurationController.INSTANCE.enablePreviousConfigurationShortcutProperty().set(this.toggleEnablePreviousConfigurationShortcut.isSelected());
    }

    @Override
    public void initBinding() {
        configurationManagedAndVisibleOnWindowsOnly(toggleEnableAutoShowVirtualKeyboard);
        configurationManagedAndVisibleOnWindowsOnly(toggleEnableLaunchLCSystemStartup);
        buttonAddMakaton.managedProperty().bind(buttonAddMakaton.visibleProperty());
        labelMakatonEnabled.managedProperty().bind(labelMakatonEnabled.visibleProperty());
    }

    private void configurationManagedAndVisibleOnWindowsOnly(Node node) {
        node.setManaged(SystemType.current() == SystemType.WINDOWS);
        node.setVisible(SystemType.current() == SystemType.WINDOWS);
    }

    @Override
    public void initListener() {
        buttonCleanupFiles.setOnAction(e -> {
            CleanupTempFileTask cleanupTempFileTask = new CleanupTempFileTask();
            cleanupTempFileTask.setOnSucceeded(et -> LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("notification.cleanup.file.success",
                    FileNameUtils.getFileSize(cleanupTempFileTask.getValue())))));
            AsyncExecutorController.INSTANCE.addAndExecute(true, false, cleanupTempFileTask);
        });
        buttonAddMakaton.setOnAction(e -> {
            LargeTextInputDialog emailDialog = new LargeTextInputDialog(Translation.getText("optional.resource.add.makaton.message"), "Email adhérent");
            emailDialog.setHeaderText(Translation.getText("optional.resource.add.makaton.header"));
            StageUtils.centerOnOwnerOrOnCurrentStage(emailDialog);
            emailDialog.showAndWait().ifPresent(email -> AsyncExecutorController.INSTANCE.addAndExecute(true, false, OptionalResourceController.INSTANCE.installResource(OptionalResourceEnum.MAKATON, this::updateInstalledOptions, email)));
        });
    }

    @Override
    public Region getView() {
        return this;
    }

    @Override
    public String getTabTitleId() {
        return "user.config.tab.stage";
    }

}
