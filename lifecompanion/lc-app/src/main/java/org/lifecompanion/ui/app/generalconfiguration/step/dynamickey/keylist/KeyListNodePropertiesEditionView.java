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

package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.keylist;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.ui.common.control.specific.selector.KeyListSelectorControl;
import org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.AbstractSimplerKeyActionContainerPropertiesEditionView;
import org.lifecompanion.framework.commons.translation.Translation;

public class KeyListNodePropertiesEditionView extends AbstractSimplerKeyActionContainerPropertiesEditionView<KeyListNodeI> {
    private BooleanProperty selectedNodeIsLeaf;
    private BooleanProperty selectedNodeIsLink;
    private KeyListSelectorControl linkedNodeSelector;

    public BooleanProperty selectedNodeIsLeafProperty() {
        return selectedNodeIsLeaf == null ? selectedNodeIsLeaf = new SimpleBooleanProperty(false) : selectedNodeIsLeaf;
    }

    public BooleanProperty selectedNodeIsLinkProperty() {
        return selectedNodeIsLink == null ? selectedNodeIsLink = new SimpleBooleanProperty(false) : selectedNodeIsLink;
    }

    @Override
    protected int addFieldsAfterTextInGeneralPart(GridPane gridPaneConfiguration, int rowIndex, final int columnCount) {
        rowIndex = super.addFieldsAfterTextInGeneralPart(gridPaneConfiguration, rowIndex, columnCount);

        linkedNodeSelector = new KeyListSelectorControl(null);
        final Label labelLinkedNode = new Label(Translation.getText("general.configuration.view.key.list.field.linked.node"));
        gridPaneConfiguration.add(labelLinkedNode, 0, rowIndex);
        gridPaneConfiguration.add(linkedNodeSelector, 1, rowIndex++, 2, 1);
        labelLinkedNode.visibleProperty().bind(linkedNodeSelector.visibleProperty());
        labelLinkedNode.managedProperty().bind(linkedNodeSelector.managedProperty());
        return rowIndex;
    }


    @Override
    public void initBinding() {
        super.initBinding();
        linkedNodeSelector.visibleProperty().bind(this.selectedNode.isNotNull().and(selectedNodeIsLinkProperty()));
        linkedNodeSelector.managedProperty().bind(linkedNodeSelector.visibleProperty());
    }

    @Override
    protected boolean enableWriteFields() {
        return true;
    }

    @Override
    protected boolean enableOverSpeakFields() {
        return true;
    }

    @Override
    protected String getTextToSpeakFieldLabelId() {
        return "general.configuration.view.key.list.field.text.to.speak";
    }

    @Override
    protected void bindBidirectionalContent(KeyListNodeI ov, KeyListNodeI nv) {
        super.bindBidirectionalContent(ov, nv);
        linkedNodeSelector.setInputKeyNode(nv);
        if (nv != null) {
            linkedNodeSelector.selectedKeylistCategoryIdProperty().bindBidirectional(nv.linkedNodeIdProperty());
            this.selectedNodeIsLinkProperty().set(nv.isLinkNode());
            this.selectedNodeIsLeafProperty().set(nv.isLeafNode());
        }
    }

    @Override
    protected void unbindBidirectionalContent(KeyListNodeI ov, KeyListNodeI nv) {
        super.unbindBidirectionalContent(ov, nv);
        if (ov != null) {
            linkedNodeSelector.selectedKeylistCategoryIdProperty().unbindBidirectional(ov.linkedNodeIdProperty());
        }
        linkedNodeSelector.setInputKeyNode(null);
    }


}
