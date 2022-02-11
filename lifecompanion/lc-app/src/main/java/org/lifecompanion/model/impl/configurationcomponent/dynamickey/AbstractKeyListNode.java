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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.model.api.configurationcomponent.TreeIdentifiableComponentI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.util.CopyUtils;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.framework.commons.fx.io.XMLIgnoreNullValue;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractKeyListNode extends AbstractSimplerKeyActionContainer implements KeyListNodeI {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKeyListNode.class);

    private transient final boolean link;
    private transient final boolean leaf;

    @XMLIgnoreNullValue
    private final StringProperty linkedNodeId;
    private final transient IntegerProperty level;
    private final ObservableList<KeyListNodeI> children;
    private final ObjectProperty<KeyListNodeI> parent;

    protected AbstractKeyListNode(boolean leaf, boolean link) {
        super();
        parent = new SimpleObjectProperty<>();
        this.leaf = leaf;
        this.link = link;
        this.children = leaf ? null : FXCollections.observableArrayList();
        this.level = new SimpleIntegerProperty(1);
        this.linkedNodeId = new SimpleStringProperty();
        if (this.children != null) {
            this.children.addListener(LCUtils.createListChangeListenerV2(
                    added -> ((AbstractKeyListNode) added).parent.set(this),
                    removed -> {
                        // Before removing the parent, we should check the count because our add/removed mapping is * > 1
                        if (!children.contains(removed)) {
                            ((AbstractKeyListNode) removed).parent.set(null);
                        }
                    }
            ));
        }
        this.parent.addListener((obs, ov, nv) -> {
            level.unbind();
            level.set(1);
            if (nv != null) {
                level.bind(nv.levelProperty().add(1));
            }
        });
    }

    // PROPS
    //========================================================================
    @Override
    public KeyListNodeI duplicate(boolean changeId) {
        return (AbstractKeyListNode) CopyUtils.createDeepCopyViaXMLSerialization(this, true);
    }

    @Override
    public boolean isLeafNode() {
        return leaf;
    }

    @Override
    public boolean isTreeIdentifiableComponentLeaf() {
        return isLeafNode();
    }

    @Override
    public void idsChanged(Map<String, String> changes) {
        super.idsChanged(changes);
        String previousId = this.linkedNodeIdProperty().get();
        if (StringUtils.isNotBlank(previousId) && changes.containsKey(previousId)) {
            this.linkedNodeIdProperty().set(changes.get(previousId));
        }
    }

    @Override
    public <T extends TreeIdentifiableComponentI> List<T> getTreeIdentifiableChildren() {
        return (List<T>) getChildren();
    }

    @Override
    public boolean isLinkNode() {
        return link;
    }

    @Override
    public ObservableList<KeyListNodeI> getChildren() {
        return children;
    }

    @Override
    public ReadOnlyObjectProperty<KeyListNodeI> parentProperty() {
        return parent;
    }

    @Override
    public ReadOnlyIntegerProperty levelProperty() {
        return level;
    }

    @Override
    public StringProperty linkedNodeIdProperty() {
        return linkedNodeId;
    }

    @Override
    public String getHumanReadableText() {
        if (StringUtils.isNotBlank(textProperty().get())) return textProperty().get();
        if (enableWriteProperty().get() && StringUtils.isNotBlank(textToWriteProperty().get())) return textToWriteProperty().get();
        if (enableSpeakProperty().get() && StringUtils.isNotBlank(textToSpeakProperty().get())) return textToSpeakProperty().get();
        return Translation.getText("general.configuration.view.keylist.empty.text.in.node");
    }

    //========================================================================

    // IO
    //========================================================================
    public static final String NODE_NAME = "KeyListNode";

    @Override
    protected String getNodeName() {
        return NODE_NAME;
    }

    @Override
    public Element serialize(IOContextI context) {
        final Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(AbstractKeyListNode.class, this, node);
        if (!leaf) {
            for (KeyListNodeI child : children) {
                node.addContent(child.serialize(context));
            }
        }
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(AbstractKeyListNode.class, this, node);
        if (!this.leaf) {
            final List<Element> nodeChildren = node.getChildren(NODE_NAME);
            for (Element child : nodeChildren) {
                Pair<Boolean, XMLSerializable<IOContextI>> childComponentResult = ConfigurationComponentIOHelper.create(child, context, null);
                if (!childComponentResult.getLeft()) {
                    KeyListNodeI keyListNode = (KeyListNodeI) childComponentResult.getRight();
                    keyListNode.deserialize(child, context);
                    this.children.add(keyListNode);
                }
            }
        }
    }

    @Override
    public void traverseTreeToBottom(Consumer<KeyListNodeI> nodeConsumer) {
        nodeConsumer.accept(this);
        if (this.children != null) {
            this.children.forEach(c -> c.traverseTreeToBottom(nodeConsumer));
        }
    }

    private StringBuilder printTo(int depth, StringBuilder sb) {
        for (int i = 0; i < depth; i++)
            sb.append("   ");
        sb.append(textProperty().get()).append(leaf ? "" : " (cat)").append(" - ").append(level.get()).append("\n");
        if (children != null) {
            children.forEach(c -> ((AbstractKeyListNode) c).printTo(depth + 1, sb));
        }
        return sb;
    }
    //========================================================================
}
