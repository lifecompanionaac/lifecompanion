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
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.controller.editaction.UserCompActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.javafx.FXControlUtils;

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

        Label labelExplain = new Label(Translation.getText("tooltip.menu.select.save.component"));
        labelExplain.getStyleClass().addAll("text-label-center", "text-font-size-90", "text-font-italic", "text-wrap-enabled", "text-fill-dimgrey");
        labelExplain.setPrefWidth(220);

        VBox boxContent = new VBox(3.0, buttonSaveAsModel, labelExplain);
        boxContent.setAlignment(Pos.CENTER);

        this.setTitle(Translation.getText("ribbon.part.misc.add"));
        this.setContent(boxContent);
    }

    @Override
    public void initListener() {
        this.buttonSaveAsModel.setOnAction(e -> {
            DisplayableComponentI currentComponent = SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().get();
            if (currentComponent != null) {
                ConfigActionController.INSTANCE.executeAction(new UserCompActions.CreateOrUpdateUserComp(currentComponent));
            }
        });
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
