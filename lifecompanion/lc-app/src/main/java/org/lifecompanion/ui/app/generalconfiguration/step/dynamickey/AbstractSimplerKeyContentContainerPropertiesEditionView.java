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

package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.SimplerKeyContentContainerI;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.ui.common.pane.specific.cell.TextPositionListCell;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.common.control.generic.colorpicker.LCColorPicker;
import org.lifecompanion.ui.common.control.specific.imagedictionary.ImageUseComponentSelectorControl;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.function.Consumer;

public abstract class AbstractSimplerKeyContentContainerPropertiesEditionView<T extends SimplerKeyContentContainerI> extends ScrollPane implements LCViewInitHelper {
    protected final ObjectProperty<T> selectedNode;

    private ComboBox<TextPosition> comboBoxTextPosition;
    protected TextField fieldText;
    private Label labelText;
    private LCColorPicker colorPickerBackgroundColor, colorPickerStrokeColor, colorPickerTextColor;
    private ImageUseComponentSelectorControl imageUseComponentSelectorControl;
    private GridPane gridPaneConfiguration;

    /**
     * To know if we are currently binding the value : useful to make sure listener handle events correctly
     */
    protected boolean bindingCurrentSelection;

    private Consumer<T> removeRequestListener;
    private Runnable addRequestListener;

    protected final EventHandler<ActionEvent> actionEventTextFieldOnAction = event -> {
        if (this.addRequestListener != null) addRequestListener.run();
    };

    public AbstractSimplerKeyContentContainerPropertiesEditionView() {
        this.selectedNode = new SimpleObjectProperty<>();
        initAll();
    }

    // PROPS
    //========================================================================
    public ObjectProperty<T> selectedNodeProperty() {
        return selectedNode;
    }

    public void setAddRequestListener(Runnable addRequestListener) {
        this.addRequestListener = addRequestListener;
    }

    public void setRemoveRequestListener(Consumer<T> removeRequestListener) {
        this.removeRequestListener = removeRequestListener;
    }
    //========================================================================


    @Override
    public void initUI() {
        int rowIndex = 0;
        final int columnCount = 3;
        gridPaneConfiguration = new GridPane();
        gridPaneConfiguration.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneConfiguration.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        gridPaneConfiguration.setPadding(new Insets(5.0));

        // General
        Label labelGeneralPart = FXControlUtils.createTitleLabel(Translation.getText("general.configuration.view.key.list.part.title.general"));
        labelText = new Label(Translation.getText("general.configuration.view.key.list.field.text"));
        labelText.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelText, Priority.ALWAYS);
        fieldText = new TextField();
        fieldText.minWidthProperty().bind(gridPaneConfiguration.widthProperty().divide(2.0));

        gridPaneConfiguration.add(labelGeneralPart, 0, rowIndex++, columnCount, 1);
        gridPaneConfiguration.add(labelText, 0, rowIndex);
        gridPaneConfiguration.add(fieldText, 1, rowIndex++, 2, 1);

        rowIndex = addFieldsAfterTextInGeneralPart(gridPaneConfiguration, rowIndex, columnCount);

        // Visual
        Label labelVisualPart = FXControlUtils.createTitleLabel(Translation.getText("general.configuration.view.key.list.part.title.visual"));
        imageUseComponentSelectorControl = new ImageUseComponentSelectorControl();

        comboBoxTextPosition = new ComboBox<>(FXCollections.observableArrayList(TextPosition.values()));
        this.comboBoxTextPosition.setButtonCell(new TextPositionListCell(false));
        this.comboBoxTextPosition.setCellFactory(lv -> new TextPositionListCell(true));
        comboBoxTextPosition.setMaxWidth(Double.MAX_VALUE);


        colorPickerBackgroundColor = new LCColorPicker();
        colorPickerBackgroundColor.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(colorPickerBackgroundColor, Priority.ALWAYS);
        colorPickerStrokeColor = new LCColorPicker(LCColorPicker.ColorPickerMode.DARK);
        colorPickerStrokeColor.setMaxWidth(Double.MAX_VALUE);
        colorPickerTextColor = new LCColorPicker();
        colorPickerStrokeColor.setMaxWidth(Double.MAX_VALUE);

        gridPaneConfiguration.add(labelVisualPart, 0, rowIndex++, columnCount, 1);

        GridPane gridStyle = new GridPane();
        int rowStyle = 0;
        gridStyle.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        gridStyle.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);

        gridStyle.add(new Label(Translation.getText("general.configuration.view.key.list.field.text.position")), 0, rowStyle++, 2, 1);
        gridStyle.add(createNullValueToggleSwitch(comboBoxTextPosition,
                comboBoxTextPosition.getSelectionModel().selectedItemProperty(),
                () -> comboBoxTextPosition.getSelectionModel().clearSelection()), 0, rowStyle);
        gridStyle.add(comboBoxTextPosition, 1, rowStyle++);

        gridStyle.add(new Label(Translation.getText("general.configuration.view.key.list.field.background.color")), 0, rowStyle++, 2, 1);
        gridStyle.add(createDeleteColorSwitch(colorPickerBackgroundColor), 0, rowStyle);
        gridStyle.add(colorPickerBackgroundColor, 1, rowStyle++);
        gridStyle.add(new Label(Translation.getText("general.configuration.view.key.list.field.stroke.color")), 0, rowStyle++, 2, 1);
        gridStyle.add(createDeleteColorSwitch(colorPickerStrokeColor), 0, rowStyle);
        gridStyle.add(colorPickerStrokeColor, 1, rowStyle++);
        gridStyle.add(new Label(Translation.getText("general.configuration.view.key.list.field.text.color")), 0, rowStyle++, 2, 1);
        gridStyle.add(createDeleteColorSwitch(colorPickerTextColor), 0, rowStyle);
        gridStyle.add(colorPickerTextColor, 1, rowStyle++);

        VBox boxImage = new VBox(GeneralConfigurationStepViewI.GRID_V_GAP, imageUseComponentSelectorControl);

        HBox.setHgrow(gridStyle, Priority.SOMETIMES);
        HBox.setHgrow(boxImage, Priority.SOMETIMES);
        HBox boxImagesAndColors = new HBox(GeneralConfigurationStepViewI.GRID_H_GAP, boxImage, new Separator(Orientation.VERTICAL), gridStyle);
        gridStyle.maxWidthProperty().bind(boxImagesAndColors.widthProperty().divide(2.1));
        boxImage.maxWidthProperty().bind(boxImagesAndColors.widthProperty().divide(2.1));
        boxImagesAndColors.setAlignment(Pos.CENTER);

        gridPaneConfiguration.add(boxImagesAndColors, 0, rowIndex, columnCount, 4);

        // Total
        this.setFitToWidth(true);
        this.setContent(gridPaneConfiguration);
    }

    protected abstract int addFieldsAfterTextInGeneralPart(GridPane gridPaneConfiguration, int rowIndex, int columnCount);


    @Override
    public void initListener() {
        fieldText.setOnAction(actionEventTextFieldOnAction);
    }

    @Override
    public void initBinding() {
        gridPaneConfiguration.disableProperty().bind(this.selectedNode.isNull());
        this.imageUseComponentSelectorControl.modelProperty().bind(selectedNode);
        this.selectedNode.addListener((obs, ov, nv) -> {
            try {
                bindingCurrentSelection = true;
                unbindBidirectionalContent(ov, nv);
                if (ov != null) {
                    fieldText.textProperty().unbindBidirectional(ov.textProperty());
                    fieldText.setText(null);
                    comboBoxTextPosition.valueProperty().unbindBidirectional(ov.textPositionProperty());
                    comboBoxTextPosition.setValue(null);
                    colorPickerBackgroundColor.valueProperty().unbindBidirectional(ov.backgroundColorProperty());
                    colorPickerBackgroundColor.setValue(null);
                    colorPickerStrokeColor.valueProperty().unbindBidirectional(ov.strokeColorProperty());
                    colorPickerStrokeColor.setValue(null);
                    colorPickerTextColor.valueProperty().unbindBidirectional(ov.textColorProperty());
                    colorPickerTextColor.setValue(null);
                }
                bindBidirectionalContent(ov, nv);
                if (nv != null) {
                    fieldText.textProperty().bindBidirectional(nv.textProperty());
                    comboBoxTextPosition.valueProperty().bindBidirectional(nv.textPositionProperty());
                    colorPickerBackgroundColor.valueProperty().bindBidirectional(nv.backgroundColorProperty());
                    colorPickerStrokeColor.valueProperty().bindBidirectional(nv.strokeColorProperty());
                    colorPickerTextColor.valueProperty().bindBidirectional(nv.textColorProperty());
                    fieldText.requestFocus();
                    this.setVvalue(0.0);
                }
            } finally {
                bindingCurrentSelection = false;
            }
        });
    }


    // HELPER
    //========================================================================
    private Node createDeleteColorSwitch(LCColorPicker colorPickerToBind) {
        return createNullValueToggleSwitch(colorPickerToBind, colorPickerToBind.valueProperty(), () -> colorPickerToBind.setValue(null));
    }

    private Node createNullValueToggleSwitch(Node field, ReadOnlyObjectProperty<?> valueProp, Runnable nullSetterValue) {
        ToggleSwitch toggleSwitch = new ToggleSwitch();
        field.disableProperty().bind(toggleSwitch.selectedProperty().not());
        toggleSwitch.selectedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                nullSetterValue.run();
            }
        });
        valueProp.addListener((obs, ov, nv) -> {
            if (nv == null) {
                toggleSwitch.setSelected(false);
            } else {
                toggleSwitch.setSelected(true);
            }
        });
        return toggleSwitch;
    }

    protected void bindTextFieldsValues(TextField sourceField, TextField destField) {
        sourceField.textProperty().addListener((obs, ov, nv) -> {
            if (!bindingCurrentSelection && StringUtils.isEquals(ov, destField.getText())) {
                destField.setText(nv);
            }
        });
    }
    //========================================================================

    // SUB IMPLEMENTATION
    //========================================================================
    protected abstract void unbindBidirectionalContent(T ov, T nv);

    protected abstract void bindBidirectionalContent(T ov, T nv);
    //========================================================================
}
