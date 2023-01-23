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
package org.lifecompanion.ui.app.main.ribbon.available.create;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.app.displayablecomponent.CommonComponentStage;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.util.javafx.FXControlUtils;

/**
 * @author Mathieu THEBAUD
 */
public class MiscAddRibbonPart extends RibbonBasePart<Void> implements LCViewInitHelper {
    private Button buttonSaveAsModel;

    public MiscAddRibbonPart() {
        this.initAll();
    }

    @Override
    public void initUI() {
        this.buttonSaveAsModel = FXControlUtils.createTextButtonWithGraphics(Translation.getText("menu.select.save.component"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.SAVE).sizeFactor(2).color(LCGraphicStyle.MAIN_DARK),
                "tooltip.menu.select.save.component");
        this.buttonSaveAsModel.setWrapText(true);
        buttonSaveAsModel.setPrefWidth(100.0);
        buttonSaveAsModel.setTextAlignment(TextAlignment.CENTER);

        VBox boxContent = new VBox(buttonSaveAsModel);
        boxContent.setAlignment(Pos.CENTER);

        this.setTitle(Translation.getText("ribbon.part.misc.add"));
        this.setContent(boxContent);
    }

    @Override
    public void initListener() {
        this.buttonSaveAsModel.setOnAction(e -> CommonComponentStage.getInstance().show());
    }

    @Override
    public void initBinding() {
        this.disableProperty().bind(SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().isNull());
    }

    @Override
    public void bind(final Void modelP) {
    }

    @Override
    public void unbind(final Void modelP) {
    }
}
