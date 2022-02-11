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
import org.lifecompanion.model.api.profile.ChangelogEntryI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;

/**
 * Implementation of {@link ChangelogEntryI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ChangelogEntry implements ChangelogEntryI {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChangelogEntry.class);

    private String systemUserName, profileName, changeDescription, appVersion;
    private int modificationCount;
    private Date when;

    @XMLGenericProperty(SystemType.class)
    private SystemType systemType;

    public ChangelogEntry() {
    }

    public ChangelogEntry(String systemUserName, String profileName, String changeDescription, String appVersion, int modificationCount, Date when, SystemType systemType) {
        this.systemUserName = systemUserName;
        this.profileName = profileName;
        this.changeDescription = changeDescription;
        this.appVersion = appVersion;
        this.modificationCount = modificationCount;
        this.when = when;
        this.systemType = systemType;
    }

    @Override
    public String getSystemUserName() {
        return systemUserName;
    }

    @Override
    public Date getWhen() {
        return when;
    }

    @Override
    public String getProfileName() {
        return profileName;
    }

    @Override
    public int getModificationCount() {
        return modificationCount;
    }

    @Override
    public SystemType getSystem() {
        return systemType;
    }

    @Override
    public String getChangeDescription() {
        return changeDescription;
    }

    @Override
    public String getAppVersion() {
        return appVersion;
    }

    // IO
    //========================================================================
    private static final String NODE_CHANGELOG_ENTRY = "ChangelogEntry";

    @Override
    public Element serialize(File context) {
        Element element = new Element(NODE_CHANGELOG_ENTRY);
        XMLObjectSerializer.serializeInto(ChangelogEntry.class, this, element);
        return element;
    }

    @Override
    public void deserialize(Element node, File context) throws LCException {
        XMLObjectSerializer.deserializeInto(ChangelogEntry.class, this, node);
    }
    //========================================================================
}
