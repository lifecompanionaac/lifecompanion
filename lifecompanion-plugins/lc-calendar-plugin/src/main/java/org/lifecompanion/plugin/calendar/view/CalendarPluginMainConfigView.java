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

package org.lifecompanion.plugin.calendar.view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.calendar.CalendarPlugin;
import org.lifecompanion.plugin.calendar.CalendarPluginProperties;
import org.lifecompanion.plugin.calendar.controller.ExportCalendarTask;
import org.lifecompanion.plugin.calendar.model.LCCalendar;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.common.control.generic.DurationPickerControl;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarPluginMainConfigView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarPluginMainConfigView.class);

    static final String STEP_ID = "CalendarPluginMainConfigView";
    private Button buttonEditCurrentWeek, buttonEditLeisure, buttonPrintCalendar;
    private DurationPickerControl durationPickerMaxAlarmRepeatTimeMs, durationPickerAlarmRepeatIntervalTimeMs;

    public CalendarPluginMainConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "calendar.plugin.main.config.view.title";
    }

    @Override
    public String getStep() {
        return STEP_ID;
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
        buttonEditCurrentWeek = FXControlUtils.createRightTextButton(Translation.getText("calendar.plugin.config.view.main.button.current.week"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CALENDAR).size(20).color(LCGraphicStyle.MAIN_DARK),
                null);
        GridPane.setHalignment(buttonEditCurrentWeek, HPos.CENTER);
        buttonEditLeisure = FXControlUtils.createRightTextButton(Translation.getText("calendar.plugin.config.view.main.button.leisure"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.GAMEPAD).size(20).color(LCGraphicStyle.MAIN_DARK),
                null);
        GridPane.setHalignment(buttonEditLeisure, HPos.CENTER);
        buttonPrintCalendar = new Button("Imprimer");
        buttonPrintCalendar = FXControlUtils.createRightTextButton(Translation.getText("calendar.plugin.config.view.main.button.print"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PRINT).size(20).color(LCGraphicStyle.MAIN_DARK),
                null);
        GridPane.setHalignment(buttonPrintCalendar, HPos.CENTER);

        durationPickerAlarmRepeatIntervalTimeMs = new DurationPickerControl();

        durationPickerMaxAlarmRepeatTimeMs = new DurationPickerControl();
        final Label labelRepeatAlarm = new Label(Translation.getText("calendar.plugin.field.max.repeat.alarm.duration"));
        labelRepeatAlarm.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelRepeatAlarm, Priority.ALWAYS);
        Label labelRepeatAlarmExplain = new Label(Translation.getText("calendar.plugin.field.max.repeat.alarm.duration.explain"));
        labelRepeatAlarmExplain.getStyleClass().addAll("text-font-italic", "text-fill-gray", "text-font-size-90");

        GridPane gridPaneConfiguration = new GridPane();
        gridPaneConfiguration.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneConfiguration.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        gridPaneConfiguration.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        int gridRowIndex = 0;

        gridPaneConfiguration.add(FXControlUtils.createTitleLabel("calendar.plugin.config.view.main.title.content"), 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(buttonEditCurrentWeek, 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(buttonEditLeisure, 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(buttonPrintCalendar, 0, gridRowIndex++, 2, 1);

        gridPaneConfiguration.add(FXControlUtils.createTitleLabel("calendar.plugin.config.view.main.title.config"), 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(new Label(Translation.getText("calendar.plugin.field.repeat.alarm.interval.duration")), 0, gridRowIndex);
        gridPaneConfiguration.add(durationPickerAlarmRepeatIntervalTimeMs, 1, gridRowIndex++);
        gridPaneConfiguration.add(labelRepeatAlarm, 0, gridRowIndex);
        gridPaneConfiguration.add(durationPickerMaxAlarmRepeatTimeMs, 1, gridRowIndex++);
        gridPaneConfiguration.add(labelRepeatAlarmExplain, 0, gridRowIndex++, 2, 1);

        this.setCenter(gridPaneConfiguration);
    }

    @Override
    public void initListener() {
        this.buttonEditCurrentWeek.setOnAction(e -> GeneralConfigurationController.INSTANCE.showStep(CalendarPluginWeekConfigView.STEP_ID, editedCalendar));
        this.buttonEditLeisure.setOnAction(e -> GeneralConfigurationController.INSTANCE.showStep(CalendarPluginLeisureConfigView.STEP_ID, editedCalendar));
        this.buttonPrintCalendar.setOnAction(event -> {
            ExportCalendarTask exportCalendarTask = new ExportCalendarTask(configuration, AppModeController.INSTANCE.getEditModeContext().getConfigurationDescription(), editedCalendar);
            AsyncExecutorController.INSTANCE.addAndExecute(true, false, exportCalendarTask);
        });
    }

    @Override
    public void saveChanges() {
        configuration.getPluginConfigProperties(CalendarPlugin.PLUGIN_ID, CalendarPluginProperties.class).setCalendar(editedCalendar);
    }

    private LCCalendar editedCalendar;
    private LCConfigurationI configuration;

    @Override
    public void bind(LCConfigurationI model) {
        this.editedCalendar = (LCCalendar) model.getPluginConfigProperties(CalendarPlugin.PLUGIN_ID, CalendarPluginProperties.class).getCalendar().duplicate(false);
        this.configuration = model;
        durationPickerMaxAlarmRepeatTimeMs.durationProperty().bindBidirectional(editedCalendar.maxAlarmRepeatTimeMsProperty());
        durationPickerMaxAlarmRepeatTimeMs.tryToPickBestUnit();
        durationPickerAlarmRepeatIntervalTimeMs.durationProperty().bindBidirectional(editedCalendar.repeatAlarmIntervalTimeMsProperty());
        durationPickerAlarmRepeatIntervalTimeMs.tryToPickBestUnit();
    }

    @Override
    public void unbind(LCConfigurationI model) {
        durationPickerMaxAlarmRepeatTimeMs.durationProperty().unbindBidirectional(editedCalendar.maxAlarmRepeatTimeMsProperty());
        durationPickerAlarmRepeatIntervalTimeMs.durationProperty().unbindBidirectional(editedCalendar.repeatAlarmIntervalTimeMsProperty());
        this.editedCalendar = null;
        this.configuration = null;
    }
}
