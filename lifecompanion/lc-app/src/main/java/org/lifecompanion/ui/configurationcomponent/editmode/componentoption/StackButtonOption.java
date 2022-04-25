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

import java.util.Arrays;
import java.util.List;

import org.controlsfx.glyphfont.FontAwesome;

import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.model.api.ui.editmode.ConfigOptionComponentI;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;

/**
 * Button on stack to be able to switch between displayed element.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class StackButtonOption extends BaseOption<StackComponentI> implements LCViewInitHelper, ConfigOptionComponentI {
    private Button buttonNextGrid, buttonPreviousGrid;

    public StackButtonOption(final StackComponentI modelP) {
        super(modelP);
        this.initAll();
    }

    @Override
    public List<Node> getOptions() {
        return Arrays.asList(this.buttonPreviousGrid, this.buttonNextGrid);
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
    }

    @SuppressWarnings("deprecation")
    @Override
    public void initListener() {
        this.buttonNextGrid.setOnAction((ea) -> {
            this.model.displayNext();
        });
        this.buttonPreviousGrid.setOnAction((ea) -> {
            this.model.displayPrevious();
        });
    }

    @Override
    public boolean hideOnUnselect() {
        return true;
    }
}
