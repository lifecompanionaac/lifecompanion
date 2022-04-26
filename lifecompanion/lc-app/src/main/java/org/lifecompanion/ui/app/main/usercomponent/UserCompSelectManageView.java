/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.ui.app.main.usercomponent;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.glyphfont.FontAwesome;
import org.fxmisc.easybind.EasyBind;
import org.lifecompanion.controller.editaction.UserCompActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.profile.UserCompController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.common.control.specific.imagedictionary.ImageSelectorDialog;
import org.lifecompanion.ui.common.pane.specific.cell.UserCompDetailListCell;
import org.lifecompanion.ui.common.pane.specific.cell.UserCompListCell;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class UserCompSelectManageView extends BorderPane implements LCViewInitHelper {

    private Consumer<UserCompDescriptionI> selectionCallback;

    private ListView<UserCompDescriptionI> userCompListView;
    private ObservableList<UserCompDescriptionI> items;
    private FilteredList<UserCompDescriptionI> filteredList;
    private TextField fieldSearchFilter;

    private Button buttonEdit, buttonRemove;

    public UserCompSelectManageView() {
        this.items = FXCollections.observableArrayList();
        this.filteredList = new FilteredList<>(this.items);
        this.initAll();
    }

    @Override
    public void initUI() {
        this.buttonEdit = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_MATERIAL.create('\uE254').size(18).color(LCGraphicStyle.MAIN_PRIMARY), "tooltip.user.comp.edit");
        this.buttonRemove = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH_ALT).size(19).color(LCGraphicStyle.SECOND_PRIMARY), "tooltip.user.comp.remove");
        VBox boxButtons = new VBox(5.0, buttonEdit, buttonRemove);
        boxButtons.setAlignment(Pos.TOP_RIGHT);
        BorderPane.setMargin(boxButtons, new Insets(10.0, 0.0, 0.0, 0.0));

        //Search field
        this.fieldSearchFilter = TextFields.createClearableTextField();
        this.fieldSearchFilter.setPromptText(Translation.getText("user.comp.search.tip"));
        HBox.setHgrow(this.fieldSearchFilter, Priority.ALWAYS);
        HBox boxFilter = new HBox(5.0, GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.SEARCH).sizeFactor(1).color(LCGraphicStyle.MAIN_LIGHT), this.fieldSearchFilter);
        boxFilter.setAlignment(Pos.CENTER_LEFT);

        //List of user components
        this.userCompListView = new ListView<>(this.filteredList);
        this.userCompListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.userCompListView.getStyleClass().addAll("border-transparent", "background-transparent");
        this.userCompListView.setCellFactory(lv -> new UserCompDetailListCell());
        this.userCompListView.setFixedCellSize(110);
        BorderPane.setMargin(this.userCompListView, new Insets(10.0, 0.0, 0.0, 0.0));

        //Top
        this.setTop(boxFilter);
        this.setCenter(this.userCompListView);
        this.setRight(boxButtons);
        this.setPadding(new Insets(10.0));
        this.setPrefWidth(UserCompSelectorDialog.USERCOMP_DIALOG_WIDTH);
        this.setPrefHeight(UserCompSelectorDialog.USERCOMP_DIALOG_HEIGHT);
    }

    @Override
    public void initListener() {
        this.fieldSearchFilter.textProperty().addListener((obs, ov, nv) -> {
            // FIXME : use other search method
            Predicate<UserCompDescriptionI> predicate = (p) -> UserCompController.INSTANCE.getPredicateFor(nv).test(p);
            this.filteredList.setPredicate(predicate);
        });
        //        this.buttonRemove.setOnAction(e -> {
        //            ConfigActionController.INSTANCE
        //                    .executeAction(new UserCompActions.DeleteUserComp(buttonRemove, new ArrayList<>(this.userCompListView.getSelectionModel().getSelectedItems())));
        //        });
        //        this.buttonEdit.setOnAction(e -> {
        //            ConfigActionController.INSTANCE.executeAction(new UserCompActions.EditUserCompAction(this, this.userCompListView.getSelectionModel().getSelectedItem()));
        //        });
        //        this.menuItemClearSelection.setOnAction(e -> {
        //            this.userCompListView.getSelectionModel().clearSelection();
        //        });
        //        this.menuItemSelectAll.setOnAction(e -> {
        //            this.userCompListView.getSelectionModel().selectAll();
        //        });
    }

    @Override
    public void initBinding() {
        this.buttonEdit.disableProperty().bind(userCompListView.getSelectionModel().selectedItemProperty().isNull());
        this.buttonRemove.disableProperty().bind(userCompListView.getSelectionModel().selectedItemProperty().isNull());
        // TODO : on show/hide
        EasyBind.listBind(this.items, UserCompController.INSTANCE.getUserComponents());
    }

    public void remove(UserCompDescriptionI item) {
        UserCompController.INSTANCE.getUserComponents().remove(item);
    }

    public UserCompDescriptionI getSelectedUserCompDescription() {
        return userCompListView.getSelectionModel().getSelectedItem();
    }
}
