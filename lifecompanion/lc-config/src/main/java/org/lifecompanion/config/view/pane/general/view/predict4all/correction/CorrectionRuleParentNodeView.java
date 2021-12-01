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

package org.lifecompanion.config.view.pane.general.view.predict4all.correction;

import java.util.HashMap;
import java.util.Map;

import org.lifecompanion.config.view.pane.general.view.predict4all.P4ACorrectionConfigurationView;
import org.predict4all.nlp.words.correction.CorrectionRuleNode;
import org.predict4all.nlp.words.correction.CorrectionRuleNodeType;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class CorrectionRuleParentNodeView extends CorrectionRuleBaseNodeView {
    private final Map<CorrectionRuleNode, CorrectionRuleBaseNodeView> children;
    private VBox boxRules;

    public CorrectionRuleParentNodeView(final P4ACorrectionConfigurationView srcView, final CorrectionRuleParentNodeView parentView, final int level,
                                        final CorrectionRuleNode node) {
        super(srcView, parentView, level, node);
        this.children = new HashMap<>();
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        super.initUI();
        this.boxRules = new VBox(10.0);
        BorderPane.setMargin(this.boxRules, new Insets(5.0, 0.0, 0.0, 0.0));
        this.setRuleCenter(this.boxRules);
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    @Override
    public void initBinding() {
        super.initBinding();
    }

    @Override
    void initChildView() {
        for (CorrectionRuleNode child : this.node.getChildren()) {
            this._insertChild(null, child, false);
        }
    }
    //========================================================================

    // Class part : "MODEL"
    //========================================================================
    private void _insertChild(final CorrectionRuleNode previousNode, final CorrectionRuleNode child, final boolean insertToModel) {
        CorrectionRuleBaseNodeView childView = child.getType() == CorrectionRuleNodeType.LEAF
                ? new CorrectionRuleLeafNodeView(this.srcView, this, this.level + 1, child)
                : new CorrectionRuleParentNodeView(this.srcView, this, this.level + 1, child);
        this.children.put(child, childView);
        childView.requestLayout();
        int insertIndex = previousNode == null ? 0 : this.boxRules.getChildren().indexOf(this.children.get(previousNode));
        this.boxRules.getChildren().add(insertIndex, childView);
        if (insertToModel) {
            int insertIndexModel = previousNode == null ? 0 : this.node.getChildren().indexOf(previousNode);
            this.node.getChildren().add(insertIndexModel, child);
            childView.setChildrenDisplay(true);
        }
    }

    @Override
    public void insertChild(final CorrectionRuleNode previousNode, final CorrectionRuleNode child) {
        this.initializeChildrenViewsIfNeeded();
        this._insertChild(previousNode, child, true);
    }

    @Override
    void removeChild(final CorrectionRuleNode child) {
        this.initializeChildrenViewsIfNeeded();
        CorrectionRuleBaseNodeView childView = this.children.remove(child);
        // Fix #426 - NPE if node with menu item is delete while visible (see JDK-8244234)
        childView.setVisible(false);
        this.boxRules.getChildren().remove(childView);
        this.node.getChildren().remove(child);
    }
    //========================================================================

}
