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
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.configurationcomponent.ConfigurationChildComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionManager;
import org.lifecompanion.model.impl.configurationcomponent.GridPartKeyComponent;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.plugin.PluginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ConfigurationComponentIOHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationComponentIOHelper.class);

    public static final String ATB_TYPE = "nodeType";
    public static final String ATB_TYPE_LIGHT = "ntyp";
    public static final String ATB_PLUGIN_ID = "dependencyPluginId";

    /**
     * Boolean that becomes true once default {@link XMLSerializable} from default modules are discovered (typeAlias will be filled with them)
     */
    private static final AtomicBoolean defaultTypeInitialized = new AtomicBoolean(false);

    /**
     * Contains a map that convert type from loaded element (e.g. <strong>nodeType</strong> attribute)
     * to real Java types.<br>
     * This method is new, so the previously saved type are converted with {@link BackwardCompatibilityHelper}.<br>
     * This map is meant to be initialized just once on startup.
     */
    private static Map<String, Pair<Class<?>, PluginInfo>> typeAlias;

    private static final AtomicBoolean optimizedTypeInitialized = new AtomicBoolean(false);

    /**
     * Optimized type alias manually set by dev to reduce file sizes and memory footprint.
     */
    private static Map<Class<?>, String> optimizedTypeAlias;


    /**
     * Create the base serialize object from a xml serialized component.<br>
     * This will not call {@link XMLSerializable#deserialize(Element, Object)} on the create object.
     *
     * @param element the element that contains the object serialized
     * @return the created component
     * @throws LCException if the component can't be created
     */
    @SuppressWarnings("unchecked")
    public static Pair<Boolean, XMLSerializable<IOContextI>> create(final Element element, IOContextI ioContext, Supplier<XMLSerializable<IOContextI>> fallbackSupplier) throws LCException {
        String className = element.getAttribute(ATB_TYPE_LIGHT) != null ? element.getAttributeValue(ATB_TYPE_LIGHT) : element.getAttributeValue(ATB_TYPE);
        try {
            // Check if the plugin dependency is loaded, if not use fallback when enable
            String pluginDependencyId = element.getAttributeValue(ATB_PLUGIN_ID);
            if (pluginDependencyId != null && !PluginController.INSTANCE.isPluginLoaded(pluginDependencyId)) {
                if (ioContext.isFallbackOnDefaultInstanceOnFail()) {
                    return Pair.of(true, fallbackSupplier != null ? fallbackSupplier.get() : null);
                } else {
                    throw LCException.newException().withMessage("error.io.manager.xml.element.read", element.getName(), element.getAttributes()).build();
                }
            }
            // Normal situation, no plugin or plugin is loaded
            else {
                Class<XMLSerializable<IOContextI>> loadedClass = getClassForName(className);
                XMLSerializable<IOContextI> createdObject = loadedClass.getDeclaredConstructor().newInstance();
                return Pair.of(false, createdObject);
            }
        } catch (Throwable t) {
            // Unknown error on loading
            LOGGER.warn("Problem while creating the object from a serialized object in XML", t);
            if (ioContext.isFallbackOnDefaultInstanceOnFail()) {
                return Pair.of(true, fallbackSupplier != null ? fallbackSupplier.get() : null);
            } else {
                throw LCException.newException().withMessage("error.io.manager.xml.element.read", element.getName(), element.getAttributes()).withCause(t).build();
            }
        }
    }

    /**
     * Set the base element on a XML serializable object to be deserialized
     *
     * @param caller the calling class
     * @param node   the node where we must put the base
     */
    public static Element addTypeAlias(final XMLSerializable<?> caller, final Element node, IOContextI ioContext) {
        node.setAttribute(ATB_TYPE_LIGHT, getOptimizedTypeAlias().getOrDefault(caller.getClass(), caller.getClass().getSimpleName()));
        Pair<Class<?>, PluginInfo> pluginInfoForType = getTypeAlias().get(caller.getClass().getSimpleName());
        // When the saved element is from a plugin : "flag" the XML element to be dependent on the plugin and add the plugin id to dependencies list
        if (pluginInfoForType != null && pluginInfoForType.getRight() != null) {
            node.setAttribute(ATB_PLUGIN_ID, pluginInfoForType.getRight().getPluginId());
            ioContext.getAutomaticPluginDependencyIds().add(pluginInfoForType.getRight().getPluginId());
        }
        return node;
    }

    public static <T> Class<T> getClassForName(final String className) throws ClassNotFoundException {
        if (getTypeAlias().containsKey(className)) {
            return (Class<T>) getTypeAlias().get(className).getLeft();
        } else {
            // Backward compatibility : type were directly written in XML
            return (Class<T>) Class.forName(BackwardCompatibilityHelper.getBackwardCompatibleType(className));
        }
    }

    private static void initializeTypeMap() {
        if (!defaultTypeInitialized.getAndSet(true)) {
            addSerializableTypes(ReflectionHelper.findImplementationsInModules(XMLSerializable.class), null);
            addSerializableTypes(ReflectionHelper.findImplementationsInModules(SelectionModeI.class), null);
            getOptimizedTypeAlias().forEach((type, name) -> {
                typeAlias.put(name, Pair.of(type, null));
            });
        }
    }

    private static Map<Class<?>, String> getOptimizedTypeAlias() {
        if (!optimizedTypeInitialized.getAndSet(true)) {
            optimizedTypeAlias = new HashMap<>(10);
            optimizedTypeAlias.put(GridPartKeyComponent.class, "GPKC");
            optimizedTypeAlias.put(SimpleUseActionManager.class, "SUAM");
        }
        return optimizedTypeAlias;
    }

    private static Map<String, Pair<Class<?>, PluginInfo>> getTypeAlias() {
        initializeTypeMap();
        return typeAlias;
    }


    public static void addSerializableTypes(List<? extends Class> types, PluginInfo pluginInfo) {
        if (typeAlias == null) {
            typeAlias = new HashMap<>(150);
        }
        for (Class<?> type : types) {
            String typeName = type.getSimpleName();
            Pair<Class<?>, PluginInfo> previous = typeAlias.put(typeName, Pair.of(type, pluginInfo));
            if (previous != null) {
                LOGGER.error("Found two types with the same name : {} / {} and {}", typeName, previous.getLeft().getName(), type.getName());
            }
        }
    }

    // FIXME : change method names
    public static <T extends ConfigurationChildComponentI> void serializeComponentDependencies(final IOContextI context, final T element, final Element node) {
        PluginController.INSTANCE.serializePluginInformation(element, context, node);
    }

    public static <T extends ConfigurationChildComponentI> void deserializeComponentDependencies(final IOContextI context, final T element, final Element node) throws LCException {
        //Plugins
        PluginController.INSTANCE.deserializePluginInformation(element, context, node);
    }
}
