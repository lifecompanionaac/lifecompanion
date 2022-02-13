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
package org.lifecompanion.ui.app.main.ribbon.available.withselection;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.editaction.ExpandCollapseActions;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

/**
 * Part to expand or collapse a selected grid part component.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ExpandCollapseRibbonPart extends RibbonBasePart<GridPartComponentI> implements LCViewInitHelper {
    private Button buttonETop, buttonCTop, buttonCBottom, buttonEBottom, buttonERight, buttonCRight, buttonELeft, buttonCLeft;

    private VBox totalVbox;
    private StackPane paneExpand2, paneCollapse2;

    public ExpandCollapseRibbonPart() {
        this.initAll();
    }

    @Override
    public void initUI() {
        //Total
        totalVbox = new VBox();
        totalVbox.setAlignment(Pos.CENTER);

        this.setContent(totalVbox);
        this.setTitle(Translation.getText("ribbon.expand.collapse.title"));


        this.buttonETop = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_UP).size(16).color(LCGraphicStyle.SECOND_PRIMARY), "tooltip.expand.top");
        this.buttonEBottom = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_DOWN).size(16).color(LCGraphicStyle.SECOND_PRIMARY), "tooltip.expand.bottom");
        this.buttonELeft = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_LEFT).size(16).color(LCGraphicStyle.SECOND_PRIMARY), "tooltip.expand.left");
        this.buttonERight = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(16).color(LCGraphicStyle.SECOND_PRIMARY), "tooltip.expand.right");

        StackPane.setAlignment(this.buttonEBottom, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(this.buttonETop, Pos.TOP_CENTER);
        StackPane.setAlignment(this.buttonELeft, Pos.CENTER_LEFT);
        StackPane.setAlignment(this.buttonERight, Pos.CENTER_RIGHT);

        paneExpand2 = new StackPane(buttonETop, buttonEBottom, buttonELeft, buttonERight);
        paneExpand2.setPrefSize(70.0, 60.0);
        paneExpand2.setMaxSize(70.0, 60.0);

        totalVbox.getChildren().addAll(paneExpand2, new Label(Translation.getText("button.expand.title")));


        this.buttonCTop = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_DOWN).size(16).color(LCGraphicStyle.SECOND_PRIMARY), "tooltip.collapse.top");
        this.buttonCBottom = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_UP).size(16).color(LCGraphicStyle.SECOND_PRIMARY), "tooltip.collapse.bottom");
        this.buttonCLeft = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(16).color(LCGraphicStyle.SECOND_PRIMARY), "tooltip.collapse.left");
        this.buttonCRight = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_LEFT).size(16).color(LCGraphicStyle.SECOND_PRIMARY), "tooltip.collapse.right");

        StackPane.setAlignment(this.buttonCBottom, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(this.buttonCTop, Pos.TOP_CENTER);
        StackPane.setAlignment(this.buttonCLeft, Pos.CENTER_LEFT);
        StackPane.setAlignment(this.buttonCRight, Pos.CENTER_RIGHT);

        paneCollapse2 = new StackPane(buttonCTop, buttonCBottom, buttonCLeft, buttonCRight);
        paneCollapse2.setPrefSize(65.0, 55.0);
        paneCollapse2.setMaxSize(65.0, 55.0);

        totalVbox.getChildren().addAll(paneCollapse2, new Label(Translation.getText("button.collapse.title")));
    }

    @Override
    public void initListener() {
        //Left
        this.buttonELeft.setOnAction((ea) -> ConfigActionController.INSTANCE
                .executeAction(new ExpandCollapseActions.ExpandLeftAction(this.model.get(), this.model.get().gridParentProperty().get().getGrid())));
        this.buttonCLeft.setOnAction((ea) -> ConfigActionController.INSTANCE.executeAction(
                new ExpandCollapseActions.CollapseLeftAction(this.model.get(), this.model.get().gridParentProperty().get().getGrid())));
        //Right
        this.buttonERight.setOnAction((ea) -> ConfigActionController.INSTANCE
                .executeAction(new ExpandCollapseActions.ExpandRightAction(this.model.get(), this.model.get().gridParentProperty().get().getGrid())));
        this.buttonCRight.setOnAction((ea) -> ConfigActionController.INSTANCE.executeAction(
                new ExpandCollapseActions.CollapseRightAction(this.model.get(), this.model.get().gridParentProperty().get().getGrid())));
        //Top
        this.buttonETop.setOnAction((ea) -> ConfigActionController.INSTANCE
                .executeAction(new ExpandCollapseActions.ExpandTopAction(this.model.get(), this.model.get().gridParentProperty().get().getGrid())));
        this.buttonCTop.setOnAction((ea) -> ConfigActionController.INSTANCE
                .executeAction(new ExpandCollapseActions.CollapseTopAction(this.model.get(), this.model.get().gridParentProperty().get().getGrid())));
        //Bottom
        this.buttonEBottom.setOnAction((ea) -> ConfigActionController.INSTANCE.executeAction(
                new ExpandCollapseActions.ExpandBottomAction(this.model.get(), this.model.get().gridParentProperty().get().getGrid())));
        this.buttonCBottom.setOnAction((ea) -> ConfigActionController.INSTANCE.executeAction(
                new ExpandCollapseActions.CollapseBottomAction(this.model.get(), this.model.get().gridParentProperty().get().getGrid())));
    }

    @Override
    public void initBinding() {
        this.model.bind(SelectionController.INSTANCE.selectedComponentProperty());
    }

    @Override
    public void bind(final GridPartComponentI modelP) {
        if (modelP.isParentExist()) {
            this.setDisable(false);
            this.buttonERight.disableProperty().bind(modelP.expandRightDisabledProperty());
            this.buttonELeft.disableProperty().bind(modelP.expandLeftDisabledProperty());
            this.buttonETop.disableProperty().bind(modelP.expandTopDisabledProperty());
            this.buttonEBottom.disableProperty().bind(modelP.expandBottomDisabledProperty());
            this.buttonCRight.disableProperty().bind(modelP.collapseRightDisabledProperty());
            this.buttonCLeft.disableProperty().bind(modelP.collapseLeftDisabledProperty());
            this.buttonCTop.disableProperty().bind(modelP.collapseTopDisabledProperty());
            this.buttonCBottom.disableProperty().bind(modelP.collapseBottomDisabledProperty());
        }else{
            this.setDisable(true);
        }
    }

    @Override
    public void unbind(final GridPartComponentI modelP) {
        this.buttonERight.disableProperty().unbind();
        this.buttonELeft.disableProperty().unbind();
        this.buttonETop.disableProperty().unbind();
        this.buttonEBottom.disableProperty().unbind();
        this.buttonCRight.disableProperty().unbind();
        this.buttonCLeft.disableProperty().unbind();
        this.buttonCTop.disableProperty().unbind();
        this.buttonCBottom.disableProperty().unbind();
    }
}
