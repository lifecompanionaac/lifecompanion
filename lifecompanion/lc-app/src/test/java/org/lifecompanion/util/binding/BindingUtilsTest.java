/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.util.binding;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class BindingUtilsTest {
    @Test
    public void testSwap() {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 3, 5, 6);
        List<Integer> added = new ArrayList<>();
        List<Integer> removed = new ArrayList<>();
        list.addListener(BindingUtils.createUniqueAddOrRemoveListener(list, added::add, removed::add));
        Collections.swap(list, 1, 2);
        assertIterableEquals(removed, added);
    }

    @Test
    public void testSort() {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 3, 5, 6);
        List<Integer> added = new ArrayList<>();
        List<Integer> removed = new ArrayList<>();
        list.addListener(BindingUtils.createUniqueAddOrRemoveListener(list, added::add, removed::add));
        Collections.sort(list);
        assertIterableEquals(removed, added);
    }

    @Test
    public void testAddNonExisting() {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 3, 5, 6);
        List<Integer> added = new ArrayList<>();
        List<Integer> removed = new ArrayList<>();
        list.addListener(BindingUtils.createUniqueAddOrRemoveListener(list, added::add, removed::add));
        list.add(2);
        assertIterableEquals(List.of(2), added);
        assertIterableEquals(List.of(), removed);
    }

    @Test
    public void testAddExisting() {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 3, 5, 6);
        List<Integer> added = new ArrayList<>();
        List<Integer> removed = new ArrayList<>();
        list.addListener(BindingUtils.createUniqueAddOrRemoveListener(list, added::add, removed::add));
        list.add(3);
        assertIterableEquals(List.of(), added);
        assertIterableEquals(List.of(), removed);
    }

    @Test
    public void testRemoveTotal() {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 3, 5, 6);
        List<Integer> added = new ArrayList<>();
        List<Integer> removed = new ArrayList<>();
        list.addListener(BindingUtils.createUniqueAddOrRemoveListener(list, added::add, removed::add));
        list.remove(1);// remove the 3
        assertIterableEquals(List.of(), added);
        assertIterableEquals(List.of(3), removed);
    }

    @Test
    public void testRemoveStillInList() {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 3, 3, 5, 6);
        List<Integer> added = new ArrayList<>();
        List<Integer> removed = new ArrayList<>();
        list.addListener(BindingUtils.createUniqueAddOrRemoveListener(list, added::add, removed::add));
        list.remove(1);// remove the 3 (still one in list)
        assertIterableEquals(List.of(), added);
        assertIterableEquals(List.of(), removed);
    }

    @Test
    public void testClear() {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 3, 5, 6);
        List<Integer> added = new ArrayList<>();
        List<Integer> removed = new ArrayList<>();
        list.addListener(BindingUtils.createUniqueAddOrRemoveListener(list, added::add, removed::add));
        list.clear();
        assertIterableEquals(List.of(1, 3, 5, 6), removed);
        assertIterableEquals(List.of(), added);
    }

    @Test
    public void testSetAll() {
        ObservableList<Integer> list = FXCollections.observableArrayList(1, 3, 5, 6);
        List<Integer> added = new ArrayList<>();
        List<Integer> removed = new ArrayList<>();
        list.addListener(BindingUtils.createUniqueAddOrRemoveListener(list, added::add, removed::add));
        list.setAll(3, 6, 9, 10);
        assertIterableEquals(List.of(1, 5), removed);
        assertIterableEquals(List.of(9, 10), added);
    }
}
