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
package org.lifecompanion.model.api.voicesynthesizer;

import javafx.beans.property.StringProperty;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;

/**
 * Represent a pronunciation exception in voice synthesizer (replace a text with another before calling the speak method)
 *
 * @author Mathieu THEBAUD
 */
public interface PronunciationExceptionI extends XMLSerializable<IOContextI> {

    /**
     * @return text present in the text to speak before replace
     */
    StringProperty originalTextProperty();

    /**
     * @return text that should be use instead of the {@link #originalTextProperty()}
     */
    StringProperty replaceTextProperty();

    /**
     * @return a cloned instance of this pronunciation exception.
     */
    PronunciationExceptionI clone();
}
