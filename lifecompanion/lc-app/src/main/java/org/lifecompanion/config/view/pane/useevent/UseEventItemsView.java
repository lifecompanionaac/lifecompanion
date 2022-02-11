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
package org.lifecompanion.config.view.pane.useevent;

import javafx.collections.ObservableList;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.config.view.pane.categorized.AbstractCategorizedItemsView;
import org.lifecompanion.config.view.pane.categorized.cell.AbstractCategorizedItemView;
import org.lifecompanion.config.view.pane.useevent.cell.UseEventItemView;

import java.util.function.Consumer;

public class UseEventItemsView extends AbstractCategorizedItemsView<UseEventGeneratorI> {

    public UseEventItemsView(final ObservableList<UseEventGeneratorI> itemsP, final Consumer<UseEventGeneratorI> selectionCallbackP) {
        super(itemsP, selectionCallbackP);
    }

    @Override
    protected AbstractCategorizedItemView<UseEventGeneratorI> createCell(UseEventGeneratorI element, final Consumer<UseEventGeneratorI> selectionCallback) {
        return new UseEventItemView(element, selectionCallback);
    }
}
