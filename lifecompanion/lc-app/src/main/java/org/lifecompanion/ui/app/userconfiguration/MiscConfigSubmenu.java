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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editaction.GlobalActions;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.ErrorHandlingController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.task.GenerateRandomConfigurationTask;
import org.lifecompanion.controller.io.task.GenerateTechDemoConfigurationTask;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.model.api.configurationcomponent.IdentifiableComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.style.ShapeStyle;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListLeaf;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.DesktopUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Miscellaneous items config tab
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MiscConfigSubmenu extends ScrollPane implements LCViewInitHelper, UserConfigSubmenuI {
    private final static Logger LOGGER = LoggerFactory.getLogger(MiscConfigSubmenu.class);

    /**
     * Button to package logs (for debug use)
     */
    private Button buttonPackageLogs, buttonOpenLogFolder, buttonOpenLogFile;

    /**
     * Button to open folders
     */
    private Button buttonOpenRootFolder, buttonOpenCurrentProfileFolder, buttonOpenCurrentConfigFolder, buttonExecuteGC, buttonOpenConfigCleanXml, buttonDetectKeylistDuplicates, buttonSetKeylistNodesShape,
            buttonGenerateTechDemoConfiguration, buttonGenerateRandomConfiguration;

    private Label labelMemoryInfo;

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
        Label labelTitleFolder = FXControlUtils.createTitleLabel("misc.config.tab.part.folders");
        this.buttonOpenRootFolder = this.createButton("button.open.root.folder");
        this.buttonOpenCurrentProfileFolder = this.createButton("button.open.current.profile.folder");
        this.buttonOpenCurrentConfigFolder = this.createButton("button.open.current.config.folder");
        this.buttonOpenConfigCleanXml = this.createButton("button.open.current.config.clean.xml.folder");


        //Logs
        Label labelTitleLog = FXControlUtils.createTitleLabel("misc.config.tab.part.logs");
        this.buttonOpenLogFile = this.createButton("button.open.log.file");
        this.buttonOpenLogFolder = this.createButton("button.open.log.folder");
        this.buttonPackageLogs = this.createButton("button.package.log.debug");

        Label labelExplain = new Label(Translation.getText("misc.submenu.explain.text"));
        labelExplain.getStyleClass()
                .addAll("text-wrap-enabled", "text-weight-bold");
        labelExplain.setTextAlignment(TextAlignment.JUSTIFY);

        Label labelTitleMemory = FXControlUtils.createTitleLabel("misc.config.tab.part.memory");
        labelMemoryInfo = new Label();
        labelMemoryInfo.setAlignment(Pos.CENTER);
        buttonExecuteGC = createButton("misc.config.tab.memory.button.gc");

        //PDF
        Label labelTitlePdf = FXControlUtils.createTitleLabel("misc.config.tab.part.pdf");
        Button buttonGeneratePdf = createButton("button.generate.lists.pdf");
        buttonGeneratePdf.setOnAction(e -> {
            GridPane gridPaneTotal = new GridPane();
            ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.ExportEditActionsToPdfAction(gridPaneTotal));
        });

        //Add
        VBox boxChildren = new VBox(10,
                labelExplain,
                labelTitleFolder,
                this.buttonOpenRootFolder,
                this.buttonOpenCurrentProfileFolder,
                this.buttonOpenCurrentConfigFolder,
                buttonOpenConfigCleanXml,
                labelTitleLog,
                buttonOpenLogFile,
                buttonOpenLogFolder,
                this.buttonPackageLogs,
                labelTitlePdf,
                buttonGeneratePdf,
                labelTitleMemory,
                labelMemoryInfo,
                buttonExecuteGC
        );
        boxChildren.setAlignment(Pos.TOP_CENTER);
        this.setContent(boxChildren);
        this.setFitToWidth(true);

        Label labelTitleTesting = FXControlUtils.createTitleLabel("misc.config.tab.part.dev");
        buttonGenerateTechDemoConfiguration = this.createButton("button.generate.tech.demo.configuration");
        buttonGenerateRandomConfiguration = this.createButton("button.testing.random.configuration");
        this.buttonDetectKeylistDuplicates = this.createButton("button.detect.keylist.duplicates");
        this.buttonSetKeylistNodesShape = this.createButton("button.set.keylist.node.shape");

        // Developers : to test your feature, create and add your nodes here and make sure "org.lifecompanion.debug.dev.env" property is enabled
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.PROP_DEV_MODE)) {
            boxChildren.getChildren()
                    .addAll(labelTitleTesting, buttonGenerateTechDemoConfiguration, buttonDetectKeylistDuplicates, buttonGenerateRandomConfiguration, buttonSetKeylistNodesShape);
        }
    }

    private Button createButton(final String textId) {
        Button button = new Button(Translation.getText(textId));
        button.setPrefWidth(350.0);
        return button;
    }

    @Override
    public void initListener() {
        this.buttonPackageLogs.setOnAction(e -> ConfigActionController.INSTANCE.executeAction(new GlobalActions.PackageLogAction(buttonPackageLogs)));
        this.buttonOpenRootFolder.setOnAction(e -> this.openFileOrFolder(buttonOpenRootFolder, "."));
        this.buttonOpenLogFolder.setOnAction(e -> this.openFileOrFolder(buttonOpenLogFolder, System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "logs"));
        this.buttonExecuteGC.setOnAction(e -> Runtime.getRuntime()
                .gc());
        this.buttonOpenCurrentProfileFolder.setOnAction(e -> this.openFileOrFolder(
                buttonOpenCurrentProfileFolder,
                IOHelper.getProfileDirectoryPath(ProfileController.INSTANCE.currentProfileProperty()
                        .get()
                        .getID())
        ));
        this.buttonOpenCurrentConfigFolder.setOnAction(e -> this.openFileOrFolder(buttonOpenCurrentConfigFolder,
                IOHelper.getConfigurationDirectoryPath(ProfileController.INSTANCE.currentProfileProperty()
                                .get()
                                .getID(),
                        AppModeController.INSTANCE.getEditModeContext()
                                .configurationProperty()
                                .get()
                                .getID()
                )
        ));
        this.buttonOpenLogFile.setOnAction(e -> this.openFileOrFolder(buttonOpenLogFile,
                System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "logs" + File.separator +
                        "application.log"
        ));
        this.buttonOpenConfigCleanXml.setOnAction(e -> {
            File configurationDirectory = new File(IOHelper.getConfigurationDirectoryPath(ProfileController.INSTANCE.currentProfileProperty()
                            .get()
                            .getID(),
                    AppModeController.INSTANCE.getEditModeContext()
                            .configurationProperty()
                            .get()
                            .getID()
            ));
            final File destDirTempConfig = org.lifecompanion.util.IOUtils.getTempDir("configuration-debug-dir");
            exploreAndFormatXmlFiles(configurationDirectory, destDirTempConfig, configurationDirectory);
            openFileOrFolder(buttonOpenConfigCleanXml, destDirTempConfig.getPath());
        });
        this.buttonDetectKeylistDuplicates.setOnAction(e -> {
            this.detectAndFixKeylistDuplicates();
        });
        this.buttonGenerateRandomConfiguration.setOnAction(e -> {
            AsyncExecutorController.INSTANCE.addAndExecute(true, false, new GenerateRandomConfigurationTask());
        });
        this.buttonGenerateTechDemoConfiguration.setOnAction(e -> {
            AsyncExecutorController.INSTANCE.addAndExecute(true, false, new GenerateTechDemoConfigurationTask());
        });
        this.buttonSetKeylistNodesShape.setOnAction(e -> {
            setKeylistShapes();
        });
    }

    private void setKeylistShapes() {
        final LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext()
                .getConfiguration();
        if (configuration != null) {
            final KeyListNodeI keyListNodes = configuration.rootKeyListNodeProperty().get();
            keyListNodes.traverseTreeToBottom(n -> {
                if (n.isLinkNode() || !n.isLeafNode()) {
                    n.shapeStyleProperty().set(ShapeStyle.TP_ANGLE_CUT);
                }
            });
        }
    }


    private static final Format PRETTY_XML_FORMAT = Format.getPrettyFormat()
            .setEncoding(StandardCharsets.UTF_8.name());

    private void exploreAndFormatXmlFiles(File root, File destRoot, File file) {
        try {
            if (file.isFile() && "xml".equalsIgnoreCase(FileNameUtils.getExtension(file))) {
                File destFile = new File(destRoot.getPath() + File.separator + IOUtils.getRelativePath(file.getPath(), root.getPath()));
                SAXBuilder saxBuilder = new SAXBuilder();
                try (BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                    Document doc = saxBuilder.build(is);
                    XMLOutputter xmlOutputter = new XMLOutputter(PRETTY_XML_FORMAT);
                    destFile.getParentFile()
                            .mkdirs();
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
        if (!DesktopUtils.openFile(file)) {
            ErrorHandlingController.INSTANCE.showErrorNotificationWithExceptionDetails(Translation.getText("open.folder.lc.error.title"),
                    LCException.newException()
                            .withMessage("open.folder.lc.error.directory.not.found", file.getAbsolutePath())
                            .build()
            );
        }
    }

    private void detectAndFixKeylistDuplicates() {
        final LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext()
                .getConfiguration();
        if (configuration != null) {
            final KeyListNodeI keyListNodes = configuration.rootKeyListNodeProperty()
                    .get();
            HashMap<String, List<KeyListNodeI>> nodesById = new HashMap<>();
            keyListNodes.traverseTreeToBottom(node -> nodesById.computeIfAbsent(node.getID(), k -> new ArrayList<>())
                    .add(node));
            nodesById.forEach((id, nodes) -> {
                if (nodes.size() > 1) {
                    if (nodes.stream()
                            .allMatch(n -> n instanceof KeyListLeaf)) {
                        nodes.forEach(IdentifiableComponentI::generateID);
                        LOGGER.info("Solved duplicates for {}", id);
                    } else {
                        LOGGER.info("Should check/fix this key list node for ID {}\n\tDuplicates are : {}",
                                id,
                                nodes.stream()
                                        .map(n -> "[" + n.getClass()
                                                .getSimpleName() + "] - " + n.textProperty()
                                                .get())
                                        .collect(Collectors.joining(", "))
                        );
                    }
                }
            });
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
                FXThreadUtils.runOnFXThread(() -> labelMemoryInfo.setText(Translation.getText("misc.config.tab.memory.info",
                        FileNameUtils.getFileSize(totalMemory - freeMemory),
                        FileNameUtils.getFileSize(totalMemory),
                        FileNameUtils.getFileSize(maxMemory)
                )));
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
