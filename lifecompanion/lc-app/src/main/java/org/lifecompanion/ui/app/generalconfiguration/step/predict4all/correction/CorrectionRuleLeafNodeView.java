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

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.common.pane.specific.cell.CorrectionCategoryDetailledListCell;
import org.lifecompanion.ui.common.pane.specific.cell.CorrectionCategoryListCell;
import org.lifecompanion.ui.app.generalconfiguration.step.predict4all.P4AConfigUtils;
import org.lifecompanion.ui.app.generalconfiguration.step.predict4all.P4ACorrectionConfigurationView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.predict4all.nlp.words.correction.CorrectionRuleNode;

public class CorrectionRuleLeafNodeView extends CorrectionRuleBaseNodeView {
    private ToggleSwitch toggleBidirectinonal;
    private Slider sliderCost;
    private Label labelList1, labelList2;
    private ItemListFlowPane itemList1, itemList2;
    private ComboBox<CorrectionCategory> comboboxCorrectionCategories;

    public CorrectionRuleLeafNodeView(final P4ACorrectionConfigurationView srcView, final CorrectionRuleParentNodeView parentView, final int level,
                                      final CorrectionRuleNode node) {
        super(srcView, parentView, level, node);
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    private void initUILeaf() {
        this.comboboxCorrectionCategories = new ComboBox<>(FXCollections.observableArrayList(CorrectionCategory.values()));
        this.comboboxCorrectionCategories.setButtonCell(new CorrectionCategoryListCell());
        this.comboboxCorrectionCategories.setCellFactory(lv -> new CorrectionCategoryDetailledListCell());
        this.comboboxCorrectionCategories.setPrefWidth(200);

        Label labelCategories = new Label(Translation.getText("predict4all.config.correction.type.label"));
        HBox boxType = new HBox(5.0, labelCategories, this.comboboxCorrectionCategories);
        boxType.setAlignment(Pos.CENTER);
        HBox.setHgrow(labelCategories, Priority.ALWAYS);
        labelCategories.setMaxWidth(Double.MAX_VALUE);

        this.labelList1 = new Label();
        this.itemList1 = new ItemListFlowPane();
        this.labelList2 = new Label();
        this.itemList2 = new ItemListFlowPane();

        this.toggleBidirectinonal = new ToggleSwitch(Translation.getText("predict4all.config.rule.edit.bidir"));
        this.toggleBidirectinonal.setMaxWidth(Double.MAX_VALUE);

        this.sliderCost = FXControlUtils.createBaseSlider(0.0, 2.0, 1.0);
        this.sliderCost.setMinorTickCount(4);
        this.sliderCost.setMajorTickUnit(1.0);
        this.sliderCost.setShowTickMarks(true);
        this.sliderCost.setShowTickLabels(true);
        this.sliderCost.setLabelFormatter(CorrectionRuleLeafNodeView.STR_CONVERTER_COST);
        this.sliderCost.setPrefWidth(200.0);
        HBox.setMargin(this.sliderCost, new Insets(0.0, 10.0, 0.0, 0.0));
        Label labelCost = new Label(Translation.getText("predict4all.config.correction.cost.frequency"));
        HBox boxCost = new HBox(5.0, labelCost, this.sliderCost);
        boxCost.setAlignment(Pos.CENTER);
        HBox.setHgrow(labelCost, Priority.ALWAYS);
        labelCost.setMaxWidth(Double.MAX_VALUE);

        VBox totalNode = new VBox(5.0, boxType, new Separator(Orientation.HORIZONTAL), this.labelList1, this.itemList1, this.labelList2,
                this.itemList2, new Separator(Orientation.HORIZONTAL), this.toggleBidirectinonal, boxCost);
        totalNode.setAlignment(Pos.CENTER_LEFT);
        totalNode.getChildren().forEach(n -> n.managedProperty().bind(n.visibleProperty()));
        this.setRuleCenter(totalNode);
        totalNode.getStyleClass().add("correction-rule-leaf");
    }

    private void initBindingLeaf() {
        // COST
        Double cost = this.node.getCorrectionRule().getCost();
        if (cost != null) {
            if (cost == 1.0) {
                this.sliderCost.setValue(1.0);
            } else if (cost < 1.0) {
                this.sliderCost.setValue(2.0 - (cost - P4AConfigUtils.COST_BOUND_LOWER) / (1.0 - P4AConfigUtils.COST_BOUND_LOWER));
            } else {
                this.sliderCost.setValue(1.0 - (cost - 1.0) / (P4AConfigUtils.COST_BOUND_UPPER - 1.0));
            }
        }
        this.sliderCost.valueProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                double val = nv.doubleValue();
                if (Math.abs(val - 1.0) < 0.01) {
                    this.node.getCorrectionRule().withCost(1.0);
                } else if (val < 1.0) {
                    this.node.getCorrectionRule().withCost(P4AConfigUtils.COST_BOUND_UPPER - (P4AConfigUtils.COST_BOUND_UPPER - 1.0) * val);
                } else {
                    this.node.getCorrectionRule().withCost(1.0 - (1.0 - P4AConfigUtils.COST_BOUND_LOWER) * (val - 1.0));
                }
            }
        });

        this.toggleBidirectinonal.setSelected(this.node.getCorrectionRule().isBidirectional());
        this.toggleBidirectinonal.selectedProperty().addListener((obs, ov, nv) -> this.node.getCorrectionRule().withBidirectional(nv));

        // CATEGORY
        CorrectionCategory category = P4ACorrectionTranslateUtils.getCorrectionCategoryFor(this.node);
        this.comboboxCorrectionCategories.setValue(category);
        this.updateViewForCategory(category, false);
        this.comboboxCorrectionCategories.valueProperty().addListener((obs, ov, nv) -> this.updateViewForCategory(nv, true));

        // ITEMS IN LIST
        Runnable changeListener = () -> this.updateModelForCategory(this.comboboxCorrectionCategories.getValue());
        this.itemList1.setOnChangeListener(changeListener);
        this.itemList2.setOnChangeListener(changeListener);
    }

    private void updateViewForCategory(final CorrectionCategory category, final boolean clearModel) {
        if (clearModel) {
            this.node.getCorrectionRule().withError();
            this.node.getCorrectionRule().withReplacement();
        }
        if (category != null) {
            this.itemList2.setVisible(false);
            this.labelList2.setVisible(false);
            this.toggleBidirectinonal.setVisible(false);
            switch (category) {
                case CLASSIC:
                    this.labelList1.setText(Translation.getText("predict4all.rule.edit.type.classic.label1"));
                    this.labelList2.setText(Translation.getText("predict4all.rule.edit.type.classic.label2"));
                    this.labelList2.setVisible(true);
                    this.itemList2.setVisible(true);
                    this.itemList1.setItems(this.node.getCorrectionRule().getErrors());
                    this.itemList2.setItems(this.node.getCorrectionRule().getReplacements());
                    this.toggleBidirectinonal.setVisible(true);
                    break;
                case INSERT:
                    this.labelList1.setText(Translation.getText("predict4all.rule.edit.type.insert.label"));
                    this.itemList1.setItems(this.node.getCorrectionRule().getReplacements());
                    break;
                case DELETE:
                    this.labelList1.setText(Translation.getText("predict4all.rule.edit.type.delete.label"));
                    this.itemList1.setItems(this.node.getCorrectionRule().getErrors());
                    break;
                case CONFUSION:
                    this.labelList1.setText(Translation.getText("predict4all.rule.edit.type.confusion.label"));
                    this.itemList1.setItems(this.node.getCorrectionRule().getErrors());
                    break;
                default:
                    break;
            }
        } else {
            this.toggleBidirectinonal.setVisible(false);
            this.labelList1.setVisible(false);
            this.itemList1.setVisible(false);
            this.labelList2.setVisible(false);
            this.itemList2.setVisible(false);
        }
    }

    private void updateModelForCategory(final CorrectionCategory category) {
        switch (category) {
            case CLASSIC:
                this.node.getCorrectionRule().withError(this.itemList1.getItems());
                this.node.getCorrectionRule().withReplacement(this.itemList2.getItems());
                break;
            case INSERT:
                this.node.getCorrectionRule().withError("");
                this.node.getCorrectionRule().withReplacement(this.itemList1.getItems());
                break;
            case DELETE:
                this.node.getCorrectionRule().withReplacement("");
                this.node.getCorrectionRule().withError(this.itemList1.getItems());
                break;
            case CONFUSION:
                this.node.getCorrectionRule().withError(this.itemList1.getItems());
                this.node.getCorrectionRule().withReplacement(this.itemList1.getItems());
                break;
            default:
                break;
        }
    }

    @Override
    void initChildView() {
        this.initUILeaf();
        this.initBindingLeaf();
    }
    //========================================================================

    // Class part : "MODEL"
    //========================================================================
    @Override
    void insertChild(final CorrectionRuleNode previousNode, final CorrectionRuleNode child) {
        this.parentView.insertChild(this.node, child);
    }

    @Override
    protected boolean isRuleItemDisabled() {
        return this.level > CorrectionRuleBaseNodeView.MAX_LEVEL;
    }

    @Override
    void removeChild(final CorrectionRuleNode child) {
    }
    //========================================================================

    // Class part : "STRING CONVERTER COST"
    //========================================================================
    private static final StringConverter<Double> STR_CONVERTER_COST = new StringConverter<Double>() {

        @Override
        public String toString(final Double val) {
            double valI = val != null ? val : 0.0;
            if (valI < 1.0) {
                return Translation.getText("predict4all.config.tick.rule.leaf.frequency.low");
            }
            if (valI == 1.0) {
                return Translation.getText("predict4all.config.tick.rule.leaf.frequency.medium");
            }
            return Translation.getText("predict4all.config.tick.rule.leaf.frequency.high");
        }

        @Override
        public Double fromString(final String string) {
            return 0.0;
        }
    };
    //========================================================================

}
