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
package org.lifecompanion.base.data.common;

import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.DuplicableComponentI;
import org.lifecompanion.api.component.definition.TreeDisplayableComponentI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.io.XMLSerializable;
import org.lifecompanion.base.data.io.IOContext;
import org.lifecompanion.base.data.io.IOManager;
import org.lifecompanion.framework.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Utils to copy object and properties.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CopyUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(CopyUtils.class);

    private CopyUtils() {
    }

    public static <C, T extends XMLSerializable<C>> T createSimpleCopy(T source, C context, Supplier<T> constructor) throws LCException {
        Element xmlElement = source.serialize(context);
        T result = constructor.get();
        result.deserialize(xmlElement, context);
        return result;
    }

    /**
     * Create a deep copy of a given component.<br>
     * The copy use the XML serialization to copy the component.
     *
     * @param source   the component that should be copied
     * @param changeID if the copied component should have a different ID than the source
     * @return the cloned component, or null if the copy fails
     */
    public static <T extends DuplicableComponentI & XMLSerializable<IOContextI>> DuplicableComponentI createDeepCopyViaXMLSerialization(
            final T source, final boolean changeID) {
        //Copy, but ID musn't be the same
        IOContext context = new IOContext(LCUtils.getTempDir("componentcopy"));
        context.setFallbackOnDefaultInstanceOnFail(false);
        //Serialize element to copy
        Element serialized = source.serialize(context);
        try {
            //Load serialized version
            Pair<Boolean, XMLSerializable<IOContextI>> duplicatedResult = IOManager.create(serialized, context, null);
            XMLSerializable<IOContextI> duplicated = duplicatedResult.getRight();
            duplicated.deserialize(serialized, context);
            if (changeID) {
                HashMap<String, String> idChanges = new HashMap<>();
                CopyUtils.changeIDs((TreeDisplayableComponentI) duplicated, idChanges);
                CopyUtils.dispatchIdChanges((TreeDisplayableComponentI) duplicated, idChanges);
            }
            return (DuplicableComponentI) duplicated;
        } catch (LCException e) {
            CopyUtils.LOGGER.warn("Couldn't create the component from its serialized XML", e);
            return null;
        }
    }

    /**
     * Method that use the tree structure of tree component to change every ID
     *
     * @param component the root component, its ID will be changed too
     */
    private static void changeIDs(final TreeDisplayableComponentI component, final HashMap<String, String> idChanges) {
        String previousId = component.getID();
        String newId = component.generateID();
        idChanges.put(previousId, newId);
        if (!component.isNodeLeaf()) {
            ObservableList<TreeDisplayableComponentI> childrenNode = component.getChildrenNode();
            for (TreeDisplayableComponentI child : childrenNode) {
                CopyUtils.changeIDs(child, idChanges);
            }
        }
    }

    private static void dispatchIdChanges(final TreeDisplayableComponentI component, final HashMap<String, String> idChanges) {
        if (component instanceof DuplicableComponentI) {
            ((DuplicableComponentI) component).idsChanged(idChanges);
        }
        if (!component.isNodeLeaf()) {
            ObservableList<TreeDisplayableComponentI> childrenNode = component.getChildrenNode();
            for (TreeDisplayableComponentI child : childrenNode) {
                CopyUtils.dispatchIdChanges(child, idChanges);
            }
        }
    }
}
