package org.lifecompanion.plugin.aac4all.wp2.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import org.lifecompanion.controller.appinstallation.InstallationConfigurationController;
import org.lifecompanion.controller.categorizedelement.useaction.UseActionController;
import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.selectionmode.ComponentToScanI;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.model.api.selectionmode.SelectionModeParameterI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.plugin.aac4all.wp2.AAC4AllWp2Plugin;
import org.lifecompanion.plugin.aac4all.wp2.AAC4AllWp2PluginProperties;
import org.lifecompanion.plugin.aac4all.wp2.model.logs.*;
import org.lifecompanion.util.model.SelectionModeUtils;
import tobii.Tobii;


import javafx.beans.value.ChangeListener;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public enum AAC4AllWp2EvaluationController implements ModeListenerI {
    INSTANCE;

    private Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create();

    private final long TRAINING_DURATION_MS = (long) 10* 60 * 1000; //  min à passer en 10 min
    private final long EVALUATION_DURATION_MS = (long) 15 * 60 * 1000;//15 min

    private boolean evaluationMode = false;
    private String filePathLogs;
    private AAC4AllWp2PluginProperties currentAAC4AllWp2PluginProperties;
    private BooleanProperty evaluationRunning;

    private WP2Evaluation currentEvaluation;
    private WP2KeyboardEvaluation currentKeyboardEvaluation;
    private WP2SentenceEvaluation currentSentenceEvaluation;

    private final List<String> phraseSetFR;

    private String currentSentence = "";

    private String functionalCurrentKeyboard = "";
    private GridPartComponentI keyboardConsigne;
    private GridPartComponentI keyboardEVA;
    private GridPartComponentI endGrid;

    private Map<KeyboardType, GridPartComponentI> keyboardsMap;
    private RandomType randomType;
    private int currentRandomIndex;
    private KeyboardType currentKeyboardType;

    private StringProperty patientID;

    public String getFunctionalCurrentKeyboard() {
        return functionalCurrentKeyboard;
    }

    private String instructionCurrentKeyboard = "";

    public String getInstructionCurrentKeyboard() {
        return instructionCurrentKeyboard;
    }

    private GridPartComponentI currentKeyboard;

    public GridPartComponentI getCurrentKeyboard() {
        return currentKeyboard;
    }


    AAC4AllWp2EvaluationController() {
        phraseSetFR = new ArrayList<>();
        try (Scanner scan = new Scanner(ResourceHelper.getInputStreamForPath("/text/PhraseSetFR.txt"), StandardCharsets.UTF_8)) {
            while (scan.hasNextLine()) {
                phraseSetFR.add(StringUtils.trimToEmpty(scan.nextLine()));
            }
        }
    }

    public String getCurrentSentence() {
        return currentSentence;
    }


    private LCConfigurationI configuration;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledEyetrackingTask;

    private ChangeListener highlightKey = new javafx.beans.value.ChangeListener<GridPartComponentI>() {
        @Override
        public void changed(ObservableValue<? extends GridPartComponentI> observable, GridPartComponentI oldValue, GridPartComponentI newValue) {
            if (newValue != null) {
                HighLightLog log = new HighLightLog(newValue.nameProperty().getValue(), newValue.columnProperty().getValue());
                currentSentenceEvaluation.getLogs().add(new WP2Logs(LocalDateTime.now(), LogType.HIGHLIGHT, log));
            }
        }
    };
    private Consumer<ComponentToScanI> highlightRow = new Consumer<ComponentToScanI>() {
        @Override
        public void accept(ComponentToScanI rowScanned) {
            String rowValues = "";
            if (rowScanned != null) {
                for (int i = 0; i < rowScanned.getComponents().size(); i++) {// attention car pout l'espace on a Case(1,1) on pourrait le remplavcer à la main par _ ou par " " par exemple
                    //rowValues = rowScanned.getPartIn(configuration.selectionModeProperty().get().currentGridProperty().get(), i).nameProperty().getValue() + "-";
                    rowValues = rowValues + rowScanned.getPartIn(configuration.selectionModeProperty().get().currentGridProperty().get(), i).nameProperty().getValue() + "-";
                }
                HighLightLog log = new HighLightLog(rowValues, rowScanned.getIndex());
                currentSentenceEvaluation.getLogs().add(new WP2Logs(LocalDateTime.now(), LogType.HIGHLIGHT, log));


            }
        }
    };
    private BiConsumer<GridComponentI, ComponentToScanI> validationRow = new BiConsumer<GridComponentI, ComponentToScanI>() {
            @Override
            public void accept(GridComponentI gridComponentI, ComponentToScanI componentToScanI) {
                if (componentToScanI != null) {
                    String rowValues = "";
                    for (int i = 0; i < componentToScanI.getComponents().size(); i++) {// attention car pout l'espace on a Case(1,1) on pourrait le remplavcer à la main par _ ou par " " par exemple
                        rowValues = rowValues + componentToScanI.getPartIn(gridComponentI, i).nameProperty().getValue() + "-";
                    }
                    ValidationLog log = new ValidationLog(rowValues, componentToScanI.getIndex());
                    currentSentenceEvaluation.getLogs().add(new WP2Logs(LocalDateTime.now(), LogType.VALIDATION, log));
                    recordLogs();
                }
            }
        };
    private BiConsumer<UseActionTriggerComponentI, UseActionEvent> validationKey = new BiConsumer<UseActionTriggerComponentI, UseActionEvent>() {
            @Override
            public void accept(UseActionTriggerComponentI component, UseActionEvent event) {
                if (component instanceof GridPartKeyComponentI && event == UseActionEvent.ACTIVATION) {
                    GridPartKeyComponentI key = (GridPartKeyComponentI) component;
                    ValidationLog log = new ValidationLog(key.nameProperty().getValue(),key.columnProperty().getValue());
                    currentSentenceEvaluation.getLogs().add(new WP2Logs(LocalDateTime.now(), LogType.VALIDATION,log));
                    recordLogs();
                }

            }
        };


        @Override
    public void modeStart(LCConfigurationI configuration) {
        this.configuration = configuration;
        currentAAC4AllWp2PluginProperties = configuration.getPluginConfigProperties(AAC4AllWp2Plugin.ID, AAC4AllWp2PluginProperties.class);
        patientID = currentAAC4AllWp2PluginProperties.patientIdProperty();

        //TODO : nom de fichier log variable avec date id et patient
        File pathToDestinationDir = InstallationConfigurationController.INSTANCE.getUserDirectory();
        filePathLogs = String.format("%s%s_%s.json", pathToDestinationDir + "/lifecompanion-plugins/aac4all-wp2-plugin/result/", patientID.getValue(), LocalDate.now().toString());

        this.keyboardConsigne = this.configuration.getAllComponent().values().stream()
                .filter(d -> d instanceof GridPartComponentI)
                .filter(c -> c.nameProperty().get().startsWith("Consigne"))
                .map(c -> (GridPartComponentI) c)
                .findAny().orElse(null);

        this.keyboardEVA = this.configuration.getAllComponent().values().stream()
                .filter(d -> d instanceof GridPartComponentI)
                .filter(c -> c.nameProperty().get().startsWith("EVA"))
                .map(c -> (GridPartComponentI) c)
                .findAny().orElse(null);

        this.endGrid = this.configuration.getAllComponent().values().stream()
                .filter(d -> d instanceof GridPartComponentI)
                .filter(c -> c.nameProperty().get().startsWith("Fin"))
                .map(c -> (GridPartComponentI) c)
                .findAny().orElse(null);


        KeyboardType[] values = KeyboardType.values();
        keyboardsMap = new HashMap<>();
        for (KeyboardType keyboardType : values) {
            GridPartComponentI keyboard = this.configuration.getAllComponent().values().stream()
                    .filter(d -> d instanceof GridPartComponentI)
                    .filter(c -> c.nameProperty().get().startsWith(keyboardType.getGridName()))
                    .map(c -> (GridPartComponentI) c)
                    .findAny().orElse(null);
            if (keyboard != null) {
                keyboardsMap.put(keyboardType, keyboard);
            }
        }

    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.configuration = null;
        this.currentAAC4AllWp2PluginProperties = null;
        //TODO ; vide tout ce qui est rempli dans modeStart()
    }

    public void startDailyTraining() {
        List<RandomType> randonTypePossible = FXCollections.observableList(Arrays.stream(RandomType.values()).toList());
        int indexTrainingKeyboard = new Random().nextInt(randonTypePossible.size());
        randomType = randonTypePossible.get(indexTrainingKeyboard); // TODO stocker dans properties

        while (randomType.getKeyboards().size() != keyboardsMap.size()) {
            indexTrainingKeyboard = new Random().nextInt(randonTypePossible.size());
            randomType = randonTypePossible.get(indexTrainingKeyboard);
        }
        currentRandomIndex = 0;
        SelectionModeParameterI selectionModeParameter = configuration.getSelectionModeParameter();
        currentEvaluation = new WP2Evaluation(LocalDateTime.now(), patientID.get(), selectionModeParameter.scanPauseProperty().get(), selectionModeParameter.scanFirstPauseProperty().get(), selectionModeParameter.maxScanBeforeStopProperty().get());

        goToNextKeyboardToEvaluate();


    }

    private boolean goToNextKeyboardToEvaluate() {
        if (currentRandomIndex < randomType.getKeyboards().size()) {
            this.currentKeyboardType = randomType.getKeyboards().get(currentRandomIndex++);
            currentKeyboard = keyboardsMap.get(currentKeyboardType);
            updatevariables();
            return true;
        }
        return false;
    }

    public void updatevariables() {
        currentKeyboardEvaluation = new WP2KeyboardEvaluation(currentKeyboardType);
        functionalCurrentKeyboard = Translation.getText("aac4all.wp2.plugin.functional.description." + currentKeyboardType.getTranslationId());
        instructionCurrentKeyboard = Translation.getText("aac4all.wp2.plugin.instruction.description." + currentKeyboardType.getTranslationId());
        UseVariableController.INSTANCE.requestVariablesUpdate();
    }


    public void recordLogs() {
        File resultFile = new File(filePathLogs);
        if (!resultFile.getParentFile().exists()) resultFile.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile))) {
            writer.write(gson.toJson(currentEvaluation));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void emptyAllColors(){
        SelectionModeI selectionMode = configuration.selectionModeProperty().get();
        List<ComponentToScanI> rows = null;
        if (selectionMode != null && selectionMode.currentGridProperty().get() != null) {
            rows = SelectionModeUtils.getRowColumnScanningComponents(selectionMode.currentGridProperty().get(), false);
            for (int i = 0; i < rows.size(); i++) {
                ComponentToScanI selectedComponentToScan = SelectionModeUtils.getRowColumnScanningComponents(selectionMode.currentGridProperty().get(), false).get(i);
                for (int j = 0; j < selectedComponentToScan.getComponents().size(); j++) {
                    GridPartComponentI gridPartComponent = selectedComponentToScan.getPartIn(selectionMode.currentGridProperty().get(), j);//les touches ?
                    gridPartComponent.getKeyStyle().backgroundColorProperty().forced().setValue(null);
                }
            }
        }
    }

    public void nextDailyTraining() {
        currentEvaluation.getEvaluations().add(currentKeyboardEvaluation);
        recordLogs();
        currentKeyboardEvaluation = null;

        emptyAllColors();



            if (!goToNextKeyboardToEvaluate()) {
            SelectionModeController.INSTANCE.goToGridPart(endGrid);
        } else {
            SelectionModeController.INSTANCE.goToGridPart(keyboardConsigne);
        }
    }


    public void setEvaFatigueScore(int score) {
        currentKeyboardEvaluation.setFatigueScore(score);
        recordLogs();}
    public void setEvaFatigueInitScore(int score) {
        currentKeyboardEvaluation.setFatigueInitScore(score);
        recordLogs();}
    public void setEvaSatisfactionScore(Integer score) {
        currentKeyboardEvaluation.setSatisfactionScore(score);
        recordLogs();}


    public void initCurrentKeyboard(){
        currentKeyboardEvaluation = new WP2KeyboardEvaluation(currentKeyboardType);
    }

    public void startLogListener() {
        //currentKeyboardEvaluation = new WP2KeyboardEvaluation(currentKeyboardType);

        if (currentKeyboardEvaluation != null) {

            SelectionModeController.INSTANCE.addScannedPartChangedListeners(validationRow);
            SelectionModeController.INSTANCE.currentOverPartProperty().addListener(highlightKey);
            UseActionController.INSTANCE.addActionExecutionListener(validationKey);
            SelectionModeController.INSTANCE.addOverScannedPartChangedListener(highlightRow);
            // TODO : penser à supprimer les deux nouveaux listeners lorsqu'on termine le log



            scheduler = Executors.newScheduledThreadPool(1);
            scheduledEyetrackingTask = scheduler.scheduleAtFixedRate(() -> {
                float[] position = Tobii.gazePosition();
                float rawGazeX = position[0];
                float rawGazeY = position[1];
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                //System.out.println(screenSize);
                int gazeX = (int) (rawGazeX * screenSize.getWidth());
                int gazeY = (int) (rawGazeY * screenSize.getHeight());
                //System.out.println(rawGazeX);
                //System.out.println(rawGazeY);
                currentSentenceEvaluation.getLogs().add(new WP2Logs(LocalDateTime.now(), LogType.EYETRACKING_POSITION, new EyetrackingPosition(gazeX, gazeY)));
            }, 0, 100, TimeUnit.MILLISECONDS); //0, 20, TimeUnit.MILLISECONDS);
        }
    }

    public void stopLogListener() {
        //TODO stopper les
        //recupérer l'état final de l'éditeur de texte avant clean
        currentSentenceEvaluation.setTextEntry(WritingStateController.INSTANCE.getLastSentence());

        //TODO stop eyetracking logs
        if (scheduledEyetrackingTask != null && !scheduledEyetrackingTask.isCancelled()) {
            scheduler.shutdown();
        }
        //currentSentenceEvaluation = null;
        SelectionModeController.INSTANCE.removeScannedPartChangedListeners(validationRow);
        SelectionModeController.INSTANCE.currentOverPartProperty().removeListener(highlightKey);
        UseActionController.INSTANCE.removeActionExecutionListener(validationKey);
        SelectionModeController.INSTANCE.removeOverScannedPartChangedListener(highlightRow);
    }


    public void startEvaluation() {
        evaluationMode = true;

        // TODO : lancer les claviers en fonction de RandomType donnée dans les réglages.
        randomType = RandomType.fromName(currentAAC4AllWp2PluginProperties.getRandomTypeEval().getValue());


        currentRandomIndex = 0;
        SelectionModeParameterI selectionModeParameter = configuration.getSelectionModeParameter();
        currentEvaluation = new WP2Evaluation(LocalDateTime.now(), patientID.toString(), selectionModeParameter.scanPauseProperty().get(), selectionModeParameter.scanFirstPauseProperty().get(), selectionModeParameter.maxScanBeforeStopProperty().get());
        goToNextKeyboardToEvaluate();
        recordLogs();

    }



    public void startTraining() {
        long time;
        if (evaluationMode) {
            time = EVALUATION_DURATION_MS;
        } else {
            time = TRAINING_DURATION_MS;
        }

        emptyAllColors();




        // TODO: go to currentKeyboardEvaluation
        SelectionModeController.INSTANCE.goToGridPart(currentKeyboard);

        // TODO: clean l'éditeur
        WritingStateController.INSTANCE.removeAll(WritingEventSource.USER_ACTIONS);

        //TODO : affiche les phrases à saisir
        StartDislaySentence();

        // TODO : démarer le listener log
        startLogListener();

        // chrono
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                //TODO stopper le listener validation, hightligh etc
                stopLogListener();

                recordLogs();

                // go to EVA interface
                SelectionModeController.INSTANCE.goToGridPart(keyboardEVA);
                //stop sentence display and clean editor
                StopDislaySentence();
                timer.cancel();

            }
        };
        timer.schedule(timerTask, time);
    }


    public void StartDislaySentence() {

        if (currentSentenceEvaluation == null) {
            currentSentenceEvaluation = new WP2SentenceEvaluation(currentSentence, new Date());
            currentKeyboardEvaluation.getSentenceLogs().add(currentSentenceEvaluation);
            currentSentence = phraseSetFR.get(new Random().nextInt(phraseSetFR.size()));
            UseVariableController.INSTANCE.requestVariablesUpdate();
        } else {
            recordLogs();
            currentSentenceEvaluation.setTextEntry(WritingStateController.INSTANCE.getLastSentence());
            currentSentence = phraseSetFR.get(new Random().nextInt(phraseSetFR.size()));
            UseVariableController.INSTANCE.requestVariablesUpdate();
            currentSentenceEvaluation = new WP2SentenceEvaluation(currentSentence, new Date());
            currentKeyboardEvaluation.getSentenceLogs().add(currentSentenceEvaluation);
            WritingStateController.INSTANCE.removeAll(WritingEventSource.USER_ACTIONS);
            currentSentenceEvaluation.setSentence(currentSentence);
        }
    }

    public void StopDislaySentence() {
        currentSentence = "";
        UseVariableController.INSTANCE.requestVariablesUpdate();
        WritingStateController.INSTANCE.removeAll(WritingEventSource.USER_ACTIONS);
    }


}
