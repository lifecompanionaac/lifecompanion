package org.lifecompanion.plugin.flirc.ui.control;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import org.lifecompanion.controller.editmode.ErrorHandlingController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.plugin.flirc.model.IRLearningDialogResult;
import org.lifecompanion.plugin.flirc.model.steps.*;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IRLearningDialog extends Dialog<IRLearningDialogResult> implements LCViewInitHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(IRLearningDialog.class);

    private static final int LEARN_STEP_COUNT = 3;
    private final static List<IRLearningStep> STEPS = new ArrayList<>();

    static {
        STEPS.add(new WaitingDeviceStep());
        STEPS.add(new PrepareLearningStep());
        for (int i = 0; i < LEARN_STEP_COUNT; i++) {
            STEPS.add(new LearningCodeStep(i + 1, LEARN_STEP_COUNT));
        }
    }

    private Label labelTitle, labelDescription;
    private ImageView imageViewStepImage;
    private Button buttonManualStep, buttonCancel;
    private ProgressIndicator progressIndicatorStep;
    private final SimpleObjectProperty<IRLearningStep> currentStep;
    private IRLearningStepTask currentTask;
    private final ExecutorService executorService;

    private final List<List<String>> recordedCodes;

    public IRLearningDialog() {
        this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(true);
        this.setResultConverter(dialogButton -> null);
        this.currentStep = new SimpleObjectProperty<>();
        executorService = Executors.newSingleThreadExecutor(LCNamedThreadFactory.daemonThreadFactory("IRLearningThread"));
        recordedCodes = new ArrayList<>();
        initAll();
    }

    @Override
    public void initUI() {
        labelTitle = new Label();
        labelTitle.getStyleClass().addAll("text-font-size-120", "text-wrap-enabled", "text-weight-bold", "text-fill-primary-dark");
        labelDescription = new Label();
        labelDescription.getStyleClass().addAll("text-wrap-enabled", "text-fill-dimgrey");
        imageViewStepImage = new ImageView();
        imageViewStepImage.setFitHeight(200.0);
        imageViewStepImage.setFitWidth(250.0);
        imageViewStepImage.setPreserveRatio(true);

        buttonManualStep = FXControlUtils.createTextButtonWithBackground(null);

        buttonCancel = FXControlUtils.createLeftTextButton(Translation.getText("flirc.plugin.ui.button.cancel.learning"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TIMES).sizeFactor(1).color(LCGraphicStyle.SECOND_DARK), null);

        progressIndicatorStep = new ProgressIndicator(-1);
        progressIndicatorStep.setPrefSize(30, 30);

        VBox boxContent = new VBox(8.0, imageViewStepImage, labelTitle, labelDescription, progressIndicatorStep, buttonManualStep);
        boxContent.setPrefWidth(400);
        boxContent.setPrefHeight(350);
        boxContent.setAlignment(Pos.TOP_CENTER);

        BorderPane borderPaneContent = new BorderPane(boxContent);
        HBox boxBottom = new HBox(buttonCancel);
        boxBottom.setAlignment(Pos.CENTER);
        borderPaneContent.setBottom(boxBottom);
        this.getDialogPane().setContent(borderPaneContent);
    }

    @Override
    public void initListener() {
        this.setOnShown(e -> currentStep.set(STEPS.get(0)));
        this.setOnHidden(e -> {
            if (currentTask != null) {
                currentTask.cancel();
            }
            executorService.shutdown();
        });
        this.buttonManualStep.setOnAction(e -> {
            IRLearningStep step = currentStep.get();
            if (step != null && step.isManualStep()) {
                buttonManualStep.setVisible(false);
                progressIndicatorStep.setVisible(true);
                runStepTask(step);
            }
        });
        this.buttonCancel.setOnAction(e -> setResult(new IRLearningDialogResult(true, null)));
    }

    @Override
    public void initBinding() {
        buttonManualStep.managedProperty().bind(buttonManualStep.visibleProperty());
        progressIndicatorStep.managedProperty().bind(progressIndicatorStep.visibleProperty());

        this.currentStep.addListener((obs, ov, step) -> {
            labelTitle.setText(step.getName());
            labelDescription.setText(step.getDescription());
            imageViewStepImage.setImage(IconHelper.get(step.getImage()));
            boolean manualStep = step.isManualStep();
            buttonManualStep.setVisible(manualStep);
            progressIndicatorStep.setVisible(!manualStep);
            buttonManualStep.setText(step.getManualStepButtonName());
            if (!manualStep) {
                runStepTask(step);
            }
            String notificationOnShown = step.getNotificationOnShown();
            if (notificationOnShown != null) {
                LCNotification notification = LCNotification.createInfo(notificationOnShown);
                notification.setMsDuration(2500);
                LCNotificationController.INSTANCE.showNotification(notification);
            }
        });
    }

    private void runStepTask(IRLearningStep step) {
        currentTask = step.getTask();
        currentTask.setOnFailed(e -> {
            setResult(new IRLearningDialogResult(true, null));
            ErrorHandlingController.INSTANCE.showExceptionDialog(e.getSource().getException());
        });
        currentTask.setOnSucceeded(e -> {
            if (step.generateCodes()) {
                List<String> result = currentTask.getValue();
                if (!CollectionUtils.isEmpty(result)) {
                    recordedCodes.add(result);
                }
            }
            int i = STEPS.indexOf(step);
            if (i + 1 < STEPS.size()) {
                currentStep.set(STEPS.get(i + 1));
            } else {
                setResult(new IRLearningDialogResult(false, recordedCodes));
            }
        });
        executorService.submit(currentTask);
    }
}
