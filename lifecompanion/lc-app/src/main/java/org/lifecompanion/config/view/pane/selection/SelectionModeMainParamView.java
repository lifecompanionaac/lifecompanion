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

package org.lifecompanion.config.view.pane.selection;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.configurationcomponent.SelectionModeUserI;
import org.lifecompanion.model.api.selectionmode.FireActionEvent;
import org.lifecompanion.model.api.selectionmode.FireEventInput;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.impl.selectionmode.SelectionModeEnum;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStepViewI;
import org.lifecompanion.base.view.reusable.impl.BaseConfigurationViewBorderPane;
import org.lifecompanion.config.data.action.impl.SelectionModeActions;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.view.reusable.KeySelectorControl;
import org.lifecompanion.config.view.reusable.MouseButtonSelectorControl;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.Arrays;
import java.util.List;

public class SelectionModeMainParamView extends BaseConfigurationViewBorderPane<SelectionModeUserI> implements LCViewInitHelper {

    /**
     * Combobox to select the mode
     */
    private ComboBox<SelectionModeEnum> comboboxSelectionMode;

    private List<Node> keyboardNodes, mouseNodes;

    /**
     * Fire event input selection
     */
    private ComboBox<FireEventInput> comboBoxFireEventInput;
    private ComboBox<FireActionEvent> comboBoxFireActionEvent;

    /**
     * To select keyboard key
     */
    private KeySelectorControl inputEventKeySelector;

    private MouseButtonSelectorControl mouseButtonSelectorControl;

    /**
     * Spinner with for time to fire, and time to repeat
     */
    private Spinner<Double> spinnerTimeToFireAction, spinnerTimeBeforeRepeat;

    /**
     * Button advanced parameter
     */
    private Button buttonShowSelectionModeConfiguration;

    private final EventHandler<ActionEvent> selectionModeConfigurationHandler;
    private GridPane gridPaneConfiguration;

    public SelectionModeMainParamView(EventHandler<ActionEvent> selectionModeConfigurationHandler) {
        this.selectionModeConfigurationHandler = selectionModeConfigurationHandler;
        initAll();
    }

    @Override
    public void bind(SelectionModeUserI model) {
        this.comboboxSelectionMode.getSelectionModel()
                .select(SelectionModeEnum.getEnumFor(model.getSelectionModeParameter().selectionModeTypeProperty().get()));
        this.comboBoxFireActionEvent.getSelectionModel().select(model.getSelectionModeParameter().fireActivationEventProperty().get());
        this.comboBoxFireEventInput.getSelectionModel().select(model.getSelectionModeParameter().fireEventInputProperty().get());
        this.inputEventKeySelector.valueProperty().set(model.getSelectionModeParameter().keyboardFireKeyProperty().get());
        this.spinnerTimeToFireAction.getValueFactory().setValue(model.getSelectionModeParameter().timeToFireActionProperty().get() / 1000.0);
        this.spinnerTimeBeforeRepeat.getValueFactory().setValue(model.getSelectionModeParameter().timeBeforeRepeatProperty().get() / 1000.0);
        this.mouseButtonSelectorControl.valueProperty().set(model.getSelectionModeParameter().mouseButtonActivationProperty().get());
    }

    @Override
    public void unbind(SelectionModeUserI model) {
    }

    public void saveChanges() {
        if (model.get().getSelectionModeParameter().selectionModeTypeProperty().get() != comboboxSelectionMode.getValue().getModeClass()) {
            ConfigActionController.INSTANCE.executeAction(new SelectionModeActions.ChangeSelectionModeAction(
                    AppModeController.INSTANCE.getEditModeContext().configurationProperty().get(), model.get(), comboboxSelectionMode.getValue().getModeClass()));
        }
        model.get().getSelectionModeParameter().fireActivationEventProperty().set(comboBoxFireActionEvent.getValue());
        model.get().getSelectionModeParameter().fireEventInputProperty().set(comboBoxFireEventInput.getValue());
        model.get().getSelectionModeParameter().keyboardFireKeyProperty().set(inputEventKeySelector.valueProperty().get());
        model.get().getSelectionModeParameter().timeToFireActionProperty().set((int) (spinnerTimeToFireAction.getValue() * 1000.0));
        model.get().getSelectionModeParameter().timeBeforeRepeatProperty().set((int) (spinnerTimeBeforeRepeat.getValue() * 1000.0));
        model.get().getSelectionModeParameter().mouseButtonActivationProperty().set(this.mouseButtonSelectorControl.getValue());
    }

    @Override
    public void initUI() {
        // Preload selection mode images - fix to correctly display combobox on first show
        for (SelectionModeEnum mode : SelectionModeEnum.values()) {
            IconManager.get(SelectionModeEnum.ICON_URL_SELECTION_MODE + mode.getLogoUrl());
        }

        // Selection mode
        this.comboboxSelectionMode = new ComboBox<>(FXCollections.observableArrayList(SelectionModeEnum.values()));
        this.comboboxSelectionMode.setCellFactory((lv) -> new SelectionModeDetailListCell());
        this.comboboxSelectionMode.setButtonCell(new SelectionModeSimpleListCell());
        this.comboboxSelectionMode.setVisibleRowCount(4);
        UIUtils.createAndAttachTooltip(comboboxSelectionMode, "tooltip.explain.selection.mode.param.mode.selection");

        // Input event
        this.comboBoxFireEventInput = new ComboBox<>(FXCollections.observableArrayList(FireEventInput.values()));
        this.comboBoxFireEventInput.setCellFactory((lv) -> new FireEventInputListCell());
        this.comboBoxFireEventInput.setButtonCell(new FireEventInputListCell());
        this.comboBoxFireEventInput.setMaxWidth(Double.MAX_VALUE);
        Label labelInputFireEvent = new Label(Translation.getText("selection.mode.inputevent.label"));
        labelInputFireEvent.getStyleClass().add("menu-part-title");
        labelInputFireEvent.setMaxWidth(Double.MAX_VALUE);
        UIUtils.createAndAttachTooltip(comboBoxFireEventInput, "tooltip.explain.selection.event.input");

        // Keyboard input parameters
        this.inputEventKeySelector = new KeySelectorControl(null);
        Label labelKeyboardKeyFilter = new Label(Translation.getText("selection.mode.input.key.selector.label"));
        this.keyboardNodes = Arrays.asList(labelKeyboardKeyFilter, inputEventKeySelector);

        // Mouse input paramters
        mouseButtonSelectorControl = new MouseButtonSelectorControl();
        Label labelMouseEventFilter = new Label(Translation.getText("selection.mode.input.mouse.button.selector.label"));
        mouseNodes = Arrays.asList(labelMouseEventFilter, mouseButtonSelectorControl);

        // Fire event and filter
        comboBoxFireActionEvent = new ComboBox<>(FXCollections.observableArrayList(FireActionEvent.values()));
        comboBoxFireActionEvent.setCellFactory(lv -> new FireActionEventInputListCell());
        comboBoxFireActionEvent.setButtonCell(new FireActionEventInputListCell());
        comboBoxFireActionEvent.setMaxWidth(Double.MAX_VALUE);

        Label labelTimeToFireAction = new Label(Translation.getText("selection.mode.param.time.to.fire.action"));
        labelTimeToFireAction.setWrapText(true);
        Label labelTimeBeforeRepeat = new Label(Translation.getText("selection.mode.param.time.before.repeat"));
        labelTimeBeforeRepeat.setWrapText(true);
        this.spinnerTimeToFireAction = UIUtils.createDoubleSpinner(0.0, 120.0, 0.0, 0.1, 150);
        UIUtils.createAndAttachTooltip(spinnerTimeToFireAction, "tooltip.explain.selection.time.to.fire.action");
        this.spinnerTimeBeforeRepeat = UIUtils.createDoubleSpinner(0.0, 120.0, 0.0, 0.1, 150);
        UIUtils.createAndAttachTooltip(spinnerTimeBeforeRepeat, "tooltip.explain.selection.time.before.repeat");
        GridPane.setHalignment(spinnerTimeToFireAction, HPos.RIGHT);
        GridPane.setHalignment(spinnerTimeBeforeRepeat, HPos.RIGHT);

        this.buttonShowSelectionModeConfiguration = UIUtils.createRightTextButton(Translation.getText("selection.mode.button.param.show.advanced.parameter"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).size(20).color(LCGraphicStyle.MAIN_DARK),
                null);
        GridPane.setHalignment(buttonShowSelectionModeConfiguration, HPos.CENTER);

        // GridPane total
        gridPaneConfiguration = new GridPane();
        gridPaneConfiguration.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneConfiguration.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        int gridRowIndex = 0;

        // Selection mode selection
        Label labelSelectionMode = new Label(Translation.getText("general.configuration.selection.mode.combobox.label"));
        labelSelectionMode.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelSelectionMode, Priority.ALWAYS);
        labelSelectionMode.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        gridPaneConfiguration.add(UIUtils.createTitleLabel("general.configuration.view.step.selection.mode.title"), 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(labelSelectionMode, 0, gridRowIndex);
        gridPaneConfiguration.add(comboboxSelectionMode, 1, gridRowIndex++);
        gridPaneConfiguration.add(buttonShowSelectionModeConfiguration, 0, gridRowIndex++, 2, 1);

        // Input configuration
        gridPaneConfiguration.add(UIUtils.createTitleLabel("general.configuration.selection.mode.title.part.input.type"), 0, gridRowIndex++, 2, 1);
        Label labelInputType = new Label(Translation.getText("general.configuration.selection.input.type.label"));
        gridPaneConfiguration.add(labelInputType, 0, gridRowIndex);
        gridPaneConfiguration.add(comboBoxFireEventInput, 1, gridRowIndex++);

        // Mouse/keyboard : only one of them display at the same time, so put both on same row/columns
        gridPaneConfiguration.add(labelKeyboardKeyFilter, 0, gridRowIndex);
        gridPaneConfiguration.add(labelMouseEventFilter, 0, gridRowIndex);
        gridPaneConfiguration.add(inputEventKeySelector, 1, gridRowIndex);
        gridPaneConfiguration.add(mouseButtonSelectorControl, 1, gridRowIndex++);

        // Fire action
        gridPaneConfiguration.add(UIUtils.createTitleLabel("general.configuration.selection.mode.title.filter.actions"), 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(new Label(Translation.getText("general.configuration.selection.mode.title.filter.actions.field")), 0, gridRowIndex);
        gridPaneConfiguration.add(comboBoxFireActionEvent, 1, gridRowIndex++);

        // Fire configuration
        gridPaneConfiguration.add(labelTimeToFireAction, 0, gridRowIndex);
        gridPaneConfiguration.add(spinnerTimeToFireAction, 1, gridRowIndex++);
        gridPaneConfiguration.add(labelTimeBeforeRepeat, 0, gridRowIndex);
        gridPaneConfiguration.add(spinnerTimeBeforeRepeat, 1, gridRowIndex++);

        // Add to center
        gridPaneConfiguration.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        this.setCenter(gridPaneConfiguration);
    }

    @Override
    public void initListener() {
        buttonShowSelectionModeConfiguration.setOnAction(selectionModeConfigurationHandler);
    }

    public SelectionModeEnum getSelectedSelectionMode() {
        return this.comboboxSelectionMode.getValue();
    }

    public ObjectProperty<SelectionModeUserI> modelProperty() {
        return model;
    }

    @Override
    public void initBinding() {
        for (Node keyboardNode : keyboardNodes) keyboardNode.visibleProperty().bind(comboBoxFireEventInput.valueProperty().isEqualTo(FireEventInput.KEYBOARD));
        for (Node mouseNode : mouseNodes) mouseNode.visibleProperty().bind(comboBoxFireEventInput.valueProperty().isEqualTo(FireEventInput.MOUSE));
        this.spinnerTimeToFireAction.disableProperty().bind(Bindings.createBooleanBinding(
                () -> comboBoxFireActionEvent.getValue() == null || !comboBoxFireActionEvent.getValue().isEnableTimeToFireAction(), comboBoxFireActionEvent.valueProperty()));
    }
}
