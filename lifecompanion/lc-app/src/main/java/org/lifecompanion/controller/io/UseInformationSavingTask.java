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

package org.lifecompanion.controller.io;

import javafx.concurrent.Task;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;
import org.lifecompanion.framework.commons.translation.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Task to save configuration use info in a directory.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseInformationSavingTask extends Task<Void> {
    private final static Logger LOGGER = LoggerFactory.getLogger(UseInformationSavingTask.class);

    private final File directory;
    private final LCConfigurationI configuration;

    public UseInformationSavingTask(final File directoryP, final LCConfigurationI configurationP) {
        this.directory = directoryP;
        this.configuration = configurationP;
        this.updateTitle(Translation.getText("task.save.use.information.title"));
        this.updateProgress(0, 1);
    }

    @Override
    protected Void call() throws Exception {
        this.updateProgress(-1, 1);
        //Create the context, or use existing if there is one

        Map<String, Element> useInformationElements = new HashMap<>();
        this.configuration.serializeUseInformation(useInformationElements);

        //Create configuration directory when needed
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }
        //Save the informations to the xml
        File xmlFilePath = new File(this.directory.getPath() + File.separator + LCConstant.CONFIGURATION_USE_INFO_XML_NAME);
        UseInformationSavingTask.LOGGER.info("Will save {} use information to the file {}", useInformationElements.size(), xmlFilePath);
        //Create xml
        Element xmlRoot = new Element(IOContextI.NODE_USE_INFOS);
        Set<String> ids = useInformationElements.keySet();
        for (String id : ids) {
            Element infoElement = new Element(IOContextI.NODE_USE_INFO);
            XMLUtils.write(id, IOContextI.ATB_ID, infoElement);
            infoElement.addContent(useInformationElements.get(id));
            xmlRoot.addContent(infoElement);
        }
        //Save it to file
        XMLHelper.writeXml(xmlFilePath, xmlRoot);
        return null;
    }
}
