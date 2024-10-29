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

package org.lifecompanion.plugin.caaai.ui.useaction.common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.caaai.model.AiContextValue;
import org.lifecompanion.plugin.caaai.model.useaction.common.AppendContextForNextSuggestionsAction;
import org.lifecompanion.util.binding.BindingUtils;

public abstract class AppendContextForNextSuggestionsActionConfigView<V extends AiContextValue, A extends AppendContextForNextSuggestionsAction<?, V>> extends HBox implements UseActionConfigurationViewI<A> {

    abstract protected String getComboBoxLabel();

    abstract protected V[] getComboBoxValues();

    @Override
    public Region getConfigurationView() {
        return this;
    }

    private ComboBox<V> contextValueComboBox;

    @Override
    public void initUI() {
        this.contextValueComboBox = new ComboBox<>();
        this.contextValueComboBox.setCellFactory(lv -> new ContextValueListCell());
        this.contextValueComboBox.setButtonCell(new ContextValueListCell());
        contextValueComboBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.contextValueComboBox, Priority.ALWAYS);
        Label labelField = new Label(this.getComboBoxLabel());
        labelField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelField, Priority.ALWAYS);
        this.setSpacing(10.0);
        this.getChildren().addAll(labelField, this.contextValueComboBox);
    }

    @Override
    public void editStarts(final A element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.contextValueComboBox.setItems(FXCollections.observableArrayList(this.getComboBoxValues()));
        V toSelect = element.contextValueProperty().get();
        if (toSelect != null) {
            this.contextValueComboBox.valueProperty().set(toSelect);
        }
    }

    @Override
    public void editEnds(final A element) {
        V selected = this.contextValueComboBox.getSelectionModel().getSelectedItem();
        element.contextValueProperty().set(selected);
        this.contextValueComboBox.setItems(null);
    }

    @Override
    public void editCancelled(A element) {
        this.contextValueComboBox.setItems(null);
    }


    private class ContextValueListCell extends ListCell<V> {
        @Override
        protected void updateItem(final V item, final boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                BindingUtils.unbindAndSetNull(textProperty());
            } else {
                this.setText(item.getTextValue());
            }
        }
    }
}
