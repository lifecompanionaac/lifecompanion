package org.lifecompanion.plugin.aac4all.wp2.controller;

import javafx.beans.value.ChangeListener;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.textprediction.CustomCharPredictionController;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.selectionmode.ComponentToScanI;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.impl.textprediction.charprediction.LCCharPredictor;
import org.lifecompanion.plugin.aac4all.wp2.model.keyoption.AAC4AllKeyOptionCurSta;
import org.lifecompanion.plugin.aac4all.wp2.model.keyoption.AAC4AllKeyOptionReolocG;
import org.lifecompanion.plugin.aac4all.wp2.model.keyoption.AAC4AllKeyOptionReolocL;
import org.lifecompanion.plugin.aac4all.wp2.model.useaction.CurStaUseAction;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.lifecompanion.util.model.SelectionModeUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public enum AAC4AllWp2Controller implements ModeListenerI {
    INSTANCE;

    private final BiConsumer<GridComponentI, ComponentToScanI> scannedPartChangedListener;
    private ChangeListener<Boolean> capitalizeNextChangeListener;

    AAC4AllWp2Controller() {
        scannedPartChangedListener = this::partScanComponentChanged;
    }

    private LCConfigurationI configuration;

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.configuration = configuration;
        CustomCharPredictionController.INSTANCE.forcePredictionLoad();
        SelectionModeController.INSTANCE.addScannedPartChangedListeners(this.scannedPartChangedListener);
        CustomCharPredictionController.INSTANCE.addPredictorStartedListener(predictor -> {
            initRelocG(configuration);
            initCurSta(configuration);
        });
        capitalizeNextChangeListener = (obs, ov, nv) -> {
            if (nv) {
                WritingStateController.INSTANCE.switchCapitalizeNext(WritingEventSource.SYSTEM);
            }
        };
        WritingStateController.INSTANCE.capitalizeNextProperty().addListener(capitalizeNextChangeListener);
    }

    // TODO : replace with content from char prediction
    private String curStaCharacters = "abcdefghijklmnopqrstuvwxyzéèàê' ";
    private List<PredictResult> predict = transformResult(List.of(' ', 'a', 'à', 'b', 'c', 'ç', 'd', 'e', 'é', 'è', 'ê', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '\''));
    private int curStaIndex = 0;
    private List<AAC4AllKeyOptionCurSta> curStaKeys;

    private ChangeListener<String> curStaChangeListener;


    private void initCurSta(LCConfigurationI configuration) {

        Map<GridComponentI, List<AAC4AllKeyOptionCurSta>> curStaKeyOptions = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(AAC4AllKeyOptionCurSta.class, configuration, curStaKeyOptions, null);

        HashSet<Character> acceptedCharact = new HashSet<>(curStaCharacters.chars().mapToObj(c -> (char) c).collect(Collectors.toSet()));
        predict = transformResult(LCCharPredictor.INSTANCE.predict(WritingStateController.INSTANCE.textBeforeCaretProperty().get(), acceptedCharact.size(), acceptedCharact));

        curStaKeys = curStaKeyOptions.values().stream().flatMap(Collection::stream).toList();
        curStaIndex = 0;

        updateCurSta();

        curStaChangeListener = (obs, ov, nv) -> {
            //HashSet<Character> acceptedCharact = new HashSet<>(curStaCharacters.chars().mapToObj(c -> (char) c).collect(Collectors.toSet()));
            if (this.configuration != null) {
                this.configuration.getAllComponent()
                        .values()
                        .stream()
                        .filter(c -> c instanceof UseActionTriggerComponentI)
                        .map(c -> (UseActionTriggerComponentI) c)
                        .map(c -> c.getActionManager().getFirstActionOfType(
                                UseActionEvent.OVER, CurStaUseAction.class))
                        .filter(Objects::nonNull)
                        .forEach(CurStaUseAction::cleanLastText);
            }
            predict = transformResult(LCCharPredictor.INSTANCE.predict(WritingStateController.INSTANCE.textBeforeCaretProperty().get(), acceptedCharact.size(), acceptedCharact));
            curStaIndex = 0;
            updateCurSta();
        };
        WritingStateController.INSTANCE.textBeforeCaretProperty().addListener(curStaChangeListener);
    }

    private static List<PredictResult> transformResult(List<Character> prediction) {
        List<PredictResult> result = new ArrayList<>();
        for (int i = 0; i < prediction.size(); i++) {
            result.add(new PredictResult(AAC4AllKeyOptionCurSta.ActionType.WRITE_PRED, String.valueOf(prediction.get(i))));
        }
        result.add(6, new PredictResult(AAC4AllKeyOptionCurSta.ActionType.DELETE_LAST_CHAR, ""));
        result.add(13, new PredictResult(AAC4AllKeyOptionCurSta.ActionType.VALIDATE, ""));

        for (int i = 0; i < result.size(); i++) {
            if ((i - 7) % 8 == 0 && i >= 7) {
                result.add(i, new PredictResult(AAC4AllKeyOptionCurSta.ActionType.MOVE_BACK, ""));

            }
        }
        return result;
    }

    private final static PredictResult EMPTY = new PredictResult(AAC4AllKeyOptionCurSta.ActionType.WRITE_PRED, "");


    private static class PredictResult {
        private AAC4AllKeyOptionCurSta.ActionType actionType;
        private String predict;

        public PredictResult(AAC4AllKeyOptionCurSta.ActionType actionType, String predict) {
            this.actionType = actionType;
            this.predict = predict;
        }
    }


    private void updateCurSta() {
        FXThreadUtils.runOnFXThread(() -> {
            if (curStaKeys != null) {
                int middleIndex = curStaKeys.size() / 2;
                // From middle to end : display current index to end
                for (int i = middleIndex; i < curStaKeys.size(); i++) {
                    AAC4AllKeyOptionCurSta key = curStaKeys.get(i);
                    if (curStaIndex + i - middleIndex < predict.size()) {
                        updateKeyWith(key, predict.get(curStaIndex + i - middleIndex));
                    } else {
                        updateKeyWith(key, EMPTY);
                    }
                }
                // From middle to start : display previous keys
                int curStaBackIndex = curStaIndex - 1;
                for (int i = middleIndex - 1; i >= 0; i--) {
                    AAC4AllKeyOptionCurSta key = curStaKeys.get(i);
                    if (curStaBackIndex < predict.size() && curStaBackIndex >= 0) {
                        updateKeyWith(key, predict.get(curStaBackIndex));
                    } else {
                        updateKeyWith(key, EMPTY);
                    }
                    curStaBackIndex--;
                }
            }
        });
    }

    private static void updateKeyWith(AAC4AllKeyOptionCurSta key, PredictResult predictResult) {
        key.predictionProperty().set(predictResult.actionType != AAC4AllKeyOptionCurSta.ActionType.WRITE_PRED ? predictResult.actionType.getText() : predictResult.predict);
        key.actionTypeProperty().set(predictResult.actionType);
    }

    public void shiftCurSta() {
        if (curStaIndex + 1 < predict.size()) {
            curStaIndex++;
        } else {
            curStaIndex = 0;
        }
        this.updateCurSta();
    }

    public void moveBackCurSta() {
        this.curStaIndex = Math.max(0, this.curStaIndex - 8);
        if (this.configuration != null) {
            this.configuration.getAllComponent()
                    .values()
                    .stream()
                    .filter(c -> c instanceof UseActionTriggerComponentI)
                    .map(c -> (UseActionTriggerComponentI) c)
                    .map(c -> c.getActionManager().getFirstActionOfType(
                            UseActionEvent.OVER, CurStaUseAction.class))
                    .filter(Objects::nonNull)
                    .forEach(CurStaUseAction::cleanLastText);
        }
        this.updateCurSta();
    }

    public void validerCurSta() {
        AAC4AllWp2EvaluationController.INSTANCE.StartDislaySentence();
        this.curStaIndex = 0;
        this.updateCurSta();
    }

    public void deleteLastCharCurSta() {
        WritingStateController.INSTANCE.removeLastChar(WritingEventSource.USER_ACTIONS);
        this.curStaIndex = 0;
        this.updateCurSta();
    }


    private Map<AAC4AllKeyOptionReolocG, String> previousLineG;
    private ChangeListener<String> relocGChangeListener;

    private void initRelocG(LCConfigurationI configuration) {
        relocGChangeListener = (obs, ov, nv) -> {
            FXThreadUtils.runOnFXThread(() -> {
                SelectionModeI selectionMode = configuration.selectionModeProperty().get();
                List<ComponentToScanI> rows = null;
                if (selectionMode != null && selectionMode.currentGridProperty().get() != null) {
                    rows = SelectionModeUtils.getRowColumnScanningComponents(selectionMode.currentGridProperty().get(), false);
                    for (int i = 0; i < rows.size(); i++) {
                        ComponentToScanI selectedComponentToScan = SelectionModeUtils.getRowColumnScanningComponents(selectionMode.currentGridProperty().get(), false).get(i);

                        previousLineG = new HashMap<>();
                        String charsPreviousLineG = "";

                        //saving the configuration of the line.
                        for (int j = 0; j < selectedComponentToScan.getComponents().size(); j++) {
                            if(selectionMode!=null) {
                                GridPartComponentI gridPartComponent = selectedComponentToScan.getPartIn(selectionMode.currentGridProperty().get(), j);
                                if (gridPartComponent instanceof GridPartKeyComponentI key) {
                                    if (key.keyOptionProperty().get() instanceof AAC4AllKeyOptionReolocG aac4AllKeyOptionReolocG) {
                                        previousLineG.put(aac4AllKeyOptionReolocG, aac4AllKeyOptionReolocG.predictionProperty().get());
                                        charsPreviousLineG = charsPreviousLineG + aac4AllKeyOptionReolocG.predictionProperty().get();
                                    }
                                }
                            }
                        }
                        HashSet<Character> acceptedCharact = new HashSet<>(charsPreviousLineG.chars().mapToObj(c -> (char) c).collect(Collectors.toSet()));
                        List<Character> predict = LCCharPredictor.INSTANCE.predict(WritingStateController.INSTANCE.textBeforeCaretProperty().get(), acceptedCharact.size(), acceptedCharact);
                        LinkedList<Character> charForKeys = new LinkedList<>(acceptedCharact);

                        //modifing the line with character predictionwha
                        int indexPosition = 0; // for save index of prediction for RéoLoc keys
                        for (int j = 0; j < selectedComponentToScan.getComponents().size(); j++) {
                            if(selectionMode!=null) {
                                GridPartComponentI gridPartComponent = selectedComponentToScan.getPartIn(selectionMode.currentGridProperty().get(), j);
                                if (gridPartComponent instanceof GridPartKeyComponentI key) {
                                    if (key.keyOptionProperty().get() instanceof AAC4AllKeyOptionReolocG aac4AllKeyOptionReolocG) {
                                        // There is a prediction
                                        if (j - indexPosition < predict.size()) {
                                            Character pred = predict.get(j - indexPosition);
                                            charForKeys.remove(pred);
                                            aac4AllKeyOptionReolocG.predictionProperty().set(String.valueOf(pred));
                                        }
                                        // No prediction: take char left
                                        else {
                                            aac4AllKeyOptionReolocG.predictionProperty().set(String.valueOf(charForKeys.poll()));
                                        }
                                    } else indexPosition++;
                                }
                            }
                        }
                    }
                }
            });
        };
        WritingStateController.INSTANCE.textBeforeCaretProperty().addListener(relocGChangeListener);
    }


    @Override
    public void modeStop(LCConfigurationI configuration) {
        SelectionModeController.INSTANCE.removeScannedPartChangedListeners(this.scannedPartChangedListener);
        if (relocGChangeListener != null)
            WritingStateController.INSTANCE.textBeforeCaretProperty().removeListener(relocGChangeListener);
        if (curStaChangeListener != null)
            WritingStateController.INSTANCE.textBeforeCaretProperty().removeListener(curStaChangeListener);
        if(capitalizeNextChangeListener!=null){
            WritingStateController.INSTANCE.capitalizeNextProperty().removeListener(capitalizeNextChangeListener);
        }
        curStaKeys = null;
        this.configuration = null;
    }

    private Map<AAC4AllKeyOptionReolocL, String> previousLine;

    public void partScanComponentChanged(GridComponentI gridComponent, ComponentToScanI selectedComponentToScan) {
        FXThreadUtils.runOnFXThread(() -> {
            if (selectedComponentToScan == null) {
                // Should reset previous line to default configuration
                if (previousLine != null) {
                    previousLine.forEach((aac4AllKeyOptionReolocL, previousValue) -> aac4AllKeyOptionReolocL.predictionProperty().set(previousValue));
                }
            } else {
                // Should organize the keys with char prediction
                previousLine = new HashMap<>();
                String charsPreviousLine = "";

                //saving the configuration of the line.
                for (int i = 0; i < selectedComponentToScan.getComponents().size(); i++) {
                    GridPartComponentI gridPartComponent = selectedComponentToScan.getPartIn(gridComponent, i);
                    if (gridPartComponent instanceof GridPartKeyComponentI key) {
                        if (key.keyOptionProperty().get() instanceof AAC4AllKeyOptionReolocL aac4AllKeyOptionReolocL) {
                            previousLine.put(aac4AllKeyOptionReolocL, aac4AllKeyOptionReolocL.predictionProperty().get());
                            charsPreviousLine = charsPreviousLine + aac4AllKeyOptionReolocL.predictionProperty().get();
                        }
                    }
                }

                HashSet<Character> acceptedCharact = new HashSet<>(charsPreviousLine.chars().mapToObj(c -> (char) c).collect(Collectors.toSet()));
                List<Character> predict = LCCharPredictor.INSTANCE.predict(WritingStateController.INSTANCE.textBeforeCaretProperty().get(), acceptedCharact.size(), acceptedCharact);
                LinkedList<Character> charForKeys = new LinkedList<>(acceptedCharact);

                //modifing the line with character predictionwha
                int indexPosition = 0; // for save index of prediction for RéoLoc keys
                for (int i = 0; i < selectedComponentToScan.getComponents().size(); i++) {
                    GridPartComponentI gridPartComponent = selectedComponentToScan.getPartIn(gridComponent, i);
                    if (gridPartComponent instanceof GridPartKeyComponentI key) {
                        if (key.keyOptionProperty().get() instanceof AAC4AllKeyOptionReolocL aac4AllKeyOptionReolocL) {
                            // There is a prediction
                            if (i - indexPosition < predict.size()) {
                                Character pred = predict.get(i - indexPosition);
                                charForKeys.remove(pred);
                                aac4AllKeyOptionReolocL.predictionProperty().set(String.valueOf(pred));
                            }
                            // No prediction: take char left
                            else {
                                aac4AllKeyOptionReolocL.predictionProperty().set(String.valueOf(charForKeys.poll()));
                            }
                        } else indexPosition++;
                    }
                }

            }
        });
    }
}
