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

package org.lifecompanion.controller.io.task;

import org.lifecompanion.controller.io.XMLHelper;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.util.model.LCTask;
import org.lifecompanion.controller.plugin.PluginController;

import java.io.File;
import java.util.Set;


/**
 * Task to check plugin dependencies on a given Element to load.
 *
 * @author Mathieu THEBAUD
 */
public class CheckElementPluginTask extends LCTask<Pair<String, Set<String>>> {
    private final File xmlFile;

    public CheckElementPluginTask(final File xmlFile) {
        super("task.check.plugin.usage.title");
        this.xmlFile = xmlFile;
    }

    @Override
    protected Pair<String, Set<String>> call() throws Exception {
        return PluginController.INSTANCE.checkPluginDependencies(XMLHelper.readXml(xmlFile));
    }
}
