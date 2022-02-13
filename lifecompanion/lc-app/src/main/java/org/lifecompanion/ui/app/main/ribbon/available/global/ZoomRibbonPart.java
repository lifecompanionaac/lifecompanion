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

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.impl.configurationcomponent.DisplayableComponentBaseImpl;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.text.DecimalFormat;

/**
 * Zoom level ribbon part.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ZoomRibbonPart extends RibbonBasePart<DisplayableComponentBaseImpl> implements LCViewInitHelper {
    private static final DecimalFormat DECIMAL_ZOOM_FORMAT = new DecimalFormat("#");

    /**
     * Button modify zoom level
     */
    private Button buttonResetZoom, buttonIncreaseZoom, buttonDecreaseZoom;

    /**
     * Label to display zoom level
     */
    private Label labelZoomLevel;

    public ZoomRibbonPart() {
        this.initAll();
    }

    @Override
    public void initUI() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5.0);
        gridPane.setVgap(5.0);
        gridPane.setAlignment(Pos.CENTER);
        this.buttonIncreaseZoom = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.SEARCH_PLUS).size(20).color(LCGraphicStyle.MAIN_DARK),
                "tooltip.buttons.zoom.increase");
        this.buttonDecreaseZoom = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.SEARCH_MINUS).size(20).color(LCGraphicStyle.SECOND_DARK),
                "tooltip.buttons.zoom.decrease");
        this.buttonResetZoom = FXControlUtils.createRightTextButton(Translation.getText("reset.zoom.level.label"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.UNDO).size(18).color(LCGraphicStyle.SECOND_DARK), "tooltip.buttons.zoom.reset");
        this.labelZoomLevel = new Label();
        gridPane.add(this.buttonDecreaseZoom, 0, 0);
        gridPane.add(this.buttonIncreaseZoom, 1, 0);
        gridPane.add(new Separator(Orientation.HORIZONTAL), 0, 1, 2, 1);
        gridPane.add(this.buttonResetZoom, 0, 2, 2, 1);
        gridPane.add(new Separator(Orientation.HORIZONTAL), 0, 3, 2, 1);
        gridPane.add(this.labelZoomLevel, 0, 4, 2, 1);
        GridPane.setHgrow(this.buttonDecreaseZoom, Priority.ALWAYS);
        GridPane.setHgrow(this.buttonIncreaseZoom, Priority.ALWAYS);
        this.labelZoomLevel.setMaxWidth(Double.MAX_VALUE);
        this.labelZoomLevel.setAlignment(Pos.CENTER);
        this.setTitle(Translation.getText("ribbon.part.zoom.level"));
        this.setContent(gridPane);
    }

    @Override
    public void initListener() {
        this.buttonDecreaseZoom.setOnAction(e -> AppModeController.INSTANCE.getEditModeContext().zoomOut());
        this.buttonIncreaseZoom.setOnAction(e -> AppModeController.INSTANCE.getEditModeContext().zoomIn());
        this.buttonResetZoom.setOnAction(e -> AppModeController.INSTANCE.getEditModeContext().resetZoom());
    }

    @Override
    public void initBinding() {
        this.labelZoomLevel.textProperty()
                .bind(Bindings.createStringBinding(
                        () -> ZoomRibbonPart.DECIMAL_ZOOM_FORMAT.format(AppModeController.INSTANCE.getEditModeContext().configurationScaleProperty().get() * 100.0) + " %",
                        AppModeController.INSTANCE.getEditModeContext().configurationScaleProperty()));
        this.disableProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationProperty().isNull());
    }

    // Class part : "Bind/unbind"
    //========================================================================
    @Override
    public void bind(final DisplayableComponentBaseImpl modelP) {
    }

    @Override
    public void unbind(final DisplayableComponentBaseImpl modelP) {
    }
    //========================================================================
}
