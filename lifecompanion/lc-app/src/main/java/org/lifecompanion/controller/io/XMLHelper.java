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

package org.lifecompanion.controller.io;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.lifecompanion.model.api.io.XMLSerializable;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class XMLHelper {
    private static final Format FORMAT = Format
            //            .getPrettyFormat()
            .getCompactFormat()
            .setEncoding(StandardCharsets.UTF_8.name());

    public static void writeXml(File path, Element element) throws IOException {
        XMLOutputter xmlOutputter = new XMLOutputter(FORMAT);
        try (OutputStream os = new FileOutputStream(path)) {
            xmlOutputter.output(element, os);// Internally buffered
        }
    }

    public static Element readXml(File path) throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        try (BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            Document doc = saxBuilder.build(is);
            return doc.getRootElement();
        }
    }

    public static <T, K extends XMLSerializable<T>> K loadXMLSerializable(final File path, final K model, final T context) throws Exception {
        model.deserialize(readXml(path), context);
        return model;
    }

    public static <T> void saveXMLSerializable(final File path, final XMLSerializable<T> model, final T context) throws Exception {
        writeXml(path, model.serialize(context));
    }
}
