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
package org.lifecompanion.config.view.pane.profile;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.base.data.common.DisableSelectionSelectionModel;
import org.lifecompanion.base.data.common.Triple;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.control.refacto.ProfileController;
import org.lifecompanion.config.data.action.impl.LCProfileActions;
import org.lifecompanion.config.data.action.impl.LCProfileActions.ProfileExportAction;
import org.lifecompanion.config.data.action.impl.LCProfileActions.RemoveProfileAction;
import org.lifecompanion.config.data.component.profile.ProfileConfigSelectionController;
import org.lifecompanion.config.data.component.profile.ProfileConfigStep;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.pane.profilconfig.ProfileConfigStepViewI;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * View that allow user to select a profile.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProfileSelectionView extends BorderPane implements LCViewInitHelper, ProfileConfigStepViewI {
    /**
     * List view to diplay profiles
     */
    private ListView<LCProfileI> profileListView;

    /**
     * Button to add,remove,edit a profile
     */
    private Button buttonAdd, buttonRemove, buttonEdit, buttonExport, buttonImport;

    /**
     * Button to add a profile (show add choice : from cloud, local file, from starch...)
     */
    private Button buttonAddProfile;

    public ProfileSelectionView() {
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        Triple<HBox, Label, Node> header = ConfigUIUtils.createHeader("profile.selection.view.title", null);
        this.setTop(header.getLeft());

        //List view to display profile
        this.profileListView = new ListView<>();
        Label labelPlaceholderProfile = new Label(Translation.getText("profile.selection.profile.list.empty"));
        labelPlaceholderProfile.setWrapText(true);
        this.profileListView.setPlaceholder(labelPlaceholderProfile);
        this.profileListView.setFixedCellSize(80.0);
        this.profileListView.setCellFactory((listView) -> new ProfileAdvancedListCell(this::profileSelected));
        this.profileListView.setSelectionModel(new DisableSelectionSelectionModel<>());

        //Create buttons
        this.buttonAdd = UIUtils.createGraphicMaterialButton(
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(40).color(LCGraphicStyle.SECOND_PRIMARY),
                "tooltip.profile.list.add");
        this.buttonRemove = UIUtils.createGraphicButton(
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(20).color(LCGraphicStyle.SECOND_DARK), "tooltip.profile.list.remove");
        this.buttonEdit = UIUtils.createGraphicButton(LCGlyphFont.FONT_MATERIAL.create('\uE254').size(22).color(LCGraphicStyle.MAIN_DARK),
                "tooltip.profile.list.edit");
        this.buttonImport = UIUtils.createGraphicButton(
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.DOWNLOAD).size(20).color(LCGraphicStyle.MAIN_PRIMARY),
                "tooltip.profile.list.import");
        this.buttonExport = UIUtils.createGraphicButton(
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.UPLOAD).size(20).color(LCGraphicStyle.MAIN_DARK), "tooltip.profile.list.export");
        //Place and style add
        StackPane.setAlignment(this.buttonAdd, Pos.TOP_LEFT);
        StackPane.setMargin(this.buttonAdd, new Insets(-30, 10, 5, -7));
        this.buttonAdd.getStyleClass().add("material-button-round-add");
        //Place and style others buttons
        StackPane.setAlignment(this.buttonEdit, Pos.TOP_RIGHT);
        StackPane.setAlignment(this.buttonRemove, Pos.TOP_RIGHT);
        StackPane.setAlignment(this.buttonImport, Pos.TOP_RIGHT);
        StackPane.setAlignment(this.buttonExport, Pos.TOP_RIGHT);
        StackPane.setMargin(this.buttonEdit, new Insets(-7, 90, 0, 0));
        StackPane.setMargin(this.buttonRemove, new Insets(-7, 60, 0, 0));
        StackPane.setMargin(this.buttonExport, new Insets(-7, 30, 0, 0));
        StackPane.setMargin(this.buttonImport, new Insets(-6, 0, 0, 0));

        buttonAddProfile = UIUtils.createRightTextButton(Translation.getText("profile.selection.add.profile.button"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(22).color(LCGraphicStyle.MAIN_PRIMARY), "profile.selection.add.profile.button.tooltip");
        buttonAddProfile.getStyleClass().add("button-icon-text-bigger");
        HBox bottomButtons = new HBox(10.0, buttonAddProfile);
        BorderPane.setMargin(bottomButtons, new Insets(0, 10.0, 10.0, 10.0));
        bottomButtons.setAlignment(Pos.CENTER);

        //Total add
        BorderPane.setMargin(profileListView, new Insets(10.0));
        this.setCenter(profileListView);
        this.setBottom(bottomButtons);
    }

    @Override
    public void initListener() {
        this.buttonAddProfile.setOnAction((ea) -> ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_ADD, ProfileConfigStep.PROFILE_LIST, null));
        //On remove, delete selected
        this.buttonRemove.setOnAction((ea) -> {
            LCProfileI selectedItem = this.profileListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                LCProfileActions.RemoveProfileAction removeAction = new RemoveProfileAction(buttonRemove, selectedItem);
                ConfigActionController.INSTANCE.executeAction(removeAction);
            }
        });
        //On edit, try to edit selected
        this.buttonEdit.setOnAction((ea) -> {
            LCProfileI selectedItem = this.profileListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_EDIT, ProfileConfigSelectionController.INSTANCE.currentStepProperty().get(), selectedItem);
            }
        });
        // Export
        this.buttonExport.setOnAction((ea) -> {
            LCProfileI selectedItem = this.profileListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                ConfigActionController.INSTANCE.executeAction(new ProfileExportAction(buttonExport, selectedItem));
            }
        });
    }

    @Override
    public void initBinding() {
        this.profileListView.setItems(ProfileController.INSTANCE.getProfiles());
        this.buttonRemove.disableProperty().bind(this.profileListView.getSelectionModel().selectedItemProperty().isNull());
        this.buttonEdit.disableProperty().bind(this.profileListView.getSelectionModel().selectedItemProperty().isNull());
        this.buttonExport.disableProperty().bind(this.profileListView.getSelectionModel().selectedItemProperty().isNull());
    }

    //========================================================================

    // Class part : "Profile step"
    //========================================================================
    @Override
    public void beforeShow() {
        this.profileListView.getSelectionModel().clearSelection();
    }

    @Override
    public boolean cancelRequest() {
        return ProfileConfigSelectionController.INSTANCE.showNoProfileWarning(this);
    }

    @Override
    public Node getView() {
        return this;
    }


    private void profileSelected(final LCProfileI profile) {
        if (profile != null) {
            ConfigActionController.INSTANCE.executeAction(new LCProfileActions.SelectProfileAction(this, profile));
            ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_LIST, null, null);
        }
    }
    //========================================================================

}
