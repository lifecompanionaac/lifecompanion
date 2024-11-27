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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.LinkType;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.DynamicLocalFileNodeI;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListLocalDirectoryNode;
import org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.AbstractSimplerKeyActionContainerPropertiesEditionView;
import org.lifecompanion.ui.common.control.generic.FileSelectorControl;
import org.lifecompanion.ui.common.control.specific.selector.ComponentSelectorControl;
import org.lifecompanion.ui.common.control.specific.selector.KeyListSelectorControl;
import org.lifecompanion.ui.common.pane.specific.cell.LinkTypeDetailListCell;
import org.lifecompanion.ui.common.pane.specific.cell.LinkTypeSimpleListCell;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.util.javafx.DialogUtils;

import java.io.File;

public class KeyListNodePropertiesEditionView extends AbstractSimplerKeyActionContainerPropertiesEditionView<KeyListNodeI> {
    private BooleanProperty selectedNodeIsLeaf;
    private BooleanProperty selectedNodeIsLink;
    private BooleanProperty selectedNodeIsDynamicLocalDirectory;
    private ObjectProperty<LinkType> selectedNodeLinkType;

    private KeyListSelectorControl linkedNodeSelector;
    private ComponentSelectorControl<GridComponentI> gridSelector;
    private ComboBox<LinkType> comboBoxLinkType;

    private FileSelectorControl fileSelectorControlLocalDirectory;

    public BooleanProperty selectedNodeIsLeafProperty() {
        return selectedNodeIsLeaf == null ? selectedNodeIsLeaf = new SimpleBooleanProperty(false) : selectedNodeIsLeaf;
    }

    public BooleanProperty selectedNodeIsLinkProperty() {
        return selectedNodeIsLink == null ? selectedNodeIsLink = new SimpleBooleanProperty(false) : selectedNodeIsLink;
    }

    public BooleanProperty selectedNodeIsDynamicLocalDirectoryProperty() {
        return selectedNodeIsDynamicLocalDirectory == null ? selectedNodeIsDynamicLocalDirectory = new SimpleBooleanProperty(false) : selectedNodeIsDynamicLocalDirectory;
    }

    public ObjectProperty<LinkType> selectedNodeLinkType() {
        return selectedNodeLinkType == null ? selectedNodeLinkType = new SimpleObjectProperty<>() : selectedNodeLinkType;
    }

    @Override
    protected int addFieldsAfterTextInGeneralPart(GridPane gridPaneConfiguration, int rowIndex, final int columnCount) {
        rowIndex = super.addFieldsAfterTextInGeneralPart(gridPaneConfiguration, rowIndex, columnCount);

        comboBoxLinkType = new ComboBox<>(FXCollections.observableArrayList(LinkType.values()));
        comboBoxLinkType.setButtonCell(new LinkTypeSimpleListCell());
        comboBoxLinkType.setCellFactory(lv -> new LinkTypeDetailListCell());
        this.comboBoxLinkType.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(comboBoxLinkType, Priority.ALWAYS);

        final Label labelLinkType = new Label(Translation.getText("general.configuration.view.key.list.field.link.type"));
        gridPaneConfiguration.add(labelLinkType, 0, rowIndex);
        gridPaneConfiguration.add(comboBoxLinkType, 1, rowIndex++, 2, 1);
        labelLinkType.visibleProperty().bind(comboBoxLinkType.visibleProperty());
        labelLinkType.managedProperty().bind(comboBoxLinkType.managedProperty());

        linkedNodeSelector = new KeyListSelectorControl(null);
        final Label labelLinkedNode = new Label(Translation.getText("general.configuration.view.key.list.field.linked.node"));
        gridPaneConfiguration.add(labelLinkedNode, 0, rowIndex);
        gridPaneConfiguration.add(linkedNodeSelector, 1, rowIndex, 2, 1);
        labelLinkedNode.visibleProperty().bind(linkedNodeSelector.visibleProperty());
        labelLinkedNode.managedProperty().bind(linkedNodeSelector.managedProperty());

        gridSelector = new ComponentSelectorControl<>(GridComponentI.class, null);
        Label labelLinkedGrid = new Label(Translation.getText("general.configuration.view.key.list.field.linked.grid"));
        gridPaneConfiguration.add(labelLinkedGrid, 0, rowIndex);
        gridPaneConfiguration.add(gridSelector, 1, rowIndex++, 2, 1);
        labelLinkedGrid.visibleProperty().bind(gridSelector.visibleProperty());
        labelLinkedGrid.managedProperty().bind(gridSelector.managedProperty());

        Label labelFileSelector = new Label(Translation.getText("general.configuration.view.key.list.field.target.path.directory"));
        fileSelectorControlLocalDirectory = new FileSelectorControl(null, FileSelectorControl.FileSelectorControlMode.FOLDER, FileChooserType.OPEN_FOLDER, true);
        gridPaneConfiguration.add(labelFileSelector, 0, rowIndex);
        gridPaneConfiguration.add(fileSelectorControlLocalDirectory, 1, rowIndex++, 2, 1);
        labelFileSelector.visibleProperty().bind(fileSelectorControlLocalDirectory.visibleProperty());
        labelFileSelector.managedProperty().bind(fileSelectorControlLocalDirectory.managedProperty());

        return rowIndex;
    }


    @Override
    public void initBinding() {
        super.initBinding();
        comboBoxLinkType.visibleProperty().bind(this.selectedNode.isNotNull().and(selectedNodeIsLinkProperty()));
        comboBoxLinkType.managedProperty().bind(comboBoxLinkType.visibleProperty());

        linkedNodeSelector.visibleProperty().bind(this.selectedNode.isNotNull().and(selectedNodeIsLinkProperty()).and(selectedNodeLinkType().isEqualTo(LinkType.KEYLIST)));
        linkedNodeSelector.managedProperty().bind(linkedNodeSelector.visibleProperty());

        gridSelector.visibleProperty().bind(this.selectedNode.isNotNull().and(selectedNodeIsLinkProperty()).and(selectedNodeLinkType().isEqualTo(LinkType.GRID)));
        gridSelector.managedProperty().bind(gridSelector.visibleProperty());

        fileSelectorControlLocalDirectory.visibleProperty().bind(this.selectedNode.isNotNull().and(selectedNodeIsDynamicLocalDirectoryProperty()));
        fileSelectorControlLocalDirectory.managedProperty().bind(fileSelectorControlLocalDirectory.visibleProperty());
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

    private ChangeListener<GridComponentI> gridChangeListener;
    private ChangeListener<String> gridIdChangeListener;
    private ChangeListener<File> fileChangeListener;

    @Override
    protected void bindBidirectionalContent(KeyListNodeI ov, KeyListNodeI nv) {
        super.bindBidirectionalContent(ov, nv);
        linkedNodeSelector.setInputKeyNode(nv);
        if (nv != null) {
            selectedNodeLinkType().bind(nv.linkTypeProperty());

            linkedNodeSelector.selectedKeylistCategoryIdProperty().bindBidirectional(nv.linkedNodeIdProperty());
            this.selectedNodeIsLinkProperty().set(nv.isLinkNode());
            this.selectedNodeIsLeafProperty().set(nv.isLeafNode());
            this.selectedNodeIsDynamicLocalDirectoryProperty().set(nv instanceof DynamicLocalFileNodeI dynamicLocalFileNode && !dynamicLocalFileNode.isGeneratedChild());

            comboBoxLinkType.valueProperty().bindBidirectional(nv.linkTypeProperty());

            gridSelector.selectById(nv.linkedGridIdProperty().get());
            gridChangeListener = (obs, oGrid, nGrid) -> nv.linkedGridIdProperty().set(nGrid != null ? nGrid.getID() : null);
            gridSelector.selectedComponentProperty().addListener(gridChangeListener);
            gridIdChangeListener = (obs, oGridId, nGridId) -> gridSelector.selectById(nGridId);
            nv.linkedGridIdProperty().addListener(gridIdChangeListener);

            if (nv instanceof DynamicLocalFileNodeI dynamicLocalFileNode && dynamicLocalFileNode.getTargetPath() != null) {
                fileSelectorControlLocalDirectory.valueProperty().set(new File(dynamicLocalFileNode.getTargetPath()));
            } else {
                fileSelectorControlLocalDirectory.valueProperty().set(null);
            }
            fileChangeListener = (obs, ovF, nvF) -> {
                if (nv instanceof DynamicLocalFileNodeI dynamicLocalFileNode) {
                    dynamicLocalFileNode.setTargetPath(nvF != null ? nvF.getPath() : null);
                }
            };
            fileSelectorControlLocalDirectory.valueProperty().addListener(fileChangeListener);
        }
    }

    @Override
    protected void unbindBidirectionalContent(KeyListNodeI ov, KeyListNodeI nv) {
        super.unbindBidirectionalContent(ov, nv);
        if (ov != null) {
            BindingUtils.unbindAndSetNull(selectedNodeLinkType());

            linkedNodeSelector.selectedKeylistCategoryIdProperty().unbindBidirectional(ov.linkedNodeIdProperty());
            comboBoxLinkType.valueProperty().unbindBidirectional(ov.linkTypeProperty());

            gridSelector.selectedComponentProperty().removeListener(gridChangeListener);
            gridChangeListener = null;
            ov.linkedGridIdProperty().removeListener(gridIdChangeListener);
            gridIdChangeListener = null;

            fileSelectorControlLocalDirectory.valueProperty().removeListener(fileChangeListener);
            fileChangeListener = null;
        }
        gridSelector.clearSelection();
        linkedNodeSelector.setInputKeyNode(null);
    }

    @Override
    public void initListener() {
        super.initListener();
        fileSelectorControlLocalDirectory.setBeforeSelectionValidator(() -> {
            if (fileSelectorControlLocalDirectory.valueProperty().get() != null) {
                return DialogUtils.alertWithSourceAndType(this.fileSelectorControlLocalDirectory, Alert.AlertType.CONFIRMATION)
                        .withContentText(Translation.getText("keylist.dynamic.local.directory.change.warning.content"))
                        .withHeaderText(Translation.getText("keylist.dynamic.local.directory.change.warning.header"))
                        .showAndWait() == ButtonType.OK;
            }
            return true;
        });
    }
}
