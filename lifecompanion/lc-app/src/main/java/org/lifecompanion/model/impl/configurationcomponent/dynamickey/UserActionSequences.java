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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequenceI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequencesI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.util.CopyUtils;

import java.util.Map;

public class UserActionSequences implements UserActionSequencesI {
    private final ObservableList<UserActionSequenceI> userActionSequences;

    public UserActionSequences() {
        this.userActionSequences = FXCollections.observableArrayList();
    }

    @Override
    public ObservableList<UserActionSequenceI> getUserActionSequences() {
        return userActionSequences;
    }

    private static final String NODE_NAME = "UserActionSequences";

    @Override
    public Element serialize(IOContextI context) {
        Element node = new Element(NODE_NAME);
        IOHelper.addTypeAlias(this, node, context);
        for (UserActionSequenceI userActionSequence : userActionSequences) {
            node.addContent(userActionSequence.serialize(context));
        }
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        for (Element child : node.getChildren()) {
            UserActionSequenceI sequence = new UserActionSequence();
            sequence.deserialize(child, context);
            userActionSequences.add(sequence);
        }
    }

    @Override
    public UserActionSequences duplicate(boolean changeId) {
        return (UserActionSequences) CopyUtils.createDeepCopyViaXMLSerialization(this, changeId);
    }

    @Override
    public void idsChanged(Map<String, String> changes) {
    }
}
