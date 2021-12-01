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

package org.lifecompanion.config.view.component.option;

import org.lifecompanion.api.component.definition.GridChildComponentI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.SelectableComponentI;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.config.UserBaseConfiguration;
import org.lifecompanion.api.style2.definition.AbstractShapeCompStyleI;
import org.lifecompanion.api.style2.definition.GridStyleUserI;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import org.lifecompanion.base.data.style.impl.ShapeStyleBinder;

/**
 * Option that allow a component to be selected
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SelectableOption<T extends SelectableComponentI & GridChildComponentI> extends BaseOptionRegion<T> {

	private Rectangle stroke = new Rectangle();

	public SelectableOption(final T modelP, final boolean grayStrokeOnNotSelected) {
		super(modelP);
		//Style
		this.stroke.setFill(null);
		this.stroke.strokeWidthProperty().bind(UserBaseConfiguration.INSTANCE.selectionStrokeSizeProperty());
		this.stroke.setLayoutX(-LCGraphicStyle.SELECTED_STROKE_GAP);
		this.stroke.setLayoutY(-LCGraphicStyle.SELECTED_STROKE_GAP);
		this.stroke.setStrokeLineCap(StrokeLineCap.ROUND);
		//Bind size and visible
		this.stroke.widthProperty().bind(this.widthProperty().add(LCGraphicStyle.SELECTED_STROKE_GAP * 2));
		this.stroke.heightProperty().bind(this.heightProperty().add(LCGraphicStyle.SELECTED_STROKE_GAP * 2));
		this.stroke.visibleProperty()
				.bind(modelP.showSelectedProperty().or(modelP.showPossibleSelectedProperty().or(new SimpleBooleanProperty(grayStrokeOnNotSelected))));
		//Bind color
		this.stroke.strokeProperty().bind(Bindings.createObjectBinding(() -> {
			return modelP.showSelectedProperty().get() || modelP.showPossibleSelectedProperty().get() ? LCGraphicStyle.SECOND_PRIMARY
					: Color.LIGHTGRAY;
		}, modelP.showSelectedProperty(), modelP.showPossibleSelectedProperty()));
		//Bind style (shape)
		AbstractShapeCompStyleI<?> shapeStyle = null;
		if (this.model instanceof GridPartKeyComponentI) {
			shapeStyle = ((GridPartKeyComponentI) this.model).getKeyStyle();
		} else {
			shapeStyle = ((GridStyleUserI) this.model).getGridShapeStyle();
		}
		ShapeStyleBinder.bindArcSizeComp(this.stroke, shapeStyle, this.stroke.widthProperty(), this.stroke.heightProperty(),
				UserBaseConfiguration.INSTANCE.selectionStrokeSizeProperty(), 1);
		//Bind show selected
		modelP.showSelectedProperty().addListener((obs, oV, nV) -> {
			if (nV || !this.model.showPossibleSelectedProperty().get()) {
				this.stroke.getStrokeDashArray().clear();
				SelectableOption.this.stroke.toFront();
			}
		});
		modelP.showPossibleSelectedProperty().addListener((obs, oV, nV) -> {
			if (nV && !this.model.showSelectedProperty().get()) {
				this.stroke.getStrokeDashArray().addAll(UserBaseConfiguration.INSTANCE.selectionDashSizeProperty().get(),
						UserBaseConfiguration.INSTANCE.selectionDashSizeProperty().get());
				SelectableOption.this.stroke.toFront();
			} else {
				this.stroke.getStrokeDashArray().clear();
				SelectableOption.this.stroke.toFront();
			}
		});
		this.getChildren().add(this.stroke);
		this.setPickOnBounds(false);
	}
}
