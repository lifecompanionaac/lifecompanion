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

package org.lifecompanion.util.javafx;

import com.sun.glass.ui.Window;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Dialog;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.configurationcomponent.FramePosition;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.notification.NotificationStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings("restriction")
public class StageUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(StageUtils.class);

    public static String getStageDefaultTitle() {
        return LCConstant.NAME + " v" + InstallationController.INSTANCE.getBuildProperties().getVersionLabel();
    }

    public static void moveStageTo(final Stage stage, final FramePosition framePosition) {
        Screen screen = getStageScreen(stage);
        Rectangle2D screenBounds = screen.getVisualBounds();//Issue #169 : windows shouldn't be on window task bar
        double centerX = screenBounds.getMinX() + (screenBounds.getMaxX() - screenBounds.getMinX()) / 2.0 - stage.getWidth() / 2.0;
        double centerY = screenBounds.getMinY() + (screenBounds.getMaxY() - screenBounds.getMinY()) / 2.0 - stage.getHeight() / 2.0;
        switch (framePosition) {
            case BOTTOM_RIGHT:
                stage.setX(screenBounds.getMaxX() - stage.getWidth());
                stage.setY(screenBounds.getMaxY() - stage.getHeight());
                break;
            case BOTTOM_LEFT:
                stage.setX(screenBounds.getMinX());
                stage.setY(screenBounds.getMaxY() - stage.getHeight());
                break;
            case TOP_RIGHT:
                stage.setX(screenBounds.getMaxX() - stage.getWidth());
                stage.setY(screenBounds.getMinY());
                break;
            case TOP_LEFT:
                stage.setX(screenBounds.getMinX());
                stage.setY(screenBounds.getMinY());
                break;
            case TOP:
                stage.setX(centerX);
                stage.setY(screenBounds.getMinY());
                break;
            case LEFT:
                stage.setX(screenBounds.getMinX());
                stage.setY(centerY);
                break;
            case RIGHT:
                stage.setX(screenBounds.getMaxX() - stage.getWidth());
                stage.setY(centerY);
                break;
            case BOTTOM:
                stage.setX(centerX);
                stage.setY(screenBounds.getMaxY() - stage.getHeight());
                break;
            default:
                centerOnScreen(screen, stage);
                break;
        }
    }

    public static Stage getEditOrUseStageVisible() {
        return AppModeController.INSTANCE.modeProperty().get() == AppMode.EDIT || AppModeController.INSTANCE.getUseModeContext()
                .stageProperty()
                .get() == null ? AppModeController.INSTANCE.getEditModeContext()
                .getStage() : AppModeController.INSTANCE.getUseModeContext()
                .stageProperty()
                .get();
    }

    public static javafx.stage.Window getOnTopWindowExcludingNotification() {
        return Stage.getWindows().stream().filter(javafx.stage.Window::isShowing).filter(w -> !(w instanceof NotificationStage)).map(w -> {
            int depth = 0;
            javafx.stage.Window owner = w;
            do {
                depth++;
                owner = (owner instanceof Stage) ? ((Stage) owner).getOwner() : null;
            } while (owner != null);
            return Pair.of(w, depth);
        }).min((p1, p2) -> Integer.compare(p2.getRight(), p1.getRight())).map(Pair::getLeft).orElse(null);
    }

    /**
     * Method that use the internal API to set the stage focusable property.<br>
     * This is a workaround, this should be changed if a public API to change focus state is exposed.<br>
     * <strong>This method will fail if called before the stage is shown</strong><br>
     * Could hopefully change if <a href="https://bugs.openjdk.org/browse/JDK-8090742?jql=text%20~%20%22unfocusable%20stage%22%20ORDER%20BY%20created%20DESC%2C%20lastViewed%20DESC">JDK-8090742</a> is done one day...
     *
     * @param stage     the stage to change
     * @param focusable the focusable value
     */
    public static void setFocusableInternalAPI(final Stage stage, final boolean focusable) {
        try {
            Method getPeer = javafx.stage.Window.class.getDeclaredMethod("getPeer");
            getPeer.setAccessible(true);
            final Object tkStage = getPeer.invoke(stage);

            Method getPlatformWindow = tkStage.getClass().getDeclaredMethod("getPlatformWindow");
            getPlatformWindow.setAccessible(true);

            Window platformWindow = (Window) getPlatformWindow.invoke(tkStage);
            platformWindow.setFocusable(focusable);
            LOGGER.info("Called setFocusable({}) on native  stage", focusable);
        } catch (Throwable t) {
            LOGGER.warn("Couldn't use sun* internal API to change the window properties", t);
        }
    }

    public static void applyDefaultStageConfiguration(Stage stage) {
        stage.setTitle(LCConstant.NAME);
        stage.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        stage.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
    }

    // SCREENS
    //========================================================================
    public static boolean isStageOutOfScreenBounds(Stage stage) {
        Screen stageScreen = getStageScreen(stage);
        Rectangle2D stageScreenBounds = stageScreen.getBounds();
        return stage.getWidth() > stageScreenBounds.getWidth() - 1 || stage.getHeight() > stageScreenBounds.getHeight() - 1;
    }

    public static Screen getStageScreen(Stage stage) {
        Rectangle2D stageCenterPoint = new Rectangle2D(stage.getX() + stage.getWidth() / 2, stage.getY() + stage.getHeight() / 2, 1, 1);
        ObservableList<Screen> screensContainingStage = Screen.getScreensForRectangle(stageCenterPoint);
        return screensContainingStage.isEmpty() ? Screen.getPrimary() : screensContainingStage.get(0);
    }

    public static void centerOnScreen(Screen screen, Stage stage) {
        Rectangle2D visualBounds = screen.getVisualBounds();
        stage.setX(visualBounds.getMinX());
        stage.setY(visualBounds.getMinY());
        stage.centerOnScreen();
    }

    public static void centerOnOwnerOrOnCurrentStageAndShow(Stage stage) {
        javafx.stage.Window stageToCenterOn = stage.getOwner();
        if (stageToCenterOn == null) {
            stageToCenterOn = getEditOrUseStageVisible();
        }
        if (stageToCenterOn != null) {
            double stageWidth = stage.getWidth() > 0 ? stage.getWidth() : 300.0;
            double stageHeight = stage.getHeight() > 0 ? stage.getHeight() : 300.0;
            stage.setX((stageToCenterOn.getX() + stageToCenterOn.getWidth() / 2.0) - stageWidth / 2.0);
            stage.setY((stageToCenterOn.getY() + stageToCenterOn.getHeight() / 2.0) - stageHeight / 2.0);
        }
        stage.show();
    }

    public static void centerOnOwnerOrOnCurrentStage(Dialog<?> dialog) {
        javafx.stage.Window stageToCenterOn = dialog.getOwner();
        if (stageToCenterOn == null) {
            stageToCenterOn = getEditOrUseStageVisible();
        }
        if (stageToCenterOn != null) {
            double stageWidth = dialog.getWidth() > 0 ? dialog.getWidth() : 300.0;
            double stageHeight = dialog.getHeight() > 0 ? dialog.getHeight() : 300.0;
            dialog.setX((stageToCenterOn.getX() + stageToCenterOn.getWidth() / 2.0) - stageWidth / 2.0);
            dialog.setY((stageToCenterOn.getY() + stageToCenterOn.getHeight() / 2.0) - stageHeight / 2.0);
        }
    }

    public static Screen getDestinationScreen() {
        int screenIndex = UserConfigurationController.INSTANCE.screenIndexProperty().get();
        Screen found = null;
        ObservableList<Screen> screens = Screen.getScreens();
        Screen primaryScreen = Screen.getPrimary();
        if (screenIndex >= 1) {
            found = screens.stream().skip(screenIndex).filter(s -> s != primaryScreen).findFirst().orElse(null);
        }
        if (found == null) {
            LOGGER.warn("Ignored user config screen index {} as the JFX screen list doesn't match, will return the primary screen", screenIndex);
            found = primaryScreen;
        }
        return found;
    }

    public static GraphicsDevice getDestinationGraphicDevice() {
        int screenIndex = UserConfigurationController.INSTANCE.screenIndexProperty().get();
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (localGraphicsEnvironment != null) {
            GraphicsDevice found = null;
            GraphicsDevice defaultScreenDevice = localGraphicsEnvironment.getDefaultScreenDevice();
            GraphicsDevice[] screenDevices = localGraphicsEnvironment.getScreenDevices();
            if (screenIndex >= 1) {
                found = Arrays.stream(screenDevices).filter(s -> s != defaultScreenDevice).findFirst().orElse(null);
            }
            if (found == null) {
                LOGGER.warn("Ignored user config screen index {} as the AWT screen list doesn't match, will return the primary screen", screenIndex);
                found = defaultScreenDevice;
            }
            return found;
        }
        return null;
    }

    public static void fixMaximizedVisualBounds(Stage stage) {
        if (stage.getStyle() == StageStyle.UTILITY) {
            // Undecorated and maximized ignore the task bar height, which can cause layout problems
            stage.maximizedProperty().addListener((obs, ov, nv) -> {
                if (nv) {
                    Screen stageScreen = StageUtils.getStageScreen(stage);
                    stage.setHeight(stageScreen.getVisualBounds().getHeight());
                }
            });
        } else {
            LOGGER.warn("fixMaximizedVisualBounds(...) ignored as the stage style is {} and not {}", stage.getStyle(), StageStyle.UTILITY);
        }
    }
    //========================================================================
}
