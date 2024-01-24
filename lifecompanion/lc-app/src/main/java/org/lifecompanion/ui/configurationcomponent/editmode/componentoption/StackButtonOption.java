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

package org.lifecompanion.ui.configurationcomponent.editmode.componentoption;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.lifecompanion.controller.editaction.GridStackActions;
import org.lifecompanion.controller.editmode.ComponentActionController;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.model.api.ui.editmode.ConfigOptionComponentI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.ui.editmode.AddComponents;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Button on stack to be able to switch between displayed element.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class StackButtonOption extends BaseOption<StackComponentI> implements LCViewInitHelper, ConfigOptionComponentI {
    private Button buttonNextGrid, buttonPreviousGrid, buttonAddGrid, buttonCopyCurrentGrid, buttonAddFromModel;

    public StackButtonOption(final StackComponentI modelP) {
        super(modelP);
        this.initAll();
    }

    @Override
    public List<Node> getOptions() {
        return Arrays.asList(this.buttonPreviousGrid, this.buttonNextGrid, this.buttonAddGrid, this.buttonCopyCurrentGrid, this.buttonAddFromModel);
    }

    @Override
    public Orientation getOrientation() {
        return Orientation.HORIZONTAL;
    }

    @Override
    public void initUI() {
        this.buttonPreviousGrid = new Button();
        ButtonComponentOption.applyButtonBaseStyle(this.buttonPreviousGrid, model instanceof GridPartComponentI ? LCGraphicStyle.THIRD_DARK : LCGraphicStyle.MAIN_DARK, FontAwesome.Glyph.CHEVRON_LEFT);
        this.buttonPreviousGrid.disableProperty().bind(this.model.previousPossibleProperty().not());
        this.buttonNextGrid = new Button();
        this.buttonNextGrid.disableProperty().bind(this.model.nextPossibleProperty().not());
        ButtonComponentOption.applyButtonBaseStyle(this.buttonNextGrid, model instanceof GridPartComponentI ? LCGraphicStyle.THIRD_DARK : LCGraphicStyle.MAIN_DARK, FontAwesome.Glyph.CHEVRON_RIGHT);

        this.buttonAddGrid = new Button();
        ButtonComponentOption.applyButtonBaseStyle(this.buttonAddGrid, model instanceof GridPartComponentI ? LCGraphicStyle.THIRD_DARK : LCGraphicStyle.MAIN_DARK, FontAwesome.Glyph.TH_LARGE);
        this.buttonAddGrid.setTooltip(FXControlUtils.createTooltip(Translation.getText("stack.button.tooltip.add.grid")));
        this.buttonCopyCurrentGrid = new Button();
        ButtonComponentOption.applyButtonBaseStyle(this.buttonCopyCurrentGrid, model instanceof GridPartComponentI ? LCGraphicStyle.THIRD_DARK : LCGraphicStyle.MAIN_DARK, FontAwesome.Glyph.COPY);
        this.buttonCopyCurrentGrid.setTooltip(FXControlUtils.createTooltip(Translation.getText("stack.button.tooltip.duplicate.grid")));

        this.buttonAddFromModel = new Button();
        ButtonComponentOption.applyButtonBaseStyle(this.buttonAddFromModel, model instanceof GridPartComponentI ? LCGraphicStyle.THIRD_DARK : LCGraphicStyle.MAIN_DARK, FontAwesome.Glyph.USER_PLUS);
        this.buttonAddFromModel.setTooltip(FXControlUtils.createTooltip(Translation.getText("stack.button.tooltip.add.from.model")));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void initListener() {
        this.buttonNextGrid.setOnAction(ea -> this.model.displayNextForEditMode());
        this.buttonPreviousGrid.setOnAction(ea -> this.model.displayPreviousForEditMode());
        this.buttonAddGrid.setOnAction(ea -> ConfigActionController.INSTANCE.executeAction(new GridStackActions.AddGridInStackAction(model, true, true)));
        this.buttonCopyCurrentGrid.setOnAction(ea ->
                ConfigActionController.INSTANCE.executeAction(new GridStackActions.AddGridInStackAction(model,
                        ComponentActionController.createComponentCopy(model.displayedComponentProperty().get(), true),
                        true,
                        true)));
        this.buttonAddFromModel.setOnAction(ea -> ConfigActionController.INSTANCE.executeAction(new AddComponents.AddUserModelGridInStack().createAddAction()));
    }

    @Override
    public boolean hideOnUnselect() {
        return true;
    }
}
