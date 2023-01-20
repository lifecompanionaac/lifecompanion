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

package org.lifecompanion.plugin.ppp.view.config;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editmode.LCFileChoosers;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.ppp.model.Action;
import org.lifecompanion.plugin.ppp.model.UserProfile;
import org.lifecompanion.plugin.ppp.services.FilesService;
import org.lifecompanion.plugin.ppp.services.RecordsService;
import org.lifecompanion.plugin.ppp.tasks.ExportDataTask;
import org.lifecompanion.plugin.ppp.tasks.LoadConfigProfileTask;
import org.lifecompanion.plugin.ppp.tasks.SaveProfileTask;
import org.lifecompanion.plugin.ppp.view.commons.FormatterListCell;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.lifecompanion.controller.editmode.FileChooserType.OTHER_MISC_EXTERNAL;


public class GeneralConfigView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    public static final String STEP_NAME = "PPPPluginGeneralConfigView";

    private TextField fieldUserId;
    private Spinner<Integer> fieldBaseScore;
    private DatePicker fieldBaseScoreAt;
    private ListView<Action> actionsListView;
    private TextField actionsAddTextField;
    private Button actionsAddBtn;
    private Button actionsMoveUpBtn;
    private Button actionsMoveDownBtn;
    private Button actionsDeleteBtn;
    private Button showRecordsBtn;
    private Button exportRecordsBtn;

    private LCConfigurationI editedConfiguration;
    private UserProfile editedProfile;

    public GeneralConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "ppp.plugin.view.config.title";
    }

    @Override
    public String getStep() {
        return STEP_NAME;
    }

    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public void initUI() {
        Label labelUserId = new Label(Translation.getText("ppp.plugin.view.config.general.fields.user_id.label"));
        labelUserId.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        this.fieldUserId = new TextField();
        GridPane.setHgrow(this.fieldUserId, Priority.ALWAYS);
        this.fieldUserId.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHalignment(this.fieldUserId, HPos.RIGHT);

        Label labelBaseScore = new Label(Translation.getText("ppp.plugin.view.config.general.fields.base_score.label"));
        this.fieldBaseScore = FXControlUtils.createIntSpinner(0, 60, 0, 1, 100.0);
        GridPane.setHalignment(this.fieldBaseScore, HPos.RIGHT);

        Label labelBaseScoreAt = new Label(Translation.getText("ppp.plugin.view.config.general.fields.base_score_at.label"));
        this.fieldBaseScoreAt = new DatePicker();
        GridPane.setHalignment(this.fieldBaseScoreAt, HPos.RIGHT);

        GridPane configLayout = new GridPane();
        configLayout.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        configLayout.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        configLayout.add(labelUserId, 0, 0);
        configLayout.add(this.fieldUserId, 1, 0);
        configLayout.add(labelBaseScore, 0, 1);
        configLayout.add(this.fieldBaseScore, 1, 1);
        configLayout.add(labelBaseScoreAt, 0, 2);
        configLayout.add(this.fieldBaseScoreAt, 1, 2);

        this.actionsListView = new ListView<>();
        this.actionsListView.setMaxHeight(150);
        this.actionsListView.setCellFactory((lv) -> new FormatterListCell<>(Action::getName));

        this.actionsMoveUpBtn = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.ARROW_UP).size(20).color(
                        LCGraphicStyle.MAIN_PRIMARY), "ppp.plugin.view.config.actions.move_up.name");
        this.actionsMoveDownBtn = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.ARROW_DOWN).size(20).color(
                        LCGraphicStyle.MAIN_PRIMARY), "ppp.plugin.view.config.actions.move_down.name");
        this.actionsDeleteBtn = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH_ALT).size(20).color(
                        LCGraphicStyle.SECOND_PRIMARY), "ppp.plugin.view.config.actions.remove.name");
        VBox actionsButtons = new VBox(this.actionsMoveUpBtn, this.actionsMoveDownBtn, this.actionsDeleteBtn);
        actionsButtons.setAlignment(Pos.CENTER);

        this.actionsAddTextField = new TextField();
        this.actionsAddTextField.setPromptText(Translation.getText("ppp.plugin.view.config.actions.add.placeholder"));
        this.actionsAddBtn = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(20).color(
                        LCGraphicStyle.MAIN_PRIMARY), "ppp.plugin.view.config.actions.add.name");
        HBox actionsAdd = new HBox(this.actionsAddTextField, this.actionsAddBtn);
        actionsAdd.setPadding(new Insets(5, 0, 0, 0));
        actionsAdd.setAlignment(Pos.CENTER);
        HBox.setHgrow(this.actionsAddTextField, Priority.ALWAYS);

        BorderPane actionsLayout = new BorderPane();
        actionsLayout.setCenter(this.actionsListView);
        actionsLayout.setRight(actionsButtons);
        actionsLayout.setBottom(actionsAdd);

        this.showRecordsBtn = FXControlUtils.createRightTextButton(
                Translation.getText("ppp.plugin.view.config.records.actions.show_records.name"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.LINE_CHART).size(20).color(LCGraphicStyle.MAIN_DARK),
                null);
        GridPane.setHgrow(this.showRecordsBtn, Priority.ALWAYS);
        GridPane.setHalignment(this.showRecordsBtn, HPos.CENTER);
        this.exportRecordsBtn = FXControlUtils.createRightTextButton(
                Translation.getText("ppp.plugin.view.config.records.actions.export_records.name"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.UPLOAD).size(20).color(LCGraphicStyle.MAIN_DARK),
                null);
        GridPane.setHgrow(this.exportRecordsBtn, Priority.ALWAYS);
        GridPane.setHalignment(this.exportRecordsBtn, HPos.CENTER);

        GridPane recordsButtons = new GridPane();
        recordsButtons.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        recordsButtons.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        recordsButtons.add(this.showRecordsBtn, 0, 0);
        recordsButtons.add(this.exportRecordsBtn, 1, 0);

        VBox vboxTotal = new VBox(5.0,
                FXControlUtils.createTitleLabel(Translation.getText("ppp.plugin.view.config.general.title")),
                configLayout,
                FXControlUtils.createTitleLabel(Translation.getText("ppp.plugin.view.config.actions.title")),
                actionsLayout,
                FXControlUtils.createTitleLabel(Translation.getText("ppp.plugin.view.config.records.title")),
                recordsButtons
        );
        vboxTotal.setPadding(new Insets(5.0));
        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        ScrollPane scrollPane = new ScrollPane(vboxTotal);
        scrollPane.setFitToWidth(true);
        this.setCenter(scrollPane);
    }

    @Override
    public void initBinding() {
        this.actionsMoveUpBtn.disableProperty().bind(Bindings.createBooleanBinding(() -> {
                    int selectedIndex = this.actionsListView.selectionModelProperty().get().selectedIndexProperty().get();
                    return selectedIndex <= 0;
                }, this.actionsListView.getItems(),
                this.actionsListView.selectionModelProperty().get().selectedIndexProperty()));
        this.actionsMoveDownBtn.disableProperty().bind(Bindings.createBooleanBinding(() -> {
                    int selectedIndex = this.actionsListView.selectionModelProperty().get().selectedIndexProperty().get();
                    return selectedIndex == -1 || selectedIndex >= (this.actionsListView.getItems().size() - 1);
                }, this.actionsListView.getItems(),
                this.actionsListView.selectionModelProperty().get().selectedIndexProperty()));
        this.actionsDeleteBtn.disableProperty().bind(
                this.actionsListView.selectionModelProperty().get().selectedItemProperty().isNull());
    }

    @Override
    public void initListener() {
        this.actionsMoveUpBtn.setOnAction(event -> {
            int selectedIndex = this.actionsListView.selectionModelProperty().get().selectedIndexProperty().get();

            Collections.swap(this.actionsListView.getItems(), selectedIndex, selectedIndex - 1);
            this.actionsListView.selectionModelProperty().get().select(selectedIndex - 1);
        });
        this.actionsMoveDownBtn.setOnAction(event -> {
            int selectedIndex = this.actionsListView.selectionModelProperty().get().selectedIndexProperty().get();

            Collections.swap(this.actionsListView.getItems(), selectedIndex, selectedIndex + 1);
            this.actionsListView.selectionModelProperty().get().select(selectedIndex + 1);
        });
        this.actionsDeleteBtn.setOnAction(event -> {
            Action selectedAction = this.actionsListView.selectionModelProperty().get().selectedItemProperty().get();
            if (selectedAction != null) {
                this.actionsListView.getItems().remove(selectedAction);
            }
        });

        Runnable addActionToList = () -> {
            String name = StringUtils.stripToEmpty(this.actionsAddTextField.getText());
            if (name.length() > 0) {
                this.actionsAddTextField.setText("");

                this.actionsListView.getItems().add(new Action(name));
            }
        };
        this.actionsAddTextField.setOnAction(e -> addActionToList.run());
        this.actionsAddBtn.setOnAction(e -> addActionToList.run());

        this.showRecordsBtn.setOnAction(
                event -> RecordsService.INSTANCE.showRecordStage(this.editedConfiguration));
        this.exportRecordsBtn.setOnAction(event -> {
            FileChooser fileChooser = LCFileChoosers.getOtherFileChooser(
                    Translation.getText("ppp.plugin.view.config.records.actions.export_records.chooser.title"),
                    FilesService.DATA_EXTENSION_FILTER, OTHER_MISC_EXTERNAL);
            fileChooser.setInitialFileName(IOHelper.DATE_FORMAT_FILENAME_WITHOUT_TIME.format(new Date()) + "-"
                    + IOUtils.getValidFileName(this.editedProfile.getUserId()) + "-ppp");

            File destinationZipFile = fileChooser.showSaveDialog(FXUtils.getSourceWindow(this));
            if (destinationZipFile != null) {
                Task<Void> task = new ExportDataTask(this.editedConfiguration, destinationZipFile);
                AsyncExecutorController.INSTANCE.addAndExecute(true, false, task);
            }
        });

        this.fieldBaseScoreAt.getEditor().textProperty().addListener((obs, ov, nv) -> {
            try {
                this.fieldBaseScoreAt.setValue(this.fieldBaseScoreAt.getConverter().fromString(nv));
            } catch (Exception exception) {
                // Ignore parsing exception.
            }
        });
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
    }

    @Override
    public void afterHide() {
    }

    @Override
    public void saveChanges() {
        this.editedProfile.setUserId(this.fieldUserId.getText());
        this.editedProfile.setBaseScore(this.fieldBaseScore.getValue());
        this.editedProfile.setBaseScoreAt(this.fieldBaseScoreAt.getValue());
        this.editedProfile.setActions(new ArrayList<>(this.actionsListView.getItems()));

        AsyncExecutorController.INSTANCE.addAndExecute(true, false,
                new SaveProfileTask(this.editedConfiguration, this.editedProfile));
    }

    @Override
    public void cancelChanges() {
    }

    @Override
    public void bind(LCConfigurationI config) {
        this.editedConfiguration = config;
        LoadConfigProfileTask task = new LoadConfigProfileTask(this.editedConfiguration);
        task.setOnSucceeded(e -> {
            this.editedProfile = task.getValue();

            this.fieldUserId.textProperty().set(this.editedProfile.getUserId());
            this.fieldBaseScore.getValueFactory().setValue(this.editedProfile.getBaseScore());
            this.fieldBaseScoreAt.setValue(this.editedProfile.getBaseScoreAt());
            this.actionsListView.setItems(FXCollections.observableArrayList(this.editedProfile.getActions()));
        });

        AsyncExecutorController.INSTANCE.addAndExecute(true, false, task);
    }

    @Override
    public void unbind(LCConfigurationI config) {
        this.editedConfiguration = null;
        this.editedProfile = null;

        this.actionsListView.setItems(null);
    }
}
