package org.lifecompanion.plugin.ppp.view.records;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.plugin.ppp.view.commons.TimePicker;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DatePickerDialog extends Dialog<ZonedDateTime> implements LCViewInitHelper {
    private final ZonedDateTime initialValue;
    private DatePicker datePicker;
    private Label labelErrorMessage;
    private TimePicker timePicker;

    public DatePickerDialog(ZonedDateTime initialValue) {
        this.initialValue = initialValue;
        initAll();
    }


    @Override
    public void initUI() {
        this.setTitle(LCConstant.NAME);
        this.initStyle(StageStyle.UTILITY);

        this.setHeaderText(Translation.getText("ppp.plugin.view.date.time.picker.dialog.header"));

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10.0);
        gridPane.setVgap(10.0);
        gridPane.setAlignment(Pos.CENTER);
        int rowIndex = 0;
        gridPane.add(new Label(Translation.getText("ppp.plugin.view.date.time.picker.date.value")), 0, rowIndex);
        gridPane.add(datePicker = new DatePicker(initialValue.toLocalDate()), 1, rowIndex++);
        gridPane.add(new Label(Translation.getText("ppp.plugin.view.date.time.picker.time.value")), 0, rowIndex);
        gridPane.add(timePicker = new TimePicker(initialValue.toLocalTime()), 1, rowIndex++);

        gridPane.add(labelErrorMessage = new Label(Translation.getText("ppp.plugin.view.date.time.picker.error")), 0, rowIndex++, 2, 1);
        labelErrorMessage.setVisible(false);
        labelErrorMessage.setStyle("-fx-text-fill: firebrick;");

        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        this.getDialogPane().setContent(gridPane);
        this.getDialogPane().getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? datePicker.getValue().atTime(timePicker.valueProperty().get()).atZone(ZoneId.systemDefault()) : null);
    }

    @Override
    public void initListener() {
        this.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> {
            if (!validate())
                event.consume();
        });
    }

    private boolean validate() {
        if (datePicker.getValue() == null || timePicker.valueProperty().get() == null) {
            labelErrorMessage.setVisible(true);
            return false;
        }
        return true;
    }

    @Override
    public void initBinding() {
        datePicker.getEditor().textProperty().addListener((obs, ov, nv) -> {
            try {
                datePicker.setValue(datePicker.getConverter().fromString(nv));
            } catch (Exception exception) {
                // Ignore parsing exception.
            }
        });
    }
}
