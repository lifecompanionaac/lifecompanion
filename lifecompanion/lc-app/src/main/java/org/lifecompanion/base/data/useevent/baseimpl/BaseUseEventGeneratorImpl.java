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
package org.lifecompanion.base.data.useevent.baseimpl;

import javafx.beans.property.*;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.DuplicableComponentI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionManagerI;
import org.lifecompanion.api.component.definition.useevent.UseEventGeneratorI;
import org.lifecompanion.api.component.definition.useevent.UseEventListenerI;
import org.lifecompanion.api.component.definition.useevent.UseEventSubCategoryI;
import org.lifecompanion.api.component.definition.useevent.UseVariableDefinitionI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.io.IOManager;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionManager;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base implementation for {@link UseEventGeneratorI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class BaseUseEventGeneratorImpl implements UseEventGeneratorI {
    protected boolean parameterizableAction = true;
    protected int order = Integer.MAX_VALUE;
    protected String staticDescriptionID = "unknow.action.description", nameID = "unknow.action.name", configIconPath;
    private StringProperty variableDescription;
    protected UseEventSubCategoryI category;
    protected SystemType[] allowSystems = SystemType.values();
    protected List<UseVariableDefinitionI> generatedVariables;
    private UseActionManagerI useActionManager;
    private ObjectProperty<LCConfigurationI> configurationParent;

    protected UseEventListenerI useEventListener;

    protected BaseUseEventGeneratorImpl() {
        this.variableDescription = new SimpleStringProperty(this, "variableDescription");
        this.useActionManager = new SimpleUseActionManager(this, UseActionEvent.EVENT);
        this.generatedVariables = new ArrayList<>(3);
        this.configurationParent = new SimpleObjectProperty<>();
    }

    @Override
    public String getStaticDescription() {
        return Translation.getText(this.staticDescriptionID);
    }

    @Override
    public StringProperty variableDescriptionProperty() {
        return this.variableDescription;
    }

    @Override
    public String getName() {
        return Translation.getText(this.nameID);
    }

    @Override
    public String getConfigIconPath() {
        return LCConstant.INT_PATH_USE_USE_EVENT_ICON_PATH + this.configIconPath;
    }

    @Override
    public SystemType[] allowedSystemType() {
        return this.allowSystems;
    }

    @Override
    public UseEventSubCategoryI getCategory() {
        return this.category;
    }

    @Override
    public int order() {
        return this.order;
    }

    @Override
    public boolean isParameterizableElement() {
        return this.parameterizableAction;
    }

    @Override
    public List<UseVariableDefinitionI> getGeneratedVariables() {
        return this.generatedVariables;
    }

    // Class part : "Configuration child component"
    //========================================================================
    //Configuration child only use for the configuration parent use by action trigger component

    @Override
    public void idsChanged(final Map<String, String> changes) {
        this.useActionManager.dispatchIdsChanged(changes);
    }

    @Override
    public DuplicableComponentI duplicate(final boolean changeID) {
        return null;
    }

    @Override
    public BooleanProperty removedProperty() {
        return null;
    }

    @Override
    public void dispatchRemovedPropertyValue(final boolean value) {
    }

    @Override
    public String getID() {
        return null;
    }

    @Override
    public String generateID() {
        return null;
    }

    @Override
    public ObjectProperty<LCConfigurationI> configurationParentProperty() {
        return this.configurationParent;
    }
    //========================================================================

    // Class part : "IO"
    //========================================================================
    public static final String NODE_USE_EVENT_GENERATOR = "UseEventGenerator";

    @Override
    public Element serialize(final IOContextI context) {
        Element element = new Element(BaseUseEventGeneratorImpl.NODE_USE_EVENT_GENERATOR);
        IOManager.addTypeAlias(this, element, context);
        //Action manager
        element.addContent(this.useActionManager.serialize(context));
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        //Action manager
        Element actionManagerNode = node.getChild(SimpleUseActionManager.NODE_USE_ACTION_MANAGER);
        this.useActionManager.deserialize(actionManagerNode, context);
    }
    //========================================================================

    // Class part : "Use info"
    //========================================================================
    @Override
    public void serializeUseInformation(final Map<String, Element> elements) {
    }

    @Override
    public void deserializeUseInformation(final Map<String, Element> elements) throws LCException {
    }
    //========================================================================

    // Class part : "Action"
    //========================================================================
    @Override
    public UseActionManagerI getActionManager() {
        return this.useActionManager;
    }
    //========================================================================

    // Class part : "Listener"
    //========================================================================

    @Override
    public void attachListener(final UseEventListenerI listener) {
        this.useEventListener = listener;
    }

    @Override
    public void detachListener() {
        this.useEventListener = null;
    }
    //========================================================================

}
