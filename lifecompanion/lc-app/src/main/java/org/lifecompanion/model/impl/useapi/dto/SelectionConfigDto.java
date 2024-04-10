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

import org.lifecompanion.model.impl.selectionmode.SelectionModeEnum;

public class SelectionConfigDto {
    private SelectionModeEnum mode;
    private Integer scanLoop;
    private Integer scanTime;
    private Boolean disableAutoStart;

    public SelectionConfigDto(SelectionModeEnum mode, Integer scanLoop, Integer scanTime, Boolean disableAutoStart) {
        this.mode = mode;
        this.scanLoop = scanLoop;
        this.scanTime = scanTime;
        this.disableAutoStart = disableAutoStart;
    }

    public SelectionConfigDto(Integer scanLoop, Integer scanTime) {
        this(null, scanLoop, scanTime, true);
    }

    public SelectionConfigDto(SelectionModeEnum mode) {
        this(mode, null, null, null);
    }

    public SelectionModeEnum getMode() {
        return mode;
    }

    public void setMode(SelectionModeEnum mode) {
        this.mode = mode;
    }

    public Integer getScanLoop() {
        return scanLoop;
    }

    public void setScanLoop(Integer scanLoop) {
        this.scanLoop = scanLoop;
    }

    public Integer getScanTime() {
        return scanTime;
    }

    public void setScanTime(Integer scanTime) {
        this.scanTime = scanTime;
    }

    public Boolean getDisableAutoStart() {
        return disableAutoStart;
    }

    public void setDisableAutoStart(Boolean disableAutoStart) {
        this.disableAutoStart = disableAutoStart;
    }
}
