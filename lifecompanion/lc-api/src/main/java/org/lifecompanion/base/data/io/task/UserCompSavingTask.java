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

import org.lifecompanion.api.component.definition.usercomp.UserCompDescriptionI;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.io.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Saving task for {@link UserCompDescriptionI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserCompSavingTask extends AbstractSavingUtilsTask<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCompSavingTask.class);

    private final File directory;
    private final UserCompDescriptionI userComponent;

    public UserCompSavingTask(final File directory, final UserCompDescriptionI comp) {
        super("task.save.user.comp.title");
        this.directory = directory;
        this.userComponent = comp;
    }

    @Override
    protected Void call() throws Exception {
        this.directory.mkdirs();
        //First : save the description into a XML
        File descriptionXmlFile = new File(this.directory.getPath() + File.separator + LCConstant.USER_COMP_DESCRIPTION_XML_NAME);
        UserCompSavingTask.LOGGER.info("User component description will be saved to {}", descriptionXmlFile);
        XMLHelper.writeXml(descriptionXmlFile, this.userComponent.serialize(this.directory));
        //Save the component into XML (if the component is loaded)
        if (this.userComponent.getUserComponent().isLoaded()) {
            UserCompSavingTask.LOGGER.info("User component displayable component is loaded, will save it to {}", LCConstant.USER_COMP_XML_NAME);
            this.saveXmlSerializable(this.userComponent.getUserComponent(), this.directory, LCConstant.USER_COMP_XML_NAME);
            this.userComponent.getUserComponent().unloadComponent();
        }
        return null;
    }

}
