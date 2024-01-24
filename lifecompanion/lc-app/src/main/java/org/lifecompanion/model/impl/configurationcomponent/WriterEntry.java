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
package org.lifecompanion.model.impl.configurationcomponent;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.WriterEntryI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

/**
 * Base implementation for {@link WriterI} {@link WriterEntryI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WriterEntry implements WriterEntryI {
    private StringProperty entryText;
    private ObjectProperty<ImageElementI> image;
    private BooleanProperty disableAppend;
    private ObjectProperty<Color> fontColor;

    public WriterEntry() {
        this.entryText = new SimpleStringProperty(this, "entryText", "");
        this.image = new SimpleObjectProperty<>(this, "image", null);
        this.disableAppend = new SimpleBooleanProperty(this, "disableAppend", false);
        this.fontColor = new SimpleObjectProperty<>(this, "fontColor", null);
    }

    public WriterEntry(final String text, final boolean disableAppend) {
        this();
        this.entryText.set(text);
        this.disableAppend.set(disableAppend);
    }

    @Override
    public StringProperty entryTextProperty() {
        return this.entryText;
    }

    @Override
    public ObjectProperty<ImageElementI> imageProperty() {
        return this.image;
    }

    @Override
    public BooleanProperty disableInsertProperty() {
        return this.disableAppend;
    }

    @Override
    public void capitalize() {
        this.entryText.set(StringUtils.capitalize(this.entryText.get()));
    }

    @Override
    public void toUpperCase() {
        this.entryText.set(StringUtils.toUpperCase(this.entryText.get()));
    }

    @Override
    public boolean isValid() {
        return entryText.get() != null;
    }

    @Override
    public ObjectProperty<Color> fontColorProperty() {
        return fontColor;
    }

    @Override
    public String toString() {
        return "Entry[" + this.entryText.get() + "]";
    }

    private static final String ATB_IMAGE_ID = "imageId";
    private static final String NODE_WRITER_ENTRY = "WriterEntry";

    @Override
    public Element serialize(final Void context) {
        Element element = new Element(WriterEntry.NODE_WRITER_ENTRY);
        if (this.image.get() != null) {
            XMLUtils.write(this.image.get().getId(), WriterEntry.ATB_IMAGE_ID, element);
        } else {
            XMLUtils.write("" + null, WriterEntry.ATB_IMAGE_ID, element);
        }
        XMLObjectSerializer.serializeInto(WriterEntry.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final Void context) throws LCException {
        XMLObjectSerializer.deserializeInto(WriterEntry.class, this, node);
        String imageID = XMLUtils.readString(WriterEntry.ATB_IMAGE_ID, node);
        if (imageID != null) {
            this.image.set(ImageDictionaries.INSTANCE.getById(imageID));
        } else {
            this.image.set(null);
        }
    }

}
