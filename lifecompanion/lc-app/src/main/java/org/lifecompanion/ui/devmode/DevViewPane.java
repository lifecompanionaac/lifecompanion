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

package org.lifecompanion.ui.devmode;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.lifecompanion.ui.common.pane.generic.cell.LogEntryListCell;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.controller.devmode.DevModeController;
import org.lifecompanion.model.impl.devmode.LogEntry;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;

import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * View used in dev mode to display console and infos.<br>
 * Used by plugin developpers.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class DevViewPane extends BorderPane implements LCViewInitHelper {

    /**
     * List that contains log entries
     */
    private ListView<LogEntry> logEntryListView;

    /**
     * Text area that contains standard stream
     */
    private TextArea textAreaSystemOut, textAreaSystemErr;

    private TextField fieldSystemProperty;

    /**
     * Label to display memory info
     */
    private Label labelMemoryInfo;
    private Label labelSystemValue;

    /**
     * Button to run GC
     */
    private Button buttonRunGc;

    /**
     * Timer to display memory info
     */
    private Timer timerMemoryInfo;

    public DevViewPane() {
        this.initAll();
    }

    @Override
    public void initUI() {
        //Log tab
        this.logEntryListView = new ListView<>(DevModeController.INSTANCE.getLogEntries());
        this.logEntryListView.setCellFactory(lv -> new LogEntryListCell());
        Tab tabLog = new Tab(Translation.getText("dev.stage.tab.log"), this.logEntryListView);

        //Memory tab
        this.labelMemoryInfo = new Label();
        this.buttonRunGc = new Button(Translation.getText("dev.stage.run.gc"));
        fieldSystemProperty = new TextField();
        fieldSystemProperty.setPromptText("System prop");
        fieldSystemProperty.setPrefWidth(150.0);
        labelSystemValue = new Label();
        VBox boxMemory = new VBox(10.0, labelMemoryInfo, buttonRunGc, fieldSystemProperty, labelSystemValue);
        boxMemory.setAlignment(Pos.CENTER);
        Tab tabMem = new Tab(Translation.getText("dev.stage.tab.memory"), boxMemory);

        //Tab System.out
        this.textAreaSystemOut = this.createTextAreaForStream(System.out, System::setOut, false);
        Tab tabOut = new Tab(Translation.getText("dev.stage.tab.system.out"), this.textAreaSystemOut);

        //Tab System.err
        this.textAreaSystemErr = this.createTextAreaForStream(System.err, System::setErr, true);
        Tab tabErr = new Tab(Translation.getText("dev.stage.tab.system.err"), this.textAreaSystemErr);

        //Add
        TabPane tabPane = new TabPane(tabLog, tabMem, tabOut, tabErr);
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        this.setCenter(tabPane);
    }

    @Override
    public void initListener() {
        //Time for memory infos
        timerMemoryInfo = new Timer(true);
        timerMemoryInfo.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Runtime runtime = Runtime.getRuntime();
                long maxMemory = runtime.maxMemory();
                long freeMemory = runtime.freeMemory();
                long totalMemory = runtime.totalMemory();
                FXThreadUtils.runOnFXThread(() -> labelMemoryInfo.setText(Translation.getText("dev.stage.memory.infos",
                        FileNameUtils.getFileSize(totalMemory - freeMemory), FileNameUtils.getFileSize(totalMemory), FileNameUtils.getFileSize(maxMemory))));
            }
        }, 4000, 1000);
        //Button to run GC
        this.buttonRunGc.setOnAction(e -> Runtime.getRuntime().gc());
        this.fieldSystemProperty.setOnAction(event -> labelSystemValue.setText(fieldSystemProperty.getText() + " = " + System.getProperty(fieldSystemProperty.getText())));
    }

    private TextArea createTextAreaForStream(final PrintStream printStream, final Consumer<PrintStream> setter, final boolean err) {
        final TextArea textArea = new TextArea();
        textArea.getStyleClass().add("log-text-area");
        // .log-text-area {
        //	-fx-font-family: "Consolas";
        //	-fx-font-size: 11.0px;
        //}
        textArea.getStyleClass().add(err ? "text-fill-red" : "");
        PrintStream printStreamH = new PrintStream(printStream) {
            @Override
            public void write(final byte[] bytes, final int i, final int i1) {
                super.write(bytes, i, i1);
                final String text = new String(bytes, i, i1);
                FXThreadUtils.runOnFXThread(() -> {
                    textArea.appendText(text);
                    textArea.end();
                });
            }
        };
        setter.accept(printStreamH);
        return textArea;
    }

}
