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
package org.lifecompanion.config.view.pane.menu;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.ui.config.ConfigurationProfileLevelEnum;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.control.refacto.AppModeController;
import org.lifecompanion.base.data.control.refacto.ProfileController;
import org.lifecompanion.config.data.action.impl.LCConfigurationActions;
import org.lifecompanion.config.data.action.impl.LCConfigurationActions.RemoveConfigurationAction;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.Date;

public class CurrentConfigDetailView extends VBox implements LCViewInitHelper {
    private Label labelConfigName;
    private Label labelLastSaveDate;
    private Label labelPartTitle;
    private Label labelUnsavedModif;
    private Button buttonSave, buttonExport, buttonRemove;

    public CurrentConfigDetailView() {
        this.initAll();
    }

    @Override
    public void initUI() {
        //Style
        this.getStyleClass().add("main-menu-section");
        this.labelPartTitle = new Label(Translation.getText("configuration.menu.detail.title"));
        this.labelPartTitle.getStyleClass().add("menu-part-title");
        this.labelPartTitle.setMaxWidth(Double.MAX_VALUE);
        //Display config infos
        this.labelConfigName = new Label(Translation.getText("configuration.menu.no.name"));
        this.labelConfigName.getStyleClass().add("import-blue-title");
        VBox.setMargin(this.labelConfigName, new Insets(0, 0, 0, 8));
        this.labelLastSaveDate = new Label(Translation.getText("configuration.menu.label.no.last.date"));
        this.labelUnsavedModif = new Label();
        this.labelLastSaveDate.setStyle("-fx-text-fill: gray");
        this.labelUnsavedModif.setStyle("-fx-text-fill: gray");
        VBox.setMargin(this.labelLastSaveDate, new Insets(5, 0, 1, 10));
        VBox.setMargin(this.labelUnsavedModif, new Insets(0, 0, 10, 10));
        //Action line 1
        HBox boxButton1 = new HBox();
        VBox.setMargin(boxButton1, new Insets(10, 0, 0, 0));
        this.buttonSave = UIUtils.createFixedWidthTextButton(Translation.getText("configuration.menu.item.save"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.SAVE).sizeFactor(2).color(LCGraphicStyle.MAIN_PRIMARY), MainMenu.BUTTON_WIDTH,
                "tooltip.save.current.configuration");
        this.buttonExport = UIUtils.createFixedWidthTextButton(Translation.getText("configuration.menu.item.export"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.UPLOAD).sizeFactor(2).color(LCGraphicStyle.MAIN_DARK), MainMenu.BUTTON_WIDTH,
                "tooltip.export.current.configuration");
        this.buttonRemove = UIUtils.createFixedWidthTextButton(Translation.getText("configuration.menu.item.delete"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).sizeFactor(2).color(LCGraphicStyle.SECOND_DARK), MainMenu.BUTTON_WIDTH,
                "tooltip.remove.current.configuration");
        boxButton1.getChildren().addAll(this.buttonSave, this.buttonExport, this.buttonRemove);
        boxButton1.setAlignment(Pos.CENTER);
        //Total
        this.getChildren().addAll(this.labelPartTitle, this.labelConfigName, this.labelLastSaveDate, this.labelUnsavedModif, boxButton1);
    }

    @Override
    public void initListener() {
        this.buttonSave.setOnAction(LCConfigurationActions.HANDLER_SAVE);
        this.buttonExport.setOnAction(LCConfigurationActions.HANDLER_EXPORT);
        this.buttonRemove.setOnAction((ea) -> ConfigActionController.INSTANCE.executeAction(new RemoveConfigurationAction(buttonRemove, ProfileController.INSTANCE.currentProfileProperty().get(),
                AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get())));

    }

    @Override
    public void initBinding() {
        AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                this.labelConfigName.textProperty().unbind();
                this.labelLastSaveDate.textProperty().unbind();
            }
            if (nv != null) {
                this.labelConfigName.textProperty().bind(nv.configurationNameProperty());
                //Date change outside JavaFX Thread, so use listener
                nv.configurationLastDateProperty().addListener((obsd, ovd, nvd) -> {
                    Platform.runLater(() -> this.labelLastSaveDate.setText(nvd != null
                            ? Translation.getText("configuration.menu.label.last.date", StringUtils.dateToStringDateWithOnlyHoursMinuteSecond(nvd))
                            : Translation.getText("configuration.menu.label.no.last.date")));
                });
                Platform.runLater(() -> {
                    Date currentDate = nv.configurationLastDateProperty().get();
                    this.labelLastSaveDate.setText(currentDate != null
                            ? Translation.getText("configuration.menu.label.last.date",
                            StringUtils.dateToStringDateWithOnlyHoursMinuteSecond(currentDate))
                            : Translation.getText("configuration.menu.label.no.last.date"));
                });
            } else {
                this.labelConfigName.setText(Translation.getText("configuration.menu.no.name"));
                this.labelLastSaveDate.setText(Translation.getText("configuration.menu.label.no.last.date"));
            }
        });
        //Can't remove/export unsaved configuration
        this.buttonRemove.disableProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().isNull());
        this.buttonExport.disableProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().isNull());
        //Bind unsaved modifications
        AppModeController.INSTANCE.getEditModeContext().configurationProperty()
                .addListener((ChangeListener<LCConfigurationI>) (observableP, oldValueP, newValueP) -> {
                    //Init value
                    Platform.runLater(() -> {
                        this.labelUnsavedModif
                                .setText(Translation.getText("configuration.menu.label.unsaved.modif", newValueP.unsavedActionProperty().get()));
                    });
                    //Bind unsaved
                    newValueP.unsavedActionProperty().addListener((ChangeListener<Number>) (observableP1, oldValueP1, newValueP1) -> Platform
                            .runLater(() -> this.labelUnsavedModif.setText(Translation.getText("configuration.menu.label.unsaved.modif", newValueP1))));
                });
        //Visibility
        ConfigUIUtils.bindShowForLevelFrom(this.buttonExport, ConfigurationProfileLevelEnum.NORMAL);
    }
}
