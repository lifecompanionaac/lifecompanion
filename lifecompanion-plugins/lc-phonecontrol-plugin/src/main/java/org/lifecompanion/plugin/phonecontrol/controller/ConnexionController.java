package org.lifecompanion.plugin.phonecontrol.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.plugin.phonecontrol.PhoneControlPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnexionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnexionController.class);
    File adb = null;

    public ConnexionController() { }
    
    public void installAdb(File dataDirectory) {
        String inputFolder = null;
        String adbFileName = "adb";
        SystemType systemType = SystemType.current();

        if (systemType == SystemType.WINDOWS) {
            inputFolder = "/adb/platform-tools-latest-win.zip";
            adbFileName += ".exe";
        } else if (systemType == SystemType.UNIX) {
            inputFolder = "/adb/platform-tools-latest-linux.zip";
        } else {
            LOGGER.error("Unsupported system type");

            return;
        }

        File adbZip = new File(dataDirectory + File.separator + "platform-tools.zip");
        File adbFolder = new File(dataDirectory + File.separator + "platform-tools");

        if (adbFolder.exists()) {
            try {
                LOGGER.info("ADB folder exists, trying to get the latest version.");
                String url = systemType == SystemType.WINDOWS ? 
                    "https://dl.google.com/android/repository/platform-tools-latest-windows.zip" : 
                    "https://dl.google.com/android/repository/platform-tools-latest-linux.zip";

                try (InputStream in = URI.create(url).toURL().openStream()) {
                    Files.copy(in, adbZip.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                IOUtils.unzipInto(adbZip, adbFolder, null);
                adbZip.delete();
            } catch (Exception e) {
                LOGGER.error("Failed to download the latest ADB version, falling back to input folder.", e);
                installAdbFromInputFolder(inputFolder, adbZip, adbFolder);
            }
        } else {
            installAdbFromInputFolder(inputFolder, adbZip, adbFolder);
        }

        adb = new File(dataDirectory + File.separator + "platform-tools" + File.separator + adbFileName);
        adb.setExecutable(true);
    }

    private void installAdbFromInputFolder(String inputFolder, File adbZip, File adbFolder) {
        try {
            if (inputFolder != null) {
                LOGGER.info("Installing ADB from input folder.");
                InputStream is = PhoneControlPlugin.class.getResourceAsStream(inputFolder);
                FileOutputStream fos = new FileOutputStream(adbZip);
                IOUtils.copyStream(is, fos);
                IOUtils.unzipInto(adbZip, adbFolder, null);
                adbZip.delete();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to install ADB from input folder.", e);
        }
    }

    public String getAdbPath() {
        return adb.toString();
    }
}
