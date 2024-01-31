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

package org.lifecompanion.ui.virtualmouse;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.virtualmouse.ScanningMouseController;
import org.lifecompanion.controller.virtualmouse.VirtualMouseController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.VirtualMouseDrawing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Scene to display the virtual mouse components.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ScanningMouseScene extends Scene implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScanningMouseScene.class);
    /**
     * Root for this scene
     */
    private final Group root;
    Line lineX;
    Line lineY;

    public ScanningMouseScene(final Group root) {
        super(root);
        this.root = root;
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        this.setFill(Color.TRANSPARENT);
        lineX = new Line(0, 0, 0, 0);
        lineX.endYProperty().bind(heightProperty());
        lineX.setStrokeWidth(5.0);
        lineX.setStroke(Color.RED);

        lineY = new Line(0, 0, 0, 0);
        lineY.endXProperty().bind(widthProperty());
        lineY.setStrokeWidth(5.0);
        lineY.setStroke(Color.RED);

        this.root.getChildren().add(lineX);
        this.root.getChildren().add(lineY);

    }

    @Override
    public void initBinding() {
        ScanningMouseController.INSTANCE.mouseXProperty().addListener((obs, ov, nv) -> {
            lineX.setStartX(nv.doubleValue());
            lineX.setEndX(nv.doubleValue());
        });
        ScanningMouseController.INSTANCE.mouseYProperty().addListener((obs, ov, nv) -> {
            lineY.setStartY(nv.doubleValue());
            lineY.setEndY(nv.doubleValue());
        });
    }

    @Override
    public void initListener() {
    }

    public void startMouseClic(BiConsumer<Double, Double> callback) {
        LOGGER.info("First clic !");
        ScanningMouseController.INSTANCE.startMovingMouseForX();

        SelectionModeController.INSTANCE.pauseCurrentScanningUntilNextSelection(() -> {
            LOGGER.info("Second clic !");
            ScanningMouseController.INSTANCE.stopMovingMouse();
            ScanningMouseController.INSTANCE.startMovingMouseForY();

            SelectionModeController.INSTANCE.pauseCurrentScanningUntilNextSelection(() -> {
                LOGGER.info("Third clic !");
                ScanningMouseController.INSTANCE.stopMovingMouse();
                callback.accept(lineX.getStartX(), lineY.getStartY());
                return true;
            });
            return true;
        });
    }
    //========================================================================

}
