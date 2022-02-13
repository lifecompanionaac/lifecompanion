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

package org.lifecompanion.ui.app.generalconfiguration.step.predict4all.correction;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.app.generalconfiguration.step.predict4all.P4AConfigUtils;
import org.lifecompanion.ui.app.generalconfiguration.step.predict4all.P4ACorrectionConfigurationView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.predict4all.nlp.words.correction.CorrectionRule;
import org.predict4all.nlp.words.correction.CorrectionRuleNode;
import org.predict4all.nlp.words.correction.CorrectionRuleNodeType;

import java.util.concurrent.atomic.AtomicReference;

public abstract class CorrectionRuleBaseNodeView extends BorderPane implements LCViewInitHelper {
    protected static final int MAX_LEVEL = 2;
    protected static final long ANIMATION_TIME_MS_FADE = 500, ANIMATION_TIME_MS_ROTATE = 200;

    protected final P4ACorrectionConfigurationView srcView;
    protected final CorrectionRuleParentNodeView parentView;
    protected final CorrectionRuleNode node;
    protected final int level;

    private final AtomicReference<Transition> currentChildrenTransition;
    private boolean childrenDisplayed = false;

    private boolean childViewInitialized;
    private TextField fieldRuleName;
    private Label labelRuleName;
    private Glyph glyphForBtn;
    private Button buttonToggleRuleDisplay;
    private HBox boxTop;
    private ToggleSwitch toggleSwitchEnabled;
    private MenuButton buttonMenu;
    private MenuItem menuItemRemove, menuItemAddCategory, menuItemAddRule;

    public CorrectionRuleBaseNodeView(final P4ACorrectionConfigurationView srcView, final CorrectionRuleParentNodeView parentView, final int level, final CorrectionRuleNode node) {
        this.currentChildrenTransition = new AtomicReference<>();
        this.srcView = srcView;
        this.parentView = parentView;
        this.level = level;
        this.node = node;
    }

    // Class part : "UI init"
    //========================================================================
    @Override
    public void initUI() {
        this.glyphForBtn = GlyphFontRegistry.font("FontAwesome").create(FontAwesome.Glyph.CHEVRON_RIGHT).size(16).color(LCGraphicStyle.MAIN_DARK);
        this.buttonToggleRuleDisplay = FXControlUtils.createGraphicButton(this.glyphForBtn, "tooltip.button.toggle.show.correction.rule");
        this.fieldRuleName = new TextField();
        this.labelRuleName = new Label();
        this.labelRuleName.getStyleClass().add("correction-rule-title");
        this.labelRuleName.setMaxWidth(Double.MAX_VALUE);
        this.toggleSwitchEnabled = new ToggleSwitch();
        this.toggleSwitchEnabled.setScaleX(0.8);
        this.toggleSwitchEnabled.setScaleY(0.8);

        this.menuItemRemove = new MenuItem(Translation.getText("predict4all.menu.button.remove.rule"));
        this.menuItemAddCategory = new MenuItem(Translation.getText("predict4all.menu.button.add.category"));
        this.menuItemAddRule = new MenuItem(Translation.getText("predict4all.menu.button.add.rule"));
        this.buttonMenu = FXControlUtils.createGraphicMenuButton(null, "tooltip.button.toggle.show.correction.rule.menu");
        this.buttonMenu.getItems().addAll(this.menuItemAddRule, this.menuItemAddCategory, new SeparatorMenuItem(), this.menuItemRemove);
        HBox.setMargin(this.buttonMenu, new Insets(0.0, 0.0, 0.0, -10.0));

        this.boxTop = new HBox(3.0, this.buttonToggleRuleDisplay, this.fieldRuleName, this.labelRuleName, this.toggleSwitchEnabled, this.buttonMenu);
        this.boxTop.getStyleClass().add("box-with-bottom-border");
        HBox.setHgrow(this.fieldRuleName, Priority.ALWAYS);
        HBox.setHgrow(this.labelRuleName, Priority.ALWAYS);
        this.boxTop.setAlignment(Pos.CENTER);
        this.boxTop.setPadding(new Insets(1, 5, 0, 5));

        BorderPane.setMargin(this.boxTop, new Insets(3.0, 3.0, 0.0, 3.0));
        this.setTop(this.boxTop);
        this.setPadding(new Insets(0.0, 2.0, 0.0, this.level * 20.0));
    }

    @Override
    public void initListener() {
        this.labelRuleName.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {
                this.editName(true);
            }
        });
        this.fieldRuleName.setOnAction(e -> this.editName(false));
        this.fieldRuleName.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                this.editName(false);
            }
        });
        this.menuItemAddCategory.setOnAction(e -> this.insertChild(null, new CorrectionRuleNode(CorrectionRuleNodeType.NODE)));
        this.menuItemAddRule.setOnAction(e -> {
            CorrectionRuleNode leafNode = new CorrectionRuleNode(CorrectionRuleNodeType.LEAF);
            leafNode.setCorrectionRule(CorrectionRule.ruleBuilder());
            this.insertChild(null, leafNode);
        });
        this.menuItemRemove.setOnAction(e -> this.removeFromParent());
        this.buttonToggleRuleDisplay.setOnAction(e -> this.toggleRuleDisplay());
    }

    @Override
    public void initBinding() {
        this.glyphForBtn.textFillProperty()
                .bind(Bindings.when(this.disabledProperty().or(this.toggleSwitchEnabled.selectedProperty().not())).then(LCGraphicStyle.SECOND_DARK).otherwise(LCGraphicStyle.MAIN_DARK));
        this.toggleSwitchEnabled.setSelected(this.node.isEnabled());
        this.updateDisabledOnChildren();
        this.fieldRuleName.setText(this.node.getName());
        this.editName(false);

        this.menuItemAddCategory.setDisable(this.level >= CorrectionRuleBaseNodeView.MAX_LEVEL);
        this.menuItemAddRule.setDisable(this.isRuleItemDisabled());
        this.menuItemRemove.setDisable(this.level == 0);

        // Bind enabled on change
        this.toggleSwitchEnabled.selectedProperty().addListener((obs, ov, nv) -> {
            this.node.setEnabled(nv);
            this.updateDisabledOnChildren();
        });
    }

    //========================================================================

    // Class part : "UI changes"
    //========================================================================
    private void editName(final boolean edit) {
        if (!this.isDisabled()) {
            this.fieldRuleName.setManaged(edit);
            this.fieldRuleName.setVisible(edit);
            this.labelRuleName.setManaged(!edit);
            this.labelRuleName.setVisible(!edit);
            this.labelRuleName.setText(this.fieldRuleName.getText());
            if (edit) {
                this.fieldRuleName.requestFocus();
            } else {
                this.node.setName(this.fieldRuleName.getText());
            }
        }
    }

    private void toggleRuleDisplay() {
        this.setChildrenDisplay(!this.childrenDisplayed);
    }

    public void setChildrenDisplay(final boolean childrenDisplayed) {
        this.childrenDisplayed = childrenDisplayed;
        if (this.childrenDisplayed) {
            this.initializeChildrenViewsIfNeeded();
        }

        P4AConfigUtils.unsetPreviousTransitionAndStop(this.currentChildrenTransition);

        Node center = this.getCenter();
        center.setVisible(true);
        center.setManaged(true);
        center.setOpacity(childrenDisplayed ? 0.0 : 1.0);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(CorrectionRuleBaseNodeView.ANIMATION_TIME_MS_FADE), center);
        fadeTransition.setFromValue(center.getOpacity());
        fadeTransition.setToValue(childrenDisplayed ? 1.0 : 0.0);
        fadeTransition.setOnFinished(f -> {
            center.setVisible(childrenDisplayed);
            center.setManaged(childrenDisplayed);
        });

        RotateTransition rotateTransition = new RotateTransition(Duration.millis(CorrectionRuleBaseNodeView.ANIMATION_TIME_MS_ROTATE), this.buttonToggleRuleDisplay);
        rotateTransition.setInterpolator(Interpolator.EASE_BOTH);
        rotateTransition.setFromAngle(this.buttonToggleRuleDisplay.getRotate());
        rotateTransition.setToAngle(childrenDisplayed ? 90.0 : 0.0);

        P4AConfigUtils.setCurrentTransitionAndPlay(this.currentChildrenTransition, new ParallelTransition(rotateTransition, fadeTransition));
    }

    protected void initializeChildrenViewsIfNeeded() {
        if (!this.childViewInitialized) {
            this.childViewInitialized = true;
            this.initChildView();
            this.updateDisabledOnChildren();
        }
    }

    void setRuleCenter(final Node center) {
        center.setVisible(this.childrenDisplayed);
        center.setManaged(this.childrenDisplayed);
        this.setCenter(center);
    }

    protected void updateDisabledOnChildren() {
        this.getChildren().stream().filter(n -> n != this.boxTop).forEach(n -> {
            n.setDisable(!this.toggleSwitchEnabled.isSelected());
        });
    }
    //========================================================================

    // Class part : "MODEL"
    //========================================================================
    abstract void insertChild(CorrectionRuleNode previousNode, CorrectionRuleNode child);

    abstract void removeChild(CorrectionRuleNode child);

    abstract void initChildView();

    void removeFromParent() {
        this.parentView.removeChild(this.node);
    }

    protected boolean isRuleItemDisabled() {
        return this.level >= CorrectionRuleBaseNodeView.MAX_LEVEL;
    }

    public CorrectionRuleNode getCorrectionRuleNode() {
        return this.node;
    }
    //========================================================================
}
