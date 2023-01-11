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
package org.lifecompanion.model.impl.configurationcomponent.keyoption.dynamickey;

import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequenceItemI;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.PlaySoundAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SpeakTextAction;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserActionSequenceCurrentKeyOption extends AbstractSimplerKeyContentContainerKeyOption<UserActionSequenceItemI> {

    public UserActionSequenceCurrentKeyOption() {
        super();
        this.optionNameId = "key.option.user.action.sequence.current.key.option.name";
        this.optionDescriptionId = "key.option.user.action.sequence.current.key.option.description";
        this.iconName = "icon_type_user_action_sequence_current_item.png";
    }

    @Override
    protected String getDefaultTextContentProperty() {
        return AppModeController.INSTANCE.modeProperty().get() == AppMode.EDIT ? Translation.getText("key.option.user.action.current.key.default.text") : "";
    }

    public List<BaseUseActionI<UseActionTriggerComponentI>> getActionsToExecuteOnStart() {
        List<BaseUseActionI<UseActionTriggerComponentI>> actionToExecuteOnStart = new ArrayList<>();
        final UserActionSequenceItemI simplerKeyContentContainerV = currentSimplerKeyContentContainer.get();
        if (simplerKeyContentContainerV != null) {
            if (simplerKeyContentContainerV.enableSpeakProperty().get()) {
                SpeakTextAction speakTextAction = new SpeakTextAction();
                speakTextAction.textToSpeakProperty().set(simplerKeyContentContainerV.textToSpeakProperty().get());
                actionToExecuteOnStart.add(speakTextAction);
            }
            if (simplerKeyContentContainerV.enablePlayRecordedSoundProperty().get()) {
                PlaySoundAction playSoundAction = new PlaySoundAction();
                playSoundAction.getSoundResourceHolder().updateSound(simplerKeyContentContainerV.getSoundResourceHolder().filePathProperty().get(), simplerKeyContentContainerV.getSoundResourceHolder().durationInSecondProperty().get());
                playSoundAction.setMaxGainOnPlay(true);
                actionToExecuteOnStart.add(playSoundAction);
            }
        }
        return actionToExecuteOnStart;
    }
}
