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

package org.lifecompanion.plugin.calendar.view.prop;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.calendar.model.CalendarEvent;
import org.lifecompanion.plugin.calendar.model.DayOfWeek;
import org.lifecompanion.plugin.calendar.model.LCCalendar;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.CommonListViewActionContainer;
import org.lifecompanion.ui.common.pane.specific.cell.DetailledSimplerKeyContentContainerListCell;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.javafx.FXControlUtils;


public class CalendarWeekPropertiesView extends BorderPane implements LCViewInitHelper {
    private CalendarEventPropertiesView calendarEventPropertiesView;
    private ListView<CalendarEvent> listViewDayEvents;
    private Button buttonAddEvent;
    private CommonListViewActionContainer<CalendarEvent> commonListViewActionContainer;
    private ToggleGroup toggleGroupDays;
    private LCCalendar editedCalendar;
    private Label labelCurrentDay;

    public CalendarWeekPropertiesView() {
        this.initAll();
    }

    @Override
    public void initUI() {
        // Top : current week and days
        HBox boxDayButton = new HBox(4);
        toggleGroupDays = new ToggleGroup();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            boxDayButton.getChildren().add(createDayButton(dayOfWeek));
        }
        boxDayButton.setAlignment(Pos.CENTER);
        boxDayButton.setPrefHeight(45.0);

        labelCurrentDay = new Label(Translation.getText("calendar.plugin.field.current.day.label.none"));
        labelCurrentDay.getStyleClass().add("text-h4");

        VBox boxTop = new VBox(2.0, boxDayButton, labelCurrentDay, new Separator(Orientation.HORIZONTAL));
        boxTop.setAlignment(Pos.CENTER);

        // Center : prop view
        calendarEventPropertiesView = new CalendarEventPropertiesView();

        // Left list view and add button
        this.buttonAddEvent = FXControlUtils.createRightTextButton(Translation.getText("calendar.plugin.button.add.event.on.day"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        listViewDayEvents = new ListView<>();
        this.commonListViewActionContainer = new CommonListViewActionContainer<>(listViewDayEvents);
        listViewDayEvents.setCellFactory(lv -> new DetailledSimplerKeyContentContainerListCell<>(commonListViewActionContainer));

        // FIXME
        listViewDayEvents.setFixedCellSize(110.0);
        listViewDayEvents.setPrefWidth(150.0);
        // END OF FIXME

        VBox.setVgrow(listViewDayEvents, Priority.ALWAYS);
        VBox boxLeft = new VBox(10, listViewDayEvents, buttonAddEvent);
        boxLeft.setAlignment(Pos.CENTER);

        BorderPane.setMargin(boxLeft, new Insets(5, GeneralConfigurationStepViewI.GRID_V_GAP, 0, 0));

        this.setTop(boxTop);
        this.setLeft(boxLeft);
        this.setCenter(calendarEventPropertiesView);
        this.setPadding(new Insets(5, GeneralConfigurationStepViewI.PADDING, GeneralConfigurationStepViewI.PADDING, GeneralConfigurationStepViewI.PADDING));
    }

    @Override
    public void initListener() {
        this.buttonAddEvent.setOnAction(e -> {
            final CalendarEvent added = new CalendarEvent();
            listViewDayEvents.getItems().add(added);
            listViewDayEvents.getSelectionModel().select(added);
        });
        this.toggleGroupDays.selectedToggleProperty().addListener((obs, ov, nv) -> {
            if (editedCalendar != null && nv != null) {
                final Object dayOfWeek = nv.getUserData();
                editedCalendar.getDays().stream().filter(d -> d.dayOfWeekProperty().get() == dayOfWeek).findFirst().ifPresent(cd -> {
                    listViewDayEvents.setItems(cd.getEvents());
                    labelCurrentDay.setText(Translation.getText("calendar.plugin.field.current.day.label.edit", Translation.getText(cd.dayOfWeekProperty().get().getTranslationId())));
                });
            } else {
                listViewDayEvents.setItems(null);
                labelCurrentDay.setText(Translation.getText("calendar.plugin.field.current.day.label.none"));
            }
        });
        this.commonListViewActionContainer.setDuplicateFunction(item -> {
            CalendarEvent duplicated = item.duplicate(true);
            duplicated.textProperty().set(Translation.getText("general.configuration.view.user.action.copy.label.key.text") + " " + duplicated.textProperty().get());
            return duplicated;
        });
    }

    private ButtonBase createDayButton(DayOfWeek dayOfWeek) {
        final ToggleButton dayToggle = new ToggleButton(Translation.getText(dayOfWeek.getTranslationId()));
        dayToggle.getStyleClass().addAll("background-none", "opacity-80-hover", "opacity-60-pressed", "round-lightgrey-border-select");
        toggleGroupDays.getToggles().add(dayToggle);
        dayToggle.setUserData(dayOfWeek);
        return dayToggle;
    }

    @Override
    public void initBinding() {
        this.calendarEventPropertiesView.selectedNodeProperty().bind(listViewDayEvents.getSelectionModel().selectedItemProperty());
    }

    public void setEditedCalendar(LCCalendar editedCalendar) {
        this.editedCalendar = editedCalendar;
        if (this.editedCalendar == null) {
            listViewDayEvents.setItems(null);
            toggleGroupDays.selectToggle(null);
        } else {
            toggleGroupDays.getToggles().stream().filter(d -> d.getUserData() == DayOfWeek.current()).findAny().ifPresent(toggleGroupDays::selectToggle);
        }
    }
}
