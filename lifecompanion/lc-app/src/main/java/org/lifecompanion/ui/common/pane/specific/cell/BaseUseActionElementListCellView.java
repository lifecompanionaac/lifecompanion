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
package org.lifecompanion.ui.common.pane.specific.cell;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.ui.common.pane.specific.cell.AbstractCategorizedElementListCellView;

import java.util.function.BiConsumer;

/**
 * List cell view to display use action selected on component.<br>
 * A double clic on the cell fire the edit.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class BaseUseActionElementListCellView extends AbstractCategorizedElementListCellView<BaseUseActionI<?>> {

    public BaseUseActionElementListCellView(ListView<BaseUseActionI<?>> listView, final BiConsumer<Node, BaseUseActionI<?>> actionSelectedCallbackP) {
        super(listView, actionSelectedCallbackP);
    }
}
