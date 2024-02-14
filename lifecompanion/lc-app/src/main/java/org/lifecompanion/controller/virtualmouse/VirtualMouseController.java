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

package org.lifecompanion.controller.virtualmouse;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.model.api.configurationcomponent.FramePosition;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.javafx.RobotProvider;
import org.lifecompanion.util.javafx.StageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.geom.AffineTransform;

/**
 * Controller to simulate mouse event on a configuration.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum VirtualMouseController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMouseController.class);

    private static final int MOUSE_ACTION_DELAY = 40;

    private static final int MOUSE_SCROLL_DELAY = 20;

    /**
     * AWT robot to simulate mouse events
     */
    private Robot robot;

    /**
     * Frame scale (got from AWT : used to correct mouse position)
     */
    private double frameXScale = 1.0, frameYScale = 1.0;

    /**
     * Output scale (got from JFX : used to correct mouse position)
     */
    private double xOutputScale = 1.0, yOutputScale = 1.0;

    VirtualMouseController() {
    }

    // Class part : "Clic API"
    //========================================================================
    private static boolean checkIfVirtualMouseEnabled() {
        boolean enabled = !GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_VIRTUAL_MOUSE);
        if (!enabled) {
            LOGGER.info("Ignored virtual mouse action because {} is enabled", GlobalRuntimeConfiguration.DISABLE_VIRTUAL_MOUSE);
        }
        return enabled;
    }

    // Class part : "Internal mouse event API"
    //========================================================================
    public void moveMouseToWithDelay(final double x, final double y) {
        mouseMoveDirect(x, y);
        this.robot.delay(VirtualMouseController.MOUSE_ACTION_DELAY);
    }

    public void mouseMoveDirect(double x, double y) {
        this.robot.mouseMove((int) (x * this.xOutputScale / this.frameXScale), (int) (y * this.yOutputScale / this.frameYScale));
    }

    public void executeMouseClic(final int mouseButton) {
        this.robot.mousePress(InputEvent.getMaskForButton(mouseButton));
        this.robot.delay(VirtualMouseController.MOUSE_ACTION_DELAY);
        this.robot.mouseRelease(InputEvent.getMaskForButton(mouseButton));
    }

    public void executeMouseWheelDown(final int amount) {
        if (checkIfVirtualMouseEnabled()) {
            this.checkRobotInit();
            this.executeMouseWheel(amount);
        }
    }

    public void executeMouseWheelUp(final int amount) {
        if (checkIfVirtualMouseEnabled()) {
            this.checkRobotInit();
            this.executeMouseWheel(-amount);
        }
    }

    /**
     * Simulate a mouse wheel.</br>
     * Code explanation : mouse wheel can be consumed by a background frame only if the mouse is on this frame, so to ensure that the mouse will probably be on the frame, we set the
     * mouse to the center or beside the center if the frame is on the center.</br>
     * This can sometimes not work and be dirty, but that the only pure Java solution by now...
     *
     * @param amount amount of wheel move
     */
    private void executeMouseWheel(final int amount) {
        final Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        final Stage stage = AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
        double x = screenBounds.getWidth() / 2.0, y = screenBounds.getHeight() / 2.0;
        while (x > 0 && x > y && new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).contains(x, y)) {
            x -= 10.0;
            y -= 10.0;
        }
        if (x > 0 && y > 0) {
            mouseMoveDirect(x, y);
            this.robot.delay(MOUSE_SCROLL_DELAY);
            this.robot.mouseWheel(amount);
            centerMouseOnStage();
            LOGGER.info("Found a position where the mouse can be set to scroll : {}x{}", x, y);
        }
    }

    public void centerMouseOnStage() {
        // Ignore when using multiple screen as AWT Robot doesn't work well on multiple screens
        if (UserConfigurationController.INSTANCE.screenIndexProperty().get() == 0) {
            this.checkRobotInit();
            Stage stage = AppModeController.INSTANCE.getUseModeContext().getStage();
            if (robot != null && stage != null) {
                double x = stage.getX() + stage.getWidth() / 2.0;
                double y = stage.getY() + stage.getHeight() / 2.0;
                this.moveMouseToWithDelay(x, y);
            }
        }
    }

   public void moveFrameToAvoidMouse(double frameWidth, double frameHeight, double mouseX, double mouseY) {
        Stage stage = AppModeController.INSTANCE.getUseModeContext().getStage();
        //Top left
        FramePosition framePosition = null;
        if (new Rectangle2D(0, 0, frameWidth / 2, frameHeight / 2).contains(mouseX, mouseY)) {
            framePosition = FramePosition.BOTTOM_RIGHT;
        }
        //Top righ
        else if (new Rectangle2D(frameWidth / 2, 0, frameWidth / 2, frameHeight / 2).contains(mouseX, mouseY)) {
            framePosition = FramePosition.BOTTOM_LEFT;
        }
        //Bottom right
        else if (new Rectangle2D(frameWidth / 2, frameHeight / 2, frameWidth / 2, frameHeight / 2).contains(mouseX,
                mouseY)) {
            framePosition = FramePosition.TOP_LEFT;
        }
        //Bottom left
        else if (new Rectangle2D(0, frameHeight / 2, frameWidth / 2, frameHeight / 2).contains(mouseX, mouseY)) {
            framePosition = FramePosition.TOP_RIGHT;
        }
        if (framePosition != null) {
            StageUtils.moveStageTo(stage, framePosition);
            centerMouseOnStage();
        }
    }

    private void checkRobotInit() {
        if (this.robot == null) {
            this.robot = RobotProvider.getInstance();
            try {
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                if (ge != null) {
                    final AffineTransform defaultTransform = ge.getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform();
                    if (defaultTransform != null) {
                        this.frameXScale = defaultTransform.getScaleX();
                        this.frameYScale = defaultTransform.getScaleY();
                    }
                }
            } catch (Throwable t) {
                LOGGER.warn("Couldn't get default screen scaling factor", t);
            }
            Screen primaryScreen = Screen.getPrimary();
            this.xOutputScale = primaryScreen.getOutputScaleX();
            this.yOutputScale = primaryScreen.getOutputScaleY();
            LOGGER.info("Screen scaling : \n\tAWT : {}x{}\n\tOutput scale : {}x{}", frameXScale, frameYScale, xOutputScale, yOutputScale);
        }
    }

    public Rectangle2D getMainFrameBounds() {
        Stage stage = AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
        return new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
    }
    //========================================================================
}