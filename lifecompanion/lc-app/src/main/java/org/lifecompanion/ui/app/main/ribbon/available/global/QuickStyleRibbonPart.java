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
package org.lifecompanion.ui.app.main.ribbon.available.global;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.impl.configurationcomponent.DisplayableComponentBaseImpl;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.editaction.KeyActions;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.editmode.ComponentActionController;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class QuickStyleRibbonPart extends RibbonBasePart<DisplayableComponentBaseImpl> implements LCViewInitHelper {

    /**
     * Button to delete
     */
    private Button buttonDeleteStyle;

    /**
     * Button to copy/paste keys
     */
    private Button buttonCopyKeyStyle, buttonPasteKeyStyle;

    public QuickStyleRibbonPart() {
        this.initAll();
    }

    @Override
    public void initUI() {
        VBox totalBox = new VBox(5.0);
        totalBox.setAlignment(Pos.CENTER);

        //Buttons style clone
        this.buttonCopyKeyStyle = FXControlUtils.createTextButtonWithGraphics(Translation.getText("buttons.copy.key.style"),
                GlyphFontHelper.FONT_AWESOME.create('\uF24D').sizeFactor(2).color(LCGraphicStyle.MAIN_PRIMARY), "tooltip.buttons.copy.key.style");
        this.buttonCopyKeyStyle.setTextAlignment(TextAlignment.CENTER);
        this.buttonPasteKeyStyle = FXControlUtils.createTextButtonWithGraphics(Translation.getText("buttons.paste.key.style"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PAINT_BRUSH).sizeFactor(2).color(LCGraphicStyle.SECOND_DARK),
                "tooltip.buttons.paste.key.style");
        this.buttonPasteKeyStyle.setTextAlignment(TextAlignment.CENTER);
        HBox boxCloneStyle = new HBox(2.0, this.buttonCopyKeyStyle, this.buttonPasteKeyStyle);
        boxCloneStyle.setAlignment(Pos.CENTER);

        //Button to delete key style
        this.buttonDeleteStyle = FXControlUtils.createTextButtonWithGraphics(Translation.getText("buttons.delete.key.styles"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.ERASER).sizeFactor(2).color(LCGraphicStyle.SECOND_DARK),
                "tooltip.buttons.delete.key.styles");

        //Total
        totalBox.getChildren().addAll(boxCloneStyle, new Separator(Orientation.HORIZONTAL), buttonDeleteStyle);
        this.setTitle(Translation.getText("quick.style.ribbon.part.title"));
        this.setContent(totalBox);
    }

    @Override
    public void initListener() {
        this.buttonCopyKeyStyle.setOnAction(ae -> {
            ComponentActionController.INSTANCE.styleCopySourceProperty().set(SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().get());
        });
        this.buttonPasteKeyStyle.setOnAction(KeyActions.HANDLER_PASTE_STYLE);
        this.buttonDeleteStyle.setOnAction(ae -> {
            ConfigActionController.INSTANCE.executeAction(
                    new KeyActions.ClearStyleOnComponents(
                            SelectionController.INSTANCE.getSelectedKeys().isEmpty() ? Arrays.asList(SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().get()) : new ArrayList<>(SelectionController.INSTANCE.getSelectedKeys())
                    )
            );
        });
    }

    @Override
    public void initBinding() {
        this.buttonDeleteStyle.disableProperty().bind(SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().isNull());
        this.buttonCopyKeyStyle.disableProperty().bind(SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().isNull());
        this.buttonPasteKeyStyle.disableProperty().bind(SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().isNull()
                .or(ComponentActionController.INSTANCE.styleCopySourceProperty().isNull()));
    }

    @Override
    public void bind(final DisplayableComponentBaseImpl model) {
    }

    @Override
    public void unbind(final DisplayableComponentBaseImpl model) {
    }

}
