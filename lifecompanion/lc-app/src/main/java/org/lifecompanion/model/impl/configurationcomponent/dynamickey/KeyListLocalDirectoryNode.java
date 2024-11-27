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

import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.style.ShapeStyle;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;

import java.io.File;
import java.util.*;

public class KeyListLocalDirectoryNode extends AbstractKeyListNode implements DynamicLocalFileNodeI {
    private String targetPath;
    private final Map<String, KeyListNodeI> nodesByPath;

    public KeyListLocalDirectoryNode() {
        super(false, false);
        this.enableSpeakProperty().set(false);
        this.enableWriteProperty().set(false);
        this.shapeStyleProperty().set(ShapeStyle.TP_ANGLE_CUT);
        this.nodesByPath = new HashMap<>();
    }

    public static KeyListLocalDirectoryNode createFrom(File localFile) {
        KeyListLocalDirectoryNode node = new KeyListLocalDirectoryNode();
        node.setTargetPath(localFile.getPath());
        return node;
    }

    @Override
    public void setTargetPath(String path) {
        this.targetPath = path;
        File localFile = this.targetPath != null ? new File(targetPath) : null;
        String name = localFile != null ? FileNameUtils.getNameWithoutExtension(localFile) : null;
        this.textProperty().set(name);
        this.textToWriteProperty().set(name);
        this.textToSpeakProperty().set(name);
        this.updateDynamicNode();
        // TODO : take image from children ?
    }

    @Override
    public int updateDynamicNode() {
        int changeCount = 0;
        Set<String> foundPath = new HashSet<>();
        if (this.targetPath != null && !GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_EXTERNAL_ACTIONS)) {
            File[] files = new File(targetPath).listFiles();
            if (files != null) {
                // List all the directory files or directories and create node only when the node is not already created
                for (File file : files) {
                    String filePath = file.getPath();
                    foundPath.add(filePath);
                    if (!this.nodesByPath.containsKey(filePath)) {
                        KeyListNodeI createdNode = null;
                        if (IOUtils.isSupportedImage(file)) {
                            createdNode = KeyListLocalFileNode.createFrom(file);
                        } else if (file.isDirectory()) {
                            createdNode = KeyListLocalDirectoryNode.createFrom(file);
                        }
                        if (createdNode != null) {
                            final KeyListNodeI createdNodeF = createdNode;
                            nodesByPath.put(filePath, createdNode);
                            FXThreadUtils.runOnFXThread(() -> this.getChildren().add(createdNodeF));
                            changeCount++;
                        }
                    }
                }
            }
        }
        // Once every file has been listed, remove the node that has not been found
        Set<String> existingPaths = new HashSet<>(nodesByPath.keySet());
        for (String existingPath : existingPaths) {
            if (!foundPath.contains(existingPath)) {
                KeyListNodeI removed = nodesByPath.remove(existingPath);
                if (removed != null) {
                    FXThreadUtils.runOnFXThread(() -> this.getChildren().remove(removed));
                    changeCount++;
                }
            }
        }
        return super.updateDynamicNode() + changeCount;
    }

    @Override
    public boolean isGeneratedChild() {
        return isGeneratedChild(this);
    }

    static boolean isGeneratedChild(KeyListNodeI node) {
        return node.parentProperty().get() instanceof DynamicLocalFileNodeI;
    }

    @Override
    public String getTargetPath() {
        return targetPath;
    }


    @Override
    public Element serialize(IOContextI context) {
        return XMLObjectSerializer.serializeInto(KeyListLocalDirectoryNode.class, this, super.serialize(context));
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(KeyListLocalDirectoryNode.class, this, node);
        // Update cached nodes from children
        ObservableList<KeyListNodeI> children = this.getChildren();
        for (KeyListNodeI child : children) {
            if (child instanceof DynamicLocalFileNodeI dynamicLocalFileNode) {
                nodesByPath.put(dynamicLocalFileNode.getTargetPath(), child);
            }
        }
    }
}
