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

import org.lifecompanion.model.impl.configurationcomponent.TextEditorComponent;
import org.lifecompanion.ui.configurationcomponent.base.TextEditorViewBase;
import org.lifecompanion.ui.configurationcomponent.editmode.componentoption.ButtonComponentOption;
import org.lifecompanion.ui.configurationcomponent.editmode.componentoption.MoveButtonOption;
import org.lifecompanion.ui.configurationcomponent.editmode.componentoption.RootComponentOption;

public class TextEditorViewConfig extends TextEditorViewBase {
    private RootComponentOption rootComponentOption;

    public TextEditorViewConfig() {
    }

    @Override
    public void initUI() {
        super.initUI();
        //Button option
        ButtonComponentOption selectOption = new ButtonComponentOption(this.model);
        MoveButtonOption<TextEditorComponent> moveOption = new MoveButtonOption<>(this.model);
        selectOption.addOption(moveOption);
        //Root component UI
        this.rootComponentOption = new RootComponentOption(this.model);
        this.rootComponentOption.bindSize(this);
        this.getChildren().add(this.rootComponentOption);
        this.rootComponentOption.getChildren().add(selectOption);
        //UIUtils.applyPerformanceConfiguration(this);
    }

    @Override
    public void showToFront() {
        super.showToFront();
        this.rootComponentOption.toFront();
    }

    @Override
    public void updateCaretScroll(final double yPercent) {
        //In config mode, we don't any automatic scroll on canvas update
    }
}
