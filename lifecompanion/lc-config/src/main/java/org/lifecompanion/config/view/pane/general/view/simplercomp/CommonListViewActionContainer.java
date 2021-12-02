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

package org.lifecompanion.config.view.pane.general.view.simplercomp;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

public class CommonListViewActionContainer<T> {

    private final ListView<T> listView;
    private Function<T, T> duplicateFunction;
    private BiFunction<T, T, Boolean> dragEndAcceptor;
    private Predicate<T> dragEndPriorTester;
    private BiConsumer<Integer, T> dragFinishedConsumer;
    private Consumer<T> doubleClicConsumer;

    public CommonListViewActionContainer(ListView<T> listView) {
        this.listView = listView;
    }

    // SETTERS
    //========================================================================
    public void setDuplicateFunction(Function<T, T> duplicateFunction) {
        this.duplicateFunction = duplicateFunction;
    }

    public void setDragEndAcceptor(BiFunction<T, T, Boolean> dragEndAcceptor) {
        this.dragEndAcceptor = dragEndAcceptor;
    }

    public void setDragEndPriorTester(Predicate<T> dragEndPriorTester) {
        this.dragEndPriorTester = dragEndPriorTester;
    }

    public void setDragFinishedConsumer(BiConsumer<Integer, T> dragFinishedConsumer) {
        this.dragFinishedConsumer = dragFinishedConsumer;
    }

    public void setDoubleClicConsumer(Consumer<T> doubleClicConsumer) {
        this.doubleClicConsumer = doubleClicConsumer;
    }
    //========================================================================

    public void addAndScrollTo(T item) {
        listView.getItems().add(item);
        selectAndScrollTo(item);
    }

    public void selectAndScrollTo(T item) {
        listView.getSelectionModel().clearSelection();
        listView.getSelectionModel().select(item);
        listView.scrollTo(item);
    }

    public void deleteItem(T item) {
        if (item != null) {
            listView.getItems().remove(item);
        }
    }

    public void moveUp(T item) {
        if (item != null) {
            final ObservableList<T> items = listView.getItems();
            int index = items.indexOf(item);
            if (index > 0) {
                Collections.swap(items, index, index - 1);
                selectAndScrollTo(item);
            }
        }
    }


    public void moveDown(T item) {
        if (item != null) {
            final ObservableList<T> items = listView.getItems();
            int index = items.indexOf(item);
            if (index < items.size() - 1) {
                Collections.swap(items, index, index + 1);
                selectAndScrollTo(item);
            }
        }
    }

    public void duplicate(T item) {
        if (item != null) {
            T duplicated = duplicateFunction.apply(item);
            final ObservableList<T> items = listView.getItems();
            int index = items.indexOf(item);
            listView.getItems().add(index, duplicated);
            selectAndScrollTo(duplicated);
        }
    }

    public void doubleClicOn(T item) {
        if (doubleClicConsumer != null) {
            doubleClicConsumer.accept(item);
        }
    }

    // DRAG AND DROP
    //========================================================================
    private List<T> draggedNodes;

    public void dragStart(T dragSource) {
        this.draggedNodes = new ArrayList<>(listView.getSelectionModel().getSelectedItems());
        if (!draggedNodes.contains(dragSource)) {
            draggedNodes.add(dragSource);
        }
    }


    public void dragEnd(T dragDestination) {
        if (dragEndPriorTester.test(dragDestination)) {
            int movedCount = 0;
            for (T draggedNode : draggedNodes) {
                if (dragEndAcceptor.apply(dragDestination, draggedNode)) movedCount++;
            }
            if (movedCount > 0)
                this.dragFinishedConsumer.accept(movedCount, dragDestination);
        }
        draggedNodes = null;
    }

    boolean isDraggedNodes() {
        return draggedNodes != null;
    }
}
