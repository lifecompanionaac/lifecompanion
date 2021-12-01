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
package org.lifecompanion.base.data.voice;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.voice.PronunciationExceptionI;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

/**
 * Implementation for pronunciation exception
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PronunciationException implements PronunciationExceptionI {
    private StringProperty originalText, replaceText;

    public PronunciationException() {
        this.originalText = new SimpleStringProperty(this, "originalText", null);
        this.replaceText = new SimpleStringProperty(this, "replaceText", null);
    }

    @Override
    public StringProperty originalTextProperty() {
        return this.originalText;
    }

    @Override
    public StringProperty replaceTextProperty() {
        return this.replaceText;
    }

    @Override
    public PronunciationExceptionI clone() {
        PronunciationException pe = new PronunciationException();
        pe.originalText.set(this.originalText.get());
        pe.replaceText.set(this.replaceText.get());
        return pe;
    }

    // Class part : "XML"
    //========================================================================
    private static final String NODE_PRONUNCIATION_EX = "PronunciationException";

    @Override
    public Element serialize(final IOContextI context) {
        Element element = new Element(PronunciationException.NODE_PRONUNCIATION_EX);
        XMLObjectSerializer.serializeInto(PronunciationException.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        XMLObjectSerializer.deserializeInto(PronunciationException.class, this, node);
    }
    //========================================================================

}
