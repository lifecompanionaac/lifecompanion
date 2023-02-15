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
package org.lifecompanion.ui.app.profileconfigselect;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.ui.common.pane.specific.cell.ProfileAdvancedListCell;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.DisableSelectionSelectionModel;
import org.lifecompanion.util.model.Triple;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.editaction.LCProfileActions;
import org.lifecompanion.controller.editaction.LCProfileActions.ProfileExportAction;
import org.lifecompanion.controller.editaction.LCProfileActions.RemoveProfileAction;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigStep;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.editmode.ConfigActionController;
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
        Triple<HBox, Label, Node> header = FXControlUtils.createHeader("profile.selection.view.title", null);
        this.setTop(header.getLeft());

        //List view to display profile
        this.profileListView = new ListView<>();
        Label labelPlaceholderProfile = new Label(Translation.getText("profile.selection.profile.list.empty"));
        labelPlaceholderProfile.setWrapText(true);
        this.profileListView.setPlaceholder(labelPlaceholderProfile);
        this.profileListView.setFixedCellSize(80.0);
        this.profileListView.setCellFactory((listView) -> new ProfileAdvancedListCell(this::profileSelected));
        this.profileListView.setSelectionModel(new DisableSelectionSelectionModel<>());

        //Place and style others buttons
        buttonAddProfile = FXControlUtils.createRightTextButton(Translation.getText("profile.selection.add.profile.button"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(22).color(LCGraphicStyle.MAIN_PRIMARY), "profile.selection.add.profile.button.tooltip");
        buttonAddProfile.getStyleClass().add("text-font-size-120");
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
    }

    @Override
    public void initBinding() {
        this.profileListView.setItems(ProfileController.INSTANCE.getProfiles());
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
