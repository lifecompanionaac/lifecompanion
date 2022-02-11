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
package org.lifecompanion.util;

import org.jdom2.Element;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.model.api.configurationcomponent.DuplicableComponentI;
import org.lifecompanion.model.api.configurationcomponent.TreeIdentifiableComponentI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.impl.io.IOContext;
import org.lifecompanion.framework.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
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
        //Copy, but ID mustn't be the same
        IOContext context = new IOContext(LCUtils.getTempDir("componentcopy"));
        context.setFallbackOnDefaultInstanceOnFail(false);
        //Serialize element to copy
        Element serialized = source.serialize(context);
        try {
            //Load serialized version
            Pair<Boolean, XMLSerializable<IOContextI>> duplicatedResult = IOHelper.create(serialized, context, null);
            XMLSerializable<IOContextI> duplicated = duplicatedResult.getRight();
            duplicated.deserialize(serialized, context);
            if (changeID) {
                if (duplicated instanceof TreeIdentifiableComponentI) {
                    HashMap<String, String> idChanges = new HashMap<>();
                    CopyUtils.changeIDs((TreeIdentifiableComponentI) duplicated, idChanges);
                    CopyUtils.dispatchIdChanges((TreeIdentifiableComponentI) duplicated, idChanges);
                } else {
                    LOGGER.info("Ignored ID change on {} because it does not extends TreeIdentifiableComponentI interface", duplicated);
                }
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
    private static void changeIDs(final TreeIdentifiableComponentI component, final HashMap<String, String> idChanges) {
        String previousId = component.getID();
        String newId = component.generateID();
        idChanges.put(previousId, newId);
        if (!component.isTreeIdentifiableComponentLeaf()) {
            List<TreeIdentifiableComponentI> childrenNode = component.getTreeIdentifiableChildren();
            for (TreeIdentifiableComponentI child : childrenNode) {
                CopyUtils.changeIDs(child, idChanges);
            }
        }
    }

    private static void dispatchIdChanges(final TreeIdentifiableComponentI component, final HashMap<String, String> idChanges) {
        if (component instanceof DuplicableComponentI) {
            ((DuplicableComponentI) component).idsChanged(idChanges);
        }
        if (!component.isTreeIdentifiableComponentLeaf()) {
            List<TreeIdentifiableComponentI> childrenNode = component.getTreeIdentifiableChildren();
            for (TreeIdentifiableComponentI child : childrenNode) {
                CopyUtils.dispatchIdChanges(child, idChanges);
            }
        }
    }
}
