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
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.model.api.ui.editmode.ConfigOptionComponentI;

import java.util.Arrays;
import java.util.List;

/**
 * Option to be able to select the current grid in selected stack
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SelectDisplayedComponentButtonOption extends BaseOption<StackComponentI> implements LCViewInitHelper, ConfigOptionComponentI {
    private Button buttonSelect;

    public SelectDisplayedComponentButtonOption(final StackComponentI modelP) {
        super(modelP);
        this.initAll();
    }

    @Override
    public void initUI() {
        this.buttonSelect = new Button();
        ButtonComponentOption.applyButtonBaseStyle(this.buttonSelect, FontAwesome.Glyph.TH_LARGE);
    }

    @Override
    public void initListener() {
        this.buttonSelect.setOnAction(e -> {
            SelectionController.INSTANCE.setSelectedPart(this.model.displayedComponentProperty().get());
        });
    }

    @Override
    public List<Node> getOptions() {
        return List.of(this.buttonSelect);
    }

    @Override
    public Orientation getOrientation() {
        return Orientation.VERTICAL;
    }

    @Override
    public boolean hideOnUnselect() {
        return true;
    }
}
