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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import org.lifecompanion.model.api.configurationcomponent.SoundResourceHolderI;

/**
 * Represent a simpler component to use when key content should be dynamically updated and UI should be simpler to add it. <br>
 * It is for example used by keylist (simpler key list), sequences, calendar...<br>
 * It contains simpler key content to be bound to a key thank to key option.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
// TODO : UseActionTriggerComponentI to be able to add custom action on keys
public interface SimplerKeyActionContainerI extends SimplerKeyContentContainerI {

    // ACTIONS
    //========================================================================
    BooleanProperty enableWriteProperty();

    StringProperty textToWriteProperty();

    BooleanProperty enableSpaceAfterWriteProperty();

    BooleanProperty enableSpeakProperty();

    StringProperty textToSpeakProperty();

    BooleanProperty enableSpeakOnOverProperty();

    StringProperty textSpeakOnOverProperty();

    BooleanProperty enablePlayRecordedSoundProperty();

    SoundResourceHolderI getSoundResourceHolder();
    //========================================================================
}
