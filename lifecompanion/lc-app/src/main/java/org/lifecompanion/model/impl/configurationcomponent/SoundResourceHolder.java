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

package org.lifecompanion.model.impl.configurationcomponent;

import javafx.beans.property.*;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.model.api.configurationcomponent.SoundResourceHolderI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.IOResourceI;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

// FIXME : should have common class for sound data
public class SoundResourceHolder implements SoundResourceHolderI {
    private static final Logger LOGGER = LoggerFactory.getLogger(SoundResourceHolder.class);

    private final transient StringProperty fileName;
    private final transient ObjectProperty<File> filePath;
    private final IntegerProperty durationInSecond;
    private String soundResourceId;

    public SoundResourceHolder() {
        this.fileName = new SimpleStringProperty();
        this.filePath = new SimpleObjectProperty<>();
        this.durationInSecond = new SimpleIntegerProperty(this, "soundDurationInSecond", -1);
        this.filePath.addListener((obs, ov, nv) -> {
            //Bind file name
            if (nv != null) {
                this.fileName.set(nv.getName());
            } else {
                this.fileName.set(null);
            }
            //Reset resource ID if the file path changed
            if (this.soundResourceId != null && ov != null && nv != null && StringUtils.isDifferent(ov.getPath(), nv.getPath())) {
                this.soundResourceId = null;
                durationInSecond.set(-1);
            }
        });
    }

    @Override
    public ReadOnlyStringProperty fileNameProperty() {
        return fileName;
    }

    @Override
    public ReadOnlyIntegerProperty durationInSecondProperty() {
        return durationInSecond;
    }

    @Override
    public ReadOnlyObjectProperty<File> filePathProperty() {
        return filePath;
    }

    @Override
    public void updateSound(File path, Integer durationInSecond) {
        this.filePath.set(path);
        this.durationInSecond.set(durationInSecond != null ? durationInSecond : -1);
    }

    @Override
    public Element serializeIfNeeded(Element parent, IOContextI context) {
        final Element serialize = this.serialize(context);
        if (serialize != null) {
            parent.addContent(serialize);
        }
        return parent;
    }

    @Override
    public void deserializeIfNeeded(Element parent, IOContextI context, String retroCompatibilityResIdFieldName, String retroCompatibilityDurationFieldName) throws LCException {
        final Element child = parent.getChild(NODE_NAME);
        if (child != null) {
            this.deserialize(child, context);
        } else {
            deserializeFromPrevious(parent, context, retroCompatibilityResIdFieldName, retroCompatibilityDurationFieldName);
        }
    }

    private static final String NODE_NAME = "SoundResourceHolder";

    @Override
    public Element serialize(IOContextI context) {
        if (this.filePath.get() != null) {
            try {
                this.soundResourceId = context.addResourceToSave(this.soundResourceId, this.fileName.get(), this.filePath.get());
            } catch (IOException e) {
                LOGGER.warn("Couldn't save the sound resource file path is {}", this.filePath.get(), e);
            }
            return ConfigurationComponentIOHelper.addTypeAlias(this, XMLObjectSerializer.serializeInto(SoundResourceHolder.class, this, new Element(NODE_NAME)), context);
        } else
            return null;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        if (node != null) {
            XMLObjectSerializer.deserializeInto(SoundResourceHolder.class, this, node);
            //If there is a resource, try to set it
            if (this.soundResourceId != null) {
                IOResourceI resource = context.getIOResource().get(this.soundResourceId);
                if (resource != null) {
                    this.filePath.set(resource.getPath());
                    this.fileName.set(resource.getName());
                }
            }
        }
    }

    // RETRO COMPATIBILITY WITH PREVIOUS CONFIG VERSION
    //========================================================================
    private void deserializeFromPrevious(Element node, IOContextI context, String retroCompatibilityResIdFieldName, String retroCompatibilityDurationFieldName) {
        if (StringUtils.isNotBlank(retroCompatibilityResIdFieldName)) {
            final String resId = node.getAttributeValue(retroCompatibilityResIdFieldName);
            if (resId != null) {
                IOResourceI resource = context.getIOResource().get(resId);
                if (resource != null) {
                    this.soundResourceId = resId;
                    this.filePath.set(resource.getPath());
                    this.fileName.set(resource.getName());
                    if (StringUtils.isNotBlank(retroCompatibilityDurationFieldName)) {
                        final String durVal = node.getAttributeValue(retroCompatibilityDurationFieldName);
                        if (durVal != null) {
                            try {
                                durationInSecond.set(Integer.parseInt(durVal));
                            } catch (NumberFormatException e) {
                                // Ignore
                            }
                        }
                    }
                }
            }
        }
    }
    //========================================================================
}
