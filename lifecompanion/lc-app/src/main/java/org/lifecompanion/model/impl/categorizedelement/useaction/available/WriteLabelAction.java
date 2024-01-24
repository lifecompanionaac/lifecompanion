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

package org.lifecompanion.model.impl.categorizedelement.useaction.available;

import java.util.Map;

import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.impl.configurationcomponent.WriterEntry;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;

/**
 * Action to write the label of the parent key.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WriteLabelAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

    public WriteLabelAction() {
        super(GridPartKeyComponentI.class);
        this.category = DefaultUseActionSubCategories.WRITE_TEXT;
        this.nameID = "action.write.label.name";
        this.order = 2;
        this.staticDescriptionID = "action.write.label.static.description";
        this.configIconPath = "text/icon_write_label.png";
        this.parameterizableAction = false;
        this.parentComponentProperty().addListener((obs, ov, nv) -> {
            this.variableDescriptionProperty().unbind();
            if (nv != null) {
                this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("action.write.label.variable.description", nv.textContentProperty()));
            }
        });
    }

    // Class part : "Execute"
    //========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        executeWriteLabelFor(this.parentComponentProperty().get());
    }

    static void executeWriteLabelFor(GridPartKeyComponentI key) {
        if (key != null) {
            String toWrite = key.textContentProperty().get();
            if (toWrite != null) {
                //If entry doesn't have any image and contains only one char, just append
                if (toWrite.length() == 1 && key.imageVTwoProperty().get() == null) {
                    WritingStateController.INSTANCE.insertText(WritingEventSource.USER_ACTIONS, toWrite);
                } else {
                    //Create entry and add image
                    WriterEntry entry = new WriterEntry(toWrite, true);
                    if (key.imageVTwoProperty().get() != null) {
                        entry.imageProperty().set(key.imageVTwoProperty().get());
                    }
                    WritingStateController.INSTANCE.insert(WritingEventSource.USER_ACTIONS, entry);
                }
            }
        }
    }
    //========================================================================
}
