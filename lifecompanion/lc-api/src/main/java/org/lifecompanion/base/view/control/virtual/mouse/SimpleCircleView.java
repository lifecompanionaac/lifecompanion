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

package org.lifecompanion.base.view.control.virtual.mouse;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.lifecompanion.base.data.control.virtual.mouse.VirtualMouseController;
import org.lifecompanion.base.data.control.virtual.mouse.VirtualMouseDrawingI;

public class SimpleCircleView extends Circle implements VirtualMouseDrawingI {

	private final static double BASE_CIRCLE_RADIUS = 30.0, BASE_CIRCLE_STROKE = 7.0;

	public SimpleCircleView() {
		this.setFill(Color.TRANSPARENT);
	}

	@Override
	public Node getView() {
		return this;
	}

	@Override
	public void bind(final VirtualMouseController mouseController) {
		this.layoutXProperty().bind(mouseController.mouseXProperty());
		this.layoutYProperty().bind(mouseController.mouseYProperty());
		this.strokeProperty().bind(mouseController.colorProperty());
		this.radiusProperty().bind(mouseController.sizeScaleProperty().multiply(SimpleCircleView.BASE_CIRCLE_RADIUS));
		this.strokeWidthProperty().bind(mouseController.sizeScaleProperty().multiply(SimpleCircleView.BASE_CIRCLE_STROKE));
	}

	@Override
	public void unbind() {
		this.layoutXProperty().unbind();
		this.layoutYProperty().unbind();
		this.strokeProperty().unbind();
		this.radiusProperty().unbind();
		this.strokeWidthProperty().unbind();
	}

}
