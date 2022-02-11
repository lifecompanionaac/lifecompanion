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
package org.lifecompanion.config.view.pane.usercomp;

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
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.config.data.action.impl.UserCompActions;
import org.lifecompanion.config.data.action.impl.UserCompActions.DeleteUserComp;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.usercomp.UserCompController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * View to display and manage {@link UserCompDescriptionI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserCompView extends TitledPane implements LCViewInitHelper {

    private BorderPane paneCenter;
    private ListView<UserCompDescriptionI> userCompListView;
    private ObservableList<UserCompDescriptionI> items;
    private FilteredList<UserCompDescriptionI> filteredList;
    private TextField fieldSearchFilter;
    private Button buttonRemove;
    private Button buttonEdit;
    private MenuButton buttonMenu;
    private MenuItem menuItemSelectAll, menuItemClearSelection;
    private Label labelSelected;

    public UserCompView() {
        this.items = FXCollections.observableArrayList();
        this.filteredList = new FilteredList<>(this.items);
        this.initAll();
    }

    @Override
    public void initUI() {
        //Init
        this.getStyleClass().add("left-titled-pane");
        this.setText(Translation.getText("panel.user.comp.title").toUpperCase());
        this.setExpanded(false);

        //Header
        StackPane headerPane = new StackPane();
        headerPane.getStyleClass().add("bottom-border-part");
        //Button to add/remove
        HBox boxButtonLabel = new HBox();
        this.buttonRemove = UIUtils.createGraphicButton(
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.TRASH_ALT).size(18).color(LCGraphicStyle.SECOND_PRIMARY),
                "tooltip.user.comp.remove");
        buttonEdit = UIUtils.createGraphicButton(
                LCGlyphFont.FONT_MATERIAL.create('\uE254').size(17).color(LCGraphicStyle.MAIN_PRIMARY),
                "tooltip.user.comp.edit");

        this.buttonMenu = UIUtils.createGraphicMenuButton(
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.ELLIPSIS_V).size(18).color(LCGraphicStyle.MAIN_DARK), "tooltip.user.comp.menu");
        //Advance function button
        this.menuItemSelectAll = new MenuItem(Translation.getText("user.comp.list.select.all"));
        this.menuItemClearSelection = new MenuItem(Translation.getText("user.comp.list.clear"));
        this.buttonMenu.getItems().addAll(this.menuItemSelectAll, this.menuItemClearSelection);
        //Label
        this.labelSelected = new Label();
        boxButtonLabel.setAlignment(Pos.CENTER_RIGHT);
        StackPane.setAlignment(this.labelSelected, Pos.CENTER_LEFT);
        StackPane.setAlignment(boxButtonLabel, Pos.CENTER_RIGHT);
        boxButtonLabel.getChildren().addAll(this.buttonEdit, this.buttonRemove, this.buttonMenu);
        headerPane.getChildren().addAll(this.labelSelected, boxButtonLabel);

        //Search field
        this.fieldSearchFilter = TextFields.createClearableTextField();
        this.fieldSearchFilter.setPromptText(Translation.getText("user.comp.search.tip"));
        HBox.setHgrow(this.fieldSearchFilter, Priority.ALWAYS);
        HBox boxFilter = new HBox(5.0, LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.SEARCH).sizeFactor(1).color(LCGraphicStyle.MAIN_LIGHT),
                this.fieldSearchFilter);
        boxFilter.setAlignment(Pos.CENTER_LEFT);

        //List of user components
        this.userCompListView = new ListView<>(this.filteredList);
        this.userCompListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.userCompListView.getStyleClass().add("user-comp-list-view");
        this.userCompListView.setCellFactory(lv -> new UserCompListCell());
        BorderPane.setMargin(this.userCompListView, new Insets(5.0, 0.0, 0.0, 0.0));

        //Top
        VBox boxTop = new VBox(headerPane, boxFilter);
        this.paneCenter = new BorderPane();
        this.paneCenter.setTop(boxTop);
        this.paneCenter.setCenter(this.userCompListView);
        this.paneCenter.setPrefHeight(200.0);
        this.paneCenter.setMaxHeight(Double.MAX_VALUE);
        this.setContent(this.paneCenter);
    }

    @Override
    public void initListener() {
        this.fieldSearchFilter.textProperty().addListener((obs, ov, nv) -> {
            Predicate<UserCompDescriptionI> predicate = (p) -> UserCompController.INSTANCE.getPredicateFor(nv).test(p);
            this.filteredList.setPredicate(predicate);
        });
        this.buttonRemove.setOnAction(e -> {
            ConfigActionController.INSTANCE
                    .executeAction(new DeleteUserComp(buttonRemove, new ArrayList<>(this.userCompListView.getSelectionModel().getSelectedItems())));
        });
        this.buttonEdit.setOnAction(e -> {
            ConfigActionController.INSTANCE.executeAction(new UserCompActions.EditUserCompAction(this, this.userCompListView.getSelectionModel().getSelectedItem()));
        });
        this.menuItemClearSelection.setOnAction(e -> {
            this.userCompListView.getSelectionModel().clearSelection();
        });
        this.menuItemSelectAll.setOnAction(e -> {
            this.userCompListView.getSelectionModel().selectAll();
        });
    }

    @Override
    public void initBinding() {
        EasyBind.listBind(this.items, UserCompController.INSTANCE.getUserComponents());
        this.labelSelected.textProperty().bind(Bindings.createStringBinding(() -> {
            return this.userCompListView.getSelectionModel().getSelectedItems().size() + "/" + this.filteredList.size();
        }, this.filteredList, this.userCompListView.getSelectionModel().selectedItemProperty()));
        this.buttonRemove.disableProperty().bind(this.userCompListView.getSelectionModel().selectedItemProperty().isNull());
        this.buttonEdit.disableProperty().bind(this.userCompListView.getSelectionModel().selectedItemProperty().isNull());
    }

}
