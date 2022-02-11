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
package org.lifecompanion.model.api.io;

import org.jdom2.Element;
import org.lifecompanion.model.impl.exception.LCException;

/**
 * Represent a object that can be serialized with JDOM.
 *
 * @param <T> the type of the context object given when the serialization is done
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface XMLSerializable<T> {
    /**
     * Must return a XML element that represent this object in XML<br>
     * This step must be totally independent of the parent loading.<br>
     * <strong>Subclass must take care of calling the {@link #serialize(Object)} method of the parent</strong>
     *
     * @param context the context where the object is serialized
     * @return the element that represent this object as XML
     */
    Element serialize(T context);

    /**
     * Must load all the properties found in the given node into this object.<br>
     * This step must be totally independent of the parent loading.<br>
     * <strong>Subclass must take care of calling the {@link #deserialize(Element, Object)} method of the parent</strong>
     *
     * @param node    the node where the potential element can be found
     * @param context the loading context
     */
    void deserialize(Element node, T context) throws LCException;
}
