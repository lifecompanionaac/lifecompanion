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
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.configurationcomponent.FramePosition;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.notification.NotificationStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@SuppressWarnings("restriction")
public class StageUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(StageUtils.class);


    public static String getStageDefaultTitle() {
        return LCConstant.NAME + " v" + InstallationController.INSTANCE.getBuildProperties().getVersionLabel();
    }

    public static void moveStageTo(final Stage stage, final FramePosition framePosition) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();//Issue #169 : windows shouldn't be on window task bar
        double centerX = screenBounds.getWidth() / 2.0 - stage.getWidth() / 2.0;
        double centerY = screenBounds.getHeight() / 2.0 - stage.getHeight() / 2.0;
        switch (framePosition) {
            case BOTTOM_RIGHT:
                stage.setX(screenBounds.getWidth() - stage.getWidth());
                stage.setY(screenBounds.getHeight() - stage.getHeight());
                break;
            case BOTTOM_LEFT:
                stage.setX(0.0);
                stage.setY(screenBounds.getHeight() - stage.getHeight());
                break;
            case TOP_RIGHT:
                stage.setX(screenBounds.getWidth() - stage.getWidth());
                stage.setY(0.0);
                break;
            case TOP_LEFT:
                stage.setX(0.0);
                stage.setY(0.0);
                break;
            case TOP:
                stage.setX(centerX);
                stage.setY(0.0);
                break;
            case LEFT:
                stage.setX(0.0);
                stage.setY(centerY);
                break;
            case RIGHT:
                stage.setX(screenBounds.getWidth() - stage.getWidth());
                stage.setY(centerY);
                break;
            case BOTTOM:
                stage.setX(centerX);
                stage.setY(screenBounds.getHeight() - stage.getHeight());
                break;
            default:
                stage.centerOnScreen();
                break;
        }
    }

    public static Stage getEditOrUseStageVisible() {
        return AppModeController.INSTANCE.modeProperty().get() == AppMode.EDIT || AppModeController.INSTANCE.getUseModeContext().stageProperty().get() == null ? AppModeController.INSTANCE.getEditModeContext().getStage() : AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
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

    public static boolean isStageOutOfScreenBounds(Stage stage) {
        Rectangle2D stageCenterPoint = new Rectangle2D(stage.getX() + stage.getWidth() / 2, stage.getY() + stage.getHeight() / 2, 1, 1);
        ObservableList<Screen> screensContainingStage = Screen.getScreensForRectangle(stageCenterPoint);
        Screen stageScreen = screensContainingStage.isEmpty() ? Screen.getPrimary() : screensContainingStage.get(0);
        Rectangle2D stageScreenBounds = stageScreen.getBounds();
        return stage.getWidth() > stageScreenBounds.getWidth() - 1 || stage.getHeight() > stageScreenBounds.getHeight() - 1;
    }
}
