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

package org.lifecompanion.base.data.component.simplercomp;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.TreeIdentifiableComponentI;
import org.lifecompanion.api.component.definition.simplercomp.UserActionSequenceI;
import org.lifecompanion.api.component.definition.simplercomp.UserActionSequenceItemI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.common.CopyUtils;
import org.lifecompanion.base.data.io.IOManager;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.List;
import java.util.Map;

public class UserActionSequence implements UserActionSequenceI {
    private String id;
    private final StringProperty name;
    private final ObservableList<UserActionSequenceItemI> items;

    public UserActionSequence() {
        this.generateID();
        this.name = new SimpleStringProperty();
        this.items = FXCollections.observableArrayList();
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public ObservableList<UserActionSequenceItemI> getItems() {
        return items;
    }

    @Override
    public UserActionSequence duplicate(boolean changeId) {
        return (UserActionSequence) CopyUtils.createDeepCopyViaXMLSerialization(this, changeId);
    }

    @Override
    public <T extends TreeIdentifiableComponentI> List<T> getTreeIdentifiableChildren() {
        return (List<T>) getItems();
    }

    @Override
    public boolean isTreeIdentifiableComponentLeaf() {
        return false;
    }

    @Override
    public void idsChanged(Map<String, String> changes) {
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String generateID() {
        this.id = StringUtils.getNewID();
        return id;
    }

    // IO
    //========================================================================
    private static final String NODE_NAME = "UserActionSequence";

    @Override
    public Element serialize(IOContextI context) {
        Element node = new Element(NODE_NAME);
        IOManager.addTypeAlias(this, node, context);
        XMLObjectSerializer.serializeInto(UserActionSequence.class, this, node);
        for (UserActionSequenceItemI item : items) {
            node.addContent(item.serialize(context));
        }
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(UserActionSequence.class, this, node);
        for (Element child : node.getChildren()) {
            UserActionSequenceItemI item = new UserActionSequenceItem();
            item.deserialize(child, context);
            this.items.add(item);
        }
    }
    //========================================================================


    @Override
    public String toString() {
        return "UserActionSequence{" +
                "name=" + name.get() +
                '}';
    }

}
