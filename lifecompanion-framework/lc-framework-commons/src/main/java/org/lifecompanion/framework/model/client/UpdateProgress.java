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

package org.lifecompanion.framework.model.client;

import java.util.List;

public class UpdateProgress {
    private String from;
    private String to;
    private UpdateProgressType status;
    private List<UpdateFileProgress> files;

    public UpdateProgress(String from, String to, UpdateProgressType status, List<UpdateFileProgress> files) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.files = files;
    }

    public UpdateProgress() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public UpdateProgressType getStatus() {
        return status;
    }

    public void setStatus(UpdateProgressType status) {
        this.status = status;
    }

    public List<UpdateFileProgress> getFiles() {
        return files;
    }

    public void setFiles(List<UpdateFileProgress> files) {
        this.files = files;
    }
}
