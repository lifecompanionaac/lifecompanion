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

package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.useractionsequence;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequenceI;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.UserActionSequence;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.resource.LCGlyphFont;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.LangUtils;

public class UserActionSequencesEditionView extends BorderPane implements LCViewInitHelper {
    private Button buttonAddSequence, buttonRemoveSequence;
    private ComboBox<UserActionSequenceI> comboBoxSequences;
    private UserActionSequenceItemsEditionView userActionSequenceItemsEditionView;

    public UserActionSequencesEditionView() {
        initAll();
    }

    @Override
    public void initUI() {
        comboBoxSequences = new ComboBox<>();
        comboBoxSequences.setButtonCell(new UserActionSequenceListCell());
        comboBoxSequences.setCellFactory(lv -> new UserActionSequenceListCell());
        UIUtils.setFixedWidth(comboBoxSequences, 300.0);

        this.buttonAddSequence = UIUtils.createGraphicButton(LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        this.buttonRemoveSequence = UIUtils.createGraphicButton(LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(16).color(LCGraphicStyle.SECOND_DARK), null);

        final Label labelEdited = new Label(Translation.getText("field.sequence.selected.to.edit"));
        labelEdited.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelEdited, Priority.ALWAYS);

        final HBox boxTop = new HBox(5.0, labelEdited, comboBoxSequences, buttonAddSequence, buttonRemoveSequence);
        boxTop.setAlignment(Pos.CENTER);
        BorderPane.setMargin(boxTop,new Insets(0,0,10,0));
        this.setTop(boxTop);

        userActionSequenceItemsEditionView = new UserActionSequenceItemsEditionView();
        this.setCenter(userActionSequenceItemsEditionView);
    }

    @Override
    public void initListener() {
        this.buttonRemoveSequence.setOnAction(e -> {
            final UserActionSequenceI selectedItem = comboBoxSequences.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                comboBoxSequences.getItems().remove(selectedItem);
            }
        });
        this.buttonAddSequence.setOnAction(e -> {
            final UserActionSequence added = new UserActionSequence();
            added.nameProperty().set(Translation.getText("user.action.sequence.default.text.value"));
            comboBoxSequences.getItems().add(added);
            comboBoxSequences.getSelectionModel().select(added);
        });
    }

    @Override
    public void initBinding() {
        userActionSequenceItemsEditionView.editedSequenceProperty().bind(comboBoxSequences.valueProperty());
        userActionSequenceItemsEditionView.disableProperty().bind(comboBoxSequences.valueProperty().isNull());
        buttonRemoveSequence.disableProperty().bind(comboBoxSequences.valueProperty().isNull());
    }

    public void setUserActionSequences(ObservableList<UserActionSequenceI> items) {
        this.comboBoxSequences.setItems(items);
        if (LangUtils.isNotEmpty(items)) {
            comboBoxSequences.getSelectionModel().select(0);
        }
    }
}
