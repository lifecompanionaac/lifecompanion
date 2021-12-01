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

package org.lifecompanion.config.view.pane.general.view.simplercomp;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.simplercomp.KeyListNodeI;
import org.lifecompanion.base.data.control.KeyListController;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStep;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStepViewI;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class KeyListNodeMainConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private LCConfigurationI model;
    private KeyListContentConfigView keyListContentConfigView;

    public KeyListNodeMainConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.view.step.key.list.categories.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.KEY_LIST_NODE.name();
    }

    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    // UI
    //========================================================================
    @Override
    public void initUI() {
        keyListContentConfigView = new KeyListContentConfigView();
        this.setCenter(keyListContentConfigView);
        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING/2, GeneralConfigurationStepViewI.PADDING, GeneralConfigurationStepViewI.PADDING, GeneralConfigurationStepViewI.PADDING));
    }
    //========================================================================


    KeyListNodeI editedRoot;

    @Override
    public void saveChanges() {
        this.model.rootKeyListNodeProperty().set(editedRoot);
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        editedRoot = (KeyListNodeI) model.rootKeyListNodeProperty().get().duplicate(false);
        this.keyListContentConfigView.rootKeyListNodeProperty().set(editedRoot);
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.model = null;
        this.editedRoot = null;
        keyListContentConfigView.rootKeyListNodeProperty().set(null);
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
        if (stepArgs != null && stepArgs.length > 0) {
            String nodeId = (String) stepArgs[0];
            final KeyListNodeI nodeToEditInCurrentTree = KeyListController.findNodeByIdInSubtree(editedRoot, nodeId);
            if (nodeToEditInCurrentTree != null) {
                keyListContentConfigView.selectToBeEditedInTree(nodeToEditInCurrentTree);
            }
        }
    }
}
