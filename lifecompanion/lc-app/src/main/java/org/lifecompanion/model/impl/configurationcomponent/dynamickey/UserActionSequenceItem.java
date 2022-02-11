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

import javafx.beans.property.*;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequenceItemI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.util.CopyUtils;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

public class UserActionSequenceItem extends AbstractSimplerKeyActionContainer implements UserActionSequenceItemI {

    private final transient BooleanProperty actionExecuted;
    private final transient BooleanProperty currentAction;
    private final BooleanProperty subItem;
    private final IntegerProperty automaticItemTimeMs;
    private final BooleanProperty enableAutomaticItem;
    private final ObjectProperty<UserActionSequenceItemI> itemParent;

    // TODO : add automatic go to next one after timer

    public UserActionSequenceItem() {
        super();
        this.enableWriteProperty().set(false);
        this.enableSpeakOnOverProperty().set(false);
        this.subItem = new SimpleBooleanProperty(false);
        this.actionExecuted = new SimpleBooleanProperty(false);
        this.enableAutomaticItem = new SimpleBooleanProperty(false);
        this.automaticItemTimeMs = new SimpleIntegerProperty(10_000);
        this.currentAction = new SimpleBooleanProperty(false);
        this.itemParent = new SimpleObjectProperty<>();
    }

    @Override
    public UserActionSequenceItem duplicate(boolean changeId) {
        return (UserActionSequenceItem) CopyUtils.createDeepCopyViaXMLSerialization(this, changeId);
    }

    @Override
    public BooleanProperty enableAutomaticItemProperty() {
        return enableAutomaticItem;
    }

    @Override
    public IntegerProperty automaticItemTimeMsProperty() {
        return automaticItemTimeMs;
    }

    @Override
    public BooleanProperty subItemProperty() {
        return subItem;
    }

    @Override
    public BooleanProperty actionExecutedProperty() {
        return actionExecuted;
    }

    @Override
    public BooleanProperty currentActionProperty() {
        return currentAction;
    }

    @Override
    public ObjectProperty<UserActionSequenceItemI> itemParentProperty() {
        return itemParent;
    }

    public static final String NODE_NAME = "UserActionSequenceItem";

    @Override
    protected String getNodeName() {
        return NODE_NAME;
    }

    @Override
    public Element serialize(IOContextI context) {
        Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(UserActionSequenceItem.class, this, node);
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(UserActionSequenceItem.class, this, node);
    }
}
