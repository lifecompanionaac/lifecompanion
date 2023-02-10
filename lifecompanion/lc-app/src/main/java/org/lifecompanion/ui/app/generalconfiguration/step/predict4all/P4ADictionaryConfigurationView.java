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
package org.lifecompanion.ui.app.generalconfiguration.step.predict4all;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.ui.controlsfx.glyphfont.GlyphFontRegistry;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.ui.common.pane.specific.cell.P4AWordListCell;
import org.lifecompanion.util.DesktopUtils;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.model.LCTask;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.model.impl.textprediction.predict4all.P4AConfigurationSteps;
import org.lifecompanion.model.impl.textprediction.predict4all.Predict4AllWordPredictorHelper;
import org.lifecompanion.model.impl.textprediction.predict4all.PredictorModelDto;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.controller.editmode.LCFileChoosers;
import org.lifecompanion.ui.app.generalconfiguration.step.predict4all.correction.VocabularyListModifyDic;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.predict4all.nlp.Tag;
import org.predict4all.nlp.ngram.dictionary.StaticNGramTrieDictionary;
import org.predict4all.nlp.words.model.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class P4ADictionaryConfigurationView extends ScrollPane implements GeneralConfigurationStepViewI {
    private static final Logger LOGGER = LoggerFactory.getLogger(P4ADictionaryConfigurationView.class);

    private static final String PARAM_NAME_FILTER_OUT_VOCABULARY = "filterOutVocabularyPercentage";

    private static final double DEFAULT_FACTOR = 10.0;
    private static final DecimalFormat DECIMAL_FORMAT_WORD_COUNT = new DecimalFormat("###,###.##");

    private TextField fieldSearchWord;
    private ToggleSwitch toggleUserWordOnly;
    private ToggleSwitch toggleHideDisabledWords;
    private ListView<Word> listViewWords;
    private WordDetailPane wordDetailPane;
    private SplitPane splitPane;
    private TextField fieldAddedWord;
    private Button buttonAddWord;
    private Slider sliderFilterVocabulary;
    private Label labelWordCount;
    private Button buttonImportPrio, buttonImportDeprio;

    private Map<VocabularyListModifyDic, ToggleSwitch> toggleSwitchesByVoc;

    private FilteredList<Word> filterList;
    private SortedList<Word> sortedList;
    private ObservableList<Word> wordList;

    private StaticNGramTrieDictionary staticNGramDictionary;
    private PredictorModelDto predictorModelDto;


    public P4ADictionaryConfigurationView() {
        this.wordList = FXCollections.observableArrayList();
        this.filterList = new FilteredList<>(this.wordList);
        this.sortedList = new SortedList<>(this.filterList, (o1, o2) -> (o1.getWord() != null ? o1.getWord() : "").compareToIgnoreCase(o2.getWord() != null ? o2.getWord() : ""));
        this.toggleSwitchesByVoc = new HashMap<>();
        this.initAll();
    }

    @Override
    public String getMenuStepToSelect() {
        return GeneralConfigurationStep.PREDICTIONS_CONFIGURATION.name();
    }

    @Override
    public void initUI() {
        // Top search and filter
        this.fieldSearchWord = new TextField();
        this.fieldSearchWord.setPromptText(Translation.getText("predict4all.config.prompt.search.word"));
        VBox.setMargin(fieldSearchWord, new Insets(10.0, 0.0, 0.0, 0.0));
        this.toggleUserWordOnly = new ToggleSwitch(Translation.getText("predict4all.config.search.word.user.only"));
        this.toggleUserWordOnly.setMaxWidth(Double.MAX_VALUE);
        this.toggleHideDisabledWords = new ToggleSwitch(Translation.getText("predict4all.config.search.word.hide.disabled.words"));
        this.toggleHideDisabledWords.setMaxWidth(Double.MAX_VALUE);
        this.toggleHideDisabledWords.setSelected(true);

        // Filter vocabulary
        this.sliderFilterVocabulary = new Slider(0.0, 100.0, 100.0);
        this.sliderFilterVocabulary.setMajorTickUnit(25);
        this.sliderFilterVocabulary.setShowTickMarks(true);
        this.sliderFilterVocabulary.setMinorTickCount(0);
        this.sliderFilterVocabulary.setPrefWidth(160.0);
        Label labelSliderFilter = new Label(Translation.getText("predict4all.config.filter.vocabulary.percentage"));
        labelWordCount = new Label();
        labelWordCount.setFont(Font.font(9));
        labelWordCount.setMaxWidth(Double.MAX_VALUE);
        labelWordCount.setTextAlignment(TextAlignment.RIGHT);
        labelWordCount.setAlignment(Pos.CENTER_RIGHT);
        labelSliderFilter.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelSliderFilter, Priority.ALWAYS);
        HBox boxSlider = new HBox(5.0, labelSliderFilter, sliderFilterVocabulary);

        VBox boxTop = new VBox(5.0, FXControlUtils.createTitleLabel("predict4all.config.part.title.word.list"), this.fieldSearchWord, boxSlider, labelWordCount, this.toggleUserWordOnly, this.toggleHideDisabledWords);

        // Center : word list
        this.listViewWords = new ListView<>(this.sortedList);
        this.wordDetailPane = new WordDetailPane(this.listViewWords);
        this.listViewWords.setCellFactory(lv -> new P4AWordListCell(this));
        this.splitPane = new SplitPane(this.listViewWords, this.wordDetailPane);
        this.splitPane.setDividerPositions(0.6);
        splitPane.setPrefHeight(300.0);

        // Bottom : add custom word
        this.fieldAddedWord = new TextField();
        this.fieldAddedWord.setPromptText(Translation.getText("predict4all.config.prompt.add.user.word"));
        this.buttonAddWord = FXControlUtils.createGraphicButton(GlyphFontRegistry.font("FontAwesome").create(FontAwesome.Glyph.PLUS_CIRCLE).size(16).color(LCGraphicStyle.MAIN_PRIMARY),
                "predict4all.config.prompt.add.user.word");
        this.buttonAddWord.getStyleClass().add("small-button");
        this.buttonAddWord.setPadding(new Insets(0.0, 0.0, 1.0, 0.0));
        HBox.setHgrow(this.fieldAddedWord, Priority.ALWAYS);
        HBox boxAddWordToDic = new HBox(5.0, this.fieldAddedWord, this.buttonAddWord);
        boxAddWordToDic.setAlignment(Pos.CENTER);

        // Priorize words
        Label labelExplainPrioDeprio = new Label(Translation.getText("predict4all.config.part.title.priorize.deprio.explain"));
        labelExplainPrioDeprio.getStyleClass().addAll("text-font-italic", "text-fill-gray", "text-wrap-enabled");
        Label labelPrioPart = new Label(Translation.getText("predict4all.config.part.title.words.priorized"));
        labelPrioPart.setStyle("-fx-font-weight: bold;");
        VBox boxPriorizeVoc = new VBox(5.0, FXControlUtils.createTitleLabel("predict4all.config.part.title.priorize.deprio"), labelExplainPrioDeprio, labelPrioPart);
        Arrays.stream(VocabularyListModifyDic.values()).filter(VocabularyListModifyDic::isPriorize).forEach(dic -> boxPriorizeVoc.getChildren().add(this.createToggleFor(dic)));
        Label labelDeprioPart = new Label(Translation.getText("predict4all.config.part.title.words.depriorized"));
        labelDeprioPart.setStyle("-fx-font-weight: bold;");
        boxPriorizeVoc.getChildren().add(labelDeprioPart);
        Arrays.stream(VocabularyListModifyDic.values()).filter(v -> !v.isPriorize())
                .forEach(dic -> boxPriorizeVoc.getChildren().add(this.createToggleFor(dic)));
        Label labelCustomListPart = new Label(Translation.getText("predict4all.config.part.title.words.custom"));
        labelCustomListPart.setStyle("-fx-font-weight: bold;");
        boxPriorizeVoc.getChildren().add(labelCustomListPart);
        Label labelExplain = new Label(Translation.getText("predict4all.config.import.custom.list.explain"));
        labelExplain.getStyleClass().addAll("text-font-italic", "text-fill-gray", "text-wrap-enabled");
        this.buttonImportPrio = new Button(Translation.getText("predict4all.config.import.custom.list.button.prio"));
        this.buttonImportPrio.setPrefWidth(200.0);
        this.buttonImportDeprio = new Button(Translation.getText("predict4all.config.import.custom.list.button.deprio"));
        this.buttonImportDeprio.setPrefWidth(200.0);
        HBox boxButtonPrioDeprio = new HBox(10.0, buttonImportPrio, buttonImportDeprio);
        boxButtonPrioDeprio.setAlignment(Pos.CENTER);
        boxPriorizeVoc.getChildren().addAll(labelExplain, boxButtonPrioDeprio);

        // TOTAL
        VBox boxTotal = new VBox(10.0, boxTop, splitPane, boxAddWordToDic, boxPriorizeVoc);
        this.setFitToWidth(true);
        this.setContent(boxTotal);
        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
    }

    private Node createToggleFor(final VocabularyListModifyDic dic) {
        ToggleSwitch toggle = new ToggleSwitch(Translation.getText(dic.getNameId()));
        this.toggleSwitchesByVoc.put(dic, toggle);
        toggle.setMaxWidth(Double.MAX_VALUE);
        toggle.selectedProperty().addListener((obs, ov, nv) -> {
            double factor = nv
                    ? dic.isPriorize() ? DEFAULT_FACTOR : 1.0 / DEFAULT_FACTOR
                    : 1.0;
            this.executeWordImportOn(toggle, ResourceHelper.getInputStreamForPath("/predict4all/fr-words/" + dic.getFileName()), factor, false);
        });
        Hyperlink linkSource = new Hyperlink(Translation.getText("predict4all.config.link.source.word.list"));
        linkSource.getStyleClass().add("link-words-src");
        linkSource.setOnAction(e -> {
            if (dic.getLink() != null) {
                DesktopUtils.openUrlInDefaultBrowser(dic.getLink());
            }
        });
        VBox.setMargin(linkSource, new Insets(0.0, 0.0, 0.0, 14.0));
        return new VBox(2.0, toggle, linkSource);
    }


    @Override
    public void initListener() {
        this.fieldSearchWord.textProperty().addListener(i -> this.updateSearch());
        this.fieldAddedWord.setOnAction(e -> this.addCurrentWordToDictionary());
        this.toggleUserWordOnly.selectedProperty().addListener(i -> this.updateSearch());
        this.toggleHideDisabledWords.selectedProperty().addListener(i -> this.updateSearch());
        this.listViewWords.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            this.wordDetailPane.setWord(nv);
        });
        this.buttonAddWord.setOnAction(e -> this.addCurrentWordToDictionary());
        this.sliderFilterVocabulary.valueChangingProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                this.updateSortedWordIdsPerFrequency();
            }
        });
        this.buttonImportPrio.setOnAction(e -> this.executeWordChoice(this.buttonImportPrio, DEFAULT_FACTOR));
        this.buttonImportDeprio
                .setOnAction(e -> this.executeWordChoice(this.buttonImportDeprio, 1.0 / DEFAULT_FACTOR));
    }

    @Override
    public void initBinding() {
        this.labelWordCount.textProperty().bind(Bindings.createStringBinding(() -> {
            if (this.predictorModelDto != null && this.predictorModelDto.getWordDictionary() != null) {
                double toKeep = Math.min(1.0, Math.max(0.0, this.sliderFilterVocabulary.getValue()) / 100.0);
                int limitIndex = (int) (this.predictorModelDto.getWordDictionary().getAllWords().size() * toKeep);
                return Translation.getText("predict4all.config.filter.vocabulary.percentage.explained", DECIMAL_FORMAT_WORD_COUNT.format(limitIndex),
                        DECIMAL_FORMAT_WORD_COUNT.format(this.predictorModelDto.getWordDictionary().getAllWords().size()), (int) (toKeep * 100.0));
            }
            return "";
        }, this.wordList, this.sliderFilterVocabulary.valueProperty()));
    }

    private void addCurrentWordToDictionary() {
        String wordToAdd = this.fieldAddedWord.getText();
        if (!StringUtils.isBlank(wordToAdd)) {
            wordToAdd = wordToAdd.trim();
            Word existingWord = this.predictorModelDto.getWordDictionary().getWord(wordToAdd);
            if (existingWord == null || existingWord.getNGramTag() == Tag.UNKNOWN) {
                existingWord = this.predictorModelDto.getWordDictionary().putUserWord(wordToAdd);
                existingWord.setForceValid(true, true);
                this.wordList.add(existingWord);
            }
            this.fieldAddedWord.clear();
            this.selectWord(existingWord);
        }
    }

    private void selectWord(final Word existingWord) {
        if (existingWord != null) {
            if (!existingWord.isModifiedByUser() && !existingWord.isUserWord()) {
                this.toggleUserWordOnly.setSelected(false);
            }
            if (existingWord.isForceInvalid()) {
                this.toggleHideDisabledWords.setSelected(false);
            }
            this.fieldSearchWord.setText("");
            this.listViewWords.getSelectionModel().select(existingWord);
            this.listViewWords.scrollTo(existingWord);
        }
    }

    public void updateSearch() {
        this.filterList.setPredicate((w) -> w.getWord().startsWith(this.fieldSearchWord.getText()) && (!this.toggleUserWordOnly.isSelected() || w.isUserWord() || w.isModifiedByUser())
                && (!this.toggleHideDisabledWords.isSelected() || !w.isForceInvalid()));
    }

    private void updateSortedWordIdsPerFrequency() {
        double toKeep = Math.min(1.0, Math.max(0.0, this.sliderFilterVocabulary.getValue() / 100.0));
        int limitIndex = (int) (this.predictorModelDto.getWordDictionary().getAllWords().size() * toKeep);
        LOGGER.info("Keep only {} percent of the dictionary, so will go up to {} index to keep them", toKeep, limitIndex);

        long start = System.currentTimeMillis();
        AtomicInteger i = new AtomicInteger();
        int[] prefix = {};
        this.predictorModelDto.getWordDictionary().getAllWords().stream()//
                .filter(w -> !w.isUserWord() && !w.isNGramTag() && !w.isEquivalenceClass())//
                .sorted((w1, w2) -> {
                    double w1p = this.staticNGramDictionary.getProbability(prefix, 0, 0, w1.getID());
                    double w2p = this.staticNGramDictionary.getProbability(prefix, 0, 0, w2.getID());
                    return Double.compare(w2p, w1p);
                })//
                .forEach(w -> {
                    int iv = i.getAndIncrement();
                    if (iv <= limitIndex) {
                        w.setForceInvalid(false, false);
                        if (w.getProbFactor() == 1.0 || w.isModifiedByUser()) {
                            w.setModifiedBySystem(false);
                        }
                    } else {
                        w.setForceInvalid(true, false);
                    }
                });
        this.updateSearch();
        this.listViewWords.refresh();
        LOGGER.info("Executed in {} ms", System.currentTimeMillis() - start);
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return false;
    }

    @Override
    public String getTitleId() {
        return "predict4all.config.view.dictionary.title";
    }

    @Override
    public String getStep() {
        return P4AConfigurationSteps.DICTIONARY_CONFIG.name();
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
    public void saveChanges() {
        // Saving for dictionary is handled by Predict4AllRootEntryConfigurationView
    }

    @Override
    public void bind(LCConfigurationI model) {
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.listViewWords.getSelectionModel().clearSelection();
        this.wordList.clear();
        this.fieldSearchWord.clear();
        this.wordList.clear();
        this.predictorModelDto = null;
    }

    @Override
    public void afterHide() {
        if (predictorModelDto != null && this.predictorModelDto.getPredictionParameter() != null) {
            this.predictorModelDto.getPredictionParameter().getCustomParameters().put(PARAM_NAME_FILTER_OUT_VOCABULARY, Double.toString(this.sliderFilterVocabulary.getValue()));
            this.toggleSwitchesByVoc.forEach((dic, toggle) -> {
                if (toggle.isSelected()) {
                    this.predictorModelDto.getPredictionParameter().getCustomParameters().put(dic.name(), Boolean.toString(true));
                } else {
                    this.predictorModelDto.getPredictionParameter().getCustomParameters().remove(dic.name());
                }
            });
        }
        if (this.staticNGramDictionary != null) {
            try {
                this.staticNGramDictionary.close();
            } catch (Exception e) {
                LOGGER.error("Couldn't close static ngram dictionary", e);
            }
        }
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
        try {
            staticNGramDictionary = Predict4AllWordPredictorHelper.loadStaticNGramDictionary();
        } catch (IOException e) {
            LOGGER.error("Couldn't load P4A static ngram dictionary", e);
        }
        this.predictorModelDto = (PredictorModelDto) stepArgs[0];
        this.wordList.setAll(this.predictorModelDto.getWordDictionary().getAllWords());
        if (this.predictorModelDto.getPredictionParameter() != null) {
            String filterOutStr = this.predictorModelDto.getPredictionParameter().getCustomParameters().get(PARAM_NAME_FILTER_OUT_VOCABULARY);
            if (filterOutStr != null) {
                this.sliderFilterVocabulary.setValue(Double.parseDouble(filterOutStr));
            }
            this.toggleSwitchesByVoc.forEach((dic, toggle) -> {
                toggle.setSelected(this.predictorModelDto.getPredictionParameter().getCustomParameters().containsKey(dic.name()));
            });
        }
        this.updateSearch();
    }

    public PredictorModelDto getPredictorModelDto() {
        return predictorModelDto;
    }
    //========================================================================

    // Class part : "ACTIONS"
    //========================================================================
    private void executeWordChoice(final Button btnSrc, final double factor) {
        FileChooser wordFileChooser = LCFileChoosers.getOtherFileChooser(Translation.getText("predict4all.config.file.chooser.import.words.title"),
                new FileChooser.ExtensionFilter(Translation.getText("predict4all.config.file.chooser.import.words.extension"), Collections.singletonList("*.txt")), FileChooserType.OTHER_MISC_NO_EXTERNAL);
        File selectedFile = wordFileChooser.showOpenDialog(FXUtils.getSourceWindow(this));
        if (selectedFile != null) {
            try {
                FileInputStream fis = new FileInputStream(selectedFile);
                this.executeWordImportOn(btnSrc, fis, factor, true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void executeWordImportOn(final Node src, final InputStream inputStream, final double factor, final boolean showConfirm) {
        src.setDisable(true);
        LCTask<Void> importTask = new LCTask<Void>("predict4all.action.import.words") {
            @Override
            protected Void call() throws Exception {
                // Read words from file
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    int added = 0, total = 0;
                    String word;
                    while ((word = bufferedReader.readLine()) != null) {
                        if (!StringUtils.isBlank(word) && word.charAt(0) != '#') {
                            SortedMap<String, Word> wordsForPrefix = predictorModelDto.getWordDictionary().getExactWordsWithPrefixExist(word);
                            if (!wordsForPrefix.isEmpty()) {
                                Word wordO = predictorModelDto.getWordDictionary().getWord(word);
                                wordO.setProbFactor(factor, false);
                                if (factor == 1.0 && (!wordO.isForceInvalid() || wordO.isModifiedByUser())) {
                                    wordO.setModifiedBySystem(false);
                                }
                                added++;
                            }
                            total++;
                        }
                    }
                    LOGGER.info("Modified factor for {} words, on a total of {} words", added, total);
                    if (showConfirm) {
                        final int addedF = added;
                        FXThreadUtils.runOnFXThread(() -> DialogUtils
                                .alertWithSourceAndType(P4ADictionaryConfigurationView.this, Alert.AlertType.INFORMATION)
                                .withHeaderText(Translation.getText("predict4all.action.imported.success.title"))
                                .withContentText(Translation.getText(factor > 1.0 ? "predict4all.action.imported.success.prio.message" : "predict4all.action.imported.success.deprio.message", addedF))
                                .show());
                    }
                    return null;
                } catch (Exception exc) {
                    throw LCException.newException().withCause(exc).withMessageId("predict4all.action.import.words.failed").build();
                }
            }
        };
        final EventHandler<WorkerStateEvent> disableSrc = e -> src.setDisable(false);
        importTask.setOnFailed(disableSrc);
        importTask.setOnSucceeded(disableSrc);
        AsyncExecutorController.INSTANCE.addAndExecute(true, false, importTask);
    }
    //========================================================================
}
