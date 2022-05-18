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

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.ui.configurationcomponent.ComponentViewI;
import org.lifecompanion.model.api.ui.configurationcomponent.ViewProviderI;
import org.lifecompanion.model.api.ui.configurationcomponent.ViewProviderType;
import org.lifecompanion.util.CopyUtils;
import org.lifecompanion.framework.commons.fx.io.XMLIgnoreNullValue;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Base implementation for {@link DisplayableComponentI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class CoreDisplayableComponentBaseImpl implements DisplayableComponentI {
    /**
     * Object unique identifier
     */
    private String id;

    /**
     * If the component is removed from its configuration
     */
    protected final transient BooleanProperty removed;

    /**
     * If the component is displayed in a view
     */
    protected final transient BooleanProperty displayed;

    /**
     * Name
     */
    protected final transient StringProperty name;

    /**
     * Default name for this components
     */
    protected final transient StringProperty defaultName;

    /**
     * Suffix to add at the end of the default name
     */
    protected final transient StringProperty detailName;

    /**
     * Chosen name
     */
    @XMLIgnoreNullValue
    protected final StringProperty userName;

    /**
     * This component is inside this configuration
     */
    protected final ObjectProperty<LCConfigurationI> configurationParent;

    /**
     * Create the core for a displayable component.<br>
     * Initialize its ID and its properties.
     */
    public CoreDisplayableComponentBaseImpl() {
        this.generateID();
        this.name = new SimpleStringProperty(this, "name");
        this.defaultName = new SimpleStringProperty(this, "defaultName");
        this.detailName = new SimpleStringProperty(this, "parentName", null);
        this.userName = new SimpleStringProperty(this, "userName");
        this.configurationParent = new SimpleObjectProperty<>(this, "configurationParent");
        this.removed = new SimpleBooleanProperty(this, "removed", false);
        this.defaultName.set(this.getDisplayableTypeName());
        this.displayed = new SimpleBooleanProperty(this, "displayed", false);
        this.initListener();
    }

    /**
     * Create the needed listener for this configuration.
     */
    private void initListener() {
        //When configuration change, add/remove from all component
        this.configurationParent.addListener((obs, ov, nv) -> {
            //Removed from the previous configuration
            if (ov != null) {
                ov.getAllComponent().remove(this.id);
            }
            //Add the component to the new configuration
            if (nv != null) {
                if (!nv.getAllComponent().containsKey(this.id)) {
                    nv.getAllComponent().put(this.id, CoreDisplayableComponentBaseImpl.this);
                }
            }
        });
        //When removed change, add/remove the configuration
        this.removed.addListener((obs, ov, nv) -> {
            //If was removed and added again, check if the component is still in the all component list
            if (ov && !nv) {
                LCConfigurationI config = this.configurationParent.get();
                if (config != null) {
                    if (!config.getAllComponent().containsKey(this.id)) {
                        config.getAllComponent().put(this.id, CoreDisplayableComponentBaseImpl.this);
                    }
                }
            }
        });
        //Name : default or user
        this.name.bind(Bindings.createStringBinding(() -> {
            String userN = this.userName.get();
            if (StringUtils.isBlank(userN)) {
                return this.defaultName.get();
            }
            return userN;
        }, this.defaultName, this.userName));
    }

    // Class part : "Base method"
    //========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyStringProperty nameProperty() {
        return this.name;
    }

    @Override
    public String generateID() {
        this.id = StringUtils.getNewID();
        return this.id;
    }

    @Deprecated
    public void setId(String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectProperty<LCConfigurationI> configurationParentProperty() {
        return this.configurationParent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getID() {
        return this.id;
    }

    private final ComponentViewI[] displayCache = new ComponentViewI[ViewProviderType.values().length];

    @Override
    public ComponentViewI<?> getDisplay(ViewProviderI viewProvider, boolean useCache) {
        if (useCache) {
            final int index = viewProvider.getType().getCacheIndex();
            if (displayCache[index] == null) {
                displayCache[index] = viewProvider.createComponentViewFor(this, true);
            }
            return displayCache[index];
        } else {
            return viewProvider.createComponentViewFor(this, false);
        }
    }

    @Override
    public void clearViewCache() {
        for (int i = 0; i < this.displayCache.length; i++) {
            if (displayCache[i] != null) {
                displayCache[i].unbindComponentAndChildren();
                displayCache[i] = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showToFront(ViewProviderI viewProvider, boolean useCache) {
        getDisplay(viewProvider, useCache).showToFront();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchRemovedPropertyValue(final boolean value) {
        this.removed.set(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty removedProperty() {
        return this.removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyBooleanProperty displayedProperty() {
        return this.displayed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchDisplayedProperty(final boolean displayedP) {
        this.displayed.set(displayedP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DuplicableComponentI duplicate(final boolean changeID) {
        return CopyUtils.createDeepCopyViaXMLSerialization(this, changeID);
    }

    @Override
    public void idsChanged(final Map<String, String> changes) {
    }

    @Override
    public <T extends TreeIdentifiableComponentI> List<T> getTreeIdentifiableChildren() {
        return (List<T>) this.getChildrenNode();
    }

    @Override
    public boolean isTreeIdentifiableComponentLeaf() {
        return isNodeLeaf();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringProperty defaultNameProperty() {
        return this.defaultName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringProperty detailNameProperty() {
        return this.detailName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringProperty userNameProperty() {
        return this.userName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayableTypeName() {
        ComponentNameEnum componentNameEnum = ComponentNameEnum.getBySuperClass(this.getClass());
        return componentNameEnum != null ? componentNameEnum.getComponentName() : "";
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    private static final String NODE_COMP = "Component";
    private static final String NODE_COMP_LIGHT = "Cmp";

    @Override
    public Element serialize(final IOContextI contextP) {
        Element xmlElement = new Element(CoreDisplayableComponentBaseImpl.NODE_COMP_LIGHT);
        ConfigurationComponentIOHelper.addTypeAlias(this, xmlElement, contextP);
        XMLObjectSerializer.serializeInto(CoreDisplayableComponentBaseImpl.class, this, xmlElement);
        return xmlElement;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        XMLObjectSerializer.deserializeInto(CoreDisplayableComponentBaseImpl.class, this, nodeP);
    }
    //========================================================================

    // Class part : "Use information serializable"
    //========================================================================
    @Override
    public void serializeUseInformation(final Map<String, Element> elements) {
        if (!this.isNodeLeaf()) {
            ObservableList<TreeDisplayableComponentI> childrenNode = this.getChildrenNode();
            for (TreeDisplayableComponentI childNode : childrenNode) {
                if (childNode instanceof UseInformationSerializableI) {
                    ((UseInformationSerializableI) childNode).serializeUseInformation(elements);
                }
            }
        }
    }

    @Override
    public void deserializeUseInformation(final Map<String, Element> elements) throws LCException {
        if (!this.isNodeLeaf()) {
            ObservableList<TreeDisplayableComponentI> childrenNode = this.getChildrenNode();
            for (TreeDisplayableComponentI childNode : childrenNode) {
                if (childNode instanceof UseInformationSerializableI) {
                    ((UseInformationSerializableI) childNode).deserializeUseInformation(elements);
                }
            }
        }
    }
    //========================================================================

    @Override
    public String toString() {
        return "[" + this.getClass().getSimpleName() + ", name = " + this.name.get() + "]";
    }

}
