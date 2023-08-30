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
package org.lifecompanion.ui.common.pane.specific.styleedit;

import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.lifecompanion.model.api.style.AbstractShapeCompStyleI;
import org.lifecompanion.util.binding.EditActionUtils;
import org.lifecompanion.ui.common.control.generic.colorpicker.LCColorPicker;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

public abstract class AbstractShapeStyleEditView<T extends AbstractShapeCompStyleI<T>> extends AbstractStyleEditView<T> implements LCViewInitHelper {

    private LCColorPicker fieldBackgroundColor, fieldStrokeColor;
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
        this.fieldStrokeColor = new LCColorPicker(LCColorPicker.ColorPickerMode.DARK);
        this.fieldBackgroundColor = new LCColorPicker();
        this.spinnerShapeRadius = FXControlUtils.createIntSpinner(0, 300, 2, 2, 95.0);
        this.spinnerStrokeSize = FXControlUtils.createIntSpinner(0, 180, 2, 1, 95.0);

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
            this.changeListenerBackgroundColor = EditActionUtils.createSimpleBinding(this.fieldBackgroundColor.valueProperty(), this.model,
                    c -> c.backgroundColorProperty().value().getValue(), (model, nv) -> this.createChangePropAction(model.backgroundColorProperty(), nv));
            this.changeListenerStrokeColor = EditActionUtils.createSimpleBinding(this.fieldStrokeColor.valueProperty(), this.model,
                    c -> c.strokeColorProperty().value().getValue(), (model, nv) -> this.createChangePropAction(model.strokeColorProperty(), nv));
            this.changeListenerShapeRadius = EditActionUtils.createIntegerSpinnerBinding(this.spinnerShapeRadius, this.model,
                    g -> g.shapeRadiusProperty().value(), (m, nv) -> this.createChangePropAction(m.shapeRadiusProperty(), nv));
            this.changeListenerStrokeSize = EditActionUtils.createIntegerSpinnerBinding(this.spinnerStrokeSize, this.model,
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

    public LCColorPicker getFieldBackgroundColor() {
        return fieldBackgroundColor;
    }

    public Node getModifiedIndicatorFieldBackgroundColor() {
        return modifiedIndicatorFieldBackgroundColor;
    }

    public LCColorPicker getFieldStrokeColor() {
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
