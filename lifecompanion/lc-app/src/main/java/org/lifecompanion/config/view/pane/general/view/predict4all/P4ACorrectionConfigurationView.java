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
package org.lifecompanion.config.view.pane.general.view.predict4all;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.prediction.predict4all.P4AConfigurationSteps;
import org.lifecompanion.base.data.prediction.predict4all.predictor.PredictorModelDto;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStep;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStepViewI;
import org.lifecompanion.config.view.pane.general.view.predict4all.correction.CorrectionRuleParentNodeView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.predict4all.nlp.language.french.FrenchDefaultCorrectionRuleGenerator.CorrectionRuleType;
import org.predict4all.nlp.prediction.PredictionParameter;
import org.predict4all.nlp.words.correction.CorrectionRuleNode;
import org.predict4all.nlp.words.correction.CorrectionRuleNodeType;

public class P4ACorrectionConfigurationView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {

    private PredictionParameter predictionParameter;

    private Slider sliderMaxCost;

    private ComboBox<CorrectionRuleType> comboboxCorrectionRuleTypes;
    private Button buttonAddCorrecitonRuleTypes;

    private ScrollPane scrollPane;
    private CorrectionRuleParentNodeView rootNodeView;

    public P4ACorrectionConfigurationView() {
        this.initAll();
    }

    @Override
    public String getMenuStepToSelect() {
        return GeneralConfigurationStep.PREDICTIONS_CONFIGURATION.name();
    }


    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        // Root view
        this.scrollPane = new ScrollPane();
        this.scrollPane.setFitToWidth(true);

        // Top : general config
        this.sliderMaxCost = new Slider(0.0, P4AConfigUtils.MAX_COST_BOUND, 2.0);
        this.sliderMaxCost.setMinorTickCount(2);
        this.sliderMaxCost.setMajorTickUnit(2.5);
        this.sliderMaxCost.setShowTickMarks(true);
        this.sliderMaxCost.setShowTickLabels(true);
        this.sliderMaxCost.setLabelFormatter(P4ACorrectionConfigurationView.STR_CONVERTER_COST);
        HBox.setMargin(this.sliderMaxCost, new Insets(0.0, 5.0, 0.0, 0.0));

        Label labelSpinnerMaxCost = new Label(Translation.getText("predict4all.config.correction.max.correction.count"));
        HBox boxSpinnerMinCountPred = new HBox(5.0, labelSpinnerMaxCost, this.sliderMaxCost);
        labelSpinnerMaxCost.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelSpinnerMaxCost, Priority.ALWAYS);

        this.comboboxCorrectionRuleTypes = new ComboBox<>(FXCollections.observableArrayList(CorrectionRuleType.values()));
        this.comboboxCorrectionRuleTypes.setCellFactory(lv -> new CorrectionRuleTypeDetailledListCell());
        this.comboboxCorrectionRuleTypes.setButtonCell(new CorrectionRuleTypeListCell());
        this.comboboxCorrectionRuleTypes.setPrefWidth(200);
        this.comboboxCorrectionRuleTypes.setVisibleRowCount(4);
        this.buttonAddCorrecitonRuleTypes = new Button(Translation.getText("predict4all.default.rule.button.add"));

        Label labelDefaultRule = new Label(Translation.getText("predict4all.default.rule.label"));
        HBox boxDefaultRules = new HBox(10.0, labelDefaultRule, this.comboboxCorrectionRuleTypes, this.buttonAddCorrecitonRuleTypes);
        labelDefaultRule.setMaxWidth(Double.MAX_VALUE);
        boxDefaultRules.setAlignment(Pos.CENTER);
        HBox.setHgrow(labelDefaultRule, Priority.ALWAYS);

        VBox boxCenter = new VBox(6.0, UIUtils.createTitleLabel("predict4all.config.part.title.correction.param"), boxSpinnerMinCountPred,
                UIUtils.createTitleLabel("predict4all.config.part.title.correction.rules"), boxDefaultRules, this.scrollPane);
        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        this.setCenter(boxCenter);
    }

    @Override
    public void initListener() {
        this.buttonAddCorrecitonRuleTypes.setOnAction(e -> {
            CorrectionRuleType type = this.comboboxCorrectionRuleTypes.getSelectionModel().getSelectedItem();
            if (type != null) {
                this.comboboxCorrectionRuleTypes.getSelectionModel().clearSelection();
                this.rootNodeView.insertChild(null, type.generateNodeFor(this.predictionParameter));
            }
        });
    }
    //========================================================================

    private CorrectionRuleNode generateDefaultCorrectionNode() {
        CorrectionRuleNode node = new CorrectionRuleNode(CorrectionRuleNodeType.NODE);
        node.setName(Translation.getText("predict4all.rule.default.root.name"));
        return node;
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return false;
    }

    @Override
    public String getTitleId() {
        return "predict4all.config.view.correction.title";
    }

    @Override
    public String getStep() {
        return P4AConfigurationSteps.CORRECTION_CONFIG.name();
    }

    @Override
    public String getPreviousStep() {
        return P4AConfigurationSteps.CONFIG_ROOT_ENTRY_POINT.name();
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
        PredictorModelDto modelDto = (PredictorModelDto) stepArgs[0];
        this.predictionParameter = modelDto.getPredictionParameter();
        if (this.predictionParameter != null) {
            this.sliderMaxCost.setValue(this.predictionParameter.getCorrectionMaxCost());
            this.rootNodeView = new CorrectionRuleParentNodeView(this, null, 0,
                    this.predictionParameter.getCorrectionRulesRoot() != null ? this.predictionParameter.getCorrectionRulesRoot()
                            : this.generateDefaultCorrectionNode());
            this.scrollPane.setContent(this.rootNodeView);
            this.rootNodeView.setChildrenDisplay(true);
        }
    }

    @Override
    public void afterHide() {
        if (this.predictionParameter != null) {
            this.predictionParameter.setCorrectionMaxCost(this.sliderMaxCost.getValue());
            this.predictionParameter.setCorrectionRulesRoot(this.rootNodeView.getCorrectionRuleNode());
            this.predictionParameter = null;
        }
    }

    @Override
    public void saveChanges() {
    }

    @Override
    public void bind(LCConfigurationI model) {

    }

    @Override
    public void unbind(LCConfigurationI model) {
    }

    // STRING CONVERTER COST
    //========================================================================
    private static final StringConverter<Double> STR_CONVERTER_COST = new StringConverter<Double>() {
        @Override
        public String toString(final Double val) {
            double valI = val != null ? val : 0.0;
            if (valI < 2.5) {
                return Translation.getText("predict4all.config.tick.level.low");
            }
            if (valI < P4AConfigUtils.MAX_COST_BOUND) {
                return Translation.getText("predict4all.config.tick.level.medium");
            }
            return Translation.getText("predict4all.config.tick.level.high");
        }

        @Override
        public Double fromString(final String string) {
            return 0.0;
        }
    };


    //========================================================================
}
