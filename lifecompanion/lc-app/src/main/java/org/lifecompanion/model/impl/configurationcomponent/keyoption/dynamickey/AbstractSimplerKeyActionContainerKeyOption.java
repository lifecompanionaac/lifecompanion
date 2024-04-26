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

import org.lifecompanion.model.api.configurationcomponent.dynamickey.SimplerKeyActionContainerI;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.PlaySoundAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SpeakTextAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.WriteTextAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractSimplerKeyActionContainerKeyOption<T extends SimplerKeyActionContainerI> extends AbstractSimplerKeyContentContainerKeyOption<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSimplerKeyActionContainerKeyOption.class);

    public AbstractSimplerKeyActionContainerKeyOption() {
        super();
        actionsOnActivation = new ArrayList<>();
        actionsOnOver = new ArrayList<>();
    }

    private final List<BaseUseActionI<?>> actionsOnActivation, actionsOnOver;

    // UPDATE KEY
    //========================================================================
    @Override
    protected List<BaseUseActionI<?>> getActionsToAddFor(UseActionEvent event) {
        final List<BaseUseActionI<?>> actionsToAddFor = super.getActionsToAddFor(event);
        final T simplerKeyContentContainerV = currentSimplerKeyContentContainer.get();
        if (simplerKeyContentContainerV != null) {
            if (event == UseActionEvent.ACTIVATION) {
                if (simplerKeyContentContainerV.enableWriteProperty().get()) {
                    WriteTextAction writeTextAction = new WriteTextAction();
                    writeTextAction.attachedToKeyOptionProperty().set(true);
                    writeTextAction.imageToWriteProperty().setValue(simplerKeyContentContainerV.imageVTwoProperty().get());
                    writeTextAction.sourceImageUseComponentProperty().setValue(simplerKeyContentContainerV);
                    writeTextAction.textToWriteProperty().set(simplerKeyContentContainerV.textToWriteProperty().get() + (simplerKeyContentContainerV.enableSpaceAfterWriteProperty().get() ? " " : ""));
                    actionsToAddFor.add(writeTextAction);
                    actionsOnActivation.add(writeTextAction);
                }
                if (simplerKeyContentContainerV.enableSpeakProperty().get()) {
                    SpeakTextAction speakTextAction = new SpeakTextAction();
                    speakTextAction.attachedToKeyOptionProperty().set(true);
                    speakTextAction.textToSpeakProperty().set(simplerKeyContentContainerV.textToSpeakProperty().get());
                    actionsToAddFor.add(speakTextAction);
                    actionsOnActivation.add(speakTextAction);
                }
                if (simplerKeyContentContainerV.enablePlayRecordedSoundProperty().get()) {
                    PlaySoundAction playSoundAction = new PlaySoundAction();
                    playSoundAction.setMaxGainOnPlay(true);
                    playSoundAction.attachedToKeyOptionProperty().set(true);
                    playSoundAction.getSoundResourceHolder().updateSound(simplerKeyContentContainerV.getSoundResourceHolder().filePathProperty().get(), simplerKeyContentContainerV.getSoundResourceHolder().durationInSecondProperty().get());
                    actionsToAddFor.add(playSoundAction);
                    actionsOnActivation.add(playSoundAction);
                }
            } else if (event == UseActionEvent.OVER) {
                if (simplerKeyContentContainerV.enableSpeakOnOverProperty().get()) {
                    SpeakTextAction speakTextActionForOver = new SpeakTextAction();
                    speakTextActionForOver.attachedToKeyOptionProperty().set(true);
                    speakTextActionForOver.textToSpeakProperty().set(simplerKeyContentContainerV.textSpeakOnOverProperty().get());
                    actionsToAddFor.add(speakTextActionForOver);
                    actionsOnOver.add(speakTextActionForOver);
                }
            }
        }
        return actionsToAddFor;
    }


    @Override
    protected List<BaseUseActionI<?>> getActionsToRemoveFor(UseActionEvent event) {
        final List<BaseUseActionI<?>> actionsToRemoveFor = super.getActionsToRemoveFor(event);
        if (event == UseActionEvent.ACTIVATION) {
            actionsToRemoveFor.addAll(actionsOnActivation);
            actionsOnActivation.clear();
        } else if (event == UseActionEvent.OVER) {
            actionsToRemoveFor.addAll(actionsOnOver);
            actionsOnOver.clear();
        }
        return actionsToRemoveFor;
    }
    //========================================================================
}
