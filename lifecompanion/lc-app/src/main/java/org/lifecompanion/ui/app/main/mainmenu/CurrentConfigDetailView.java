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
package org.lifecompanion.ui.app.main.mainmenu;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.fxmisc.easybind.EasyBind;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.Date;

public class CurrentConfigDetailView extends VBox implements LCViewInitHelper {
    private Label labelConfigName;
    private Label labelLastSaveDate;
    private Label labelUnsavedModif;
    private Button buttonSave, buttonExport, buttonClose;

    public CurrentConfigDetailView() {
        this.initAll();
    }

    @Override
    public void initUI() {
        //Style
        this.getStyleClass().add("main-menu-section");
        Label labelPartTitle = FXControlUtils.createTitleLabel("configuration.menu.detail.title");
        //Display config infos
        this.labelConfigName = new Label(Translation.getText("configuration.label.no.current"));
        this.labelConfigName.getStyleClass().addAll("text-fill-primary-dark", "text-font-size-150", "padding-t10");
        VBox.setMargin(this.labelConfigName, new Insets(0, 0, 0, 8));
        this.labelLastSaveDate = new Label(Translation.getText("configuration.label.no.current"));
        this.labelUnsavedModif = new Label();
        this.labelLastSaveDate.setStyle("-fx-text-fill: gray");
        this.labelUnsavedModif.setStyle("-fx-text-fill: gray");
        VBox.setMargin(this.labelLastSaveDate, new Insets(5, 0, 1, 10));
        VBox.setMargin(this.labelUnsavedModif, new Insets(0, 0, 10, 10));
        //Action line 1
        HBox boxButton1 = new HBox();
        VBox.setMargin(boxButton1, new Insets(10, 0, 0, 0));
        this.buttonSave = FXControlUtils.createFixedWidthTextButton(Translation.getText("configuration.menu.item.save"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.SAVE).sizeFactor(2).color(LCGraphicStyle.MAIN_PRIMARY), MainMenu.BUTTON_WIDTH,
                "tooltip.save.current.configuration");
        this.buttonExport = FXControlUtils.createFixedWidthTextButton(Translation.getText("configuration.menu.item.export"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.UPLOAD).sizeFactor(2).color(LCGraphicStyle.MAIN_DARK), MainMenu.BUTTON_WIDTH,
                "tooltip.export.current.configuration");
        this.buttonClose = FXControlUtils.createFixedWidthTextButton(Translation.getText("configuration.menu.item.close"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TIMES).size(30).color(LCGraphicStyle.SECOND_DARK), MainMenu.BUTTON_WIDTH,
                "tooltip.remove.current.configuration");
        boxButton1.getChildren().addAll(this.buttonSave, this.buttonExport, this.buttonClose);
        boxButton1.setAlignment(Pos.CENTER);
        //Total
        this.getChildren().addAll(labelPartTitle, this.labelConfigName, this.labelLastSaveDate, this.labelUnsavedModif, boxButton1);
    }

    @Override
    public void initListener() {
        this.buttonSave.setOnAction(LCConfigurationActions.HANDLER_SAVE);
        this.buttonExport.setOnAction(LCConfigurationActions.HANDLER_EXPORT);
        this.buttonClose.setOnAction((ea) -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.CloseEditAction(buttonClose)));
    }

    @Override
    public void initBinding() {
        final ChangeListener<Date> dateChangeListener = (obsd, ovd, nvd) -> {
            FXThreadUtils.runOnFXThread(() -> this.labelLastSaveDate.setText(nvd != null
                    ? Translation.getText("configuration.menu.label.last.date", StringUtils.dateToStringDateWithOnlyHoursMinuteSecond(nvd))
                    : Translation.getText("configuration.menu.label.no.last.date")));
        };
        AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.configurationLastDateProperty().removeListener(dateChangeListener);
            }
            if (nv != null) {
                dateChangeListener.changed(null, null, nv.configurationLastDateProperty().get());
                nv.configurationLastDateProperty().addListener(dateChangeListener);//Date change outside JavaFX Thread, so use listener
            } else {
                this.labelLastSaveDate.setText(Translation.getText("configuration.label.no.current"));
            }
        });
        this.labelConfigName.textProperty().bind(EasyBind.select(AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty())
                .selectObject(LCConfigurationDescriptionI::configurationNameProperty).orElse(Translation.getText("configuration.label.no.current")));
        //Can't remove/export unsaved configuration
        this.buttonClose.disableProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().isNull());
        this.buttonExport.disableProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().isNull());
        this.buttonSave.disableProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().isNull());
        //Bind unsaved modifications
        this.labelUnsavedModif.textProperty()
                .bind(TranslationFX.getTextBinding("configuration.menu.label.unsaved.modif", AppModeController.INSTANCE.getEditModeContext().configurationUnsavedActionProperty()));
    }
}
