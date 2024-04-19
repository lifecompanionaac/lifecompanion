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
import javafx.stage.Stage;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.model.api.configurationcomponent.FramePosition;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.javafx.StageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.InputEvent;

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

    private GraphicContext graphicContext;

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

    /**
     * Move the mouse to the given screen position.<br>
     * The given position should be absolute relative to the current screen scaled with JFX scaling factor.<br>
     * For example, a screen 1920*1080 scaled to 100% should give 1920,1080 to target the bottom right corner while
     * the same screen scaled to 150% should give 1280,720 to target the same location.
     *
     * @param x x pos, should be always positive
     * @param y y pos, should be always positive
     */
    public void moveMouseRelativeScreen(double x, double y) {
        checkGraphicContextInit();
        graphicContext.getRobot()
                .mouseMove(graphicContext.getAwtBounds().x + (int) (x * this.graphicContext.getJfxXScale() / graphicContext.getAwtXScale()),
                        graphicContext.getAwtBounds().y + (int) (y * this.graphicContext.getJfxYScale() / graphicContext.getAwtYScale()));
    }

    public void pauseBeforeNext() {
        checkGraphicContextInit();
        graphicContext.getRobot().delay(MOUSE_ACTION_DELAY);
    }

    public String mouseMoveRelativeScreenUnscaled(double x, double y) {
        checkGraphicContextInit();
        if (x > 0 && y > 0 && x < graphicContext.getUnscaledScreenWidth() && y < graphicContext.getUnscaledScreenHeight()) {
            graphicContext.getRobot().mouseMove((int) (x / graphicContext.getAwtXScale()), (int) (y / graphicContext.getAwtYScale()));
        } else {
            return "Coord out of current, constraint are : 0 < x < " + graphicContext.getUnscaledScreenWidth() + " and 0 < y < " + graphicContext.getUnscaledScreenHeight();
        }
        return null;
    }

    public void executeMouseClic(final int mouseButton) {
        checkGraphicContextInit();
        graphicContext.getRobot().mousePress(InputEvent.getMaskForButton(mouseButton));
        graphicContext.getRobot().delay(VirtualMouseController.MOUSE_ACTION_DELAY);
        graphicContext.getRobot().mouseRelease(InputEvent.getMaskForButton(mouseButton));
    }

    public void executeMouseWheelDown(final int amount) {
        if (checkIfVirtualMouseEnabled()) {
            this.executeMouseWheel(amount);
        }
    }

    public void executeMouseWheelUp(final int amount) {
        if (checkIfVirtualMouseEnabled()) {
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
        checkGraphicContextInit();
        final Rectangle2D screenBounds = graphicContext.getJfxBounds();
        final Stage stage = AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
        double x = screenBounds.getWidth() / 2.0, y = screenBounds.getHeight() / 2.0;
        while (x > 0 && x > y && new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).contains(x, y)) {
            x -= 10.0;
            y -= 10.0;
        }
        if (x > 0 && y > 0) {
            this.moveMouseRelativeScreen(x, y);
            graphicContext.getRobot().delay(MOUSE_SCROLL_DELAY);
            graphicContext.getRobot().mouseWheel(amount);
            centerMouseOnStage();
            LOGGER.info("Found a position where the mouse can be set to scroll : {}x{}", x, y);
        }
    }

    public void centerMouseOnStage() {
        checkGraphicContextInit();
        Stage stage = AppModeController.INSTANCE.getUseModeContext().getStage();
        if (stage != null) {
            //            LOGGER.info("Stage\n\tlocation {},{}\n\tsize {},{}", stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            //            LOGGER.info("Converted location : {},{}", graphicContext.getJfxBounds().getMinX() + stage.getX(), graphicContext.getJfxBounds().getMinY() + stage.getY());
            this.moveMouseRelativeScreen(stage.getX() - graphicContext.getJfxBounds().getMinX() + stage.getWidth() / 2.0,
                    stage.getY() - graphicContext.getJfxBounds().getMinY() + stage.getHeight() / 2.0);
        }
    }

    public void moveFrameToAvoidMouse(double fxStageWidth, double fxStageHeight, double mouseX, double mouseY) {
        Stage stage = AppModeController.INSTANCE.getUseModeContext().getStage();
        //Top left
        FramePosition framePosition = null;
        if (new Rectangle2D(0, 0, fxStageWidth / 2, fxStageHeight / 2).contains(mouseX, mouseY)) {
            framePosition = FramePosition.BOTTOM_RIGHT;
        }
        //Top right
        else if (new Rectangle2D(fxStageWidth / 2, 0, fxStageWidth / 2, fxStageHeight / 2).contains(mouseX, mouseY)) {
            framePosition = FramePosition.BOTTOM_LEFT;
        }
        //Bottom right
        else if (new Rectangle2D(fxStageWidth / 2, fxStageHeight / 2, fxStageWidth / 2, fxStageHeight / 2).contains(mouseX,
                mouseY)) {
            framePosition = FramePosition.TOP_LEFT;
        }
        //Bottom left
        else if (new Rectangle2D(0, fxStageHeight / 2, fxStageWidth / 2, fxStageHeight / 2).contains(mouseX, mouseY)) {
            framePosition = FramePosition.TOP_RIGHT;
        }
        if (framePosition != null) {
            StageUtils.moveStageTo(stage, framePosition);
            centerMouseOnStage();
        }
    }

    private void checkGraphicContextInit() {
        if (this.graphicContext == null) {
            try {
                graphicContext = new GraphicContext();
            } catch (Exception e) {
                LOGGER.error("Could not create the graphic context info", e);
            }
        }
    }

    public GraphicContext getGraphicContext() {
        return graphicContext;
    }

    public Rectangle2D getStageBoundsRelativeCurrentScreen() {
        checkGraphicContextInit();
        Rectangle2D screenBounds = graphicContext.getJfxBounds();
        Stage stage = AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
        return new Rectangle2D((stage.getX() < 0 ? screenBounds.getWidth() : 0) + stage.getX(), (stage.getY() < 0 ? screenBounds.getHeight() : 0) + stage.getY(), stage.getWidth(), stage.getHeight());
    }
    //========================================================================
}