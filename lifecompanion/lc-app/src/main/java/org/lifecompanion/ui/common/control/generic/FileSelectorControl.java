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

package org.lifecompanion.ui.common.control.generic;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser.ExtensionFilter;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.controller.editmode.LCFileChoosers;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXUtils;

import java.io.File;

/**
 * Component to select a file from current file system.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class FileSelectorControl extends VBox implements LCViewInitHelper {
    private final FileSelectorControlMode mode;
    private ObjectProperty<File> value;
    private StringProperty fileName;
    private ExtensionFilter extensionFilter;
    private String openDialogTitle;
    private String labelText;
    private Button buttonSelectFile, buttonRemoveFile;
    private TextField fieldFileName;
    private final FileChooserType fileChooserType;

    public FileSelectorControl(final String labelTextP, FileSelectorControlMode mode, FileChooserType fileChooserType) {
        this.fileChooserType = fileChooserType;
        this.mode = mode;
        this.labelText = labelTextP;
        this.value = new SimpleObjectProperty<>();
        this.fileName = new SimpleStringProperty();
        this.initAll();
    }

    public FileSelectorControl(final String labelTextP, FileChooserType fileChooserType) {
        this(labelTextP, FileSelectorControlMode.FILE, fileChooserType);
    }

    // Class part : "Public API"
    //========================================================================
    public ExtensionFilter getExtensionFilter() {
        return this.extensionFilter;
    }

    public void setExtensionFilter(final ExtensionFilter extensionFilter) {
        this.extensionFilter = extensionFilter;
    }

    public String getOpenDialogTitle() {
        return this.openDialogTitle;
    }

    public void setOpenDialogTitle(final String openDialogTitle) {
        this.openDialogTitle = openDialogTitle;
    }

    public StringProperty fileName() {
        return this.fileName;
    }

    public ObjectProperty<File> valueProperty() {
        return this.value;
    }
    //========================================================================

    // Class part : "Internal"
    //========================================================================
    @Override
    public void initUI() {
        //Fields
        Label labelTitle = new Label(this.labelText);
        this.fieldFileName = new TextField();
        this.fieldFileName.setEditable(false);
        this.fieldFileName.textProperty().bind(this.fileName);
        this.buttonSelectFile = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.FILE).sizeFactor(1).color(LCGraphicStyle.MAIN_PRIMARY),
                "tooltip.file.selector.select.file");
        this.buttonRemoveFile = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).sizeFactor(1).color(LCGraphicStyle.SECOND_PRIMARY),
                "tooltip.file.selector.remove");

        //Total
        HBox.setHgrow(this.fieldFileName, Priority.ALWAYS);
        HBox boxButton = new HBox(5.0, this.fieldFileName, this.buttonSelectFile, this.buttonRemoveFile);
        this.getChildren().addAll(labelTitle, boxButton);
    }

    @Override
    public void initListener() {
        this.buttonSelectFile.setOnAction((ea) -> {
            if (mode == FileSelectorControlMode.FILE) {
                this.value.set(LCFileChoosers.getOtherFileChooser(this.openDialogTitle, this.extensionFilter, this.fileChooserType)
                        .showOpenDialog(FXUtils.getSourceWindow(buttonSelectFile)));
            } else {
                this.value.set(LCFileChoosers.getChooserDirectory(this.fileChooserType, this.openDialogTitle)
                        .showDialog(FXUtils.getSourceWindow(buttonSelectFile)));
            }
        });
        this.buttonRemoveFile.setOnAction((ea) -> {
            this.value.set(null);
        });
    }

    @Override
    public void initBinding() {
        this.buttonRemoveFile.disableProperty().bind(this.value.isNull());
        this.value.addListener((obs, ov, nv) -> {
            if (nv != null) {
                LCStateController.INSTANCE.updateDefaultDirectory(fileChooserType, nv.getParentFile());
                this.fileName.set(nv.getName());
            } else {
                this.fileName.set(null);
            }
        });
    }
    //========================================================================

    // Class part : "File selector mode"
    //========================================================================
    public static enum FileSelectorControlMode {
        FILE, FOLDER;
    }
    //========================================================================
}
