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

package org.lifecompanion.config.view.useaction.impl.media.sound;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.PlayRecordedSoundAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SpeakTextAction;
import org.lifecompanion.config.view.reusable.SoundRecordingControl;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Action configuration view for {@link SpeakTextAction}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PlayRecordedSoundConfigView extends VBox implements UseActionConfigurationViewI<PlayRecordedSoundAction> {

    private SoundRecordingControl soundRecordingControl;

    public PlayRecordedSoundConfigView() {
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void initUI() {
        soundRecordingControl = new SoundRecordingControl();
        this.getChildren().addAll(new Label(Translation.getText("play.recorded.sound.action.sound.record.field"), soundRecordingControl));
    }

    @Override
    public void editStarts(final PlayRecordedSoundAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.soundRecordingControl.setFileAndDuration(action.getSoundResourceHolder().filePathProperty().get(), action.getSoundResourceHolder().durationInSecondProperty().get());
    }

    @Override
    public void editEnds(final PlayRecordedSoundAction action) {
        this.soundRecordingControl.dispose();
        action.getSoundResourceHolder().updateSound(this.soundRecordingControl.getFile(),soundRecordingControl.getSoundDurationInSecond());
    }

    @Override
    public void editCancelled(PlayRecordedSoundAction element) {
        this.soundRecordingControl.dispose();
    }

    @Override
    public Class<PlayRecordedSoundAction> getConfiguredActionType() {
        return PlayRecordedSoundAction.class;
    }

}
