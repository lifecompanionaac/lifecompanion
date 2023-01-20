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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.calendar.model.CalendarLeisure;
import org.lifecompanion.plugin.calendar.model.LCCalendar;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.CommonListViewActionContainer;
import org.lifecompanion.ui.common.pane.specific.cell.DetailledSimplerKeyContentContainerListCell;
import org.lifecompanion.util.javafx.FXControlUtils;


public class CalendarLeisureEditView extends BorderPane implements LCViewInitHelper {
    private CalendarLeisurePropertiesView calendarLeisurePropertiesView;
    private ListView<CalendarLeisure> listViewCalendarLeisure;
    private Button buttonAddEvent;
    private CommonListViewActionContainer<CalendarLeisure> commonListViewActionContainer;
    private LCCalendar editedCalendar;

    public CalendarLeisureEditView() {
        this.initAll();
    }

    @Override
    public void initUI() {
        calendarLeisurePropertiesView = new CalendarLeisurePropertiesView();

        // Left list view and add button
        this.buttonAddEvent = FXControlUtils.createRightTextButton(Translation.getText("calendar.plugin.button.add.leisure"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        listViewCalendarLeisure = new ListView<>();
        this.commonListViewActionContainer = new CommonListViewActionContainer<>(listViewCalendarLeisure);
        listViewCalendarLeisure.setCellFactory(lv -> new DetailledSimplerKeyContentContainerListCell<>(commonListViewActionContainer));
        // FIXME
        listViewCalendarLeisure.setFixedCellSize(110.0);
        listViewCalendarLeisure.setPrefWidth(150.0);
        // END OF FIXME
        VBox.setVgrow(listViewCalendarLeisure, Priority.ALWAYS);
        VBox boxLeft = new VBox(10, listViewCalendarLeisure, buttonAddEvent);
        boxLeft.setAlignment(Pos.CENTER);

        BorderPane.setMargin(boxLeft, new Insets(GeneralConfigurationStepViewI.GRID_V_GAP));
        BorderPane.setMargin(listViewCalendarLeisure, new Insets(GeneralConfigurationStepViewI.GRID_V_GAP));

        this.setLeft(boxLeft);
        this.setCenter(calendarLeisurePropertiesView);
        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
    }

    @Override
    public void initListener() {
        this.buttonAddEvent.setOnAction(e -> {
            final CalendarLeisure added = new CalendarLeisure();
            listViewCalendarLeisure.getItems().add(added);
            listViewCalendarLeisure.getSelectionModel().select(added);
        });
        this.commonListViewActionContainer.setDuplicateFunction(item -> {
            CalendarLeisure duplicated = item.duplicate(true);
            duplicated.textProperty().set(Translation.getText("general.configuration.view.user.action.copy.label.key.text") + " " + duplicated.textProperty().get());
            return duplicated;
        });
    }

    @Override
    public void initBinding() {
        this.calendarLeisurePropertiesView.selectedNodeProperty().bind(listViewCalendarLeisure.getSelectionModel().selectedItemProperty());
    }

    public void setEditedCalendar(LCCalendar editedCalendar) {
        this.editedCalendar = editedCalendar;
        if (this.editedCalendar == null) {
            listViewCalendarLeisure.setItems(null);
        } else {
            listViewCalendarLeisure.setItems(editedCalendar.getAvailableLeisure());
        }
    }

    public LCCalendar getEditedCalendar() {
        return editedCalendar;
    }
}
