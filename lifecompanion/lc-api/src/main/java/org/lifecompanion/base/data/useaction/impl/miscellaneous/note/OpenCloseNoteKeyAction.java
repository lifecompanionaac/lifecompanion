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
package org.lifecompanion.base.data.useaction.impl.miscellaneous.note;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.control.events.WritingEventSource;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.image2.ImageElementI;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.component.keyoption.note.NoteKeyOption;
import org.lifecompanion.base.data.control.NoteKeyController;
import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.Map;

/**
 * Action associated with {@link NoteKeyOption} to save/load a note.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OpenCloseNoteKeyAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> wantedActivatedColor;

    private final IntegerProperty wantedStrokeSize;

    private final ObjectProperty<ImageElementI> currentImage;

    private final transient StringProperty savedText;

    private final transient BooleanProperty recordingNote;

    public OpenCloseNoteKeyAction() {
        super(GridPartKeyComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.NOTE;
        this.parameterizableAction = true;
        this.nameID = "action.save.note.name";
        this.staticDescriptionID = "action.save.note.description";
        this.configIconPath = "miscellaneous/icon_saveload_note.png";
        this.wantedActivatedColor = new SimpleObjectProperty<>(Color.RED);
        this.wantedStrokeSize = new SimpleIntegerProperty(3);
        currentImage = new SimpleObjectProperty<>();
        savedText = new SimpleStringProperty();
        recordingNote = new SimpleBooleanProperty(false);
        this.variableDescriptionProperty().set(getStaticDescription());
        this.savedText.addListener(i -> this.updateCurrentImage());
        this.recordingNote.addListener(i -> this.updateCurrentImage());
        this.updateCurrentImage();
    }

    public IntegerProperty wantedStrokeSizeProperty() {
        return this.wantedStrokeSize;
    }

    public ObjectProperty<Color> wantedActivatedColorProperty() {
        return this.wantedActivatedColor;
    }

    public ObjectProperty<ImageElementI> currentImageProperty() {
        return this.currentImage;
    }

    public boolean isRecording() {
        return this.recordingNote.get();
    }

    public StringProperty savedTextProperty() {
        return this.savedText;
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            if (parentKey.keyOptionProperty().get() instanceof NoteKeyOption) {
                NoteKeyController.INSTANCE.keyActivated((NoteKeyOption) parentKey.keyOptionProperty().get(), this);
            }
        }
    }

    public void disableRecording() {
        if (this.recordingNote.get()) {
            final String textToSerialize = this.getTextToSerialize();
            this.recordingNote.set(false);
            if (!StringUtils.isBlank(textToSerialize)) {
                WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);
            }
            GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
            if (parentKey != null) {
                parentKey.getKeyStyle().strokeColorProperty().forced().setValue(null);
                parentKey.getKeyStyle().strokeSizeProperty().forced().setValue(null);
            }
            Platform.runLater(() -> savedText.set(textToSerialize));
        }
    }

    public void enableRecording() {
        if (!this.recordingNote.get()) {
            this.recordingNote.set(true);
            if (!StringUtils.isBlank(savedText.get())) {
                WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);
                WritingStateController.INSTANCE.insertText(WritingEventSource.SYSTEM, savedText.get());
            }
            GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
            if (parentKey != null) {
                parentKey.getKeyStyle().strokeColorProperty().forced().setValue(this.wantedActivatedColor.get());
                parentKey.getKeyStyle().strokeSizeProperty().forced().setValue(this.wantedStrokeSize.get());
            }
        }
    }

    /**
     * Update key current image with the image that indicates the note state (recording, empty note, full note)
     */
    private void updateCurrentImage() {
        this.currentImage.set(NoteKeyController.INSTANCE.getImageForState(recordingNote.get(), savedText.get()));
    }

    /**
     * @return the text to serialize in use information (saved text or current typed text)
     */
    private String getTextToSerialize() {
        return this.recordingNote.get() ? WritingStateController.INSTANCE.currentTextProperty().get() : this.savedText.get();
    }

    // Class part : "IO"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(OpenCloseNoteKeyAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(OpenCloseNoteKeyAction.class, this, nodeP);
    }

    private static final String NODE_NOTE_CONTENT = "NoteModeContent";

    @Override
    public void serializeUseInformation(Map<String, Element> elements) {
        super.serializeUseInformation(elements);
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            Element elementNodeContent = new Element(NODE_NOTE_CONTENT);
            elementNodeContent.setText(getTextToSerialize());
            elements.put(parentKey.getID(), elementNodeContent);
        }
    }

    @Override
    public void deserializeUseInformation(Map<String, Element> elements) throws LCException {
        super.deserializeUseInformation(elements);
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            Element existingNote = elements.get(parentKey.getID());
            if (existingNote != null) {
                Platform.runLater(() -> this.savedText.set(existingNote.getText()));
            }
        }
    }
    //========================================================================

}
