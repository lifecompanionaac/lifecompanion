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
package org.lifecompanion.config.view.pane.tabs.style2.view.shape;

import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.lifecompanion.api.style2.definition.AbstractShapeCompStyleI;
import org.lifecompanion.api.style2.definition.StyleI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.config.data.common.LCConfigBindingUtils;
import org.lifecompanion.config.view.pane.tabs.style2.view.AbstractStyleEditView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public abstract class AbstractShapeStyleEditView<T extends AbstractShapeCompStyleI<T>> extends AbstractStyleEditView<T> implements LCViewInitHelper {

    private ColorPicker fieldBackgroundColor, fieldStrokeColor;
    private Spinner<Integer> spinnerShapeRadius, spinnerStrokeSize;
    private Label labelStrokeSize, labelShapeRadius;

    private ChangeListener<Color> changeListenerBackgroundColor, changeListenerStrokeColor;
    private ChangeListener<Number> changeListenerShapeRadius, changeListenerStrokeSize;

    private Node modifiedIndicatorFieldBackgroundColor, modificationIndicatorFieldStrokeColor, modificationIndicatorSpinnerShapeRadius, modificationIndicatorSpinnerStrokeSize;

    public AbstractShapeStyleEditView(boolean bindOnModel) {
        super(bindOnModel);
    }

    @Override
    public void initUI() {
        super.initUI();
        //Create fields
        this.fieldStrokeColor = new ColorPicker();
        this.fieldBackgroundColor = new ColorPicker();
        this.spinnerShapeRadius = UIUtils.createIntSpinner(0, 180, 2, 2, 75.0);
        this.spinnerStrokeSize = UIUtils.createIntSpinner(0, 180, 2, 2, 75.0);

        this.fieldGrid.add(new Label(Translation.getText("shape.style.background.color")), 0, 0);
        this.fieldGrid.add(this.fieldBackgroundColor, 1, 0);
        this.fieldGrid.add(modifiedIndicatorFieldBackgroundColor = this.createModifiedIndicator(AbstractShapeCompStyleI::backgroundColorProperty, fieldBackgroundColor), 2, 0);
        GridPane.setHalignment(this.fieldBackgroundColor, HPos.RIGHT);

        this.fieldGrid.add(new Label(Translation.getText("shape.style.stroke.color")), 0, 1);
        this.fieldGrid.add(this.fieldStrokeColor, 1, 1);
        this.fieldGrid.add(modificationIndicatorFieldStrokeColor = this.createModifiedIndicator(AbstractShapeCompStyleI::strokeColorProperty, fieldStrokeColor), 2, 1);
        GridPane.setHalignment(this.fieldStrokeColor, HPos.RIGHT);

        this.labelStrokeSize = new Label(Translation.getText("shape.style.stroke.size"));
        this.fieldGrid.add(this.labelStrokeSize, 0, 2);
        this.fieldGrid.add(this.spinnerStrokeSize, 1, 2);
        this.fieldGrid.add(modificationIndicatorSpinnerStrokeSize = this.createModifiedIndicator(AbstractShapeCompStyleI::strokeSizeProperty, spinnerStrokeSize), 2, 2);
        GridPane.setHalignment(this.spinnerStrokeSize, HPos.RIGHT);

        this.labelShapeRadius = new Label(Translation.getText("shape.style.shape.radius"));
        this.fieldGrid.add(this.labelShapeRadius, 0, 3);
        this.fieldGrid.add(this.spinnerShapeRadius, 1, 3);
        this.fieldGrid.add(modificationIndicatorSpinnerShapeRadius = this.createModifiedIndicator(AbstractShapeCompStyleI::shapeRadiusProperty, spinnerShapeRadius), 2, 3);
        GridPane.setHalignment(this.spinnerShapeRadius, HPos.RIGHT);

        GridPane.setHgrow(this.labelShapeRadius, Priority.ALWAYS);
    }

    @Override
    public void initListener() {
        super.initListener();
        if (bindOnModel) {
            this.changeListenerBackgroundColor = LCConfigBindingUtils.createSimpleBinding(this.fieldBackgroundColor.valueProperty(), this.model,
                    c -> c.backgroundColorProperty().value().getValue(), (model, nv) -> this.createChangePropAction(model.backgroundColorProperty(), nv));
            this.changeListenerStrokeColor = LCConfigBindingUtils.createSimpleBinding(this.fieldStrokeColor.valueProperty(), this.model,
                    c -> c.strokeColorProperty().value().getValue(), (model, nv) -> this.createChangePropAction(model.strokeColorProperty(), nv));
            this.changeListenerShapeRadius = LCConfigBindingUtils.createIntegerSpinnerBinding(this.spinnerShapeRadius, this.model,
                    g -> g.shapeRadiusProperty().value(), (m, nv) -> this.createChangePropAction(m.shapeRadiusProperty(), nv));
            this.changeListenerStrokeSize = LCConfigBindingUtils.createIntegerSpinnerBinding(this.spinnerStrokeSize, this.model,
                    g -> g.strokeSizeProperty().value(), (m, nv) -> this.createChangePropAction(m.strokeSizeProperty(), nv));
        }
    }

    @Override
    public void bind(final T model) {
        super.bind(model);
        if (bindOnModel) {
            this.fieldBackgroundColor.setValue(model.backgroundColorProperty().value().getValue());
            this.fieldStrokeColor.setValue(model.strokeColorProperty().value().getValue());
            this.spinnerShapeRadius.getValueFactory().setValue(model.shapeRadiusProperty().value().getValue().intValue());
            this.spinnerStrokeSize.getValueFactory().setValue(model.strokeSizeProperty().value().getValue().intValue());
            model.backgroundColorProperty().value().addListener(this.changeListenerBackgroundColor);
            model.strokeColorProperty().value().addListener(this.changeListenerStrokeColor);
            model.strokeSizeProperty().value().addListener(this.changeListenerStrokeSize);
            model.shapeRadiusProperty().value().addListener(this.changeListenerShapeRadius);
        }
    }

    @Override
    public void unbind(final T model) {
        super.unbind(model);
        if (bindOnModel) {
            model.backgroundColorProperty().value().removeListener(this.changeListenerBackgroundColor);
            model.strokeColorProperty().value().removeListener(this.changeListenerStrokeColor);
            model.strokeSizeProperty().value().removeListener(this.changeListenerStrokeSize);
            model.shapeRadiusProperty().value().removeListener(this.changeListenerShapeRadius);
        }
    }

    public ColorPicker getFieldBackgroundColor() {
        return fieldBackgroundColor;
    }

    public Node getModifiedIndicatorFieldBackgroundColor() {
        return modifiedIndicatorFieldBackgroundColor;
    }

    public ColorPicker getFieldStrokeColor() {
        return fieldStrokeColor;
    }

    public Spinner<Integer> getSpinnerShapeRadius() {
        return spinnerShapeRadius;
    }

    public Spinner<Integer> getSpinnerStrokeSize() {
        return spinnerStrokeSize;
    }

    public Node getModificationIndicatorFieldStrokeColor() {
        return modificationIndicatorFieldStrokeColor;
    }

    public Node getModificationIndicatorSpinnerShapeRadius() {
        return modificationIndicatorSpinnerShapeRadius;
    }

    public Node getModificationIndicatorSpinnerStrokeSize() {
        return modificationIndicatorSpinnerStrokeSize;
    }
}
