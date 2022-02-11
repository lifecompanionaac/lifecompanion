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
package org.lifecompanion.ui.common.control.specific;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.resource.LCGlyphFont;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * Component to select a file from current file system.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class KeyCodeSelectorControl extends VBox implements LCViewInitHelper {
    private ObjectProperty<KeyCode> value;
    private String labelText;

    private Button buttonRemoveKey;
    private TextField fieldKeyName;
    private Label labelTitle;

    public KeyCodeSelectorControl(final String labelTextP) {
        this.labelText = labelTextP;
        this.value = new SimpleObjectProperty<>();
        this.initAll();
    }

    // Class part : "Public API"
    //========================================================================

    public ObjectProperty<KeyCode> valueProperty() {
        return this.value;
    }
    //========================================================================

    // Class part : "Internal"
    //========================================================================
    @Override
    public void initUI() {
        //Fields
        this.labelTitle = new Label(this.labelText);
        this.labelTitle.setManaged(labelText != null);
        this.fieldKeyName = new TextField();
        this.fieldKeyName.setEditable(false);
        this.fieldKeyName.setPromptText(Translation.getText("key.selector.control.no.key"));
        this.buttonRemoveKey = UIUtils.createGraphicButton(
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).sizeFactor(1).color(LCGraphicStyle.SECOND_PRIMARY),
                "tooltip.key.selector.remove.button");

        //Total
        HBox.setHgrow(this.fieldKeyName, Priority.ALWAYS);
        HBox boxButton = new HBox(5.0, this.fieldKeyName, this.buttonRemoveKey);
        this.getChildren().addAll(this.labelTitle, boxButton);
    }

    @Override
    public void initListener() {
        this.buttonRemoveKey.setOnAction((ea) -> {
            this.value.set(null);
        });
        this.fieldKeyName.setOnKeyPressed((ke) -> {
            this.value.set(ke.getCode());
            this.labelTitle.requestFocus();
        });
    }

    @Override
    public void initBinding() {
        this.buttonRemoveKey.disableProperty().bind(this.value.isNull());
        this.value.addListener((obs, ov, nv) -> {
            if (nv != null) {
                this.fieldKeyName.setText(nv.getName());
            } else {
                this.fieldKeyName.setText(null);
            }
        });
    }
    //========================================================================

}
