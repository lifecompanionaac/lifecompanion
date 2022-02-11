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
package org.lifecompanion.model.impl.profile;

import org.jdom2.Element;
import org.lifecompanion.model.api.profile.LcTechInfoI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.base.data.control.update.InstallationController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LcTechInfo implements LcTechInfoI {
    private static final Logger LOGGER = LoggerFactory.getLogger(LcTechInfo.class);

    private String version;
    private String systemType;

    public LcTechInfo() {
    }

    @Override
    public void updateInformation() {
        this.version = InstallationController.INSTANCE.getBuildProperties().getVersionLabel();
        this.systemType = SystemType.current().getCode();
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getSystemTypeCode() {
        return this.systemType;
    }

    public static final String NODE_TECH_INFO = "TechInfo";

    @Override
    public Element serialize(final File context) {
        Element elem = new Element(LcTechInfo.NODE_TECH_INFO);
        XMLObjectSerializer.serializeInto(LcTechInfo.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element node, final File context) throws LCException {
        XMLObjectSerializer.deserializeInto(LcTechInfo.class, this, node);
        // Backward compatibility : previous version number were just integer > set the version to current if it is a previously saved configuration
        try {
            Integer.parseInt(version);
            LOGGER.warn("Previously created configuration detected, set the tech info version to current version");
            version = InstallationController.INSTANCE.getBuildProperties().getVersionLabel();
        } catch (NumberFormatException nfe) {
        }
    }

}
