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
package org.lifecompanion.model.impl.categorizedelement.useaction.available;

import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.SoundResourceHolderI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.impl.configurationcomponent.SoundResourceHolder;
import org.lifecompanion.controller.media.SoundPlayer;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;

import java.util.Map;

/**
 * Simple action to play sound
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PlayRecordedSoundAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final SoundResourceHolder soundResourceHolder;

    public PlayRecordedSoundAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.SOUND;
        this.nameID = "action.media.sound.play.recorded.sound.name";
        this.order = 0;
        this.staticDescriptionID = "action.media.sound.play.recorded.sound.static.description";
        this.configIconPath = "media/icon_record_sound.png";
        this.parameterizableAction = true;
        this.soundResourceHolder = new SoundResourceHolder();
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    public SoundResourceHolderI getSoundResourceHolder() {
        return soundResourceHolder;
    }

    // Class part : "Execute"
    //========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (this.soundResourceHolder.filePathProperty().get() != null) {
            SoundPlayer.INSTANCE.playSoundSync(this.soundResourceHolder.filePathProperty().get(), true);
        }
    }
    //========================================================================

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        return this.soundResourceHolder.serializeIfNeeded(super.serialize(contextP), contextP);
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        this.soundResourceHolder.deserializeIfNeeded(nodeP, contextP, "resourceId", "soundDurationInSecond");
    }
    //========================================================================
}
