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

package org.lifecompanion.model.impl.configurationcomponent.dynamickey;

import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;

import java.io.File;

public class KeyListLocalFileNode extends KeyListLeaf implements DynamicLocalFileNodeI {
    private String targetPath;

    public KeyListLocalFileNode() {
        super();
    }

    @Override
    public boolean isGeneratedChild() {
        return KeyListLocalDirectoryNode.isGeneratedChild(this);
    }

    @Override
    public String getTargetPath() {
        return targetPath;
    }

    @Override
    public void setTargetPath(String path) {
        // Can't be changed on file
    }

    public static KeyListLocalFileNode createFrom(File localFile) {
        KeyListLocalFileNode node = new KeyListLocalFileNode();
        node.targetPath = localFile.getPath();
        String name = FileNameUtils.getNameWithoutExtension(localFile);
        node.textProperty().set(name);
        node.textToWriteProperty().set(name);
        node.textToSpeakProperty().set(name);
        ImageElementI image = ImageDictionaries.INSTANCE.getOrAddToUserImagesDictionary(localFile);
        if (image != null) {
            node.imageVTwoProperty().set(image);
            node.textPositionProperty().set(TextPosition.BOTTOM);
        }
        return node;
    }

    @Override
    public Element serialize(IOContextI context) {
        return XMLObjectSerializer.serializeInto(KeyListLocalFileNode.class, this, super.serialize(context));
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(KeyListLocalFileNode.class, this, node);
    }
}
