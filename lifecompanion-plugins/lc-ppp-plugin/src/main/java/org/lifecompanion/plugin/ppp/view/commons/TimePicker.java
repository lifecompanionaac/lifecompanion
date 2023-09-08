package org.lifecompanion.plugin.ppp.view.commons;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.time.DateTimeException;
import java.time.LocalTime;

public class TimePicker extends Pane {
    private final ObjectProperty<LocalTime> value;
    private final TextField fieldContent;

    public TimePicker(LocalTime localTime) {
        value = new SimpleObjectProperty<>();

        fieldContent = new TextField();
        fieldContent.setPromptText("HH:mm");
        value.addListener((obs, ov, nv) -> fieldContent.setText(toString(nv)));
        fieldContent.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                updateDateFromField();
            }
        });
        fieldContent.setOnAction(e -> updateDateFromField());

        value.set(localTime);

        getChildren().add(fieldContent);
    }

    private void updateDateFromField() {
        value.set(toLocalTime(fieldContent.getText()));
    }

    public ObjectProperty<LocalTime> valueProperty() {
        return value;
    }

    public String toString(LocalTime time) {
        return time != null ? ((time.getHour() < 10 ? "0" : "") + time.getHour() +
                (time.getMinute() < 10 ? ":0" : ":") + time.getMinute()) : null;
    }

    public LocalTime toLocalTime(String str) {
        String[] parts = StringUtils.trimToEmpty(str).split(":");
        if (parts.length == 2) {
            try {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                return LocalTime.of(hour, minute);
            } catch (NumberFormatException | DateTimeException e) {
                // Ignored and return null
            }
        }
        return null;
    }
}
