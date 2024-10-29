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
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.style.ShapeStyle;
import org.lifecompanion.model.impl.imagedictionary.StaticLocalImageElement;
import org.lifecompanion.util.IOUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class KeyListFileDirectoryNode extends AbstractKeyListNode {
    // Configuration : create subdirectory / flat directory / just directory
    // Default to "My Images" on computer
    // Write text / Speak text

    private static final long CACHE_EXPIRATION_MS = 10_000;
    private List<KeyListNodeI> cachedChildren;
    private long cachedChildrenLastUpdatedAt;

    private File targetDirectory = new File("D:\\Data\\Images\\test-directory");

    public KeyListFileDirectoryNode() {
        super(false, false);
        this.enableSpeakProperty().set(false);
        this.enableWriteProperty().set(false);
        this.shapeStyleProperty().set(ShapeStyle.TP_ANGLE_CUT);
    }

    @Override
    public ObservableList<KeyListNodeI> getChildren() {
        ObservableList<KeyListNodeI> children = super.getChildren();
        // Update only when no cache or cache is too old
        if (cachedChildren == null || (System.currentTimeMillis() - cachedChildrenLastUpdatedAt) > CACHE_EXPIRATION_MS) {
            children.setAll(updateCachedChildren(createChildren(targetDirectory)));
        }
        return children;
    }

    private List<KeyListNodeI> updateCachedChildren(List<KeyListNodeI> cache) {
        this.cachedChildren = cache;
        this.cachedChildrenLastUpdatedAt = System.currentTimeMillis();
        return cache;
    }

    private List<KeyListNodeI> createChildren(File directory) {
        List<KeyListNodeI> children = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (IOUtils.isSupportedImage(file)) {
                    KeyListLeaf leaf = new KeyListLeaf();
                    String name = FileNameUtils.getNameWithoutExtension(file);
                    leaf.textProperty().set(name);
                    leaf.textToWriteProperty().set(name);
                    leaf.textToSpeakProperty().set(name);
                    leaf.imageVTwoProperty().set(new StaticLocalImageElement(file));
                    children.add(leaf);
                } else if (file.isDirectory()) {
                    KeyListNode node = new KeyListNode();
                    node.textProperty().set( FileNameUtils.getNameWithoutExtension(file));
                    node.getChildren().addAll(createChildren(file));
                    children.add(node);
                }
            }
        }
        return children;
    }
}
