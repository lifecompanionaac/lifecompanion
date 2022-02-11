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
package org.lifecompanion.ui.common.control.specific.usevariable;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.ui.editmode.ConfigurationProfileLevelEnum;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.controller.resource.LCGlyphFont;
import org.lifecompanion.util.ConfigUIUtils;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.Optional;

/**
 * Text area control that where a user help is present to add use variable.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseVariableTextArea extends BorderPane implements LCViewInitHelper {
    public static final double POP_WIDTH = 340, POP_HEIGHT = 150;

    private TextArea textArea;
    private Button buttonAddVariable;
    private HBox boxButtons;

    public UseVariableTextArea() {
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        //Text
        this.textArea = new TextArea();
        this.textArea.setPrefRowCount(2);

        //Button
        this.boxButtons = new HBox();
        this.buttonAddVariable = UIUtils.createLeftTextButton(Translation.getText("use.variable.insert.to.text.button"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CHAIN).size(16).color(LCGraphicStyle.MAIN_PRIMARY),
                "tooltip.use.variable.add.to.text");
        this.boxButtons.getChildren().addAll(this.buttonAddVariable);

        //Total
        this.setCenter(this.textArea);
        this.setBottom(this.boxButtons);
    }

    @Override
    public void initListener() {
        this.textArea.textProperty().addListener((obs, ov, nv) -> {
            //TODO : check if correct varible names and syntax
        });
        this.buttonAddVariable.setOnAction((ev) -> {
            Optional<UseVariableDefinitionI> useVariableDefinition = UseVariableSelectionDialog.getInstance().showAndWait();
            if (useVariableDefinition.isPresent()) {
                this.insertVariable(useVariableDefinition.get());
            }
        });
    }

    @Override
    public void initBinding() {
        ConfigUIUtils.bindShowForLevelFrom(this.boxButtons, ConfigurationProfileLevelEnum.NORMAL);
    }

    private void insertVariable(final UseVariableDefinitionI variable) {
        int caret = this.textArea.getCaretPosition();
        this.textArea.insertText(caret, UseVariableController.VARIABLE_OPEN_CHAR + variable.getId() + UseVariableController.VARIABLE_CLOSE_CHAR);
        this.textArea.requestFocus();
    }
    //========================================================================

    // Class part : "Get"
    //========================================================================
    public TextArea getTextArea() {
        return this.textArea;
    }

    public void setAvailableUseVariable(final ObservableList<UseVariableDefinitionI> variables) {
        UseVariableSelectionDialog.getInstance().setAvailableUseVariable(variables);
    }
    //========================================================================

    // Class part : "Delegate"
    //========================================================================
    public final void setPromptText(final String value) {
        this.textArea.setPromptText(value);
    }

    public final String getText() {
        return this.textArea.getText();
    }

    public final void setText(final String value) {
        this.textArea.setText(value);
    }

    public void appendText(final String text) {
        this.textArea.appendText(text);
    }

    public void clear() {
        this.textArea.clear();
    }

    @Override
    public void requestFocus() {
        this.textArea.requestFocus();
    }

    public StringProperty textProperty() {
        return this.textArea.textProperty();
    }
    //========================================================================

}
