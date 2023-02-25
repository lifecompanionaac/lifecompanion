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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Pair;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.spellgame.controller.task.ExportGameResultTask;
import org.lifecompanion.plugin.spellgame.controller.task.GetSpellGameResultsForConfiguration;
import org.lifecompanion.plugin.spellgame.model.SpellGameResult;
import org.lifecompanion.plugin.spellgame.model.SpellGameStepResult;
import org.lifecompanion.plugin.spellgame.ui.cell.SpellGameResultListCell;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SpellGameResultConfigView extends BorderPane implements GeneralConfigurationStepViewI {

    static final String STEP_ID = "SpellGameResultConfigView";
    private ListView<Pair<SpellGameResult, File>> listViewGameResults;
    private ProgressIndicator progressIndicator;
    private Button buttonRemoveAll;

    public SpellGameResultConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return false;
    }

    @Override
    public String getTitleId() {
        return "spellgame.plugin.config.view.history.title";
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
        listViewGameResults = new ListView<>();
        listViewGameResults.setCellFactory(SpellGameResultListCell::new);
        BorderPane.setMargin(listViewGameResults, new Insets(10.0));

        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(100, 100);

        buttonRemoveAll = FXControlUtils.createLeftTextButton(Translation.getText("spellgame.plugin.config.button.remove.all"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(24).color(LCGraphicStyle.SECOND_DARK), null);
        BorderPane.setAlignment(buttonRemoveAll, Pos.CENTER);

        this.setCenter(listViewGameResults);
        this.setBottom(buttonRemoveAll);

        this.setPadding(new Insets(PADDING));
    }

    @Override
    public void initListener() {
        this.buttonRemoveAll.setOnAction(e -> {
            ObservableList<Pair<SpellGameResult, File>> items = this.listViewGameResults.getItems();
            if (!CollectionUtils.isEmpty(items)) {
                if (DialogUtils.alertWithSourceAndType(this, Alert.AlertType.CONFIRMATION)
                        .withContentText(Translation.getText("spellgame.plugin.config.game.confirm.remove.text"))
                        .showAndWait() == ButtonType.OK) {
                    List<Pair<SpellGameResult, File>> toRemove = new ArrayList<>(items);
                    AsyncExecutorController.INSTANCE.addAndExecute(false, false, () -> {
                        toRemove.forEach(p -> {
                            IOUtils.deleteDirectoryAndChildren(p.getValue());
                        });
                    }, () -> listViewGameResults.getItems().clear());
                }
            }
        });
    }

    @Override
    public void initBinding() {
    }


    @Override
    public void beforeShow(Object[] stepArgs) {
        GetSpellGameResultsForConfiguration getSpellGameResultsForConfiguration = new GetSpellGameResultsForConfiguration(configuration);
        listViewGameResults.setPlaceholder(progressIndicator);
        progressIndicator.progressProperty().bind(getSpellGameResultsForConfiguration.progressProperty());
        getSpellGameResultsForConfiguration.setOnSucceeded(e -> {
            listViewGameResults.setPlaceholder(null);
            listViewGameResults.setItems(FXCollections.observableList(getSpellGameResultsForConfiguration.getValue()));
        });
        AsyncExecutorController.INSTANCE.addAndExecute(false, false, getSpellGameResultsForConfiguration);
    }

    @Override
    public void afterHide() {
        listViewGameResults.setItems(null);
    }

    private LCConfigurationI configuration;

    @Override
    public void bind(LCConfigurationI model) {
        this.configuration = model;
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.configuration = null;
    }

    @Override
    public void saveChanges() {
    }
}
