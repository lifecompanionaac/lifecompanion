package org.lifecompanion.plugin.spellgame.controller.task;

import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.plugin.spellgame.model.GameStepEnum;
import org.lifecompanion.plugin.spellgame.model.SpellGameStepResult;
import org.lifecompanion.plugin.spellgame.model.SpellGameWordList;
import org.lifecompanion.util.model.LCTask;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class ExportGameResultTask extends LCTask<Void> {
    private final SpellGameWordList wordList;
    private final List<SpellGameStepResult> answers;
    private final File destinationDirectory;
    private final int endIndex;
    private final int endScore;

    public ExportGameResultTask(SpellGameWordList wordList, int endIndex, int endScore, List<SpellGameStepResult> answers) {
        super("spellgame.plugin.task.generate.report.title");
        this.endIndex = endIndex;
        this.endScore = endScore;
        this.wordList = wordList;
        this.answers = answers;
        this.destinationDirectory = org.lifecompanion.util.IOUtils.getTempDir("lifecompanion-spellgame-report");
        this.destinationDirectory.mkdirs();
    }

    @Override
    protected Void call() throws Exception {
        this.answers.forEach(System.out::println);

        prepareResource("bootstrap.bundle.min.js");
        prepareResource("bootstrap.min.css");

        final String reportTemplate = IOUtils.readStreamLines(ResourceHelper.getInputStreamForPath("/report/report.html"), "UTF-8");
        final String rowTemplate = IOUtils.readStreamLines(ResourceHelper.getInputStreamForPath("/report/report_row.html"), "UTF-8");


        StringBuilder rows = new StringBuilder();
        for (int i = 0; i < answers.size(); i++) {
            SpellGameStepResult answer = answers.get(i);
            rows.append(replace(rowTemplate, FluentHashMap
                            .map("cssClass", answer.status().getCssClass())
                            .with("rowIndex", "" + (i + 1))
                            .with("word", answer.word())
                            .with("stepName", answer.step().getName())
                            .with("answer", answer.input())
                            .with("expected", answer.step().getExpectedResult(answer.word()))
                            .with("time", answer.timeSpent() / 1000.0 + "s")
                    )
            );
        }

        String resultHtml = replace(reportTemplate, FluentHashMap
                .map("wordListName", wordList.nameProperty().get())
                .with("wordListSize", (endIndex + 1) + " / " + wordList.getWords().size())
                .with("testDate", StringUtils.dateToStringDateWithHour(new Date()))
                .with("testDuration", org.lifecompanion.util.StringUtils.durationToString((int) (answers.stream().mapToLong(SpellGameStepResult::timeSpent).sum() / 1000.0)))
                .with("testScore", endScore + " / " + (endIndex + 1) * GameStepEnum.values().length)
                .with("rows", rows.toString())
        );


        final File destHtmlFile = new File(destinationDirectory.getAbsolutePath() + File.separator + "index.html");
        IOUtils.writeToFile(destHtmlFile, resultHtml, "UTF-8");
        Desktop.getDesktop().open(destHtmlFile);

        return null;
    }

    private void prepareResource(String fileName) throws IOException {
        new File(destinationDirectory.getAbsolutePath() + "/res/").mkdirs();
        try (final FileOutputStream fos = new FileOutputStream(destinationDirectory.getAbsolutePath() + "/res/" + fileName)) {
            IOUtils.copyStream(ResourceHelper.getInputStreamForPath("/report/res/" + fileName), fos);
        }
    }

    private String replace(String src, String varKey, String varValue) {
        return replace(src, FluentHashMap.map(varKey, varValue));
    }

    private static final String EMPTY_TEXT = "<span class=\"fst-italic text-secondary\">(vide)</span>";

    private static String replace(String src, Map<String, String> vars) {
        String result = src;
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            String val = entry.getValue() == null ? EMPTY_TEXT : entry.getValue();
            result = result.replaceFirst("\\{" + Matcher.quoteReplacement(entry.getKey()) + "\\}", Matcher.quoteReplacement(val));
        }
        return result;
    }
}
