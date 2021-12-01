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

package org.lifecompanion.framework.commons.fx.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.io.File;

public class SimpleDirectorySelector extends HBox implements LCViewInitHelper {
    private static Font FONT_AWESOME = Font.loadFont(SimpleDirectorySelector.class.getResourceAsStream("/framework/font/fontawesome-webfont.ttf"), 12);

    private TextField fieldFilePath;
    private Button buttonSelect;
    private final ObjectProperty<File> file;
    private final String directoryName;

    public SimpleDirectorySelector(String directoryName) {
        super(5.0);
        this.directoryName = directoryName;
        this.file = new SimpleObjectProperty<>();
        this.initAll();
    }

    public ObjectProperty<File> fileProperty() {
        return this.file;
    }

    @Override
    public void initUI() {
        this.fieldFilePath = new TextField();
        this.buttonSelect = new Button(String.valueOf('\uf15B'));
        this.buttonSelect.setFont(FONT_AWESOME);
        this.buttonSelect.setTextFill(Color.GRAY);
        HBox.setHgrow(fieldFilePath, Priority.ALWAYS);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(fieldFilePath, buttonSelect);
    }

    @Override
    public void initListener() {
        this.buttonSelect.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(Translation.getText("file.chooser.directory"));
            directoryChooser.setInitialDirectory(file.get() != null && file.get().getParentFile() != null ? file.get().getParentFile() : new File(System.getProperty("user.home")));
            File file = directoryChooser.showDialog(this.buttonSelect.getScene().getWindow());
            if (file != null) {
                this.file.set(directoryName != null ? new File(file.getPath() + File.separator + directoryName) : file);
            }
        });
    }

    @Override
    public void initBinding() {
        this.fieldFilePath.textProperty().addListener((obs, ov, nv) -> file.set(StringUtils.isBlank(nv) ? null : new File(nv)));
        this.file.addListener((obs, ov, nv) -> fieldFilePath.setText(nv != null ? nv.getPath() : null));
    }
}
