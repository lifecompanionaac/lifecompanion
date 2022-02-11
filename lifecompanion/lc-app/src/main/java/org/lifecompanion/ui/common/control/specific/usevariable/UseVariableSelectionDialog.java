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

package org.lifecompanion.ui.common.control.specific.usevariable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import org.controlsfx.control.textfield.TextFields;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.ui.common.pane.specific.cell.UseVariableDefinitionListCell;

import java.util.function.Predicate;

public class UseVariableSelectionDialog extends Dialog<UseVariableDefinitionI> implements LCViewInitHelper {
    private static final double STAGE_WIDTH = 350.0, STAGE_HEIGHT = 400.0;
    private static UseVariableSelectionDialog instance;

    private BorderPane boxSelectVariable;
    private ListView<UseVariableDefinitionI> useVariableList;
    private TextField fieldSearchVariable;
    private ObservableList<UseVariableDefinitionI> items;
    private FilteredList<UseVariableDefinitionI> filteredList;
    private Subscription listBinding;

    private UseVariableSelectionDialog() {
        this.items = FXCollections.observableArrayList();
        this.filteredList = new FilteredList<>(this.items);
        initAll();
    }

    public static UseVariableSelectionDialog getInstance() {
        if (instance == null) {
            instance = new UseVariableSelectionDialog();
        }
        return instance;
    }

    @Override
    public void initUI() {
        // Dialog stage
        this.setTitle(LCConstant.NAME);
        this.initStyle(StageStyle.UTILITY);
        this.setWidth(STAGE_WIDTH);
        this.setHeight(STAGE_HEIGHT);

        //Select variable
        this.boxSelectVariable = new BorderPane();
        this.fieldSearchVariable = TextFields.createClearableTextField();
        this.fieldSearchVariable.setPromptText(Translation.getText("use.variable.empty.search"));

        this.useVariableList = new ListView<>(this.filteredList);
        this.useVariableList.setCellFactory((lv) -> new UseVariableDefinitionListCell());
        this.useVariableList.setPrefSize(UseVariableTextArea.POP_WIDTH, UseVariableTextArea.POP_HEIGHT);
        BorderPane.setMargin(useVariableList, new Insets(5.0, 0, 0, 0));

        Label labelExplainVariable = new Label(Translation.getText("use.variable.select.dialog.header.text"));
        labelExplainVariable.getStyleClass().add("explain-text");
        labelExplainVariable.setWrapText(true);
        labelExplainVariable.setMaxWidth(STAGE_WIDTH - 20.0);

        this.boxSelectVariable.setTop(new VBox(10.0, labelExplainVariable, this.fieldSearchVariable));
        this.boxSelectVariable.setCenter(this.useVariableList);
        this.boxSelectVariable.setPadding(new Insets(5.0));

        // Dialog content
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        this.getDialogPane().setContent(boxSelectVariable);
        this.getDialogPane().getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.setResultConverter(dialogButton -> null);
    }

    @Override
    public void initListener() {
        this.useVariableList.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            setResult(nv);
            hide();
        });
        this.fieldSearchVariable.textProperty().addListener((obs, ov, nv) -> {
            Predicate<? super UseVariableDefinitionI> predicate = (p) -> UseVariableController.INSTANCE.searchForVariable(nv).test(p);
            this.filteredList.setPredicate(predicate);
        });
        this.setOnShown(e -> fieldSearchVariable.requestFocus());
    }

    @Override
    public void initBinding() {
        this.listBinding = EasyBind.listBind(this.items, UseVariableController.INSTANCE.getPossibleVariables());
    }

    public void setAvailableUseVariable(final ObservableList<UseVariableDefinitionI> variables) {
        this.listBinding.unsubscribe();
        this.useVariableList.getSelectionModel().clearSelection();
        this.items.clear();
        this.items.addAll(variables);
    }
}
