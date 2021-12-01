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

package org.lifecompanion.config.view.keyoption.impl;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import org.lifecompanion.base.data.component.keyoption.simplercomp.UserActionSequenceDisplayFilter;
import org.lifecompanion.base.data.component.keyoption.simplercomp.UserActionSequenceItemKeyOption;
import org.lifecompanion.config.data.action.impl.KeyOptionActions;
import org.lifecompanion.config.data.common.LCConfigBindingUtils;
import org.lifecompanion.config.view.keyoption.BaseKeyOptionConfigView;
import org.lifecompanion.framework.commons.translation.Translation;

public class UserActionSequenceItemKeyOptionConfigView extends BaseKeyOptionConfigView<UserActionSequenceItemKeyOption> {

    private ComboBox<UserActionSequenceDisplayFilter> comboBoxDisplayFilter;
    private ChangeListener<UserActionSequenceDisplayFilter> changeListenerDisplayFilter;

    @Override
    public Class<UserActionSequenceItemKeyOption> getConfiguredKeyOptionType() {
        return UserActionSequenceItemKeyOption.class;
    }

    @Override
    public void initUI() {
        super.initUI();
        this.comboBoxDisplayFilter = new ComboBox<>(FXCollections.observableArrayList(UserActionSequenceDisplayFilter.values()));
        comboBoxDisplayFilter.setButtonCell(new UserActionSequenceDisplayFilterListCell());
        comboBoxDisplayFilter.setCellFactory(lv -> new UserActionSequenceDisplayFilterListCell());
        this.getChildren().addAll(new Label(Translation.getText("user.action.sequence.item.display.filter.field")), comboBoxDisplayFilter);
    }

    @Override
    public void initListener() {
        super.initListener();
        this.changeListenerDisplayFilter = LCConfigBindingUtils.createSelectionModelBinding(this.comboBoxDisplayFilter.getSelectionModel(), //
                this.model, model -> model.displayFilterProperty().get(), //
                KeyOptionActions.ChangeUserActionSequenceDisplayFilter::new);
    }

    @Override
    public void bind(final UserActionSequenceItemKeyOption model) {
        this.comboBoxDisplayFilter.getSelectionModel().select(model.displayFilterProperty().get());
        model.displayFilterProperty().addListener(changeListenerDisplayFilter);
    }

    @Override
    public void unbind(final UserActionSequenceItemKeyOption model) {
        model.displayFilterProperty().removeListener(changeListenerDisplayFilter);
    }

    private class UserActionSequenceDisplayFilterListCell extends ListCell<UserActionSequenceDisplayFilter> {
        @Override
        protected void updateItem(UserActionSequenceDisplayFilter item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) setText(Translation.getText(item.getNameId()));
            else setText(null);
        }
    }
}
