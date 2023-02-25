package org.lifecompanion.plugin.spellgame.controller.task;

import javafx.util.Pair;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.spellgame.controller.SpellGameController;
import org.lifecompanion.plugin.spellgame.model.SpellGameConstant;
import org.lifecompanion.plugin.spellgame.model.SpellGameResult;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetSpellGameResultsForConfiguration extends LCTask<List<Pair<SpellGameResult, File>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetSpellGameResultsForConfiguration.class);

    private LCConfigurationI configuration;

    public GetSpellGameResultsForConfiguration(LCConfigurationI configuration) {
        super("spellgame.plugin.task.get.game.result.for.configuration");
        this.configuration = configuration;
    }

    @Override
    protected List<Pair<SpellGameResult, File>> call() throws Exception {
        List<Pair<SpellGameResult, File>> results = new ArrayList<>();
        File resultBasePath = SpellGameController.getResultBasePath(configuration);
        File[] files = resultBasePath.listFiles();
        if (files != null) {
            int fIndex = 0;
            for (File resultDir : files) {
                try {
                    File informationFile = new File(resultDir + File.separator + SpellGameConstant.INFORMATION_FILE_NAME);
                    if (informationFile.exists()) {
                        try (Reader is = new BufferedReader(new InputStreamReader(new FileInputStream(informationFile), StandardCharsets.UTF_8))) {
                            SpellGameResult spellGameResult = JsonHelper.GSON.fromJson(is, SpellGameResult.class);
                            results.add(new Pair<>(spellGameResult, resultDir));
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Couldn't read game result in {}", resultDir, e);
                }
                updateProgress(++fIndex, files.length);
            }
        }
        Collections.sort(results, (c1, c2) -> c2.getKey().getCreateAt().compareTo(c1.getKey().getCreateAt()));
        return results;
    }
}
