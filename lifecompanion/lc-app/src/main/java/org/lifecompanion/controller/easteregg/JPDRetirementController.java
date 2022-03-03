/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.controller.easteregg;

import gnu.trove.impl.sync.TSynchronizedShortObjectMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.util.Pair;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.task.ConfigurationImportTask;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.ui.easteregg.JPDRetirementView;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public enum JPDRetirementController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(JPDRetirementController.class);

    private JPDRetirementView currentView;
    private MediaPlayer mediaPlayerIntroVideo;
    private MediaPlayer mediaPlayerOpeningSound;
    private MediaPlayer mediaPlayerGlitchSound;
    private MediaPlayer backgroundSound;
    private final List<DemoConfiguration> demoConfigurations;
    private final File rootDirectory;
    private boolean launched;

    JPDRetirementController() {
        rootDirectory = new File("D:\\Dev\\jpd-day\\");
        demoConfigurations = new ArrayList<>();

        AppModeController.INSTANCE.modeProperty().addListener((obs, ov, nv) -> {
            if (nv == AppMode.EDIT) {
                launched = false;
                currentView = null;
                if (mediaPlayerIntroVideo != null) {
                    mediaPlayerIntroVideo.dispose();
                }
                if (mediaPlayerOpeningSound != null) {
                    mediaPlayerOpeningSound.dispose();
                }
                if (mediaPlayerGlitchSound != null) {
                    mediaPlayerGlitchSound.dispose();
                }
                if (backgroundSound != null) {
                    backgroundSound.stop();
                    backgroundSound.dispose();
                }
                demoConfigurations.clear();
            }
        });
    }

    public void startJPDRetirementJourney(String toSpeak) {
        if (UserConfigurationController.INSTANCE.enableJPDRetirementEasterEggProperty().get() && !launched) {
            if (StringUtils.containsIgnoreCase(toSpeak, "bonne retraite jean-paul")) {
                LCNamedThreadFactory.daemonThreadFactory("JPDRetirementController").newThread(() -> {
                    loadResources();
                    this.currentView.launchFirstStep();
                }).start();
            }
        }
    }

    private void loadResources() {
        try {
            mediaPlayerIntroVideo = new MediaPlayer(new Media(new File(rootDirectory + File.separator + "time-machine.mp4").toURI().toString()));
            mediaPlayerOpeningSound = new MediaPlayer(new Media(new File(rootDirectory + File.separator + "opening-sound.mp3").toURI().toString()));
            mediaPlayerGlitchSound = new MediaPlayer(new Media(new File(rootDirectory + File.separator + "glitch-sound.mp3").toURI().toString()));
            backgroundSound = new MediaPlayer(new Media(new File(rootDirectory + File.separator + "background-8-bit.m4a").toURI().toString()));
            backgroundSound.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundSound.setVolume(0.1);
            demoConfigurations.add(new DemoConfiguration("Mutavox", "Synthèse vocale électronique autonome (3,5kg!) avec défilement par bouton poussoir", "1987-1995", "mutavox.jpg", loadAndPrepareConfiguration("20220304_Mutavox.lcc", v -> v.displayConfigurationStep(1))));
            demoConfigurations.add(new DemoConfiguration("KeCom", "Application sur Pocket PC/PDA pour communiquer avec un seul bouton poussoir", "2002-2008", "kecom.png", loadAndPrepareConfiguration("20220304_KeCom.lcc", v -> v.displayConfigurationStep(2))));
            demoConfigurations.add(new DemoConfiguration("Sibylle", "Application sur Windows pour communiquer avec une prédiction de mots avancée", "2004-2016", "sibylle.png", loadAndPrepareConfiguration("20220304_Sibylle 3.8.0.lcc", JPDRetirementView::launchFinalStep)));
        } catch (Exception e) {
            LOGGER.error("Couldn't load one the resources for JPD easter egg", e);
        }
    }

    public MediaPlayer getMediaPlayerIntroVideo() {
        return mediaPlayerIntroVideo;
    }

    public MediaPlayer getMediaPlayerOpeningSound() {
        return mediaPlayerOpeningSound;
    }

    public MediaPlayer getMediaPlayerGlitchSound() {
        return mediaPlayerGlitchSound;
    }

    public MediaPlayer getBackgroundSound() {
        return backgroundSound;
    }

    public List<DemoConfiguration> getDemoConfigurations() {
        return demoConfigurations;
    }

    public void setCurrentView(JPDRetirementView jpdRetirementView) {
        this.currentView = jpdRetirementView;
    }

    public class DemoConfiguration {
        private final String name;
        private final String description;
        private final String year;
        private final Image image;
        private final LCConfigurationDescriptionI configurationDescription;
        private final LCConfigurationI configuration;

        public DemoConfiguration(String name, String description, String year, String imagePath, Pair<LCConfigurationDescriptionI, LCConfigurationI> configurationAndDesc) throws IOException {
            this.name = name;
            this.description = description;
            this.year = year;
            this.configurationDescription = configurationAndDesc.getKey();
            this.configuration = configurationAndDesc.getValue();
            try (FileInputStream fis = new FileInputStream(rootDirectory + File.separator + imagePath)) {
                this.image = new Image(fis);
            }
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getYear() {
            return year;
        }

        public Image getImage() {
            return image;
        }

        public LCConfigurationDescriptionI getConfigurationDescription() {
            return configurationDescription;
        }

        public LCConfigurationI getConfiguration() {
            return configuration;
        }
    }

    private Pair<LCConfigurationDescriptionI, LCConfigurationI> loadAndPrepareConfiguration(String name, Consumer<JPDRetirementView> nextStep) throws Exception {
        ConfigurationImportTask customConfigurationImport = IOHelper.createCustomConfigurationImport(IOUtils.getTempDir("jpd-retirement-import"), new File(rootDirectory + File.separator + name), true);
        Pair<LCConfigurationDescriptionI, LCConfigurationI> loadedConfiguration = ThreadUtils.executeInCurrentThread(customConfigurationImport);

        // Add action to go to the next "step"
        loadedConfiguration.getValue()
                .getAllComponent()
                .values()
                .stream()
                .filter(c -> c instanceof GridPartKeyComponentI)
                .map(c -> (GridPartKeyComponentI) c)
                .filter(c -> "Jean-Paul".equals(c.textContentProperty().get()))
                .findAny().ifPresent(key -> {
                    LOGGER.info("Found in {}", name);
                    key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(new SimpleUseActionImpl<>(UseActionTriggerComponentI.class) {
                        @Override
                        public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
                            if (nextStep != null) nextStep.accept(currentView);
                        }
                    });
                });
        return loadedConfiguration;
    }
}
