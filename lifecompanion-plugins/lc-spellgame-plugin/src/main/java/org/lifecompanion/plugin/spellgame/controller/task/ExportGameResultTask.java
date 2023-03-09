package org.lifecompanion.plugin.spellgame.controller.task;

import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.plugin.spellgame.controller.SpellGameController;
import org.lifecompanion.plugin.spellgame.model.SpellGameResult;
import org.lifecompanion.plugin.spellgame.model.SpellGameStepResult;
import org.lifecompanion.util.DesktopUtils;
import org.lifecompanion.util.model.LCTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;

import static org.lifecompanion.plugin.spellgame.model.SpellGameConstant.INFORMATION_FILE_NAME;
import static org.lifecompanion.plugin.spellgame.model.SpellGameConstant.RESULT_HTML_FILE_NAME;

public class ExportGameResultTask extends LCTask<Void> {
    private final File destinationDirectory;
    private final SpellGameResult spellGameResult;

    public ExportGameResultTask(File destinationDirectory, SpellGameResult spellGameResult) {
        super("spellgame.plugin.task.generate.report.title");
        this.spellGameResult = spellGameResult;
        this.destinationDirectory = destinationDirectory;
    }

    @Override
    protected Void call() throws Exception {
        prepareResource("bootstrap.bundle.min.js");
        prepareResource("bootstrap.min.css");

        final String reportTemplate = IOUtils.readStreamLines(ResourceHelper.getInputStreamForPath("/report/report.html"), "UTF-8");
        final String rowTemplate = IOUtils.readStreamLines(ResourceHelper.getInputStreamForPath("/report/report_row.html"), "UTF-8");

        StringBuilder rows = new StringBuilder();
        for (int i = 0; i < spellGameResult.getAnswers().size(); i++) {
            SpellGameStepResult answer = spellGameResult.getAnswers().get(i);
            rows.append(replace(rowTemplate,
                    FluentHashMap.map("cssClass", answer.status().getCssClass())
                            .with("rowIndex", "" + (i + 1))
                            .with("word", answer.word())
                            .with("stepName", answer.step().getName())
                            .with("answer", answer.input())
                            .with("expected", answer.step().getExpectedResult(answer.word()))
                            .with("time", answer.timeSpent() / 1000.0 + "s")));
        }

        String resultHtml = replace(reportTemplate,
                FluentHashMap.map("wordListName", spellGameResult.getListName())
                        .with("wordListSize", spellGameResult.getDoneCount() + " / " + spellGameResult.getListSize())
                        .with("ignoreAccents", Translation.getText(spellGameResult.isIgnoreAccents() ? "spellgame.plugin.report.field.ignore.accent.true" : "spellgame.plugin.report.field.ignore.accent.false"))
                        .with("testDate", StringUtils.dateToStringDateWithHour(new Date()))
                        .with("testDuration", org.lifecompanion.util.StringUtils.durationToString((int) (spellGameResult.getDuration() / 1000.0)))
                        .with("testScore", spellGameResult.getScore() + " / " + spellGameResult.getDoneCount() * SpellGameController.WORD_MAX_SCORE)
                        .with("rows", rows.toString()));

        try (PrintWriter pw = new PrintWriter(destinationDirectory.getAbsolutePath() + File.separator + INFORMATION_FILE_NAME, StandardCharsets.UTF_8)) {
            JsonHelper.GSON.toJson(spellGameResult, pw);
        }

        final File destHtmlFile = new File(destinationDirectory.getAbsolutePath() + File.separator + RESULT_HTML_FILE_NAME);
        IOUtils.writeToFile(destHtmlFile, resultHtml, "UTF-8");
        DesktopUtils.openUrlInDefaultBrowser(destHtmlFile.toURI().toURL().toString());

        return null;
    }

    private void prepareResource(String fileName) throws IOException {
        new File(destinationDirectory.getAbsolutePath() + "/res/").mkdirs();
        try (final FileOutputStream fos = new FileOutputStream(destinationDirectory.getAbsolutePath() + "/res/" + fileName)) {
            IOUtils.copyStream(ResourceHelper.getInputStreamForPath("/report/res/" + fileName), fos);
        }
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
