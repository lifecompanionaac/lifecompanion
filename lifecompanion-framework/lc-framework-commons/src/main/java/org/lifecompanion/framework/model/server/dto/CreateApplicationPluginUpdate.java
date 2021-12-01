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

package org.lifecompanion.framework.model.server.dto;

import org.lifecompanion.framework.model.server.update.ApplicationPlugin;
import org.lifecompanion.framework.model.server.update.ApplicationPluginUpdate;

public class CreateApplicationPluginUpdate {
    private ApplicationPlugin applicationPlugin;
    private ApplicationPluginUpdate applicationPluginUpdate;

    public CreateApplicationPluginUpdate(ApplicationPlugin applicationPlugin, ApplicationPluginUpdate applicationPluginUpdate) {
        this.applicationPlugin = applicationPlugin;
        this.applicationPluginUpdate = applicationPluginUpdate;
    }

    public CreateApplicationPluginUpdate() {
    }

    public ApplicationPlugin getApplicationPlugin() {
        return applicationPlugin;
    }

    public void setApplicationPlugin(ApplicationPlugin applicationPlugin) {
        this.applicationPlugin = applicationPlugin;
    }

    public ApplicationPluginUpdate getApplicationPluginUpdate() {
        return applicationPluginUpdate;
    }

    public void setApplicationPluginUpdate(ApplicationPluginUpdate applicationPluginUpdate) {
        this.applicationPluginUpdate = applicationPluginUpdate;
    }
}
