package org.lifecompanion.plugin.aac4all.wp2.controller;

import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
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
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.lifecompanion.util.model.SelectionModeUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public enum AAC4AllWp2Controller implements ModeListenerI {
    INSTANCE;

    private final BiConsumer<GridComponentI, ComponentToScanI> scannedPartChangedListener;

    AAC4AllWp2Controller() {
        scannedPartChangedListener = this::partScanComponentChanged;
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        SelectionModeController.INSTANCE.addScannedPartChangedListeners(this.scannedPartChangedListener);
        initRelocG(configuration);
        initCurSta(configuration);
    }

    // TODO : replace with content from char prediction
    //private List<String> curStaContent = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "é", "è", "à", "ê", " ", "'");
    private String curStaCharacters = "abcdefghijklmnopqrstuvwxyzéèàê' ";
    private List<Character> predict = List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'é', 'è', 'à', 'ê', '\'', ' ');

    private int curStaIndex = 0;
    private List<AAC4AllKeyOptionCurSta> curStaKeys;


     private void initCurSta(LCConfigurationI configuration) {
       /*HashSet<Character> acceptedCharact = new HashSet<>(curStaCharacters.chars().mapToObj(c -> (char) c).collect(Collectors.toSet()));
        predict = LCCharPredictor.INSTANCE.predict(WritingStateController.INSTANCE.textBeforeCaretProperty().get(), acceptedCharact.size(), acceptedCharact);
       // LinkedList<Character> charForKeys = new LinkedList<>(acceptedCharact);
        System.out.println("la prédiction est " + predict);
        ConfigurationComponentUtils.findKeyOptionsByGrid(AAC4AllKeyOptionCurSta.class, configuration, curStaKeyOptions, null);
        curStaKeys = curStaKeyOptions.values().stream().flatMap(Collection::stream).toList();
        System.out.println("dans la V0 de l'init");*/
        curStaIndex = 0;
        updateCurSta();

        WritingStateController.INSTANCE.textBeforeCaretProperty().addListener((obs, ov, nv) -> {
            HashSet<Character> acceptedCharact = new HashSet<>(curStaCharacters.chars().mapToObj(c -> (char) c).collect(Collectors.toSet()));
            predict = LCCharPredictor.INSTANCE.predict(WritingStateController.INSTANCE.textBeforeCaretProperty().get(), acceptedCharact.size(), acceptedCharact);
            List<Character> predict = LCCharPredictor.INSTANCE.predict(WritingStateController.INSTANCE.textBeforeCaretProperty().get(), acceptedCharact.size(), acceptedCharact);
            Map<GridComponentI, List<AAC4AllKeyOptionCurSta>> curStaKeyOptions = new HashMap<>();
            //System.out.println("la prédiction est " + predict);
            ConfigurationComponentUtils.findKeyOptionsByGrid(AAC4AllKeyOptionCurSta.class, configuration, curStaKeyOptions, null);
            curStaKeys = curStaKeyOptions.values().stream().flatMap(Collection::stream).toList();
            curStaIndex = 0;
            updateCurSta();});
    }

    private void updateCurSta() {
        FXThreadUtils.runOnFXThread(() -> {
            if (curStaKeys != null) {
                int middleIndex = curStaKeys.size() / 2;
                // From middle to end : display current index to end
                for (int i = middleIndex; i < curStaKeys.size(); i++) {
                    AAC4AllKeyOptionCurSta key = curStaKeys.get(i);
                    if (curStaIndex + i - middleIndex < predict.size()) {
                        key.predictionProperty().set(String.valueOf(predict.get(curStaIndex + i - middleIndex)));
                    } else {
                        key.predictionProperty().set("");
                    }
                }
                // From middle to start : display previous keys
                int curStaBackIndex = curStaIndex - 1;
                for (int i = middleIndex - 1; i >= 0; i--) {
                    AAC4AllKeyOptionCurSta key = curStaKeys.get(i);
                    if (curStaBackIndex < predict.size() && curStaBackIndex >= 0) {
                        key.predictionProperty().set(String.valueOf(predict.get(curStaBackIndex)));
                    } else {
                        key.predictionProperty().set("");
                    }
                    curStaBackIndex--;
                }
            }
        });
    }

    public void shiftCurSta() {
        if (curStaIndex + 1 < predict.size()) {
            curStaIndex++;
        } else {
            curStaIndex = 0;
        }
        this.updateCurSta();
    }

    private Map<AAC4AllKeyOptionReolocG, String> previousLineG;


    private void initRelocG(LCConfigurationI configuration) {
        WritingStateController.INSTANCE.textBeforeCaretProperty().addListener((obs, ov, nv) -> {
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
                            GridPartComponentI gridPartComponent = selectedComponentToScan.getPartIn(selectionMode.currentGridProperty().get(), j);
                            if (gridPartComponent instanceof GridPartKeyComponentI key) {
                                if (key.keyOptionProperty().get() instanceof AAC4AllKeyOptionReolocG aac4AllKeyOptionReolocG) {
                                    previousLineG.put(aac4AllKeyOptionReolocG, aac4AllKeyOptionReolocG.predictionProperty().get());
                                    charsPreviousLineG = charsPreviousLineG + aac4AllKeyOptionReolocG.predictionProperty().get();
                                }
                            }
                        }
                        HashSet<Character> acceptedCharact = new HashSet<>(charsPreviousLineG.chars().mapToObj(c -> (char) c).collect(Collectors.toSet()));
                        List<Character> predict = LCCharPredictor.INSTANCE.predict(WritingStateController.INSTANCE.textBeforeCaretProperty().get(), acceptedCharact.size(), acceptedCharact);
                        LinkedList<Character> charForKeys = new LinkedList<>(acceptedCharact);

                        //modifing the line with character predictionwha
                        int indexPosition = 0; // for save index of prediction for RéoLoc keys
                        for (int j = 0; j < selectedComponentToScan.getComponents().size(); j++) {
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
            });
        });
    }


    @Override
    public void modeStop(LCConfigurationI configuration) {
        SelectionModeController.INSTANCE.removeScannedPartChangedListeners(this.scannedPartChangedListener);
        curStaKeys = null;
    }

    private Map<AAC4AllKeyOptionReolocL, String> previousLine;

    public void partScanComponentChanged(GridComponentI gridComponent, ComponentToScanI selectedComponentToScan) {
        //System.out.println("Scanned part changed " + gridComponent + " : " + selectedComponentToScan);
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
