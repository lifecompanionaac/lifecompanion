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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser.ExtensionFilter;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.PlaySoundAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SpeakTextAction;
import org.lifecompanion.config.data.control.FileChooserType;
import org.lifecompanion.config.view.reusable.FileSelectorControl;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Arrays;

/**
 * Action configuration view for {@link SpeakTextAction}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PlaySoundConfigView extends VBox implements UseActionConfigurationViewI<PlaySoundAction> {
    private FileSelectorControl fileSelectorControl;

    public PlaySoundConfigView() {
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final PlaySoundAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.fileSelectorControl.valueProperty().set(action.getSoundResourceHolder().filePathProperty().get());
        this.fileSelectorControl.fileName().set(action.getSoundResourceHolder().fileNameProperty().get());
    }

    @Override
    public void editEnds(final PlaySoundAction action) {
        action.getSoundResourceHolder().updateSound(this.fileSelectorControl.valueProperty().get(),null);
    }

    @Override
    public Class<PlaySoundAction> getConfiguredActionType() {
        return PlaySoundAction.class;
    }

    @Override
    public void initUI() {
        this.fileSelectorControl = new FileSelectorControl(Translation.getText("play.sound.action.file.label"), FileChooserType.SELECT_SOUND);
        this.fileSelectorControl
                .setExtensionFilter(new ExtensionFilter(Translation.getText("file.type.sound"), Arrays.asList("*.wav", "*.mp3")));
        this.fileSelectorControl.setOpenDialogTitle(Translation.getText("play.sound.open.file.dialog.title"));
        this.getChildren().addAll(this.fileSelectorControl);
    }

}
