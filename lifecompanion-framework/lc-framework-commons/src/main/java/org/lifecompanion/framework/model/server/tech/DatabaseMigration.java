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

package org.lifecompanion.framework.model.server.tech;

import javax.persistence.Column;
import java.util.Date;

public class DatabaseMigration {
    private String id;

    @Column(name = "script_name")
    private String scriptName;

    @Column(name = "script_date")
    private Date scriptDate;

    public DatabaseMigration() {
    }

    public DatabaseMigration(String id, String scriptName, Date scriptDate) {
        this.id = id;
        this.scriptName = scriptName;
        this.scriptDate = scriptDate;
    }

    public String getId() {
        return id;
    }

    public String getScriptName() {
        return scriptName;
    }

    public Date getScriptDate() {
        return scriptDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public void setScriptDate(Date scriptDate) {
        this.scriptDate = scriptDate;
    }
}
