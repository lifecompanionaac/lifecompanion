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

package org.lifecompanion.model.impl.useapi.dto;

public class GridDto {
    private String name;
    private String id;
    private int rowCount, columnCount;
    private int firstKeyCenterX, firstKeyCenterY;
    private int keysCenterXSpacing, keysCenterYSpacing;

    public GridDto(String name, String id, int rowCount, int columnCount, int firstKeyCenterX, int firstKeyCenterY, int keysCenterXSpacing, int keysCenterYSpacing) {
        this.name = name;
        this.id = id;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.firstKeyCenterX = firstKeyCenterX;
        this.firstKeyCenterY = firstKeyCenterY;
        this.keysCenterXSpacing = keysCenterXSpacing;
        this.keysCenterYSpacing = keysCenterYSpacing;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getFirstKeyCenterX() {
        return firstKeyCenterX;
    }

    public int getFirstKeyCenterY() {
        return firstKeyCenterY;
    }

    public int getKeysCenterXSpacing() {
        return keysCenterXSpacing;
    }

    public int getKeysCenterYSpacing() {
        return keysCenterYSpacing;
    }
}
