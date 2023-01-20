package org.lifecompanion.plugin.ppp.services;

import javafx.stage.FileChooser;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.ppp.PediatricPainProfilePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public enum FilesService {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesService.class);

    public static final FileChooser.ExtensionFilter DATA_EXTENSION_FILTER = new FileChooser.ExtensionFilter(
            Translation.getText("ppp.plugin.view.commons.files.extension"), "*.lcpp");

    public static final FileChooser.ExtensionFilter PDF_EXTENSION_FILTER = new FileChooser.ExtensionFilter(
            Translation.getText("ppp.plugin.view.commons.pdf.extension"), "*.pdf");

    public <T> void jsonSave(T instanceOfT, String filePath) {
        File file = new File(filePath);
        IOUtils.createParentDirectoryIfNeeded(file);
        try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8)) {
            JsonService.GSON.toJson(instanceOfT, pw);
        } catch (Exception e) {
            LOGGER.error("Couldn't save JSON to {}", file, e);
        }
    }

    public void jsonDelete(String filePath) {
        File file = new File(filePath);

        if (file.delete()) {
            LOGGER.debug("Deleted JSON file {}", file);
        } else {
            LOGGER.error("Couldn't delete JSON file {}", file);
        }
    }

    public <T> T jsonLoadOne(Class<T> classOfT, String filePath) {
        File file = new File(filePath);

        return file.isFile() ? this.jsonLoadOne(classOfT, file) : null;
    }

    private <T> T jsonLoadOne(Class<T> classOfT, File file) {
        try (Reader is = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            return JsonService.GSON.fromJson(is, classOfT);
        } catch (Exception e) {
            LOGGER.error("Couldn't load JSON from {}", file, e);
        }

        return null;
    }

    public <T> List<T> jsonLoadMany(Class<T> classOfT, String directoryPath, Function<String, Boolean> fileNameFilter) {
        File directory = new File(directoryPath);
        if (directory.isDirectory()) {
            return this.jsonLoadMany(classOfT, directory, fileNameFilter);
        }

        return Collections.emptyList();
    }

    private <T> List<T> jsonLoadMany(Class<T> classOfT, File directory, Function<String, Boolean> fileNameFilter) {
        File[] files = directory.listFiles(
                (dir, name) -> name.toLowerCase().endsWith(".json") && fileNameFilter.apply(name));
        if (files == null) {
            LOGGER.info("Couldn't load JSON from {}, no files", directory);

            return Collections.emptyList();
        }

        List<T> elements = new ArrayList<>();
        for (File file : files) {
            T element = this.jsonLoadOne(classOfT, file);
            if (element != null) {
                elements.add(element);
            }
        }

        return elements;
    }

    public String getPluginDirectoryPath(LCConfigurationI config) {
        return IOHelper.getConfigurationDirectoryPath(
                ProfileController.INSTANCE.currentProfileProperty().get().getID(), config.getID())
                + "plugins" + File.separator
                + PediatricPainProfilePlugin.PLUGIN_ID + File.separator;
    }

    public void exportData(LCConfigurationI config, File destinationZipFile) throws IOException {
        File pluginDirectory = new File(this.getPluginDirectoryPath(config));
        IOUtils.zipInto(destinationZipFile, pluginDirectory, null);
    }

    public File importData(File dataZipFile) throws IOException {
        File tempDirectory = org.lifecompanion.util.IOUtils.getTempDir(PediatricPainProfilePlugin.PLUGIN_ID + "imported");
        IOUtils.createParentDirectoryIfNeeded(tempDirectory);
        IOUtils.unzipInto(dataZipFile, tempDirectory, null);

        return tempDirectory;
    }
}
