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

package org.lifecompanion.model.api.configurationcomponent.dynamickey;

import javafx.collections.ObservableList;
import org.lifecompanion.model.api.configurationcomponent.DuplicableComponentI;
import org.lifecompanion.model.api.configurationcomponent.IdentifiableComponentI;
import org.lifecompanion.model.api.configurationcomponent.TreeIdentifiableComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface UserActionSequenceI extends XMLSerializable<IOContextI>, DuplicableComponentI, IdentifiableComponentI, TreeIdentifiableComponentI, SimplerKeyActionContainerI {
    ObservableList<UserActionSequenceItemI> getItems();
    // TODO : action on finish ?
}
