package org.lifecompanion.plugin.calendar.controller;

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.impl.plugin.PluginInfo;
import org.lifecompanion.plugin.calendar.CalendarPlugin;
import org.lifecompanion.plugin.calendar.model.CalendarDay;
import org.lifecompanion.plugin.calendar.model.CalendarEvent;
import org.lifecompanion.plugin.calendar.model.LCCalendar;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

// TODO : translation !
public class ExportCalendarTask extends LCTask<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportCalendarTask.class);

    private final File exportDir;
    private final LCCalendar calendar;
    private final LCConfigurationI configuration;
    private final LCConfigurationDescriptionI configurationDescription;
    private final AtomicInteger progress;

    private static final String NONE_TEXT = "<span class=\"fst-italic text-secondary\">AUCUN</span>";
    private static final String NOT_ACTIVATED_TEXT = "<span class=\"fst-italic text-secondary\">NON ACTIVÉ</span>";
    private static final String EMPTY_TEXT = "<span class=\"fst-italic text-secondary\">(vide)</span>";


    public ExportCalendarTask(LCConfigurationI configuration, LCConfigurationDescriptionI configurationDescription, LCCalendar calendar) {
        super("calendar.export.task.name");
        this.exportDir = org.lifecompanion.util.IOUtils.getTempDir("export-lc-calendar");
        this.exportDir.mkdirs();
        this.calendar = calendar;
        this.configurationDescription = configurationDescription;
        this.configuration = configuration;
        this.progress = new AtomicInteger(0);
    }

    @Override
    protected Void call() throws Exception {
        final String rootTemplate = IOUtils.readStreamLines(ResourceHelper.getInputStreamForPath("/print/root_page_template.html"), "UTF-8");
        final String eventTemplate = IOUtils.readStreamLines(ResourceHelper.getInputStreamForPath("/print/event_template.html"), "UTF-8");
        final String sectionTemplate = IOUtils.readStreamLines(ResourceHelper.getInputStreamForPath("/print/section_template.html"), "UTF-8");
        final String configInfoTemplate = IOUtils.readStreamLines(ResourceHelper.getInputStreamForPath("/print/configuration_info_template.html"), "UTF-8");

        new File(exportDir.getAbsolutePath() + "/res/").mkdirs();
        try (final FileOutputStream fos = new FileOutputStream(exportDir.getAbsolutePath() + "/res/bootstrap.bundle.min.js")) {
            IOUtils.copyStream(ResourceHelper.getInputStreamForPath("/print/bootstrap.bundle.min.js"), fos);
        }
        try (final FileOutputStream fos = new FileOutputStream(exportDir.getAbsolutePath() + "/res/bootstrap.min.css")) {
            IOUtils.copyStream(ResourceHelper.getInputStreamForPath("/print/bootstrap.min.css"), fos);
        }

        StringBuilder bodyHtml = new StringBuilder();


        final ObservableList<CalendarDay> days = calendar.getDays();

        final long totalEvent = days.stream().mapToLong(d -> d.getEvents().size()).sum();
        this.updateProgress(0, totalEvent);
        int e = 0;

        Optional<PluginInfo> pluginInfo = PluginController.INSTANCE.getPluginInfoList().stream()
                .filter(p -> CalendarPlugin.PLUGIN_ID.equals(p.getPluginId())
                        && PluginController.INSTANCE.isPluginLoaded(p.getPluginId()))
                .findAny();

        bodyHtml.append(replace(sectionTemplate, "title", "INFORMATIONS"));
        final FluentHashMap<String, String> configInfos = FluentHashMap
                .map("configurationName", configurationDescription != null ? configurationDescription.configurationNameProperty().get() : "CONFIGURATION?")
                .with("lastModificationDate", configurationDescription != null ? StringUtils.dateToStringDateWithHour(configurationDescription.configurationLastDateProperty().get()) : "DATE?")
                .with("exportDate", StringUtils.dateToStringDateWithHour(new Date()))
                .with("pluginVersion", pluginInfo.map(PluginInfo::getPluginVersion).orElse("?"));
        bodyHtml.append(replace(configInfoTemplate, configInfos));

        for (CalendarDay day : days) {
            bodyHtml.append(replace(sectionTemplate, "title", StringUtils.toUpperCase(Translation.getText(day.dayOfWeekProperty().get().getTranslationId()))));
            for (CalendarEvent event : day.getEvents()) {
                // Image
                String img = handleEventImage(event);

                // Create variable map
                final FluentHashMap<String, String> vars = FluentHashMap
                        .map("eventTitle", event.textProperty().get())
                        .with("eventSubTitle", getEventSubTitle(event))
                        .with("eventAlarmText", getEventAlarmText(event))
                        .with("eventAlarmBip", getEventAlarmBip(event))
                        .with("eventTextOnStart", event.enableTextOnStartProperty().get() ? event.textOnStartProperty().get() : NOT_ACTIVATED_TEXT)
                        .with("eventTextOnFinish", event.enableTextOnFinishProperty().get() ? event.textOnFinishProperty().get() : NOT_ACTIVATED_TEXT)
                        .with("eventLinkedTo", getEventLinkedTo(event))
                        .with("eventTimer", getEventTimer(event))
                        .with("eventImage", img);
                bodyHtml.append(replace(eventTemplate, vars));
                this.updateProgress(++e, totalEvent);
            }
        }

        final File destHtmlFile = new File(exportDir.getAbsolutePath() + File.separator + "index.html");
        IOUtils.writeToFile(destHtmlFile, replace(rootTemplate, "body", bodyHtml.toString()), "UTF-8");
        Desktop.getDesktop().open(destHtmlFile);

        return null;
    }

    private static final int IMAGE_SIZE = 150;

    private String handleEventImage(CalendarEvent event) {
        final ImageElementI image = event.imageVTwoProperty().get();
        if (image != null) {
            String imagePath = "img/" + image.getId() + "." + image.getExtension();
            File imageFile = new File(exportDir.getPath() + File.separator + imagePath);
            if (!imageFile.exists()) {
                imageFile.getParentFile().mkdirs();
                try (FileInputStream fis = new FileInputStream(image.getRealFilePath())) {
                    javafx.scene.image.Image fxImage = new javafx.scene.image.Image(fis, IMAGE_SIZE, IMAGE_SIZE, true, true);
                    BufferedImage buffImage = SwingFXUtils.fromFXImage(fxImage, null);
                    if (buffImage == null) {
                        java.awt.Image ci = ImageIO.read(image.getRealFilePath()).getScaledInstance(-1, IMAGE_SIZE, java.awt.Image.SCALE_SMOOTH);
                        buffImage = new BufferedImage(ci.getWidth(null), ci.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = buffImage.createGraphics();
                        g2d.drawImage(ci, 0, 0, null);
                        g2d.dispose();
                    }
                    ImageIO.write(buffImage, image.getExtension(), imageFile);
                } catch (Exception e) {
                    LOGGER.warn("The given image {} was not saved", image.getId(), e);
                }
            }
            return "<img src=\"" + imagePath + "\" class=\"rounded mx-auto d-block\"  style=\"max-height:100px;max-width:100px;\">";
        }
        return "";
    }

    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("#.##");

    private String getEventTimer(CalendarEvent event) {
        if (event.enableAutomaticItemProperty().get()) {
            final int timeInMs = event.automaticItemTimeMsProperty().get();
            for (int i = DurationUnit.values().length - 1; i >= 0; i--) {
                if (timeInMs / DurationUnit.values()[i].toMsRatio >= 1.0) {

                    return DOUBLE_DECIMAL_FORMAT.format(timeInMs / DurationUnit.values()[i].toMsRatio) + " " + DurationUnit.values()[i].getTranslatedName();
                }
            }
        }
        return NONE_TEXT;
    }

    private String getEventLinkedTo(CalendarEvent event) {
        if (event.enableLinkToSequenceProperty().get()) {
            return "Démarre la séquence \"" + configuration
                    .userActionSequencesProperty().get().getUserActionSequences()
                    .stream()
                    .filter(s -> StringUtils.isEquals(s.getID(), event.linkedSequenceIdProperty().get()))
                    .findAny()
                    .map(seq -> seq.nameProperty().get())
                    .orElse("?") + "\"";
        }
        if (event.enableLeisureSelectionProperty().get()) {
            return "Démarre la sélection d'activité autonome";
        }
        return NONE_TEXT;
    }

    private String getEventSubTitle(CalendarEvent event) {
        if (event.enableAtFixedTimeProperty().get()) {
            return "<span class=\"badge bg-danger\">" + event.getFixedTime().getHumanReadableString() + "</span>";
        }
        if (event.enableAutostartWhenPreviousFinishedProperty().get()) {
            return "<span class=\"badge bg-success\">AUTO</span>";
        }
        return "";
    }

    private String getEventAlarmText(CalendarEvent event) {
        return !event.enableAtFixedTimeProperty().get() ? NOT_ACTIVATED_TEXT :
                (event.enableTextOnAlarmProperty().get() ? event.textOnAlarmProperty().get() : NONE_TEXT);
    }

    private String getEventAlarmBip(CalendarEvent event) {
        return event.enableAtFixedTimeProperty().get() && event.enableSoundOnAlarmProperty().get() ?
                "<span class=\"badge bg-primary\">+BIP</span>" : "";
    }

    private String replace(String src, String varKey, String varValue) {
        return replace(src, FluentHashMap.map(varKey, varValue));
    }

    private static String replace(String src, Map<String, String> vars) {
        String result = src;
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            String val = entry.getValue() == null ? EMPTY_TEXT : entry.getValue();
            result = result.replaceFirst("\\{" + Matcher.quoteReplacement(entry.getKey()) + "\\}", Matcher.quoteReplacement(val));
        }
        return result;
    }

    enum DurationUnit {
        MILLISECOND("duration.unit.millisecond", 1.0),
        SECOND("duration.unit.second", 1000.0),
        MINUTE("duration.unit.minute", 60_000.0),
        HOUR("duration.unit.hour", 3600_000.0);

        private final String translationId;
        private final double toMsRatio;

        DurationUnit(String translationId, double toMsRatio) {
            this.translationId = translationId;
            this.toMsRatio = toMsRatio;
        }

        String getTranslatedName() {
            return Translation.getText(translationId);
        }
    }
}
