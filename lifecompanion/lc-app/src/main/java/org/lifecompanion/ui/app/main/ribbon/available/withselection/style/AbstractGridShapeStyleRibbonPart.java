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

package org.lifecompanion.ui.app.main.ribbon.available.withselection.style;

import org.fxmisc.easybind.EasyBind;
import org.lifecompanion.model.api.style.GridStyleUserI;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.ui.common.pane.specific.styleedit.GridStyleEditView;
import org.lifecompanion.ui.common.pane.specific.styleedit.ShapeStyleEditView;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public abstract class AbstractGridShapeStyleRibbonPart extends RibbonBasePart<GridStyleUserI> implements LCViewInitHelper {

    private GridStyleEditView shapeStyleEditView;

    public AbstractGridShapeStyleRibbonPart() {
        this.initAll();
    }

    @Override
    public void initUI() {
        this.shapeStyleEditView = new GridStyleEditView();
        this.setContent(this.shapeStyleEditView);
    }

    @Override
    public void initListener() {
    }

    @Override
    public void initBinding() {
        EasyBind.subscribe(SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty(), (c) -> {
            if (c instanceof GridStyleUserI) {
                this.model.set((GridStyleUserI) c);
            } else {
                this.model.set(null);
            }
        });
    }

    @Override
    public void bind(final GridStyleUserI model) {
        this.shapeStyleEditView.modelProperty().set(model.getGridShapeStyle());
    }

    @Override
    public void unbind(final GridStyleUserI model) {
        this.shapeStyleEditView.modelProperty().set(null);
    }

    public static class SingleGridShapeStyleRibbonPart extends AbstractGridShapeStyleRibbonPart {
        @Override
        public void initUI() {
            super.initUI();
            this.setTitle(Translation.getText("style.ribbon.part.shape.style.single"));
        }
    }

    public static class PluralGridShapeStyleRibbonPart extends AbstractGridShapeStyleRibbonPart {
        @Override
        public void initUI() {
            super.initUI();
            this.setTitle(Translation.getText("style.ribbon.part.shape.style.plural"));
        }
    }
}
