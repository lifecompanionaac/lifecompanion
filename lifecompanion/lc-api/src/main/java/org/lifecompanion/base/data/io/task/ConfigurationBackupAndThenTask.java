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
package org.lifecompanion.base.data.io.task;

import org.lifecompanion.api.component.definition.LCConfigurationDescriptionI;
import org.lifecompanion.framework.commons.translation.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class ConfigurationBackupAndThenTask extends ConfigurationExportTask {
    private final Runnable postBackupAction;

    public ConfigurationBackupAndThenTask(LCConfigurationDescriptionI configurationDescription, final File configDirectoryP, final File configDestFileP, Runnable postBackupAction) {
        super(configurationDescription, configDirectoryP, configDestFileP);
        this.postBackupAction = postBackupAction;
        updateTitle(Translation.getText("configuration.backup.task"));
    }

    @Override
    protected Void call() throws Exception {
        super.call();
        // TODO : should may be delete previous backup ?
        if (postBackupAction != null) {
            postBackupAction.run();
        }
        return null;
    }
}
