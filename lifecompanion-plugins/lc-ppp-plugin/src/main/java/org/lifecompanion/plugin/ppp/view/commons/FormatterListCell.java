package org.lifecompanion.plugin.ppp.view.commons;

import javafx.scene.control.ListCell;

import java.util.function.Function;
import java.util.function.Supplier;

public class FormatterListCell<T> extends ListCell<T> {
    private final Function<T, String> formatter;

    private Supplier<String> nullFormatter;

    public FormatterListCell(Function<T, String> formatter) {
        super();
        this.formatter = formatter;
    }

    public FormatterListCell(Function<T, String> formatter, Supplier<String> nullFormatter) {
        this(formatter);

        this.nullFormatter = nullFormatter;
    }

    @Override
    protected void updateItem(final T item, final boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            this.setText(this.nullFormatter != null ? this.nullFormatter.get() : null);
        } else {
            this.setText(this.formatter.apply(item));
        }
    }
}
