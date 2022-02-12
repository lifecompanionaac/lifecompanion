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
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigStep;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.util.UIControlHelper;
import org.lifecompanion.util.model.Triple;

public class ConfigAddView extends BorderPane implements LCViewInitHelper, ProfileConfigStepViewI {

    public ConfigAddView() {
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        Triple<HBox, Label, Node> header = UIControlHelper.createHeader("config.add.view.title", e -> {
            final ProfileConfigStep previousStep = ProfileConfigSelectionController.INSTANCE.getPreviousStep();
            if (previousStep != null) {
                ProfileConfigSelectionController.INSTANCE.setConfigStep(previousStep, null, null);
            } else {
                ProfileConfigSelectionController.INSTANCE.hideStage();
            }
        });

        // Action grid
        final Node nodeNew = UIControlHelper.createActionTableEntry("config.selection.create.new.config.button",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(30).color(LCGraphicStyle.MAIN_DARK),
                () -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.NewEditInListAction()));
        final Node nodeImport = UIControlHelper.createActionTableEntry("config.selection.create.import.config.button",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.DOWNLOAD).size(30).color(LCGraphicStyle.MAIN_DARK),
                () -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.ImportEditAction(this, null,
                        configurationDescription -> ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_LIST, null, null))));
        final Node nodeDefault = UIControlHelper.createActionTableEntry("config.selection.add.from.default.config.button",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TH_LARGE).size(28).color(LCGraphicStyle.MAIN_DARK),
                () -> ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_ADD_FROM_DEFAULT, ProfileConfigStep.CONFIGURATION_ADD, null));
        final Node nodeDuplicate = UIControlHelper.createActionTableEntry("config.selection.create.duplicate.config.button",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.COPY).size(28).color(LCGraphicStyle.MAIN_DARK),
                () -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.DuplicateEditAction((this))));
        VBox boxActions = new VBox(nodeNew, nodeImport, nodeDefault, nodeDuplicate);
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
        return false;
    }


    @Override
    public Node getView() {
        return this;
    }
    //========================================================================

}
