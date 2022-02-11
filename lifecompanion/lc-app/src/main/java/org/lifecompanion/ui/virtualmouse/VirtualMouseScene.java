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

import java.util.HashMap;
import java.util.Map;

import org.lifecompanion.controller.virtualmouse.VirtualMouseController;
import org.lifecompanion.model.api.configurationcomponent.VirtualMouseDrawing;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

/**
 * Scene to display the virtual mouse components.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VirtualMouseScene extends Scene implements LCViewInitHelper {
	private final static Logger LOGGER = LoggerFactory.getLogger(VirtualMouseScene.class);
	/**
	 * Root for this scene
	 */
	private final Group root;

	/**
	 * All possible drawing
	 */
	private final Map<VirtualMouseDrawing, VirtualMouseDrawingI> possiblesDrawing;

	public VirtualMouseScene(final Group root) {
		super(root);
		this.root = root;
		this.possiblesDrawing = new HashMap<>();
		this.initAll();
	}

	// Class part : "UI"
	//========================================================================
	@Override
	public void initUI() {
		this.setFill(Color.TRANSPARENT);
		this.possiblesDrawing.put(VirtualMouseDrawing.SIMPLE_CIRCLE, new SimpleCircleView());
		this.possiblesDrawing.put(VirtualMouseDrawing.TARGET, new TargetView());
	}

	@Override
	public void initBinding() {
		VirtualMouseController mouseController = VirtualMouseController.INSTANCE;
		mouseController.mouseDrawingProperty().addListener((obs, ov, nv) -> {
			if (ov != null) {
				VirtualMouseDrawingI previousDrawing = this.possiblesDrawing.get(ov);
				if (previousDrawing != null) {
					previousDrawing.unbind();
					this.root.getChildren().remove(previousDrawing.getView());
				}
			}
			this.setNewMouseDrawing(mouseController, nv);
		});
		this.setNewMouseDrawing(mouseController, mouseController.mouseDrawingProperty().get());
	}

	private void setNewMouseDrawing(final VirtualMouseController mouseController, final VirtualMouseDrawing nv) {
		if (nv != null) {
			VirtualMouseDrawingI newDrawing = this.possiblesDrawing.get(nv);
			if (newDrawing != null) {
				newDrawing.bind(mouseController);
				this.root.getChildren().add(newDrawing.getView());
			} else {
				VirtualMouseScene.LOGGER.warn("Didn't find any view instance for {}", nv);
			}
		}
	}

	@Override
	public void initListener() {}
	//========================================================================

}
