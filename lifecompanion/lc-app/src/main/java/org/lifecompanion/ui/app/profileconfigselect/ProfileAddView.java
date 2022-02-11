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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.model.Triple;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.editaction.LCProfileActions;
import org.lifecompanion.controller.editmode.ProfileConfigSelectionController;
import org.lifecompanion.controller.editmode.ProfileConfigStep;
import org.lifecompanion.controller.resource.LCGlyphFont;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.util.ConfigUIUtils;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * View that allow user to select a profile.
 *
 * @author Mathieu THEBAUD
 */
public class ProfileAddView extends BorderPane implements LCViewInitHelper, ProfileConfigStepViewI {
    private Button buttonCreateNew, buttonImport, buttonDuplicate;

    public ProfileAddView() {
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        Triple<HBox, Label, Node> header = ConfigUIUtils.createHeader("profile.add.view.title",
                e -> ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigSelectionController.INSTANCE.getPreviousStep(), null, null));

        // Action grid
        GridPane gridPaneActions = new GridPane();
        this.buttonCreateNew = ConfigUIUtils.createActionTableEntry(0, "profile.selection.create.new.profile.button",
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(30).color(LCGraphicStyle.MAIN_DARK), gridPaneActions);
        this.buttonImport = ConfigUIUtils.createActionTableEntry(2, "profile.selection.create.import.profile.button",
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.DOWNLOAD).size(30).color(LCGraphicStyle.MAIN_DARK), gridPaneActions);
        this.buttonDuplicate = ConfigUIUtils.createActionTableEntry(4, "profile.selection.create.duplicate.profile.button",
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.COPY).size(30).color(LCGraphicStyle.MAIN_DARK), gridPaneActions);
        BorderPane.setMargin(gridPaneActions, new Insets(10.0));
        BorderPane.setAlignment(gridPaneActions, Pos.CENTER);

        this.setTop(header.getLeft());
        this.setCenter(gridPaneActions);
    }

    @Override
    public void initListener() {
        this.buttonCreateNew.setOnAction(e -> ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_CREATE, ProfileConfigStep.PROFILE_LIST, null));
        this.buttonImport.setOnAction(e -> ConfigActionController.INSTANCE.executeAction(new LCProfileActions.ProfileImportAction(buttonImport,
                (p -> ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_LIST, null, null)))));
        this.buttonDuplicate.setOnAction(e -> ConfigActionController.INSTANCE.executeAction(new LCProfileActions.DuplicateProfileAction(buttonDuplicate)));
    }
    //========================================================================

    // Class part : "Profile step"
    //========================================================================
    @Override
    public void beforeShow() {
    }

    @Override
    public boolean cancelRequest() {
        if (ProfileConfigSelectionController.INSTANCE.showNoProfileWarning(this)) return true;
        return false;
    }


    @Override
    public Node getView() {
        return this;
    }
    //========================================================================

}
