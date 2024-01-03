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
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.lifecompanion.model.api.style.ShapeStyle;
import org.lifecompanion.ui.common.pane.specific.cell.ShapeStyleListCell;
import org.lifecompanion.ui.common.pane.specific.cell.SimpleTextListCell;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.style.KeyCompStyleI;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.ui.common.pane.specific.cell.TextPositionListCell;
import org.lifecompanion.util.binding.EditActionUtils;
import org.lifecompanion.util.javafx.FXControlUtils;

public class KeyStyleEditView extends AbstractShapeStyleEditView<KeyCompStyleI> {
    private ToggleSwitch toggleEnableAutoFontSizing;
    private Node modificationIndicatorToggleAutoFont;
    private ChangeListener<Boolean> changeListenerAutoFontSize;

    private ComboBox<TextPosition> comboBoxTextPosition;
    private Node modificationIndicatorTextPosition;
    private ChangeListener<TextPosition> changeListenerTextPosition;

    private ComboBox<ShapeStyle> comboBoxShapeStyle;
    private Node modificationIndicatorShapeStyle;
    private ChangeListener<ShapeStyle> changeListenerShapeStyle;

    public KeyStyleEditView(boolean bindOnModel) {
        super(bindOnModel);
    }

    @Override
    public void initUI() {
        super.initUI();

        comboBoxShapeStyle = new ComboBox<>(FXCollections.observableArrayList(ShapeStyle.values()));
        this.comboBoxShapeStyle.setButtonCell(new SimpleTextListCell<>(ShapeStyle::getName));
        this.comboBoxShapeStyle.setCellFactory(lv -> new ShapeStyleListCell());
        this.comboBoxShapeStyle.setMaxWidth(Double.MAX_VALUE);

        this.fieldGrid.add(new Label(Translation.getText("key.style.shape.style")), 0, 4);
        this.fieldGrid.add(comboBoxShapeStyle, 1, 4);
        this.fieldGrid.add(modificationIndicatorShapeStyle = this.createModifiedIndicator(KeyCompStyleI::shapeStyleProperty, comboBoxShapeStyle), 2, 4);

        comboBoxTextPosition = new ComboBox<>(FXCollections.observableArrayList(TextPosition.values()));
        this.comboBoxTextPosition.setButtonCell(new TextPositionListCell(false));
        this.comboBoxTextPosition.setCellFactory(lv -> new TextPositionListCell(true));
        this.comboBoxTextPosition.setMaxWidth(Double.MAX_VALUE);

        this.fieldGrid.add(new Label(Translation.getText("pane.text.text.location")), 0, 5);
        this.fieldGrid.add(comboBoxTextPosition, 1, 5);
        this.fieldGrid.add(modificationIndicatorTextPosition = this.createModifiedIndicator(KeyCompStyleI::textPositionProperty, comboBoxTextPosition), 2, 4);

        this.toggleEnableAutoFontSizing = FXControlUtils.createToggleSwitch("key.style.enable.auto.font.size", "key.style.auto.font.size.explain");
        this.fieldGrid.add(this.toggleEnableAutoFontSizing, 0, 6, 2, 1);
        this.fieldGrid.add(modificationIndicatorToggleAutoFont = this.createModifiedIndicator(KeyCompStyleI::autoFontSizeProperty, toggleEnableAutoFontSizing), 2, 6);
    }

    @Override
    public void initListener() {
        super.initListener();
        if (bindOnModel) {
            this.changeListenerAutoFontSize = EditActionUtils.createSimpleBinding(this.toggleEnableAutoFontSizing.selectedProperty(), this.model,
                    m -> m.autoFontSizeProperty().value().getValue(), (m, v) -> this.createChangePropAction(m.autoFontSizeProperty(), v));
            this.changeListenerTextPosition = EditActionUtils.createSelectionModelBinding(this.comboBoxTextPosition.getSelectionModel(),
                    this.model, model -> model.textPositionProperty().value().getValue(),
                    (model, val) -> this.createChangePropAction(model.textPositionProperty(), val));
            this.changeListenerShapeStyle = EditActionUtils.createSelectionModelBinding(this.comboBoxShapeStyle.getSelectionModel(),
                    this.model, model -> model.shapeStyleProperty().value().getValue(),
                    (model, val) -> this.createChangePropAction(model.shapeStyleProperty(), val));
        }
    }


    @Override
    public void bind(final KeyCompStyleI model) {
        super.bind(model);
        if (bindOnModel) {
            this.toggleEnableAutoFontSizing.setSelected(model.autoFontSizeProperty().value().getValue());
            model.autoFontSizeProperty().value().addListener(this.changeListenerAutoFontSize);
            this.comboBoxTextPosition.getSelectionModel().select(model.textPositionProperty().value().getValue());
            model.textPositionProperty().value().addListener(this.changeListenerTextPosition);
            this.comboBoxShapeStyle.getSelectionModel().select(model.shapeStyleProperty().value().getValue());
            model.shapeStyleProperty().value().addListener(this.changeListenerShapeStyle);
        }
    }

    @Override
    public void unbind(final KeyCompStyleI model) {
        super.unbind(model);
        if (bindOnModel) {
            model.autoFontSizeProperty().value().removeListener(this.changeListenerAutoFontSize);
            model.textPositionProperty().value().removeListener(this.changeListenerTextPosition);
            model.shapeStyleProperty().value().removeListener(this.changeListenerShapeStyle);
        }
    }

    public ToggleSwitch getToggleEnableAutoFontSizing() {
        return toggleEnableAutoFontSizing;
    }

    public Node getModificationIndicatorToggleAutoFont() {
        return modificationIndicatorToggleAutoFont;
    }

    public ComboBox<TextPosition> getComboBoxTextPosition() {
        return comboBoxTextPosition;
    }

    public Node getModificationIndicatorTextPosition() {
        return modificationIndicatorTextPosition;
    }

    public ComboBox<ShapeStyle> getComboBoxShapeStyle() {
        return comboBoxShapeStyle;
    }

    public Node getModificationIndicatorShapeStyle() {
        return modificationIndicatorShapeStyle;
    }
}
