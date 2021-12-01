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

package org.lifecompanion.framework.utils;

public class Triple<K, T, V> {
    private final K left;
    private final T middle;
    private final V right;

    private Triple(K left, T middle, V right) {
        super();
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public K getLeft() {
        return left;
    }

    public T getMiddle() {
        return middle;
    }

    public V getRight() {
        return right;
    }

    public static <K, T, V> Triple<K, T, V> of(K left, T middle, V right) {
        return new Triple<>(left, middle, right);
    }

    @Override
    public String toString() {
        return "(" + left + ", " + middle + ", " + right + ")";
    }
}
