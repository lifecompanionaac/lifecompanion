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
package org.lifecompanion.base.data.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.collections.ObservableMap;
import org.lifecompanion.api.component.definition.DisplayableComponentI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.image2.ImageElementI;
import org.lifecompanion.api.mode.ModeListenerI;
import org.lifecompanion.base.data.component.keyoption.note.NoteKeyOption;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.base.data.image2.StaticImageElement;
import org.lifecompanion.base.data.useaction.impl.miscellaneous.note.OpenCloseNoteKeyAction;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

/**
 * Manager for all {@link NoteKeyOption} to allow user to save written text in use mode.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum NoteKeyController implements ModeListenerI {
    INSTANCE;

    private List<NoteKeyOption> noteKeys;

    private NoteKeyController() {
        this.noteKeys = new ArrayList<>();
    }

    // Class part : "Internal"
    //========================================================================
    private void searchForNoteKeys(final LCConfigurationI configuration) {
        ObservableMap<String, DisplayableComponentI> allComponentMap = configuration.getAllComponent();
        Set<String> keys = allComponentMap.keySet();
        for (String id : keys) {
            DisplayableComponentI configComponent = allComponentMap.get(id);
            if (configComponent instanceof GridPartKeyComponentI) {
                GridPartKeyComponentI key = (GridPartKeyComponentI) configComponent;
                if (key.keyOptionProperty().get() instanceof NoteKeyOption) {
                    this.noteKeys.add((NoteKeyOption) key.keyOptionProperty().get());
                }
            }
        }
    }
    //========================================================================

    // Class part : "Public API"
    //========================================================================
    public void keyActivated(NoteKeyOption targetNoteKey, OpenCloseNoteKeyAction noteAction) {
        if (!noteAction.isRecording()) {
            // Disable all previous activated keys
            for (NoteKeyOption noteKey : noteKeys) {
                if (noteKey != targetNoteKey) {
                    noteKey.getOpenCloseAction().disableRecording();
                }
            }
            targetNoteKey.getOpenCloseAction().enableRecording();
        } else {
            targetNoteKey.getOpenCloseAction().disableRecording();
        }
    }

    /**
     * Cached image for different state
     */
    private ImageElementI recordingImage, emptyImage, fullImage;

    public synchronized ImageElementI getImageForState(boolean recording, String savedText) {
        if (recording) {
            return recordingImage = createAndLoadIfNull(recordingImage, "icon_editing_notekey.png");
        } else {
            if (StringUtils.isBlank(savedText)) {
                return emptyImage = createAndLoadIfNull(emptyImage, "icon_empty_notekey.png");
            } else {
                return fullImage = createAndLoadIfNull(fullImage, "icon_full_notekey.png");
            }
        }
    }

    private ImageElementI createAndLoadIfNull(ImageElementI current, String stateImageName) {
        if (current == null) {
            return new StaticImageElement(IconManager.get("note-key-state/" + stateImageName));
        } else {
            return current;
        }
    }
    //========================================================================

    // Class part : "Mode start/stop"
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.noteKeys.clear();
        this.searchForNoteKeys(configuration);
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.noteKeys.clear();
    }
    //========================================================================

}
