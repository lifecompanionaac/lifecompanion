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
package org.lifecompanion.model.impl.io;

import org.jdom2.Element;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOResourceI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.io.File;

/**
 * Implementation for resource.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class IOResource implements IOResourceI, XMLSerializable<Void> {
    private String name;
    private String id;
    private long fileLength;

    private transient File path;

    private IOResource(final String idP) {
        this.id = idP != null ? idP : StringUtils.getNewID();
    }

    public IOResource() {
        this(null);
    }

    /**
     * Constructor with resource informations (use on saving)
     *
     * @param idP  resource id
     * @param name the resource name
     * @param path file path
     */
    public IOResource(final String idP, final String name, final File path) {
        this(idP);
        this.name = name;
        this.setPath(path);
        //On first id definition, use the file extension
        if (path != null && idP == null) {
            String ext = FileNameUtils.getExtension(path);
            if (ext != null && !ext.isEmpty()) {
                this.id = this.id + "." + ext;
            }
        }
    }

    public void setPath(final File path) {
        this.path = path;
        if (path != null) {
            this.fileLength = path.length();
        } else {
            this.fileLength = 0;
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public File getPath() {
        return this.path;
    }

    @Override
    public long getFileLength() {
        return this.fileLength;
    }

    private static final String NODE_RESOURCE = "IOResource";

    @Override
    public Element serialize(final Void context) {
        Element node = new Element(IOResource.NODE_RESOURCE);
        XMLObjectSerializer.serializeInto(IOResource.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element node, final Void context) throws LCException {
        XMLObjectSerializer.deserializeInto(IOResource.class, this, node);
    }

}
