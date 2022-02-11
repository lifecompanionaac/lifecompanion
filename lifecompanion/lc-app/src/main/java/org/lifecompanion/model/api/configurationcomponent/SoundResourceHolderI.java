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

package org.lifecompanion.model.api.configurationcomponent;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import org.jdom2.Element;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;

import java.io.File;

public interface SoundResourceHolderI extends XMLSerializable<IOContextI> {
    ReadOnlyStringProperty fileNameProperty();

    ReadOnlyIntegerProperty durationInSecondProperty();

    ReadOnlyObjectProperty<File> filePathProperty();

    void updateSound(File path, Integer durationInSecond);

    Element serializeIfNeeded(Element parent, IOContextI context);

    void deserializeIfNeeded(Element parent, IOContextI context, String retroCompatibilityResIdFieldName, String retroCompatibilityDurationFieldName) throws LCException;
}
