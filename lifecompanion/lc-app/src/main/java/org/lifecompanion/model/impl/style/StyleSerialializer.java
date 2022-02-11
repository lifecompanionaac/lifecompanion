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

package org.lifecompanion.model.impl.style;

import org.jdom2.Element;
import org.lifecompanion.model.api.style.*;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;


public class StyleSerialializer {

    // Class part : "Serializer"
    //========================================================================
    public static void serializeGridStyle(final GridStyleUserI styleUser, final Element element, final IOContextI context) {
        Element serialize = styleUser.getGridShapeStyle().serialize(context);
        if (serialize.hasAttributes()) {
            element.addContent(serialize);
        }
    }

    public static void deserializeGridStyle(final GridStyleUserI styleUser, final Element element, final IOContextI context) throws LCException {
        Element styleNode = element.getChild(ShapeCompStyleI.NODE_SHAPE_STYLE_GRID);
        if (styleNode != null) {
            styleUser.getGridShapeStyle().deserialize(styleNode, context);
        }
    }

    public static void serializeKeyStyle(final KeyStyleUserI styleUser, final Element element, final IOContextI context) {
        Element serializeKeyStyle = styleUser.getKeyStyle().serialize(context);
        if (serializeKeyStyle.hasAttributes()) {
            element.addContent(serializeKeyStyle);
        }
        Element serializeText = styleUser.getKeyTextStyle().serialize(context);
        if (serializeText.hasAttributes()) {
            element.addContent(serializeText);
        }
    }

    public static void deserializeKeyStyle(final KeyStyleUserI styleUser, final Element element, final IOContextI context) throws LCException {
        Element keyStyleNode = element.getChild(KeyCompStyleI.NODE_KEY_STYLE);
        if (keyStyleNode != null) {
            styleUser.getKeyStyle().deserialize(keyStyleNode, context);
        }
        Element textStyleNode = element.getChild(TextCompStyleI.NODE_TEXT_STYLE_KEY);
        if (textStyleNode != null) {
            styleUser.getKeyTextStyle().deserialize(textStyleNode, context);
        }
    }

    public static void serializeTextDisplayerStyle(final TextDisplayerStyleUserI styleUser, final Element element, final IOContextI context) {
        Element serializeKeyStyle = styleUser.getTextDisplayerShapeStyle().serialize(context);
        if (serializeKeyStyle.hasAttributes()) {
            element.addContent(serializeKeyStyle);
        }
        Element serializeText = styleUser.getTextDisplayerTextStyle().serialize(context);
        if (serializeText.hasAttributes()) {
            element.addContent(serializeText);
        }
    }

    public static void deserializeTextDisplayerStyle(final TextDisplayerStyleUserI styleUser, final Element element, final IOContextI context)
            throws LCException {
        Element keyStyleNode = element.getChild(ShapeCompStyleI.NODE_SHAPE_STYLE_TEXT);
        if (keyStyleNode != null) {
            styleUser.getTextDisplayerShapeStyle().deserialize(keyStyleNode, context);
        }
        Element textStyleNode = element.getChild(TextCompStyleI.NODE_TEXT_STYLE_TEXT);
        if (textStyleNode != null) {
            styleUser.getTextDisplayerTextStyle().deserialize(textStyleNode, context);
        }
    }
    //========================================================================

}
