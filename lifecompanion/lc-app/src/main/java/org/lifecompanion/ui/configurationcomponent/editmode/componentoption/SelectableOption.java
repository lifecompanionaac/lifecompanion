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

package org.lifecompanion.ui.configurationcomponent.editmode.componentoption;

import org.lifecompanion.model.api.configurationcomponent.GridChildComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.SelectableComponentI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.model.api.style.AbstractShapeCompStyleI;
import org.lifecompanion.model.api.style.GridStyleUserI;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import org.lifecompanion.model.impl.style.ShapeStyleBinder;
import org.lifecompanion.util.binding.BindingUtils;

/**
 * Option that allow a component to be selected
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SelectableOption<T extends SelectableComponentI & GridChildComponentI> extends BaseOptionRegion<T> {

    private Rectangle stroke = new Rectangle();

    public SelectableOption(final T modelP) {
        super(modelP);
        //Style
        this.stroke.setFill(null);
        this.stroke.strokeWidthProperty().bind(UserConfigurationController.INSTANCE.selectionStrokeSizeProperty());
        this.stroke.setLayoutX(-LCGraphicStyle.SELECTED_STROKE_GAP);
        this.stroke.setLayoutY(-LCGraphicStyle.SELECTED_STROKE_GAP);
        this.stroke.setStrokeLineCap(StrokeLineCap.ROUND);
        //Bind size and visible
        this.stroke.widthProperty().bind(this.widthProperty().add(LCGraphicStyle.SELECTED_STROKE_GAP * 2));
        this.stroke.heightProperty().bind(this.heightProperty().add(LCGraphicStyle.SELECTED_STROKE_GAP * 2));
        this.stroke.visibleProperty().bind(modelP.selectedProperty().or(modelP.showPossibleSelectedProperty()));
        this.stroke.setStroke(modelP instanceof GridPartKeyComponentI ? LCGraphicStyle.SECOND_DARK : modelP instanceof GridPartComponentI ? LCGraphicStyle.THIRD_DARK : LCGraphicStyle.MAIN_DARK);

        //Bind style (shape)
        AbstractShapeCompStyleI<?> shapeStyle = null;
        if (this.model instanceof GridPartKeyComponentI) {
            shapeStyle = ((GridPartKeyComponentI) this.model).getKeyStyle();
        } else {
            shapeStyle = ((GridStyleUserI) this.model).getGridShapeStyle();
        }
        ShapeStyleBinder.bindArcSizeComp(this.stroke, shapeStyle, this.stroke.widthProperty(), this.stroke.heightProperty(),
                UserConfigurationController.INSTANCE.selectionStrokeSizeProperty(), 1);
        modelP.selectedProperty().addListener(inv -> updateStrokeDash());
        modelP.showPossibleSelectedProperty().addListener(inv -> updateStrokeDash());
        this.getChildren().add(this.stroke);
        this.setPickOnBounds(false);
    }

    private void updateStrokeDash() {
        if (!this.model.selectedProperty().get() && this.model.showPossibleSelectedProperty().get()) {
            this.stroke.getStrokeDashArray().setAll(UserConfigurationController.INSTANCE.selectionDashSizeProperty().get(), UserConfigurationController.INSTANCE.selectionDashSizeProperty().get());
        } else {
            this.stroke.getStrokeDashArray().clear();
        }
        if (model.selectedProperty().get() || model.showPossibleSelectedProperty().get()) {
            SelectableOption.this.stroke.toFront();
        }
    }
}
