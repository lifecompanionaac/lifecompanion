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

package org.lifecompanion.controller.io.task;

import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.configurationcomponent.GridPartGridComponent;
import org.lifecompanion.model.impl.configurationcomponent.LCConfigurationComponent;
import org.lifecompanion.model.impl.configurationcomponent.StackComponent;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.model.impl.profile.LCConfigurationDescription;
import org.lifecompanion.model.impl.profile.LCProfile;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Random;


/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GenerateTechDemoConfigurationTask extends LCTask<Void> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GenerateTechDemoConfigurationTask.class);

    public GenerateTechDemoConfigurationTask() {
        super("task.generate.tech.demo.configuration.title");
    }

    @Override
    protected Void call() throws Exception {
        LCConfigurationComponent configuration = new LCConfigurationComponent();

        StackComponent mainStackComponent = new StackComponent();
        mainStackComponent.xProperty().set(2.0);
        mainStackComponent.yProperty().set(2.0);
        mainStackComponent.widthProperty().set(1200);
        mainStackComponent.heightProperty().set(800);
        configuration.getChildren().add(mainStackComponent);

        GridPartGridComponent homeGrid = new GridPartGridComponent();
        homeGrid.userNameProperty().set("Accueil");
        mainStackComponent.getComponentList().add(homeGrid);

        // Save configuration
        LCProfileI fakeProfile = new LCProfile();
        // FIXME : other informations
        LCConfigurationDescriptionI desc = new LCConfigurationDescription();
        desc.loadedConfigurationProperty().set(configuration);
        desc.configurationNameProperty().set("Démonstration technique de LifeCompanion");
        fakeProfile.getConfiguration().add(desc);

        File tempSave = IOUtils.getTempDir("tech-demo-configuration");
        ConfigurationSavingTask configurationSavingTask = new ConfigurationSavingTask(tempSave, configuration, fakeProfile);
        ThreadUtils.executeInCurrentThread(configurationSavingTask);
        File destConfig = IOUtils.getTempFile("tech-demo-configuration", ".lcc");
        ConfigurationExportTask configurationExportTask = new ConfigurationExportTask(desc, tempSave, destConfig);
        ThreadUtils.executeInCurrentThread(configurationExportTask);

        // FIXME : return file and handle anywhere
        LCConfigurationActions.ImportOpenEditAction importOpenConfig = new LCConfigurationActions.ImportOpenEditAction(destConfig);
        ConfigActionController.INSTANCE.executeAction(importOpenConfig);

        return null;
    }
}
