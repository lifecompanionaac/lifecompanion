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

public class ShowIndicationRandomTargetDto {
    private String color;
    private Double strokeSize;

    public ShowIndicationRandomTargetDto(String color, Double strokeSize) {
        this.color = color;
        this.strokeSize = strokeSize;
    }

    public String getColor() {
        return color;
    }

    public Double getStrokeSize() {
        return strokeSize;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setStrokeSize(Double strokeSize) {
        this.strokeSize = strokeSize;
    }
}
