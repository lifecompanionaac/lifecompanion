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

package org.lifecompanion.controller.appinstallation.task;

import org.lifecompanion.model.impl.appinstallation.OptionalResourceEnum;
import org.lifecompanion.util.model.LCTask;

public class UninstallOptionalResourceTask extends LCTask<Void> {

    private final OptionalResourceEnum optionalResource;

    public UninstallOptionalResourceTask(OptionalResourceEnum optionalResource) {
        super("makaton.task.uninstall.resource");
        this.optionalResource = optionalResource;
    }

    @Override
    protected Void call() throws Exception {
        this.optionalResource.getResource().uninstall();
        return null;
    }
}
