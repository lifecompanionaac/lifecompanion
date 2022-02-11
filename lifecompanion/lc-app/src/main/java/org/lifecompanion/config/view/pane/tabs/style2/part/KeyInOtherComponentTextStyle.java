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
package org.lifecompanion.config.view.pane.tabs.style2.part;

import org.fxmisc.easybind.EasyBind;
import org.lifecompanion.model.api.style.KeyStyleUserI;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.pane.tabs.style2.view.text.TextStyleEditView;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class KeyInOtherComponentTextStyle extends RibbonBasePart<KeyStyleUserI> implements LCViewInitHelper {

    private TextStyleEditView textStyleEditView;

    public KeyInOtherComponentTextStyle() {
        this.initAll();
    }

    @Override
    public void initUI() {
        this.textStyleEditView = new TextStyleEditView(true);
        this.setTitle(Translation.getText("style.ribbon.part.key.text.style.plural"));
        this.setContent(this.textStyleEditView);
    }

    @Override
    public void initListener() {
    }

    @Override
    public void initBinding() {
        EasyBind.subscribe(SelectionController.INSTANCE.selectedComponentBothProperty(), (c) -> {
            if (c instanceof KeyStyleUserI) {
                this.model.set((KeyStyleUserI) c);
            } else {
                this.model.set(null);
            }
        });
    }

    @Override
    public void bind(final KeyStyleUserI model) {
        this.textStyleEditView.modelProperty().set(model.getKeyTextStyle());
        // Issue #159 : because auto font now only reduce size, it's important to enable font size selection, even in auto font size mode
        //this.textStyleEditView.disableFontSizeRequestProperty().bind(model.getKeyStyle().autoFontSizeProperty().value());
    }

    @Override
    public void unbind(final KeyStyleUserI model) {
        this.textStyleEditView.modelProperty().set(null);
        //this.textStyleEditView.disableFontSizeRequestProperty().unbind();
    }
}
