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

package org.lifecompanion.config.view.pane.general.view.simplercomp.keylist;

import javafx.scene.control.ListCell;
import org.lifecompanion.api.component.definition.simplercomp.KeyListNodeI;

public class SimpleKeyListContentListCell extends ListCell<KeyListNodeI> {
    private final KeyListCellHandler keyListCellHandler;

    public SimpleKeyListContentListCell() {
        keyListCellHandler = new KeyListCellHandler(this);
    }

    @Override
    protected void updateItem(final KeyListNodeI itemP, final boolean emptyP) {
        super.updateItem(itemP, emptyP);
        keyListCellHandler.cellUpdateItem(itemP, emptyP);
    }
}

