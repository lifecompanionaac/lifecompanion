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

package org.lifecompanion.util.model;

public class Triple<T, V, U> {
    private final T left;
    private final V middle;
    private final U right;

    private Triple(T left, V middle, U right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public V getMiddle() {
        return middle;
    }

    public U getRight() {
        return right;
    }

    public static <T, V, U> Triple<T, V, U> of(T left, V middle, U right) {
        return new Triple<>(left, middle, right);
    }
}
