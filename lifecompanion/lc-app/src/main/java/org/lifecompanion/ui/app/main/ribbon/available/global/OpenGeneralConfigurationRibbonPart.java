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

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

/**
 * @author Mathieu THEBAUD
 */
public class OpenGeneralConfigurationRibbonPart extends RibbonBasePart<Void> implements LCViewInitHelper {
    private Button buttonOpenGeneralConfig;

    public OpenGeneralConfigurationRibbonPart() {
        this.initAll();
    }

    @Override
    public void initUI() {
        this.buttonOpenGeneralConfig = FXControlUtils.createTextButtonWithGraphics(Translation.getText("menu.item.general.config.open"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).sizeFactor(2).color(LCGraphicStyle.MAIN_DARK), "menu.item.general.config.open.tooltip");
        this.buttonOpenGeneralConfig.setWrapText(true);
        buttonOpenGeneralConfig.setPrefWidth(100.0);
        buttonOpenGeneralConfig.setTextAlignment(TextAlignment.CENTER);

        VBox boxContent = new VBox(buttonOpenGeneralConfig);
        boxContent.setAlignment(Pos.CENTER);

        this.setTitle(Translation.getText("ribbon.part.general.config"));
        this.setContent(boxContent);
    }

    @Override
    public void initListener() {
        this.buttonOpenGeneralConfig.setOnAction(e -> GeneralConfigurationController.INSTANCE.showStep(GeneralConfigurationStep.GENERAL_INFORMATION));
    }

    @Override
    public void initBinding() {
        this.buttonOpenGeneralConfig.disableProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationProperty().isNull());
    }

    @Override
    public void bind(final Void modelP) {
    }

    @Override
    public void unbind(final Void modelP) {
    }
}
