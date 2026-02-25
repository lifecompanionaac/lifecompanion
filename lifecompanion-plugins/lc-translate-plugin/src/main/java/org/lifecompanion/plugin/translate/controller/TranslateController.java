package org.lifecompanion.plugin.translate.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import org.lifecompanion.controller.configurationcomponent.dynamickey.KeyListController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SpeakTextAction;
import org.lifecompanion.plugin.translate.service.ArgoTranslateService;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.predict4all.nlp.utils.progressindicator.LoggingProgressIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public enum TranslateController {
    INSTANCE;

    private final Logger LOGGER = LoggerFactory.getLogger(TranslateController.class);

    private final StringProperty currentLanguage;
    private final List<ElementWithText> elementWithTexts;
    private final Map<Pair<ElementWithText, StringProperty>, String> originalTexts;
    private final ArgoTranslateService translationService;

    private final ScheduledExecutorService scheduledExecutor;

    private String originalLanguageCode;

    private final AtomicReference<SwitchLanguageTask> switchLanguageTask;

    Process translationServerProcess;

    TranslateController() {
        currentLanguage = new SimpleStringProperty();
        this.switchLanguageTask = new AtomicReference<>();
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(LCNamedThreadFactory.daemonThreadFactory("LCTranslateController"));
        originalTexts = new HashMap<>();
        elementWithTexts = new ArrayList<>();
        this.translationService = new ArgoTranslateService();
        this.translationService.initialize();
    }

    public StringProperty currentLanguageProperty() {
        return currentLanguage;
    }


    public void modeStart(LCConfigurationI configuration) {
        startTranslationServerWhenNeeded();

        // Detect all element that could be translated
        configuration.getAllComponent().values()
                .forEach(element -> {
                    if (element instanceof GridPartKeyComponentI key) {
                        if (!key.keyOptionProperty().get().disableTextContentProperty().get()) {
                            elementWithTexts.add(new GridPartKeyEWT(key));
                        } else {
                            // TODO : use variable ?
                        }
                    }
                });
        configuration.rootKeyListNodeProperty().get().traverseTreeToBottom(node -> {
            elementWithTexts.add(new KeyListNodeEWT(node));
        });

        // Configure current language
        this.originalLanguageCode = UserConfigurationController.INSTANCE.userLanguageProperty().get();
        this.currentLanguage.set(originalLanguageCode);

        // Save original texts
        for (ElementWithText elementWithText : elementWithTexts) {
            for (StringProperty textProperty : elementWithText.getTextProperties()) {
                this.originalTexts.put(Pair.of(elementWithText, textProperty), textProperty.get());
            }
        }

        LOGGER.info("Translation controller initialized, original language is {}, {} translatable element detected", originalLanguageCode, elementWithTexts.size());
    }

    public void modeStop(LCConfigurationI configuration) {
        this.elementWithTexts.clear();
        this.originalTexts.clear();
    }

    public void exit() {
        if (this.translationServerProcess != null) {
            this.translationServerProcess.destroy();
        }
        this.translationService.dispose();
    }

    private class SwitchLanguageTask extends Task<Void> {
        private final String targetLanguageCode;

        public SwitchLanguageTask(String targetLanguageCode) {
            this.targetLanguageCode = targetLanguageCode;
        }

        @Override
        protected Void call() throws Exception {
            if (!isCancelled()) {
                startTranslationServerWhenNeeded();

                LOGGER.info("Will try to switch language from {} to {}", currentLanguage.get(), targetLanguageCode);

                // Translate current text in editor
                String currentText = WritingStateController.INSTANCE.currentTextProperty().get();
                String translatedCurrentText = getTranslation(currentLanguage.get(), targetLanguageCode, currentText);
                WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);
                WritingStateController.INSTANCE.insertText(WritingEventSource.SYSTEM, translatedCurrentText);

                // All elements
                int sum = elementWithTexts.stream().mapToInt(e -> e.getTextProperties().size()).sum();
                LoggingProgressIndicator loggingProgressIndicator = new LoggingProgressIndicator("Translation", sum);
                for (ElementWithText elementWithText : elementWithTexts) {
                    for (StringProperty textProperty : elementWithText.getTextProperties()) {
                        if (!isCancelled()) {
                            String originalText = originalTexts.get(Pair.of(elementWithText, textProperty));
                            if (targetLanguageCode.equals(originalLanguageCode)) {
                                FXThreadUtils.runOnFXThread(() -> textProperty.set(originalText));
                            } else {
                                String translation = getTranslation(originalLanguageCode, targetLanguageCode, originalText);
                                FXThreadUtils.runOnFXThread(() -> textProperty.set(translation));
                            }
                        }
                        loggingProgressIndicator.increment();
                    }
                }

                // Key list refresh
                KeyListController.INSTANCE.updateKeysFromNodes();

                currentLanguage.set(targetLanguageCode);

                LOGGER.info("Switch to {} is finished", targetLanguageCode);
            }
            return null;
        }
    }

    private void startTranslationServerWhenNeeded() {
        // TODO : currently not package within the plugin
        if (translationServerProcess == null || !translationServerProcess.isAlive()) {
            try {
                String translationServerFolder = System.getenv("LIFECOMPANION_TRANSLATION_SERVER_FOLDER");
                LOGGER.info("Will try to start translation server in {}", translationServerFolder);
                File transFolderPath = new File(translationServerFolder);
                translationServerProcess = new ProcessBuilder(
                        "python", "translation-server.py",
                        "./models/",
                        "--port", "8000"
                ).directory(transFolderPath)
                        .redirectError(ProcessBuilder.Redirect.PIPE)
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .start();
                ThreadUtils.safeSleep(5_000);
            } catch (Exception e) {
                LOGGER.error("Can't start translation server", e);
            }
        }
    }

    public String getTranslation(String sourceLanguageCode, String targetLanguageCode, String text) {
        if (StringUtils.isNotBlank(text) && StringUtils.safeLength(text) > 1) {
            String translationCode = sourceLanguageCode + "_" + targetLanguageCode;
            AppModeController.INSTANCE.getUseModeContext().getConfiguration();
            File translateDirectory = getTranslateDirectory(translationCode);
            try {
                File translatedFile = new File(translateDirectory + File.separator + text.hashCode() + ".txt");
                String translate;
                if (translatedFile.exists()) {
                    translate = readFileContent(translatedFile);
                } else {
                    translate = translationService.translate(sourceLanguageCode, targetLanguageCode, text);
                    IOUtils.writeToFile(translatedFile, translate, "UTF-8");
                    //LOGGER.info("New translation created : \"{}\" to \"{}\"", text, translate);
                }
                return translate;
            } catch (Exception e) {
                LOGGER.warn("Could not translate {}>{} : \"{}\"", sourceLanguageCode, targetLanguageCode, text, e);

            }
        } else {
            //LOGGER.info("Ignored translation of \"{}\"", text);
        }
        return text;
    }

    private File getTranslateDirectory(String translationCode) {
        LCConfigurationI configuration = AppModeController.INSTANCE.getUseModeContext().getConfiguration();
        LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
        File baseDirectory;
        if (configuration != null && currentProfile != null) {
            baseDirectory = new File(IOHelper.getConfigurationPath(currentProfile.getID(), configuration.getID()) + File.separator + "translations");
        } else {
            baseDirectory = new File(System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "tmp" + File.separator + "translation-cache");
        }
        File translateDirectory = new File(baseDirectory + File.separator + translationCode + File.separator);
        translateDirectory.mkdirs();

        return translateDirectory;
    }

    public void switchToLanguage(String targetLanguageCode) {
        LOGGER.info("Request to switch to language {}", targetLanguageCode);
        SwitchLanguageTask switchToTask = new SwitchLanguageTask(targetLanguageCode);
        SwitchLanguageTask previousTask = switchLanguageTask.getAndSet(switchToTask);
        if (previousTask != null && !previousTask.isDone()) {
            previousTask.cancel(false);
        }
        this.scheduledExecutor.submit(switchToTask);
    }

    private interface ElementWithText {
        Set<StringProperty> getTextProperties();
    }


    private static class GridPartKeyEWT implements ElementWithText {
        private final GridPartKeyComponentI key;

        private GridPartKeyEWT(GridPartKeyComponentI key) {
            this.key = key;
        }

        @Override
        public Set<StringProperty> getTextProperties() {
            Set<StringProperty> properties = new HashSet<>(Set.of(key.textContentProperty()));
            addAllStringPropertiesFor(key, properties, SpeakTextAction.class, SpeakTextAction::textToSpeakProperty);
            return properties;
        }
    }

    private static class KeyListNodeEWT implements ElementWithText {
        private final KeyListNodeI node;

        private KeyListNodeEWT(KeyListNodeI node) {
            this.node = node;
        }

        @Override
        public Set<StringProperty> getTextProperties() {
            return Set.of(
                    node.textProperty(),
                    node.textToWriteProperty(),
                    node.textToSpeakProperty(),
                    node.textSpeakOnOverProperty()
            );
        }
    }

    private static <T> void addAllStringPropertiesFor(GridPartKeyComponentI key, Set<StringProperty> properties, Class<T> actionType, Function<T, StringProperty> propGetter) {
        key.getActionManager().componentActions().forEach((useActionEvent, actions) -> {
            for (BaseUseActionI<?> action : actions) {
                if (actionType.isAssignableFrom(action.getClass())) {
                    properties.add(propGetter.apply((T) action));
                }
            }
        });
    }

    private static String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = bf.readLine()) != null) {
                if (!content.isEmpty()) content.append("\n");
                content.append(line);
            }
        }
        return content.toString();
    }
}

