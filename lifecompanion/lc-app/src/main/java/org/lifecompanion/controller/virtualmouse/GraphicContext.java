/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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
import org.lifecompanion.util.javafx.RobotProvider;
import org.lifecompanion.util.javafx.StageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class GraphicContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphicContext.class);

    private final Robot robot;

    private final GraphicsDevice graphicsDevice;
    private final Rectangle awtBounds;
    private final double awtXScale, awtYScale;

    private final Screen screen;
    private final Rectangle2D jfxBounds;
    private final double jfxXScale, jfxYScale;


    public GraphicContext() throws Exception {
        this.robot = RobotProvider.getInstance();

        // AWT
        this.graphicsDevice = StageUtils.getDestinationGraphicDevice();
        final AffineTransform defaultTransform = graphicsDevice.getDefaultConfiguration().getDefaultTransform();
        if (defaultTransform != null) {
            this.awtXScale = defaultTransform.getScaleX();
            this.awtYScale = defaultTransform.getScaleY();
        } else {
            this.awtXScale = 1.0;
            this.awtYScale = 1.0;
        }
        this.awtBounds = this.graphicsDevice.getDefaultConfiguration().getBounds();

        // JFX
        this.screen = StageUtils.getDestinationScreen();
        this.jfxBounds = screen.getBounds();
        this.jfxXScale = this.screen.getOutputScaleX();
        this.jfxYScale = this.screen.getOutputScaleY();

        LOGGER.info("GraphicContext initialized for virtual mouse\n" +
                "\tJava FX, screen bounds {}, scaling ({},{})\n" +
                "\tAWT, screen bounds {}, scaling ({},{})", jfxBounds, jfxXScale, jfxYScale, awtBounds, awtXScale, awtYScale);
    }

    public Robot getRobot() {
        return robot;
    }

    public GraphicsDevice getGraphicsDevice() {
        return graphicsDevice;
    }

    public Rectangle getAwtBounds() {
        return awtBounds;
    }

    public double getAwtXScale() {
        return awtXScale;
    }

    public double getAwtYScale() {
        return awtYScale;
    }

    public Screen getScreen() {
        return screen;
    }

    public Rectangle2D getJfxBounds() {
        return jfxBounds;
    }

    public double getJfxXScale() {
        return jfxXScale;
    }

    public double getJfxYScale() {
        return jfxYScale;
    }

    public double getUnscaledScreenWidth(){
        return jfxBounds.getWidth() * jfxXScale;
    }

    public double getUnscaledScreenHeight(){
        return jfxBounds.getHeight() * jfxYScale;
    }
}
