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

package org.lifecompanion.plugin.spellgame.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.spellgame.SpellGamePlugin;
import org.lifecompanion.plugin.spellgame.SpellGamePluginProperties;
import org.lifecompanion.plugin.spellgame.model.SpellGameWordList;
import org.lifecompanion.plugin.spellgame.ui.cell.SpellGameWordListListCell;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.util.javafx.FXControlUtils;

public class SpellGameWordListConfigView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {

    static final String STEP_ID = "SpellGameWordListConfigView";

    private TextField fieldListName;

    public SpellGameWordListConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return false;
    }

    @Override
    public String getTitleId() {
        return "spellgame.plugin.config.view.wordlist.title";
    }

    @Override
    public String getStep() {
        return STEP_ID;
    }

    @Override
    public String getPreviousStep() {
        return SpellGameGeneralConfigView.STEP_ID;
    }

    @Override
    public String getMenuStepToSelect() {
        return SpellGameGeneralConfigView.STEP_ID;
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public void initUI() {
        fieldListName = new TextField();
        Label labelListName = new Label(Translation.getText("spellgame.plugin.config.field.list.name"));
        HBox boxListName = new HBox(5.0, labelListName, fieldListName);
        this.setTop(boxListName);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initBinding() {
    }

    private SpellGameWordList editedWordList;

    @Override
    public void beforeShow(Object[] stepArgs) {
        editedWordList = (SpellGameWordList) stepArgs[0];
        fieldListName.textProperty().bindBidirectional(editedWordList.nameProperty());
    }

    @Override
    public void afterHide() {
        fieldListName.textProperty().unbindBidirectional(editedWordList.nameProperty());
    }

    @Override
    public void bind(LCConfigurationI model) {
    }

    @Override
    public void unbind(LCConfigurationI model) {
    }

    @Override
    public void saveChanges() {
    }
}
