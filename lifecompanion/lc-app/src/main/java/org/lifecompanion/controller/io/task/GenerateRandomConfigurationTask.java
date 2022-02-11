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
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.util.model.LCTask;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.model.impl.profile.LCConfigurationDescription;
import org.lifecompanion.model.impl.profile.LCProfile;
import org.lifecompanion.model.impl.configurationcomponent.GridPartGridComponent;
import org.lifecompanion.model.impl.configurationcomponent.LCConfigurationComponent;
import org.lifecompanion.model.impl.configurationcomponent.StackComponent;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Random;


/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GenerateRandomConfigurationTask extends LCTask<Void> {
    private final static Logger LOGGER = LoggerFactory.getLogger(GenerateRandomConfigurationTask.class);

    private final Random random;

    public GenerateRandomConfigurationTask() {
        super("task.generate.random.configuration.title");
        random = new Random(21541254);
    }

    @Override
    protected Void call() throws Exception {
        LCConfigurationComponent configuration = new LCConfigurationComponent();

        // Root stack
        StackComponent stackComponent = new StackComponent();
        stackComponent.xProperty().set(10);
        stackComponent.yProperty().set(150);
        stackComponent.widthProperty().set(600);
        stackComponent.heightProperty().set(400);

        // Add grid
        int baseGridCount = randBetween(20, 30);
        for (int i = 0; i < baseGridCount; i++) {
            stackComponent.getComponentList().add(createRandomGrid());
            updateProgress(i, baseGridCount);
        }

        // Add to configuration
        configuration.getChildren().add(stackComponent);

        // Save configuration
        LCProfileI fakeProfile = new LCProfile();
        LCConfigurationDescriptionI desc = new LCConfigurationDescription();
        desc.loadedConfigurationProperty().set(configuration);
        desc.configurationNameProperty().set("DEBUG CONFIG - TEST");
        fakeProfile.getConfiguration().add(desc);

        File tempSave = LCUtils.getTempDir("configuration-debug");
        LOGGER.info("Save configuration in {}", tempSave);
        ConfigurationSavingTask configurationSavingTask = new ConfigurationSavingTask(tempSave, configuration, fakeProfile);
        LCUtils.executeInCurrentThread(configurationSavingTask);

        File destConfig = new File("E:\\Desktop\\DEBUG_CONFIGURATION.lcc");
        ConfigurationExportTask configurationExportTask = new ConfigurationExportTask(desc, tempSave, destConfig);
        LCUtils.executeInCurrentThread(configurationExportTask);

        return null;
    }


    private GridPartGridComponent createRandomGrid() {
        GridPartGridComponent grid = new GridPartGridComponent();
        grid.getGrid().setRow(randBetween(1, 20));
        grid.getGrid().setColumn(randBetween(1, 20));
        grid.forEachKeys(key -> {
            key.textContentProperty().set(String.valueOf(randBetween(1000, 50000)));
            if (random.nextBoolean()) {
                key.getKeyStyle().backgroundColorProperty().selected().setValue(Color.rgb(randBetween(0, 255), randBetween(0, 255), randBetween(0, 255)));
            }
            if (random.nextBoolean()) {
                final List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> images = getImageSearch();
                final Pair<ImageDictionaryI, List<List<ImageElementI>>> result = getImageSearch().get(random.nextInt(images.size()));
                final List<List<ImageElementI>> imageList = result.getValue();
                if (!imageList.isEmpty()) {
                    final List<ImageElementI> page = imageList.get(random.nextInt(imageList.size()));
                    key.imageVTwoProperty().set(page.get(random.nextInt(page.size())));
                }
            }
        });
        return grid;
    }

    private List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> cachedImageSearch;

    private List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> getImageSearch() {
        return cachedImageSearch != null ? cachedImageSearch : (cachedImageSearch = ImageDictionaries.INSTANCE.searchImage("de la du"));
    }

    int randBetween(int minInclusive, int maxExclusive) {
        return minInclusive + random.nextInt(maxExclusive - minInclusive);
    }
}
