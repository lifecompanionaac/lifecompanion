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
package org.lifecompanion.base.data.useaction.baseimpl;

import javafx.beans.property.*;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.DuplicableComponentI;
import org.lifecompanion.api.component.definition.useaction.BaseUseActionI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionSubCategoryI;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.common.CopyUtils;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.io.IOManager;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Map;
import java.util.Set;

/**
 * This class provide a default implementation of {@link BaseUseActionI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class BaseUseActionImpl<T extends UseActionTriggerComponentI> implements BaseUseActionI<T> {

    // Class part : "Attributes, will be filled by subclasses"
    //========================================================================
    protected boolean simple = false, parameterizableAction = true, movingAction = false;
    protected int order = Integer.MAX_VALUE;
    protected String staticDescriptionID = "unknow.action.description", nameID = "unknow.action.name", configIconPath;
    protected SystemType[] allowSystems = SystemType.values();
    protected UseActionEvent[] allowedEvent = UseActionEvent.values();
    protected Class<T> allowedParent;
    protected UseActionSubCategoryI category;
    private StringProperty variableDescription;
    private ObjectProperty<T> parentComponent;
    private BooleanProperty attachedToKeyOption;
    //========================================================================

    /**
     * Create a base use action.<br>
     * The subclass of this class should always set the parameters of this action inside this constructor.
     */
    public BaseUseActionImpl(final Class<T> allowedParentP) {
        if (allowedParentP == null) {
            throw new IllegalArgumentException("The allowed parent for an use action can't be null, you should give an allowed parent type");
        }
        this.variableDescription = new SimpleStringProperty(this, "variableDescription", "unknow.action.description");
        this.parentComponent = new SimpleObjectProperty<>(this, "parentComponent");
        this.attachedToKeyOption = new SimpleBooleanProperty(this, "attachedToKeyOption", false);
        this.allowedParent = allowedParentP;
    }

    // Class part : "Base property"
    //========================================================================
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
        return LCConstant.INT_PATH_USE_ACTION_ICON_PATH + this.configIconPath;
    }

    @Override
    public SystemType[] allowedSystemType() {
        return this.allowSystems;
    }

    @Override
    public UseActionSubCategoryI getCategory() {
        return this.category;
    }

    @Override
    public boolean isMovingAction() {
        return this.movingAction;
    }

    @Override
    public Class<T> allowedParent() {
        return this.allowedParent;
    }

    @Override
    public UseActionEvent[] allowedActionEvent() {
        return this.allowedEvent;
    }

    @Override
    public boolean isSimple() {
        return this.simple;
    }

    @Override
    public int order() {
        return this.order;
    }

    @Override
    public ObjectProperty<T> parentComponentProperty() {
        return this.parentComponent;
    }

    @Override
    public boolean isParameterizableElement() {
        return this.parameterizableAction;
    }

    @Override
    public BooleanProperty attachedToKeyOptionProperty() {
        return this.attachedToKeyOption;
    }

    @Override
    public DuplicableComponentI duplicate(final boolean changeID) {
        return CopyUtils.createDeepCopyViaXMLSerialization(this, false);//Action don't have IDs
    }

    @Override
    public void idsChanged(final Map<String, String> changes) {
    }
    //========================================================================

    // Class part : "XML Serialization"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        //Create base component
        Element xmlElement = new Element(BaseUseActionI.NODE_ACTION);
        IOManager.addTypeAlias(this, xmlElement, contextP);
        return xmlElement;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
    }
    //========================================================================

    // Class part : "Use information"
    //========================================================================
    @Override
    public void serializeUseInformation(final Map<String, Element> elements) {
    }

    @Override
    public void deserializeUseInformation(final Map<String, Element> elements) throws LCException {
    }
    //========================================================================
}
