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

package org.lifecompanion.ui.configurationcomponent.editmode;

import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.impl.configurationcomponent.StackComponent;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.configurationcomponent.base.StackViewBase;
import org.lifecompanion.ui.configurationcomponent.editmode.componentoption.*;
import org.lifecompanion.util.javafx.FXUtils;

/**
 * Node that display a {@link StackComponent}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class StackViewConfig extends StackViewBase {
    private RootComponentOption rootComponentOption;

    private ButtonComponentOption selectOption;

    public StackViewConfig() {
    }

    @Override
    public void initUI() {
        super.initUI();
        //Button option
        this.selectOption = new ButtonComponentOption(this.model, LCGraphicStyle.MAIN_DARK);
        MoveButtonOption<StackComponent> moveOption = new MoveButtonOption<>(this.model);
        StackButtonOption stackOption = new StackButtonOption(this.model);
        this.selectOption.addOption(stackOption);
        this.selectOption.addOption(moveOption);
        //Root component UI
        this.rootComponentOption = new RootComponentOption(this.model);
        this.rootComponentOption.bindSize(this);
        this.getChildren().add(this.rootComponentOption);
        this.rootComponentOption.getChildren().add(this.selectOption);
        FXUtils.applyPerformanceConfiguration(this);
    }

    @Override
    protected void displayedChanged(final GridPartComponentI oldValueP, final GridPartComponentI newValueP) {
        super.displayedChanged(oldValueP, newValueP);
        this.rootComponentOption.toFront();
    }

    @Override
    public void showToFront() {
        super.showToFront();
        this.rootComponentOption.toFront();
    }
}
