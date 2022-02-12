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
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.editaction.LCProfileActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigStep;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.util.UIControlHelper;
import org.lifecompanion.util.model.Triple;

/**
 * View that allow user to select a profile.
 *
 * @author Mathieu THEBAUD
 */
public class ProfileAddView extends BorderPane implements LCViewInitHelper, ProfileConfigStepViewI {

    public ProfileAddView() {
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        Triple<HBox, Label, Node> header = UIControlHelper.createHeader("profile.add.view.title",
                e -> ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigSelectionController.INSTANCE.getPreviousStep(), null, null));

        // Action grid
        final Node nodeNew = UIControlHelper.createActionTableEntry("profile.selection.create.new.profile.button",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(30).color(LCGraphicStyle.MAIN_DARK),
                () -> ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_CREATE, ProfileConfigStep.PROFILE_LIST, null));
        final Node nodeImport = UIControlHelper.createActionTableEntry("profile.selection.create.import.profile.button",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.DOWNLOAD).size(30).color(LCGraphicStyle.MAIN_DARK),
                () -> ConfigActionController.INSTANCE.executeAction(new LCProfileActions.ProfileImportAction(this,
                        (p -> ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_LIST, null, null)))));
        final Node nodeDuplicate = UIControlHelper.createActionTableEntry("profile.selection.create.duplicate.profile.button",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.COPY).size(30).color(LCGraphicStyle.MAIN_DARK),
                () -> ConfigActionController.INSTANCE.executeAction(new LCProfileActions.DuplicateProfileAction(this)));
        VBox boxActions = new VBox(nodeNew, nodeImport, nodeDuplicate);
        BorderPane.setMargin(boxActions, new Insets(10.0));
        BorderPane.setAlignment(boxActions, Pos.CENTER);

        this.setTop(header.getLeft());
        this.setCenter(boxActions);
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
