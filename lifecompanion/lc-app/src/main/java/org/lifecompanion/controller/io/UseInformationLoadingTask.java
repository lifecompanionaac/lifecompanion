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

import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.util.LCTask;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Task to load configuration use information from a config directory.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseInformationLoadingTask extends LCTask<Void> {
    private final static Logger LOGGER = LoggerFactory.getLogger(UseInformationLoadingTask.class);

    private File directory;
    private LCConfigurationI configuration;

    public UseInformationLoadingTask(final File directoryP, final LCConfigurationI configurationP) {
        super("task.load.use.information.title");
        this.directory = directoryP;
        this.configuration = configurationP;
    }

    @Override
    protected Void call() throws Exception {
        this.updateProgress(-1, 1);

        File useInfoXmlPath = new File(this.directory.getPath() + File.separator + LCConstant.CONFIGURATION_USE_INFO_XML_NAME);
        if (useInfoXmlPath.exists()) {
            try {
                Element rootElement = XMLHelper.readXml(useInfoXmlPath);
                //Load each info children
                List<Element> informationChildren = rootElement.getChildren();
                Map<String, Element> useInformations = new HashMap<>();
                for (Element infoChild : informationChildren) {
                    String id = XMLUtils.readString(IOContextI.ATB_ID, infoChild);
                    Element infoElement = infoChild.getChildren().size() != 0 ? infoChild.getChildren().get(0) : null;
                    if (infoElement != null) {
                        useInformations.put(id, infoElement);
                    }
                }
                this.configuration.deserializeUseInformation(useInformations);
                UseInformationLoadingTask.LOGGER.info("Deserialized {} use informations", useInformations.size());
            } catch (Exception e) {
                UseInformationLoadingTask.LOGGER.warn("Couldn't load the configuration use information from {}", useInfoXmlPath, e);
            }
        } else {
            LOGGER.info("Not use information for configuration : {} (file {})", configuration.getID(), useInfoXmlPath.getPath());
        }

        //Ok
        this.updateProgress(1, 1);
        return null;
    }
}
