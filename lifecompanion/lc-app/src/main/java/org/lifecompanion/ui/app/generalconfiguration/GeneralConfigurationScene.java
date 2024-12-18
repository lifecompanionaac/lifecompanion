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

package org.lifecompanion.ui.app.generalconfiguration;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.app.generalconfiguration.step.*;
import org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.KeyListNodeMainConfigurationStepView;
import org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.UserActionSequenceMainConfigurationStepView;
import org.lifecompanion.ui.app.generalconfiguration.step.predict4all.*;
import org.lifecompanion.ui.common.pane.generic.AnimatedBorderPane;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GeneralConfigurationScene extends Scene implements LCViewInitHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralConfigurationScene.class);

    private final Map<String, GeneralConfigurationStepViewI> views;

    private final BorderPane rootBorderBorderPane;
    private AnimatedBorderPane viewContentBorderPane;

    private GeneralConfigurationStepViewI currentView;

    private VBox boxMenuLeft;
    private Button buttonOk, buttonCancel;
    private Label labelTitle;
    private Node nodePreviousIndicator;

    private final Map<String, Label> stepButtons;
    private LCConfigurationI boundConfiguration;

    public GeneralConfigurationScene() {
        super(new BorderPane());
        rootBorderBorderPane = (BorderPane) getRoot();
        this.views = new HashMap<>();
        this.stepButtons = new HashMap<>();
        this.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        PluginController.INSTANCE.getStylesheets().registerListenerAndDrainCache((stylesheets) -> this.getStylesheets().addAll(stylesheets));
        // DON'T CALL initAll() > it's loaded in background on app startup
    }

    @Override
    public void initUI() {
        // Left part : menu
        boxMenuLeft = new VBox();
        boxMenuLeft.setPrefWidth(200.0);
        boxMenuLeft.setAlignment(Pos.TOP_LEFT);
        boxMenuLeft.getStyleClass().addAll("background-primary-dark", "border-width-0");

        // Init step implementations
        addStepImplementation(new GeneralInformationConfigurationStepView());
        addStepImplementation(new VoiceSynthesizerMainConfigurationStepView());
        addStepImplementation(new VoiceSynthesizerExceptionConfigurationStepView());
        addStepImplementation(new VirtualMouseConfigurationStepView());
        addStepImplementation(new ConfigurationStyleConfigurationStepView());
        addStepImplementation(new UseStageConfigurationStepView());
        addStepImplementation(new PredictorsConfigurationStepView());
        addStepImplementation(new CharPredictionConfigurationStepView());
        addStepImplementation(new SelectionModeMainConfigurationStepView());
        addStepImplementation(new SelectionModeSuppConfigurationStepView());
        addStepImplementation(new Predict4AllRootEntryConfigurationView());
        addStepImplementation(new P4ADictionaryConfigurationView());
        addStepImplementation(new P4ACorrectionConfigurationView());
        addStepImplementation(new P4ATrainingConfigurationView());
        addStepImplementation(new P4ATestingConfigurationView());
        addStepImplementation(new UseEventListMainConfigurationStepView());
        addStepImplementation(new KeyListNodeMainConfigurationStepView());
        addStepImplementation(new UserActionSequenceMainConfigurationStepView());

        // Center top : title and previous button
        Triple<HBox, Label, Node> header = FXControlUtils.createHeader("", this::previousClic);
        labelTitle = header.getMiddle();
        nodePreviousIndicator = header.getRight();
        boxMenuLeft.setPadding(new Insets(50.0, 0.0, 0.0, 0.0));

        // Center main : step view display
        viewContentBorderPane = new AnimatedBorderPane();

        // Center bottom : ok, cancel buttons
        buttonOk = FXControlUtils.createLeftTextButton(Translation.getText("general.configuration.scene.ok.button"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHECK).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        buttonCancel = FXControlUtils.createLeftTextButton(Translation.getText("general.configuration.scene.cancel.button"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TIMES).size(16).color(LCGraphicStyle.SECOND_DARK), null);
        HBox buttonBox = new HBox(buttonCancel, buttonOk);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setMinHeight(20.0);
        buttonBox.setPrefHeight(20.0);
        BorderPane.setMargin(buttonBox, new Insets(10.0));

        // Center : put it together
        BorderPane borderPaneCenter = new BorderPane();
        borderPaneCenter.setTop(header.getLeft());
        borderPaneCenter.setCenter(viewContentBorderPane);
        borderPaneCenter.setBottom(buttonBox);

        this.rootBorderBorderPane.setLeft(boxMenuLeft);
        this.rootBorderBorderPane.setCenter(borderPaneCenter);
    }

    private void addStepImplementation(GeneralConfigurationStepViewI stepView) {
        this.views.put(stepView.getStep(), stepView);

        if (stepView != null && stepView.shouldBeAddedToMainMenu()) {
            // Create menu button
            Label button = new Label(Translation.getText(stepView.getTitleId()));
            button.setMaxWidth(Double.MAX_VALUE);
            button.getStyleClass().addAll("text-fill-white", "text-font-size-120", "padding-t10-rbl5", "border-bottom-lightgrey", "opacity-85-hover");
            stepButtons.put(stepView.getStep(), button);

            // Listener on button to show step
            button.setOnMouseClicked(m -> {
                GeneralConfigurationController.INSTANCE.showStep(stepView.getStep());
            });

            // Order buttons from their preferred relative order
            boxMenuLeft.getChildren().clear();
            views.values().stream()
                 .filter(v -> v.shouldBeAddedToMainMenu())
                 .sorted(Comparator.comparingInt(GeneralConfigurationStepViewI::getStepMenuOrder))
                 .forEach(configStep -> boxMenuLeft.getChildren().add(stepButtons.get(configStep.getStep())));
        }
    }

    private void previousClic(Node node) {
        if (this.currentView.getPreviousStep() != null) {
            GeneralConfigurationController.INSTANCE.showStep(this.currentView.getPreviousStep());
        }
    }

    @Override
    public void initListener() {
        this.buttonCancel.setOnAction(e -> cancelSelected(buttonCancel));
        this.buttonOk.setOnAction(e -> okSelected());
        SystemVirtualKeyboardController.INSTANCE.registerScene(this);
        SessionStatsController.INSTANCE.registerScene(this);
    }

    @Override
    public void initBinding() {
        GeneralConfigurationController.INSTANCE.currentStepProperty().addListener((obs, ov, nv) -> showStep(nv));
        GeneralConfigurationController.INSTANCE.enableTransitionProperty().addListener((obs, ov, nv) -> viewContentBorderPane.setEnableTransition(nv));
        PluginController.INSTANCE.getGeneralConfigurationSteps().registerListenerAndDrainCache(generalConfigViewType -> {
            try {
                this.addStepImplementation(generalConfigViewType.getConstructor().newInstance());
            } catch (Throwable e) {
                LOGGER.error("Couldn't create step implementation from type {}", generalConfigViewType, e);
                throw e;
            }
        });
    }

    public boolean shouldCancelBeConfirmed() {
        for (GeneralConfigurationStepViewI view : this.views.values()) {
            if (view.shouldCancelBeConfirmed()) return true;
        }
        return false;
    }

    void cancelSelected(Node source) {
        if (this.shouldCancelBeConfirmed()) {
            if (DialogUtils
                    .alertWithSourceAndType(source, Alert.AlertType.CONFIRMATION)
                    .withContentText(Translation.getText("general.config.scene.cancel.warning.message"))
                    .withHeaderText(Translation.getText("general.config.scene.cancel.warning.header"))
                    .showAndWait() != ButtonType.OK) {
                return;
            }
        }
        clearCurrentStepAndDoOnEverySteps(GeneralConfigurationStepViewI::cancelChanges);
    }

   public void okSelected() {
        clearCurrentStepAndDoOnEverySteps(GeneralConfigurationStepViewI::saveChanges);
        AppModeController.INSTANCE.getEditModeContext().increaseUnsavedActionOnCurrentConfiguration();
    }

    private void clearCurrentStepAndDoOnEverySteps(Consumer<GeneralConfigurationStepViewI> method) {
        GeneralConfigurationController.INSTANCE.clearCurrentStep();
        for (GeneralConfigurationStepViewI view : this.views.values()) {
            method.accept(view);
        }
        this.getWindow().hide();
    }

    public void showStep(String step) {
        // Handle previous step
        if (currentView != null) {
            Label stepButton = stepButtons.get(currentView.getMenuStepToSelect());
            if (stepButton != null) {
                stepButton.getStyleClass().addAll("text-fill-white");
                stepButton.getStyleClass().removeAll("border-top-lightgrey", "background-f4f4f4");
            }
            currentView.afterHide();
            currentView = null;
        }
        // Handle new step
        if (step != null) {
            currentView = this.views.get(step);
            currentView.beforeShow(GeneralConfigurationController.INSTANCE.getStepArgs());
            this.labelTitle.setText(Translation.getText(currentView.getTitleId()));
            this.nodePreviousIndicator.setVisible(currentView.getPreviousStep() != null);
            Label stepButton = stepButtons.get(currentView.getMenuStepToSelect());
            if (stepButton != null) {
                stepButton.getStyleClass().removeAll("text-fill-white");
                stepButton.getStyleClass().addAll("border-top-lightgrey", "background-f4f4f4");
            }
            this.viewContentBorderPane.changeCenter(currentView.getViewNode());
        }
    }

    public void onShowing() {
        // Bind on every step if there is a configuration
        this.boundConfiguration = AppModeController.INSTANCE.getEditModeContext().configurationProperty().get();
        if (boundConfiguration != null) {
            for (GeneralConfigurationStepViewI view : this.views.values()) {
                view.bind(boundConfiguration);
            }
        }
    }

    public void onHiding() {
        // Unbind on every step
        if (boundConfiguration != null) {
            for (GeneralConfigurationStepViewI view : this.views.values()) {
                view.unbind(boundConfiguration);
            }
        }
        this.boundConfiguration = null;
    }
}
