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

package org.lifecompanion.plugin.calendar.view.control;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.calendar.controller.SoundAlarmController;
import org.lifecompanion.util.javafx.FXControlUtils;

public class SoundAlarmSelectorControl extends HBox implements LCViewInitHelper {
    private ComboBox<SoundAlarmController.StandardAlarm> comboBoxSoundAlarm;
    private Button buttonPreview;
    private Glyph glyphPlay, glyphStop;

    public SoundAlarmSelectorControl() {
        initAll();
    }

    public ObjectProperty<SoundAlarmController.StandardAlarm> valueProperty() {
        return comboBoxSoundAlarm.valueProperty();
    }

    @Override
    public void initUI() {
        this.comboBoxSoundAlarm = new ComboBox<>();
        this.comboBoxSoundAlarm.setButtonCell(new StandardAlarmListCell());
        this.comboBoxSoundAlarm.setCellFactory(lv -> new StandardAlarmListCell());
        this.comboBoxSoundAlarm.setItems(FXCollections.observableArrayList(SoundAlarmController.StandardAlarm.values()));
        this.glyphPlay = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLAY).sizeFactor(1).color(LCGraphicStyle.MAIN_PRIMARY);
        this.glyphStop = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.STOP).sizeFactor(1).color(LCGraphicStyle.MAIN_PRIMARY);
        buttonPreview = FXControlUtils.createGraphicButton(glyphPlay, "tooltip.explain.play.selected.alarm");
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setSpacing(5.0);
        this.getChildren().addAll(comboBoxSoundAlarm, buttonPreview);
    }

    @Override
    public void initListener() {
        this.comboBoxSoundAlarm.setOnAction(e -> {
            reinitPlayStatus();
        });
        this.buttonPreview.setOnAction(e -> {
            if (comboBoxSoundAlarm.valueProperty().get() != null) {
                buttonPreview.setGraphic(SoundAlarmController.INSTANCE.togglePlayStop(comboBoxSoundAlarm.valueProperty().get(), this::reinitPlayStatus) ? glyphStop : glyphPlay);
            }
        });
    }

    private void reinitPlayStatus() {
        SoundAlarmController.INSTANCE.stopAllAlarm();
        buttonPreview.setGraphic(glyphPlay);
    }

    @Override
    public void initBinding() {
    }

    private static class StandardAlarmListCell extends ListCell<SoundAlarmController.StandardAlarm> {
        @Override
        protected void updateItem(final SoundAlarmController.StandardAlarm itemP, final boolean emptyP) {
            super.updateItem(itemP, emptyP);
            if (itemP == null || emptyP) {
                this.setText(null);
            } else {
                this.setText(Translation.getText(itemP.getNameId()));
            }
        }
    }
}
