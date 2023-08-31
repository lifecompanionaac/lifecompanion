package org.lifecompanion.ui.common.control.generic;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.util.binding.ListBindingWithMapper;

import java.util.function.Consumer;
import java.util.function.Function;

public class FlowPaneListView<T> extends FlowPane {
    private Runnable previousContentViewUnbind;
    private final Function<FlowPaneListView<T>, FlowPaneListCell<T>> cellSupplier;
    private Consumer<T> selectionListener;
    private Consumer<T> doubleSelectionListener;

    public FlowPaneListView(Function<FlowPaneListView<T>, FlowPaneListCell<T>> cellSupplier) {
        this.cellSupplier = cellSupplier;
        this.getChildren().addListener(BindingUtils.createListChangeListenerV2(added -> {
        }, removed -> {
            if (removed instanceof FlowPaneListCell) {
                ((FlowPaneListCell<?>) removed).item.set(null);
            }
        }));
    }

    public void setSelectionListener(Consumer<T> selectionListener) {
        this.selectionListener = selectionListener;
    }

    public void setDoubleSelectionListener(Consumer<T> doubleSelectionListener) {
        this.doubleSelectionListener = doubleSelectionListener;
    }

    public void select(T item) {
        this.getChildren().stream().filter(node -> node instanceof FlowPaneListCell).map(node -> (FlowPaneListCell<?>) node).filter(cell -> cell.item.get() == item).forEach(cell -> cell.selected.set(true));
        clearSelectionForNot(item);
    }

    private void clearSelectionForNot(T item) {
        this.getChildren().stream().filter(node -> node instanceof FlowPaneListCell).map(node -> (FlowPaneListCell<?>) node).filter(cell -> cell.item.get() != item).forEach(cell -> cell.selected.set(false));
    }

    public void bindItems(ObservableList<T> items) {
        if (previousContentViewUnbind != null) {
            previousContentViewUnbind.run();
        }
        this.getChildren().clear();
        if (items != null) {
            previousContentViewUnbind = ListBindingWithMapper.mapContent(this.getChildren(), items, item -> {
                FlowPaneListCell<T> flowPaneListView = this.cellSupplier.apply(this);
                flowPaneListView.item.set(item);
                return flowPaneListView;
            });
        }
    }

    public abstract static class FlowPaneListCell<T> extends Pane {
        private final BooleanProperty selected;
        private final ObjectProperty<T> item;

        protected FlowPaneListCell(FlowPaneListView<T> flowPaneListView) {
            selected = new SimpleBooleanProperty();
            this.item = new SimpleObjectProperty<>();
            item.addListener((obs, ov, nv) -> {
                if (ov != null) {
                    unbindItem(ov);
                }
                if (nv != null) {
                    bindItem(nv);
                }
            });
            this.getStyleClass().addAll("opacity-80-hover", "opacity-60-pressed");
            this.setOnMouseClicked(e -> {
                if (e.getClickCount() >= 2) {
                    if (flowPaneListView.doubleSelectionListener != null) flowPaneListView.doubleSelectionListener.accept(item.get());
                } else {
                    if (selected.get()) selected.set(false);
                    else selected.set(true);
                    flowPaneListView.clearSelectionForNot(item.get());
                    if (flowPaneListView.selectionListener != null) flowPaneListView.selectionListener.accept(item.get());
                }
            });
        }

        protected ReadOnlyBooleanProperty selectedProperty() {
            return selected;
        }

        protected abstract void bindItem(T item);

        protected abstract void unbindItem(T item);
    }
}
