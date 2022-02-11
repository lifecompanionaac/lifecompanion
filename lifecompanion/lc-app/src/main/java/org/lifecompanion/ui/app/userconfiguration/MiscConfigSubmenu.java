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
package org.lifecompanion.ui.app.userconfiguration;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.io.IOManager;
import org.lifecompanion.controller.editaction.GlobalActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.ErrorHandlingController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Miscellaneous items config tab
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MiscConfigSubmenu extends VBox implements LCViewInitHelper, UserConfigSubmenuI {
    private final static Logger LOGGER = LoggerFactory.getLogger(MiscConfigSubmenu.class);

    /**
     * Button to package logs (for debug use)
     */
    private Button buttonPackageLogs, buttonOpenLogFolder, buttonOpenLogFile;

    /**
     * Button to open folders
     */
    private Button buttonOpenRootFolder, buttonOpenCurrentProfileFolder, buttonOpenCurrentConfigFolder, buttonExecuteGC, buttonOpenConfigCleanXml;

    private Label labelMemoryInfo;

    // TODO : button clean cache

    public MiscConfigSubmenu() {
        this.initAll();
    }

    @Override
    public Region getView() {
        return this;
    }

    @Override
    public void initUI() {
        //Folder
        Label labelTitleFolder = this.createLabel("misc.config.tab.part.folders");
        this.buttonOpenRootFolder = this.createButton("button.open.root.folder");
        this.buttonOpenCurrentProfileFolder = this.createButton("button.open.current.profile.folder");
        this.buttonOpenCurrentConfigFolder = this.createButton("button.open.current.config.folder");
        this.buttonOpenConfigCleanXml = this.createButton("button.open.current.config.clean.xml.folder");

        //Logs
        Label labelTitleLog = this.createLabel("misc.config.tab.part.logs");
        this.buttonOpenLogFile = this.createButton("button.open.log.file");
        this.buttonOpenLogFolder = this.createButton("button.open.log.folder");
        this.buttonPackageLogs = this.createButton("button.package.log.debug");

        Label labelExplain = new Label(Translation.getText("misc.submenu.explain.text"));
        labelExplain.getStyleClass().addAll("text-wrap-enabled", "text-weight-bold");
        labelExplain.setTextAlignment(TextAlignment.JUSTIFY);

        Label labelTitleMemory = this.createLabel("misc.config.tab.part.memory");
        labelMemoryInfo = new Label();
        labelMemoryInfo.setAlignment(Pos.CENTER);
        buttonExecuteGC = createButton("misc.config.tab.memory.button.gc");

        //Add
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(10.0);
        this.getChildren().addAll(labelExplain, labelTitleFolder, this.buttonOpenRootFolder, this.buttonOpenCurrentProfileFolder,
                this.buttonOpenCurrentConfigFolder, buttonOpenConfigCleanXml, labelTitleLog, buttonOpenLogFile, buttonOpenLogFolder, this.buttonPackageLogs, labelTitleMemory, labelMemoryInfo, buttonExecuteGC);
    }

    private Button createButton(final String textId) {
        Button button = new Button(Translation.getText(textId));
        button.setPrefWidth(350.0);
        return button;
    }

    private Label createLabel(final String textId) {
        Label labelTitleLog = new Label(Translation.getText(textId));
        labelTitleLog.getStyleClass().add("menu-part-title");
        labelTitleLog.setMaxWidth(Double.MAX_VALUE);
        return labelTitleLog;
    }

    @Override
    public void initListener() {
        this.buttonPackageLogs.setOnAction(e -> ConfigActionController.INSTANCE.executeAction(new GlobalActions.PackageLogAction(buttonPackageLogs)));
        this.buttonOpenRootFolder.setOnAction(e -> this.openFileOrFolder(buttonOpenRootFolder, "."));
        this.buttonOpenLogFolder.setOnAction(e -> this.openFileOrFolder(buttonOpenLogFolder, System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "logs"));
        this.buttonExecuteGC.setOnAction(e -> Runtime.getRuntime().gc());
        this.buttonOpenCurrentProfileFolder.setOnAction(
                e -> this.openFileOrFolder(buttonOpenCurrentProfileFolder, IOManager.INSTANCE.getProfileDirectoryPath(ProfileController.INSTANCE.currentProfileProperty().get().getID())));
        this.buttonOpenCurrentConfigFolder.setOnAction(
                e -> this.openFileOrFolder(buttonOpenCurrentConfigFolder, IOManager.INSTANCE.getConfigurationDirectoryPath(ProfileController.INSTANCE.currentProfileProperty().get().getID(),
                        AppModeController.INSTANCE.getEditModeContext().configurationProperty().get().getID())));
        this.buttonOpenLogFile.setOnAction(e -> this.openFileOrFolder(buttonOpenLogFile, System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "logs" + File.separator + "application.log"));
        this.buttonOpenConfigCleanXml.setOnAction(e -> {
            File configurationDirectory = new File(IOManager.INSTANCE.getConfigurationDirectoryPath(ProfileController.INSTANCE.currentProfileProperty().get().getID(),
                    AppModeController.INSTANCE.getEditModeContext().configurationProperty().get().getID()));
            final File destDirTempConfig = LCUtils.getTempDir("configuration-debug-dir");
            exploreAndFormatXmlFiles(configurationDirectory, destDirTempConfig, configurationDirectory);
            openFileOrFolder(buttonOpenConfigCleanXml, destDirTempConfig.getPath());
        });
    }

    private static final Format PRETTY_XML_FORMAT = Format
            .getPrettyFormat()
            .setEncoding(StandardCharsets.UTF_8.name());

    private void exploreAndFormatXmlFiles(File root, File destRoot, File file) {
        try {
            if (file.isFile() && "xml".equalsIgnoreCase(FileNameUtils.getExtension(file))) {
                File destFile = new File(destRoot.getPath() + File.separator + IOUtils.getRelativePath(file.getPath(), root.getPath()));
                SAXBuilder saxBuilder = new SAXBuilder();
                try (BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                    Document doc = saxBuilder.build(is);
                    XMLOutputter xmlOutputter = new XMLOutputter(PRETTY_XML_FORMAT);
                    destFile.getParentFile().mkdirs();
                    try (OutputStream os = new FileOutputStream(destFile)) {
                        xmlOutputter.output(doc.getRootElement(), os);
                    }
                }
            } else if (file.isDirectory()) {
                final File[] children = file.listFiles();
                if (children != null) {
                    for (File child : children) {
                        exploreAndFormatXmlFiles(root, destRoot, child);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("XML formatting for file {} wasn't possible", file, e);
        }
    }

    private void openFileOrFolder(Node source, final String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (Throwable t) {
                MiscConfigSubmenu.LOGGER.error("Impossible to open a lc directory : {}", file.getAbsolutePath(), t);
                ErrorHandlingController.INSTANCE.showErrorNotificationWithExceptionDetails(Translation.getText("open.folder.lc.error.title"), LCException.newException().withCause(t).withMessage("open.folder.lc.error.unknown", file.getAbsolutePath()).build());
            }
        } else {
            ErrorHandlingController.INSTANCE.showErrorNotificationWithExceptionDetails(Translation.getText("open.folder.lc.error.title"), LCException.newException().withMessage("open.folder.lc.error.directory.not.found", file.getAbsolutePath()).build());
        }
    }

    private Timer timerMemoryInfo;

    @Override
    public void updateFields() {
        timerMemoryInfo = new Timer(true);
        timerMemoryInfo.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Runtime runtime = Runtime.getRuntime();
                long maxMemory = runtime.maxMemory();
                long freeMemory = runtime.freeMemory();
                long totalMemory = runtime.totalMemory();
                LCUtils.runOnFXThread(() -> labelMemoryInfo.setText(Translation.getText("misc.config.tab.memory.info",
                        FileNameUtils.getFileSize(totalMemory - freeMemory), FileNameUtils.getFileSize(totalMemory), FileNameUtils.getFileSize(maxMemory))));
            }
        }, 500, 1000);
    }

    @Override
    public void updateModel() {
        killTimer();
    }

    @Override
    public void cancel() {
        killTimer();
    }

    private void killTimer() {
        if (this.timerMemoryInfo != null) {
            this.timerMemoryInfo.cancel();
            this.timerMemoryInfo = null;
        }
    }

    @Override
    public String getTabTitleId() {
        return "user.config.tab.misc";
    }

}
