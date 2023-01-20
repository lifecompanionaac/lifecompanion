package org.lifecompanion.plugin.spellgame.controller.task;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.util.model.LCTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ImportWordTask extends LCTask<List<String>> {
    private final File file;

    public ImportWordTask(File file) {
        super("spellgame.plugin.task.import.word.title");
        this.file = file;
    }

    @Override
    protected List<String> call() throws Exception {
        List<String> words = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String word;
            while ((word = bufferedReader.readLine()) != null) {
                if (!StringUtils.isBlank(word)) {
                    words.add(word);
                }
            }
        }
        return words;
    }
}
