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

package org.lifecompanion.config.view.pane.general.view.simplercomp.useractionsequence;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.api.component.definition.simplercomp.UserActionSequenceI;
import org.lifecompanion.api.component.definition.simplercomp.UserActionSequenceItemI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.component.simplercomp.UserActionSequenceItem;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStepViewI;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.view.pane.general.view.simplercomp.CommonListViewActionContainer;
import org.lifecompanion.config.view.pane.general.view.simplercomp.DetailledSimplerKeyContentContainerListCell;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class UserActionSequenceItemsEditionView extends ScrollPane implements LCViewInitHelper {
    private final ObjectProperty<UserActionSequenceI> editedSequence;

    private ListView<UserActionSequenceItemI> listViewItems;
    private Label labelSequenceTitle;
    private TextField fieldSequenceName;
    private Button buttonAddItem;
    private CommonListViewActionContainer<UserActionSequenceItemI> commonListViewActionContainer;

    private UserActionSequenceItemPropertiesEditionView userActionSequenceItemPropertiesEditionView;

    public UserActionSequenceItemsEditionView() {
        editedSequence = new SimpleObjectProperty<>();
        initAll();
    }

    public ObjectProperty<UserActionSequenceI> editedSequenceProperty() {
        return editedSequence;
    }

    @Override
    public void initUI() {
        // Sequence props
        labelSequenceTitle = UIUtils.createTitleLabel(null);
        fieldSequenceName = new TextField();
        fieldSequenceName.setPromptText(Translation.getText("sequence.configuration.view.field.sequence.name"));
        VBox.setMargin(fieldSequenceName, new Insets(0, 10, 0, 10));

        buttonAddItem = UIUtils.createGraphicButton(LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(18).color(LCGraphicStyle.MAIN_DARK), null);

        // Sequence items
        listViewItems = new ListView<>();
        this.commonListViewActionContainer = new CommonListViewActionContainer<>(listViewItems);
        listViewItems.setOrientation(Orientation.HORIZONTAL);
        listViewItems.setMaxHeight(DetailledSimplerKeyContentContainerListCell.CELL_HEIGHT + 20.0);
        listViewItems.setPrefHeight(DetailledSimplerKeyContentContainerListCell.CELL_HEIGHT + 20.0);
        listViewItems.setCellFactory(lv -> new DetailledSimplerKeyContentContainerListCell<>(commonListViewActionContainer));
        HBox.setHgrow(listViewItems, Priority.ALWAYS);
        HBox boxItems = new HBox(5.0, listViewItems, buttonAddItem);
        boxItems.setAlignment(Pos.CENTER);
        GridPane.setMargin(boxItems, new Insets(5, 0, 0, 0));

        userActionSequenceItemPropertiesEditionView = new UserActionSequenceItemPropertiesEditionView();

        // Total
        this.setFitToWidth(true);
        this.setContent(new VBox(GeneralConfigurationStepViewI.GRID_V_GAP, labelSequenceTitle, fieldSequenceName, boxItems, userActionSequenceItemPropertiesEditionView));
    }

    @Override
    public void initListener() {
        buttonAddItem.setOnAction(event -> {
            this.commonListViewActionContainer.addAndScrollTo(new UserActionSequenceItem());
        });
        this.commonListViewActionContainer.setDuplicateFunction(item -> {
            UserActionSequenceItemI duplicated = (UserActionSequenceItemI) item.duplicate(true);
            duplicated.textProperty().set(Translation.getText("general.configuration.view.user.action.copy.label.key.text") + " " + duplicated.textProperty().get());
            return duplicated;
        });
    }

    @Override
    public void initBinding() {
        this.editedSequence.addListener((obs, ov, nv) -> {
            if (ov != null) {
                fieldSequenceName.textProperty().unbindBidirectional(ov.nameProperty());
                this.listViewItems.setItems(null);
            }
            this.labelSequenceTitle.textProperty().unbind();
            if (nv != null) {
                fieldSequenceName.textProperty().bindBidirectional(nv.nameProperty());
                this.labelSequenceTitle.textProperty().bind(TranslationFX.getTextBinding("label.sequence.name.title", nv.nameProperty()));
                this.listViewItems.setItems(nv.getItems());
            } else {
                this.labelSequenceTitle.setText(Translation.getText("label.sequence.name.title.none.selected"));
            }
        });
        userActionSequenceItemPropertiesEditionView.selectedNodeProperty().bind(this.listViewItems.getSelectionModel().selectedItemProperty());

    }
}
