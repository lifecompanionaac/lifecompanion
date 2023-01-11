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
package org.lifecompanion.model.impl.configurationcomponent.keyoption.note;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.style.TextPosition;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.OpenCloseNoteKeyAction;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

/**
 * Key option to save/load a user text in use mode.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class NoteKeyOption extends AbstractKeyOption {

    private OpenCloseNoteKeyAction saveLoadNoteAction;

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> wantedActivatedColor;

    @XMLGenericProperty(NoteKeyDisplayMode.class)
    private final ObjectProperty<NoteKeyDisplayMode> displayMode;

    private final StringProperty keyCustomText;

    private final IntegerProperty wantedStrokeSize;

    public NoteKeyOption() {
        super();
        this.optionNameId = "key.option.note.key.name";
        this.optionDescriptionId = "key.option.note.key.description";
        this.iconName = "icon_type_notekey.png";
        this.wantedActivatedColor = new SimpleObjectProperty<>(Color.RED);
        this.wantedStrokeSize = new SimpleIntegerProperty(3);
        keyCustomText = new SimpleStringProperty();
        this.displayMode = new SimpleObjectProperty<>(NoteKeyDisplayMode.CONTENT_TEXT);
        this.disableImage.set(true);
        this.disableTextContent.set(true);
    }

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
        this.saveLoadNoteAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, OpenCloseNoteKeyAction.class);
        if (this.saveLoadNoteAction == null) {
            this.saveLoadNoteAction = new OpenCloseNoteKeyAction();
            key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(this.saveLoadNoteAction);
        }
        this.saveLoadNoteAction.attachedToKeyOptionProperty().set(true);
        this.saveLoadNoteAction.wantedActivatedColorProperty().bind(wantedActivatedColor);
        this.saveLoadNoteAction.wantedStrokeSizeProperty().bind(wantedStrokeSize);
        key.imageVTwoProperty().bind(this.saveLoadNoteAction.currentImageProperty());
        if (key.getKeyStyle().textPositionProperty().value().getValue() != TextPosition.BOTTOM) {
            key.getKeyStyle().textPositionProperty().selected().setValue(TextPosition.BOTTOM);
        }
        key.textContentProperty().bind(Bindings.createStringBinding(
                () -> displayMode.get() == NoteKeyDisplayMode.CONTENT_TEXT ? saveLoadNoteAction.savedTextProperty().get() : this.keyCustomText.get(),
                displayMode, keyCustomText, this.saveLoadNoteAction.savedTextProperty()));
    }

    public OpenCloseNoteKeyAction getOpenCloseAction() {
        return saveLoadNoteAction;
    }

    public IntegerProperty wantedStrokeSizeProperty() {
        return this.wantedStrokeSize;
    }

    public ObjectProperty<Color> wantedActivatedColorProperty() {
        return this.wantedActivatedColor;
    }

    public StringProperty keyCustomTextProperty() {
        return this.keyCustomText;
    }

    public ObjectProperty<NoteKeyDisplayMode> displayModeProperty() {
        return this.displayMode;
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
        key.imageVTwoProperty().unbind();
        key.imageVTwoProperty().set(null);
        key.textContentProperty().unbind();
        key.textContentProperty().set(null);
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element elem = super.serialize(context);
        XMLObjectSerializer.serializeInto(NoteKeyOption.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(NoteKeyOption.class, this, node);
    }

}
