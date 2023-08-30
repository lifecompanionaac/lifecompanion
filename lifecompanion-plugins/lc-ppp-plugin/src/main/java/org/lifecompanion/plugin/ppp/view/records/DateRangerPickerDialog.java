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

package org.lifecompanion.plugin.ppp.view.records;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.plugin.ppp.view.commons.FormatterListCell;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateRangerPickerDialog extends Dialog<Pair<LocalDate, LocalDate>> implements LCViewInitHelper {
    private ComboBox<QuickRange> comboBoxRanges;
    private DatePicker datePickerFrom, datePickerTo;
    private Label labelErrorMessage;

    public DateRangerPickerDialog() {
        initAll();
    }

    @Override
    public void initUI() {
        this.setTitle(LCConstant.NAME);
        this.initStyle(StageStyle.UTILITY);

        int rowIndex = 0;
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10.0);
        gridPane.setVgap(10.0);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(new Label(Translation.getText("ppp.plugin.pdf.dialog.description")), 0, rowIndex++, 2, 1);

        LocalDate startOfPreviousWeek = LocalDate.now().with(DayOfWeek.MONDAY).minusDays(7);
        QuickRange currentWeek = new QuickRange(Translation.getText("ppp.plugin.pdf.dialog.ranges.currentWeek"), new Pair<>(startOfPreviousWeek.plusDays(7), LocalDate.now().plusDays(1)));
        QuickRange lastWeek = new QuickRange(Translation.getText("ppp.plugin.pdf.dialog.ranges.lastWeek"), new Pair<>(startOfPreviousWeek, startOfPreviousWeek.plusDays(7)));
        QuickRange last30Days = new QuickRange(Translation.getText("ppp.plugin.pdf.dialog.ranges.last30Days"), new Pair<>(LocalDate.now().minusDays(30), LocalDate.now().plusDays(1)));
        ObservableList<QuickRange> ranges = FXCollections.observableArrayList(currentWeek, lastWeek, last30Days);
        gridPane.add(this.comboBoxRanges = new ComboBox<>(ranges), 0, rowIndex++, 2, 1);
        this.comboBoxRanges.setButtonCell(new FormatterListCell<>(QuickRange::getName));
        this.comboBoxRanges.setCellFactory((lv) -> new FormatterListCell<>(QuickRange::getName));
        this.comboBoxRanges.getSelectionModel().select(currentWeek);
        GridPane.setHgrow(this.comboBoxRanges, Priority.ALWAYS);
        this.comboBoxRanges.setMaxWidth(Double.MAX_VALUE);

        gridPane.add(new Label(Translation.getText("ppp.plugin.pdf.dialog.from")), 0, rowIndex);
        gridPane.add(datePickerFrom = new DatePicker(currentWeek.getRange().getKey()), 1, rowIndex++);
        GridPane.setHgrow(this.datePickerFrom, Priority.ALWAYS);
        this.datePickerFrom.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(new Label(Translation.getText("ppp.plugin.pdf.dialog.to")), 0, rowIndex);
        gridPane.add(datePickerTo = new DatePicker(currentWeek.getRange().getValue()), 1, rowIndex++);
        GridPane.setHgrow(this.datePickerTo, Priority.ALWAYS);
        this.datePickerTo.setMaxWidth(Double.MAX_VALUE);

        gridPane.add(labelErrorMessage = new Label(Translation.getText("ppp.plugin.pdf.dialog.error")), 1, rowIndex++, 2, 1);
        labelErrorMessage.setVisible(false);
        labelErrorMessage.setStyle("-fx-text-fill: firebrick;");

        // Dialog content
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        this.getDialogPane().setContent(gridPane);
        this.getDialogPane().getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? new Pair<>(datePickerFrom.getValue(), datePickerTo.getValue()) : null);
    }

    @Override
    public void initListener() {
        this.initDatePickerTextFieldListener(this.datePickerFrom);
        this.initDatePickerTextFieldListener(this.datePickerTo);

        this.comboBoxRanges.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            this.datePickerFrom.setValue(nv.getRange().getKey());
            this.datePickerTo.setValue(nv.getRange().getValue());
        });

        this.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> {
            if (!validateDates())
                event.consume();
        });
    }

    private boolean validateDates() {
        if (datePickerFrom.getValue() == null || datePickerTo.getValue() == null || datePickerTo.getValue().isBefore(datePickerFrom.getValue())) {
            labelErrorMessage.setVisible(true);
            return false;
        }
        return true;
    }

    private void initDatePickerTextFieldListener(DatePicker datePicker) {
        datePicker.getEditor().textProperty().addListener((obs, ov, nv) -> {
            try {
                datePicker.setValue(datePicker.getConverter().fromString(nv));
            } catch (Exception exception) {
                // Ignore parsing exception.
            }
        });
    }

    private static class QuickRange {
        private final String name;
        private final Pair<LocalDate, LocalDate> range;

        public QuickRange(String name, Pair<LocalDate, LocalDate> range) {
            this.name = name;
            this.range = range;
        }

        public String getName() {
            return name;
        }

        public Pair<LocalDate, LocalDate> getRange() {
            return range;
        }
    }
}
