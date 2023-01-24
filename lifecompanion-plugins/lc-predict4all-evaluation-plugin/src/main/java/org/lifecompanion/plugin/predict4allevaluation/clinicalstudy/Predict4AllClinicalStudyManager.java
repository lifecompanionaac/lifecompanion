/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.predict4allevaluation.clinicalstudy;

import com.google.gson.*;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.textprediction.WordPredictionController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.textcomponent.WritingControllerStateI;
import org.lifecompanion.model.api.textcomponent.WritingEventI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.textcomponent.WritingEventType;
import org.lifecompanion.model.api.textprediction.WordPredictionI;
import org.lifecompanion.model.api.textprediction.WordPredictionResultI;
import org.lifecompanion.model.impl.textcomponent.WritingControllerState;
import org.lifecompanion.model.impl.textcomponent.WritingEvent;
import org.lifecompanion.model.impl.textprediction.WordPrediction;
import org.lifecompanion.model.impl.textprediction.WordPredictionResult;
import org.lifecompanion.model.impl.textprediction.predict4all.Predict4AllWordPredictor;
import org.lifecompanion.model.impl.textprediction.predict4all.Predict4AllWordPredictorHelper;
import org.lifecompanion.util.IOUtils;
import org.predict4all.nlp.Separator;
import org.predict4all.nlp.prediction.PredictionParameter;
import org.predict4all.nlp.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//TODO : writing logs to the file should be async and use a queue+thread
public enum Predict4AllClinicalStudyManager implements ModeListenerI {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(Predict4AllClinicalStudyManager.class);

    private static final String LOG_DIR = "writing-logs";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");

    private final Consumer<WritingEventI> WRITING_LISTENER = this::handleWritingEvent;

    public static final String VAR_STUDY_LOGGING_STATUS = "LoggingStatus";

    /**
     * Needed selection to use the word prediction (1 = direct selection, 2 = scanning selection)
     */
    private static final int NEEDED_SELECTION_TO_USE_PREDICTION = 1;

    /**
     * Char count added after prediction (typically 1 : a space)
     */
    private static int AFTER_PRED_ADDED_CHAR = 1;

    private static final long SPEED_DIFF = 30 * 1000;

    /**
     * Listener that will be fired on start/end
     */
    private Runnable startCallback, endCallback;

    // Class part : "Logging"
    //========================================================================
    private static final Gson GSON_INPUT = new GsonBuilder()//
            .setPrettyPrinting()//
            .registerTypeAdapter(WritingControllerStateI.class, new WritingControllerStateAdapter())//
            .registerTypeAdapter(WordPredictionResultI.class, new WordPredictionResultAdapter())//
            .registerTypeAdapter(WordPredictionI.class, new WordPredictionAdapter())//
            .create();

    public static final Gson GSON_OUTPUT = new GsonBuilder()//
            .setPrettyPrinting()//
            .create();

    private PrintStream currentPrintStream;
    private File currentLogDirectory;
    private boolean firstLogWritten;
    private ClinicalStudyTestInformationDto currentTestInformation;

    /**
     * Indicate if the logging was once enabled = if logging is enabled, this is because the clinical study tests are executed.</br>
     * So this is never reset : if LifeCompanion is launched and the clinical test is done, training is disabled until next LC launch.
     */
    private boolean loggingWasEnabledInThisUse;

    private void handleWritingEvent(final WritingEventI event) {
        if (this.currentPrintStream != null) {
            if (this.firstLogWritten) {
                this.currentPrintStream.println(",");
            }
            this.currentPrintStream.println(Predict4AllClinicalStudyManager.GSON_OUTPUT.toJson(event));
        }
        this.firstLogWritten = true;
    }
    //========================================================================

    // Class part : "PUBLIC"
    //========================================================================
    /*
     * When logging start, should have the following informations :
     * - User name or ID
     * - Date (generated)
     * - Situation/context (V1 - dictée sans prédiction)
     * - Predictions data ?
     * - Prediction configuration ?
     */
    private Boolean savedEnableWordCorrection = null;
    private Integer savedMinCountToProvidePrediction = null;
    private Integer savedMinCountToProvideCorrection = null;

    public void startLoging(ClinicalStudyTestInformationDto information) {
        this.loggingWasEnabledInThisUse = true;
        if (this.currentPrintStream == null) {
            this.currentTestInformation = information;

            // WARNING : Not reliable cast
            Predict4AllWordPredictor predictor = (Predict4AllWordPredictor) WordPredictionController.INSTANCE.getDefaultPredictor();
            try {
                // Change prediction configuration with the context
                if (information != null && information.getContext() != null && predictor.getWordPredictor() != null) {
                    PredictionParameter predictionParameter = predictor.getWordPredictor().getPredictionParameter();
                    this.savedEnableWordCorrection = predictionParameter.isEnableWordCorrection();
                    this.savedMinCountToProvidePrediction = predictionParameter.getMinCountToProvidePrediction();
                    this.savedMinCountToProvideCorrection = predictionParameter.getMinCountToProvideCorrection();
                    switch (information.getContext()) {
                        case WITHOUT_PREDICTION:
                            predictionParameter.setMinCountToProvidePrediction(Integer.MAX_VALUE);
                            predictionParameter.setEnableWordCorrection(false);
                            break;
                        case WITH_PREDICTION:
                            predictionParameter.setEnableWordCorrection(false);
                            break;
                        case WITH_PREDICTION_CORRECTION:
                            predictionParameter.setEnableWordCorrection(true);
                            break;
                        default:
                            break;
                    }
                }

                // Create directory
                LCConfigurationI configuration = AppModeController.INSTANCE.getUseModeContext().getConfiguration();
                currentLogDirectory = new File(Predict4AllWordPredictorHelper.getAndInitializeCurrentProfileRoot(configuration.getID()) + File.separator + Predict4AllClinicalStudyManager.LOG_DIR + File.separator + "TEST-" + Predict4AllClinicalStudyManager.FORMAT.format(
                        new Date()) + "-" + IOUtils.getValidFileName(information != null ? information.getUserId() : ProfileController.INSTANCE.currentProfileProperty().get().nameProperty().get()));
                currentLogDirectory.mkdirs();
                // Copy prediction configuration
                //                File srcConfigPath = Predict4AllWordPredictor.INSTANCE.getCurrentConfigurationPath();
                //                if (srcConfigPath.exists()) {
                //                    File outConfigPath = new File(currentLogDirectory.getPath() + File.separator + srcConfigPath.getName());
                //                    IOUtils.copyFiles(srcConfigPath, outConfigPath);
                //                    LOGGER.info("Copied prediction configuration, from {} to {}", srcConfigPath, outConfigPath);
                //                }
                // Write information
                if (information != null) {
                    File infoPath = new File(currentLogDirectory.getPath() + File.separator + "informations.json");
                    try (PrintWriter infoPW = new PrintWriter(infoPath, "UTF-8")) {
                        infoPW.println(GSON_OUTPUT.toJson(information));
                    }
                    LOGGER.info("Clinical test information written to {}", infoPath.getPath());
                }
                // Initialize log file
                File logFile = new File(currentLogDirectory.getPath() + File.separator + "raw-log.json");
                Predict4AllClinicalStudyManager.LOGGER.info("Start clinical study test logging in {}", logFile.getPath());

                //TODO : buffer!
                this.currentPrintStream = new PrintStream(logFile, "UTF-8");
                this.currentPrintStream.println("[");
                this.firstLogWritten = false;

                if (this.startCallback != null) {
                    this.startCallback.run();
                }
            } catch (Exception e) {
                Predict4AllClinicalStudyManager.LOGGER.error("Couldn't create log file", e);
            }
            WritingStateController.INSTANCE.addWritingEventListener(this.WRITING_LISTENER);
        } else {
            LOGGER.warn("Ignored start logging on clinical study manager because logging was already started");
        }
    }

    public void stopLogging() {
        Predict4AllWordPredictor predictor = (Predict4AllWordPredictor) WordPredictionController.INSTANCE.getDefaultPredictor();
        WritingStateController.INSTANCE.removeWritingEventListener(this.WRITING_LISTENER);
        if (this.currentPrintStream != null) {
            this.currentPrintStream.println("]");
            this.currentPrintStream.close();
            this.currentPrintStream = null;
            Predict4AllClinicalStudyManager.LOGGER.info("Stopped clinical study logging");
            // Restore prediction configuration
            if (predictor.getWordPredictor() != null) {
                PredictionParameter predictionParameter = predictor.getWordPredictor().getPredictionParameter();
                if (this.savedEnableWordCorrection != null) {
                    predictionParameter.setEnableWordCorrection(savedEnableWordCorrection);
                }
                if (this.savedMinCountToProvideCorrection != null) {
                    predictionParameter.setMinCountToProvideCorrection(savedMinCountToProvideCorrection);
                }
                if (this.savedMinCountToProvidePrediction != null) {
                    predictionParameter.setMinCountToProvidePrediction(savedMinCountToProvidePrediction);
                }
            }
            this.savedEnableWordCorrection = null;
            this.savedMinCountToProvidePrediction = null;
            this.savedMinCountToProvideCorrection = null;
            // Generate XLS template
            if (this.currentTestInformation != null) {
                handleResultFile(new File(currentLogDirectory.getPath() + File.separator + "raw-log.json"),
                        new File(currentLogDirectory.getPath() + File.separator + Predict4AllClinicalStudyManager.FORMAT.format(new Date()) + "-" + IOUtils.getValidFileName(currentTestInformation != null ? currentTestInformation.getUserId() : ProfileController.INSTANCE.currentProfileProperty()
                                .get()
                                .nameProperty()
                                .get()) + "_result.xls"),
                        this.currentTestInformation,
                        false);
            }
            this.currentLogDirectory = null;
            this.currentTestInformation = null;
            if (this.endCallback != null) {
                this.endCallback.run();
            }
        }
    }

    public boolean isLogging() {
        return this.currentPrintStream != null;
    }

    public boolean isLoggingWasEnabledInThisUse() {
        return this.loggingWasEnabledInThisUse;
    }

    public void setStartCallback(Runnable startCallback) {
        this.startCallback = startCallback;
    }

    public void setEndCallback(Runnable endCallback) {
        this.endCallback = endCallback;
    }
    //========================================================================

    // Class part : "Mode"
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        this.stopLogging();
    }
    //========================================================================

    // Class part : "Handle log file > convert it to result file"
    //========================================================================
    public static void handleResultFile(File inputRawFile, File outputFile, ClinicalStudyTestInformationDto information, boolean openExcelFile) {
        List<ResultLineDto> resultLines = new ArrayList<>();
        double elasptedTime = 0;
        try (InputStreamReader is = new InputStreamReader(new FileInputStream(inputRawFile), "UTF-8")) {
            WritingEvent[] events = Predict4AllClinicalStudyManager.GSON_INPUT.fromJson(is, WritingEvent[].class);
            elasptedTime = events.length > 0 ? (events[events.length - 1].getTimestamp() - events[0].getTimestamp()) / 1000.0 / 60.0 : 0.0;
            String lastWord = null;
            List<Pair<Long, Integer>> lengthInLastTime = new ArrayList<>();
            for (WritingEvent event : events) {
                // Filter out event : keep user entries and action, and text modifications only
                if (event.getSource() != WritingEventSource.SYSTEM && event.getType() != WritingEventType.CARET_MOVE) {
                    WritingControllerStateI beforeEventState = event.getBeforeEventState();
                    WritingControllerStateI afterEventState = event.getAfterEventState();
                    String text = prepareTextBefore(beforeEventState.getTypedTextBeforeCaret());
                    String predictions = beforeEventState.getWordPredictionResult() != null ? beforeEventState.getWordPredictionResult()
                            .getPredictions()
                            .stream()
                            .map(p -> p.getPredictionToDisplay())
                            .collect(Collectors.joining(", ")) : "";
                    String selectedPred = null;
                    BigDecimal ksr = null;
                    int insertedLength = 0;
                    int wordLength = 0;

                    // IF prediction inserted : compute KSR etc... (if it's not a correction)
                    if (event.getType() == WritingEventType.INSERTION_WORD_PREDICTION) {
                        WordPrediction pred = getCustomValue(event, "prediction", WordPrediction.class);
                        selectedPred = pred.getPredictionToDisplay();
                        ksr = pred.getPreviousCharCountToRemove() <= 0 ? new BigDecimal(computeKsr(pred)) : new BigDecimal(0.0);
                        wordLength = pred.getPredictionToDisplay().length() + AFTER_PRED_ADDED_CHAR;
                        insertedLength = pred.getPreviousCharCountToRemove() <= 0 ? pred.getTextToWrite().length() + NEEDED_SELECTION_TO_USE_PREDICTION : 0;
                    }

                    // IF the inserted with the inserted text we go on a new word
                    String nLastWord = getLastWord(afterEventState.getTypedTextBeforeCaret());
                    if (StringUtils.isNotBlank(nLastWord) && !StringUtils.isEqualsIgnoreCase(nLastWord, lastWord) && event.getType() != WritingEventType.INSERTION_WORD_PREDICTION) {
                        ksr = new BigDecimal(0.0);
                        wordLength = nLastWord != null ? nLastWord.length() + AFTER_PRED_ADDED_CHAR : 0;
                    }
                    lastWord = nLastWord;

                    lengthInLastTime.add(Pair.of(event.getTimestamp(), wordLength));

                    long lastEventTime = event.getTimestamp() - SPEED_DIFF;
                    lengthInLastTime.removeIf(p -> p.getLeft() < lastEventTime);

                    double instantSpeedInCarPerMin = (1.0 * lengthInLastTime.stream()
                            .mapToInt(p -> p.getRight())
                            .sum()) / (lengthInLastTime.size() > 1 ? ((lengthInLastTime.get(lengthInLastTime.size() - 1).getLeft() - lengthInLastTime.get(0).getLeft()) / 1000.0 / 60.0) : 1.0);

                    ResultLineDto dto = new ResultLineDto(new Date(event.getTimestamp()), text, predictions, selectedPred, ksr, insertedLength, wordLength, instantSpeedInCarPerMin, null);
                    resultLines.add(dto);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Couldn't read raw input log file", e);
            return;
        }
        LOGGER.info("Result transformed from raw file {}", inputRawFile);
        // Template
        try (InputStream is = getTemplateIS()) {
            try (OutputStream os = new FileOutputStream(outputFile)) {
                Context context = new Context();
                context.putVar("resultLines", resultLines);
                context.putVar("elasptedTime", elasptedTime);
                context.putVar("testDate", information.getDate());
                context.putVar("userId", information.getUserId());
                context.putVar("testContext",
                        (information.getContext() != null ? Translation.getText(information.getContext()
                                .getNameId()) : "NULL") + (information.getContext() == ClinicalStudyTestContext.OTHER ? " / " + information.getContextOtherDescription() : ""));
                context.putVar("lcVersionAndDate", information.getLcVersionAndDate());
                context.putVar("p4aPluginVersion", information.getP4APluginVersion());
                context.putVar("p4aVersionAndDate", information.getP4AVersionAndDate());
                context.putVar("predictionCount", information.getContext() != ClinicalStudyTestContext.WITHOUT_PREDICTION ? WordPredictionController.INSTANCE.getPredictionCount() : 0);
                JxlsHelper.getInstance().processTemplate(is, os, context);
                LOGGER.info("Template modified and saved");
                // Now try to open the result directory and result file
                try {
                    Desktop.getDesktop().open(outputFile.getParentFile());
                } catch (Throwable t) {
                    LOGGER.error("Couldn't open result directory", t);
                }
                if (openExcelFile) {
                    try {
                        Desktop.getDesktop().open(outputFile);
                    } catch (Throwable t) {
                        LOGGER.error("Couldn't open result file", t);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Couldn't read/modify the input template", e);
        }
    }

    private static InputStream getTemplateIS() {
        return ResourceHelper.getInputStreamForPath("/clinical-study/p4a_result_template.xls");
    }

    private static <T> T getCustomValue(WritingEvent event, String valueName, Class<T> type) {
        return GSON_INPUT.fromJson(GSON_INPUT.toJsonTree(event.getValues().get(valueName)), type);
    }

    // FIXME : check at the bottom of the file the right computing
    private static double computeKsr(WordPredictionI prediction) {
        String toWrite = prediction.getTextToWrite();
        return 1 - //
                (((prediction.getPredictionToDisplay().length() - toWrite.length() + NEEDED_SELECTION_TO_USE_PREDICTION) * 1.0)//
                        / ((prediction.getPredictionToDisplay().length() + AFTER_PRED_ADDED_CHAR) * 1.0))//
                ;
    }

    private final static int TEXT_BEFORE_DISPLAY = 40;

    private static String prepareTextBefore(String input) {
        return input != null ? input.substring(Math.max(0, input.length() - TEXT_BEFORE_DISPLAY)) : "";
    }
    //========================================================================

    // Class part : "HELPER"
    //========================================================================
    private static String getLastWord(String textBefore) {
        // Get text before caret
        StringBuilder lastWord = new StringBuilder();
        // Search for the last word
        boolean found = false;
        boolean firstStopCharFound = false;
        boolean noStopCharFound = false;
        for (int i = textBefore.length() - 1; !found && i >= 0; i--) {
            char charAt = textBefore.charAt(i);
            boolean stopChar = Separator.getSeparatorFor(charAt) != null;
            if (stopChar) {
                if (firstStopCharFound && noStopCharFound) found = true;
                else firstStopCharFound = true;
            } else if (firstStopCharFound) {
                lastWord.insert(0, charAt);
                noStopCharFound = true;
            }
        }
        return lastWord.toString();
    }

    //========================================================================

    // Class part : "GSON"
    //========================================================================
    private static class WritingControllerStateAdapter implements JsonDeserializer<WritingControllerStateI> {

        @Override
        public WritingControllerStateI deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return context.deserialize(json, WritingControllerState.class);
        }
    }

    private static class WordPredictionResultAdapter implements JsonDeserializer<WordPredictionResultI> {

        @Override
        public WordPredictionResultI deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return context.deserialize(json, WordPredictionResult.class);
        }
    }

    private static class WordPredictionAdapter implements JsonDeserializer<WordPredictionI> {

        @Override
        public WordPredictionI deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return context.deserialize(json, WordPrediction.class);
        }
    }
    //========================================================================


    // This is the right computing for KSR with prediction or error (to adapt)
    //    for (int rowIx = topLeftRef.getFirst() + 1; rowIx < bottomLeftRef.getFirst() - 1; rowIx++) {
    //
    //        // Word end : ksr column is filled (prediction selected or not)
    //        if (!isEmpty(sheet, rowIx, topLeftRef.getSecond() + 4)) {
    //
    //            // Get values
    //            String input = getInputString(sheet, rowIx, topLeftRef.getSecond() + 1);
    //            String selectedPrediction = getInputString(sheet, rowIx, topLeftRef.getSecond() + 3);
    //            int insertedWithPred = getInputInt(sheet, rowIx, topLeftRef.getSecond() + 5);
    //
    //            int kAllWord = getInputInt(sheet, rowIx, topLeftRef.getSecond() + 6);
    //            int kRedWord;
    //
    //            // A prediction is selected
    //            if (!selectedPrediction.isBlank()) {
    //                List<Token> inputTokens = tokenizer.tokenize(input);
    //                Token lastToken = inputTokens.get(inputTokens.size() - 1);
    //                // And error was corrected
    //                String tokenText = lastToken.getText();
    //                if (!lastToken.isSeparator() && !selectedPrediction.startsWith(tokenText)) {
    //                    int typedLength = StringUtils.length(tokenText);
    //                    // Already typed char + the correction selection
    //                    kRedWord = typedLength + 1;
    //                    String predSubstring = StringUtils.substring(selectedPrediction, 0, typedLength);
    //                    // Already typed char + error edition (distance + edition cost) + rest of the word to type (including space)
    //                    kAllWord = typedLength + 1 + (int) levenshtein.distance(predSubstring, tokenText) + (selectedPrediction.length() + 1 - typedLength);
    //                }
    //                // No error
    //                else {
    //                    kRedWord = (kAllWord - insertedWithPred + 1);
    //                }
    //            }
    //            // A prediction is not selected : word was fully written by user
    //            else {
    //                kRedWord = kAllWord;
    //            }
    //
    //            double ksr = 1 - ((1.0 * kRedWord) / (1.0 * kAllWord));
    //            System.out.println(StringUtils.leftPad(input.replace("\n", "\\n"), 50) + " > " + StringUtils.leftPad(selectedPrediction, 8) + " = " + KSR_FORMAT.format(ksr));
    //            kRedCount += kRedWord;
    //            kAllCount += kAllWord;
    //        }
    //    }
    //
    //            System.out.println("kAllCount = " + kAllCount);
    //            System.out.println("kRedCount = " + kRedCount);
    //            System.out.println("KSR = " + KSR_FORMAT.format(1 - ((1.0 * kRedCount) / (1.0 * kAllCount))));
}
