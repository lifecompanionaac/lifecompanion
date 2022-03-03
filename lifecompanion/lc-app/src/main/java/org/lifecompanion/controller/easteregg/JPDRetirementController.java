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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Pair;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.task.ConfigurationImportTask;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.ui.easteregg.JPDRetirementView;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum JPDRetirementController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(JPDRetirementController.class);

    private final BooleanProperty running;
    private JPDRetirementView currentView;

    private MediaPlayer mediaPlayerIntroVideo;
    private List<DemoConfiguration> demoConfigurations;

    JPDRetirementController() {
        running = new SimpleBooleanProperty();
    }

    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }

    public void startJPDRetirementJourney() {
        if (UserConfigurationController.INSTANCE.enableJPDRetirementEasterEggProperty().get()) {
            if (!this.running.get()) {
                LCNamedThreadFactory.daemonThreadFactory("JPDRetirementController").newThread(() -> {
                    // Load resources
                    loadResources();

                    // Show the view and launch first step
                    FXThreadUtils.runOnFXThread(() -> this.running.set(true));
                    this.currentView.launchFirstStep();
                }).start();
            }
        }
    }

    private void loadResources() {
        Media introVideo = new Media(new File("C:\\Users\\Mathieu\\OneDrive - HIRM\\A-TRIER\\JPD-RETRAITE\\lifecompanion\\intro-video_v3.mp4").toURI().toString());
        mediaPlayerIntroVideo = new MediaPlayer(introVideo);
        demoConfigurations = new ArrayList<>();
        demoConfigurations.add(new DemoConfiguration("KeCom", "bla bla bla", "2004-2007", loadAndPrepareConfiguration("20220301_KeCom.lcc", this::launchSecondStep)));
        demoConfigurations.add(new DemoConfiguration("Sibylle", "bla bla bla", "2004-2007", loadAndPrepareConfiguration("20220301_Sibylle 3.8.0.lcc", this::launchThirdStep)));
    }

    private void launchThirdStep() {
    }

    public MediaPlayer getMediaPlayerIntroVideo() {
        return mediaPlayerIntroVideo;
    }

    public List<DemoConfiguration> getDemoConfigurations() {
        return demoConfigurations;
    }

    private void launchSecondStep() {
        this.currentView.launchSecondStep();
    }

    public void setCurrentView(JPDRetirementView jpdRetirementView) {
        this.currentView = jpdRetirementView;
    }

    public static class DemoConfiguration {
        private final String name;
        private final String description;
        private final String year;
        private Image image;
        private final LCConfigurationDescriptionI configurationDescription;
        private final LCConfigurationI configuration;

        public DemoConfiguration(String name, String description, String year, Pair<LCConfigurationDescriptionI, LCConfigurationI> configurationAndDesc) {
            this.name = name;
            this.description = description;
            this.year = year;
            this.configurationDescription = configurationAndDesc.getKey();
            this.configuration = configurationAndDesc.getValue();
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

    private static Pair<LCConfigurationDescriptionI, LCConfigurationI> loadAndPrepareConfiguration(String name, Runnable nextStep) {
        try {
            ConfigurationImportTask customConfigurationImport = IOHelper.createCustomConfigurationImport(IOUtils.getTempDir("jpd-retirement-import"), new File("C:\\Users\\Mathieu\\OneDrive - HIRM\\A-TRIER\\JPD-RETRAITE\\lifecompanion\\" + name), true);
            Pair<LCConfigurationDescriptionI, LCConfigurationI> loadedConfiguration = ThreadUtils.executeInCurrentThread(customConfigurationImport);

            // Add action to go to the next "step"
            loadedConfiguration.getValue()
                    .getAllComponent()
                    .values()
                    .stream()
                    .filter(c -> c instanceof GridPartKeyComponentI)
                    .map(c -> (GridPartKeyComponentI) c)
                    .filter(c -> "@".equals(c.textContentProperty().get()))
                    .findAny().ifPresent(key -> {
                key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(new SimpleUseActionImpl<>(UseActionTriggerComponentI.class) {
                    @Override
                    public void execute(UseActionEvent event, Map<String, UseVariableI<?>> variables) {
                        if (nextStep != null) nextStep.run();
                    }
                });
            });
            return loadedConfiguration;
        } catch (Exception e) {
            LOGGER.error("Couldn't load demo configuration from {}", name, e);
            return null;
        }
    }
}
