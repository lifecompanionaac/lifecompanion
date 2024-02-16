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

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.lifecompanion.controller.virtualmouse.ScanningMouseController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.VirtualMouseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Scene to display the virtual mouse components.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CrossScanningMouseScene extends Scene implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrossScanningMouseScene.class);
    /**
     * Root for this scene
     */
    private final Pane root;

    /**
	 * All possible drawing
	 */
	private final Map<VirtualMouseType, ScanningMouseDrawingI> possiblesDrawing;

    public CrossScanningMouseScene(final Pane root) {
        super(root);
        this.root = root;
        this.possiblesDrawing = new HashMap<>();
        this.root.setStyle("-fx-background-color: transparent;");
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        this.setFill(Color.TRANSPARENT);
        this.possiblesDrawing.put(VirtualMouseType.CROSS_SCANNING, new CrossScannigView());
    }

    @Override
    public void initBinding() {
        ScanningMouseController mouseController = ScanningMouseController.INSTANCE;
            mouseController.mouseDrawingProperty().addListener((obs, ov, nv) -> {
                if (ov != null) {
                    ScanningMouseDrawingI previousDrawing = this.possiblesDrawing.get(ov);
                    if (previousDrawing != null) {
                        previousDrawing.unbind();
                        this.root.getChildren().remove(previousDrawing.getView());
                    }
                }
                this.setNewMouseDrawing(mouseController, nv);
            });
            this.setNewMouseDrawing(mouseController, mouseController.mouseDrawingProperty().get());
    }

    private void setNewMouseDrawing(final ScanningMouseController mouseController, final VirtualMouseType nv) {
        if (nv != null) {
            ScanningMouseDrawingI newDrawing = this.possiblesDrawing.get(nv);
            if (newDrawing != null) {
                newDrawing.bind(mouseController);
                this.root.getChildren().add(newDrawing.getView());
            } else {
                LOGGER.error("No drawing found for {}", nv);
            }
        }
    }

    @Override
    public void initListener() {
    }

    //========================================================================

}
