package org.lifecompanion.plugin.flirc.ui.control;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.plugin.flirc.controller.FlircController;
import org.lifecompanion.plugin.flirc.utils.FlircUtils;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IRRecorderField extends VBox implements LCViewInitHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(IRRecorderField.class);

    private final static double SIMILARITY_THRESHOLD = 0.98;

    public static final DecimalFormat PERCENT_DECIMAL_FORMAT = new DecimalFormat("##0.00");

    private Button buttonLearnCode;
    private Button buttonTestCode;
    private Label labelStatus;

    private final StringProperty value;

    public IRRecorderField() {
        this.value = new SimpleStringProperty();
        this.initAll();
    }

    public StringProperty valueProperty() {
        return value;
    }

    @Override
    public void initUI() {
        Label labelExplain = new Label(Translation.getText("flirc.plugin.ui.label.explain.record"));
        labelExplain.getStyleClass().addAll("text-wrap-enabled", "text-fill-dimgrey");

        //UI
        this.buttonLearnCode = FXControlUtils.createLeftTextButton(Translation.getText("flirc.plugin.ui.button.learn.remote.code"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CIRCLE).sizeFactor(1).color(LCGraphicStyle.SECOND_DARK), null);

        this.labelStatus = new Label();

        buttonTestCode = FXControlUtils.createLeftTextButton(Translation.getText("flirc.plugin.ui.button.test.code"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLAY).sizeFactor(1).color(LCGraphicStyle.MAIN_PRIMARY),
                null);

        //Total
        HBox recordBox = new HBox(5.0, this.buttonLearnCode, new Separator(Orientation.VERTICAL), labelStatus, new Separator(Orientation.VERTICAL), buttonTestCode);
        recordBox.setAlignment(Pos.CENTER);
        this.setSpacing(10.0);
        this.getChildren().addAll(labelExplain, recordBox);
    }

    @Override
    public void initBinding() {
        buttonTestCode.disableProperty().bind(Bindings.createBooleanBinding(() -> !StringUtils.isNotBlank(value.get()), value));
        this.labelStatus.textProperty()
                .bind(Bindings.createStringBinding(() -> Translation.getText(StringUtils.isNotBlank(value.get()) ? "flirc.plugin.ui.info.button.recorded" : "flirc.plugin.ui.info.button.not.recorded"),
                        value));
    }


    @Override
    public void initListener() {
        this.buttonTestCode.setOnAction(e -> {
            try {
                // FIXME thread
                FlircController.INSTANCE.sendIr(value.get());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        this.buttonLearnCode.setOnAction(e -> {
            IRLearningDialog irLearningDialog = new IRLearningDialog();
            irLearningDialog.initOwner(FXUtils.getSourceWindow(buttonLearnCode));
            irLearningDialog.showAndWait().ifPresent(result -> {
                // Keep always the longest code
                if (!result.cancelled()) {
                    // Keep the longest code
                    List<List<String>> recordings = result.codes();
                    if (!CollectionUtils.isEmpty(recordings)) {
                        List<String> patternToCompare = new ArrayList<>();
                        for (List<String> recording : recordings) {
                            recording.stream().max(Comparator.comparingInt(StringUtils::safeLength)).ifPresent(patternToCompare::add);
                        }
                        // Compare all code pairs and sort them by the best correlation between
                        List<Pair<Double, Pair<String, String>>> comparisons = new ArrayList<>();
                        for (int i = 0; i < patternToCompare.size(); i++) {
                            for (int j = 0; j < patternToCompare.size(); j++) {
                                if (i != j) {
                                    String p1 = patternToCompare.get(i);
                                    String p2 = patternToCompare.get(j);
                                    comparisons.add(Pair.of(FlircUtils.correlationBetween(FlircUtils.toIntArray(p1), FlircUtils.toIntArray(p2)), Pair.of(p1, p2)));
                                }
                            }
                        }
                        FlircUtils.debugComparisons(comparisons);
                        Pair<Double, Pair<String, String>> bestMatchingCode = comparisons.stream().max(Comparator.comparingDouble(Pair::getLeft)).orElse(null);
                        if (bestMatchingCode != null) {
                            if (bestMatchingCode.getLeft() < SIMILARITY_THRESHOLD) {
                                DialogUtils
                                        .alertWithSourceAndType(buttonLearnCode, Alert.AlertType.WARNING)
                                        .withHeaderText(Translation.getText("flirc.plugin.ui.alert.warning.similarity.header"))
                                        .withContentText(Translation.getText("flirc.plugin.ui.alert.warning.similarity.message", PERCENT_DECIMAL_FORMAT.format(100.0 * bestMatchingCode.getLeft())))
                                        .withButtonTypes(ButtonType.OK)
                                        .showAndWait();
                            }else {
                                LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("flirc.plugin.notification.success.learning.code"));
                            }
                            this.value.set(patternToCompare.get(0));
                        } else {
                            informNoValidCodeDetected();
                        }
                    } else {
                        informNoValidCodeDetected();
                    }
                }
            });
        });
    }


    private void informNoValidCodeDetected() {
        LCNotificationController.INSTANCE.showNotification(LCNotification.createError("flirc.plugin.ui.error.code.not.recorded"));
    }
}
