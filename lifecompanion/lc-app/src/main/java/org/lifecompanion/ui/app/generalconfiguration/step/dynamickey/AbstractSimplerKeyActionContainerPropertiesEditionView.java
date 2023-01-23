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

package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.SimplerKeyActionContainerI;
import org.lifecompanion.ui.common.control.generic.SoundRecordingControl;
import org.lifecompanion.framework.commons.translation.Translation;

public abstract class AbstractSimplerKeyActionContainerPropertiesEditionView<T extends SimplerKeyActionContainerI> extends AbstractSimplerKeyContentContainerPropertiesEditionView<T> {
    private TextField fieldTextToWrite, fieldTextToSpeak, fieldTextSpeakOver;
    private ToggleSwitch toggleSwitchEnableWrite, toggleSwitchEnableSpeak, toggleSwitchSpaceAfter, toggleSwitchSpeakOver, toggleEnablePlayRecordedSound;
    private Label labelTextToWrite, labelTextToSpeak, labelTextSpeakOver, labelSpaceAfter, labelPlayRecordedSound;
    private SoundRecordingControl soundRecordingControl;

    @Override
    protected int addFieldsAfterTextInGeneralPart(GridPane gridPaneConfiguration, int rowIndex, final int columnCount) {
        fieldTextToWrite = new TextField();
        fieldTextSpeakOver = new TextField();
        labelTextSpeakOver = new Label(Translation.getText("general.configuration.view.key.list.field.speak.over"));
        GridPane.setHgrow(fieldTextToWrite, Priority.ALWAYS);
        labelTextToWrite = new Label(Translation.getText("general.configuration.view.key.list.field.text.to.write"));
        toggleSwitchEnableWrite = new ToggleSwitch();
        toggleSwitchEnableWrite.setFocusTraversable(false);
        GridPane.setHalignment(toggleSwitchEnableWrite, HPos.RIGHT);
        labelTextToSpeak = new Label(Translation.getText(getTextToSpeakFieldLabelId()));
        fieldTextToSpeak = new TextField();
        GridPane.setHgrow(fieldTextToSpeak, Priority.ALWAYS);
        toggleSwitchEnableSpeak = new ToggleSwitch();
        toggleSwitchEnableSpeak.setFocusTraversable(false);
        GridPane.setHalignment(toggleSwitchEnableSpeak, HPos.RIGHT);
        toggleSwitchSpaceAfter = new ToggleSwitch();
        GridPane.setHalignment(toggleSwitchSpaceAfter, HPos.RIGHT);
        toggleSwitchSpaceAfter.setFocusTraversable(false);
        soundRecordingControl = new SoundRecordingControl();
        GridPane.setHalignment(soundRecordingControl, HPos.RIGHT);
        toggleEnablePlayRecordedSound = new ToggleSwitch();
        toggleEnablePlayRecordedSound.setFocusTraversable(false);
        toggleSwitchSpeakOver = new ToggleSwitch();
        GridPane.setHalignment(toggleSwitchSpeakOver, HPos.RIGHT);
        toggleSwitchSpeakOver.setFocusTraversable(false);

        labelSpaceAfter = new Label(Translation.getText("general.configuration.view.key.list.field.space.after"));
        if (enableWriteFields()) {
            gridPaneConfiguration.add(labelTextToWrite, 0, rowIndex);
            gridPaneConfiguration.add(toggleSwitchEnableWrite, 1, rowIndex);
            gridPaneConfiguration.add(fieldTextToWrite, 2, rowIndex++);
            gridPaneConfiguration.add(toggleSwitchSpaceAfter, 1, rowIndex);
            gridPaneConfiguration.add(labelSpaceAfter, 2, rowIndex++);
        }
        gridPaneConfiguration.add(labelTextToSpeak, 0, rowIndex);
        gridPaneConfiguration.add(toggleSwitchEnableSpeak, 1, rowIndex);
        gridPaneConfiguration.add(fieldTextToSpeak, 2, rowIndex++);
        labelPlayRecordedSound = new Label(Translation.getText("general.configuration.view.key.list.field.play.recorded.sound"));
        gridPaneConfiguration.add(labelPlayRecordedSound, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnablePlayRecordedSound, 1, rowIndex);
        gridPaneConfiguration.add(soundRecordingControl, 2, rowIndex++);

        if (enableOverSpeakFields()) {
            gridPaneConfiguration.add(labelTextSpeakOver, 0, rowIndex);
            gridPaneConfiguration.add(toggleSwitchSpeakOver, 1, rowIndex);
            gridPaneConfiguration.add(fieldTextSpeakOver, 2, rowIndex++);
        }

        return rowIndex;
    }

    @Override
    public void initListener() {
        super.initListener();
        this.toggleEnablePlayRecordedSound.selectedProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                soundRecordingControl.dispose();
            }
        });
        this.soundRecordingControl.setFileAndDurationChangeListener((file, duration) -> {
            final T selectedItem = this.selectedNode.get();
            if (selectedItem != null) {
                selectedItem.getSoundResourceHolder().updateSound(file, duration);
            }
        });
        fieldTextToWrite.setOnAction(actionEventTextFieldOnAction);
        fieldTextToSpeak.setOnAction(actionEventTextFieldOnAction);
        fieldTextSpeakOver.setOnAction(actionEventTextFieldOnAction);
    }

    @Override
    public void initBinding() {
        super.initBinding();
        labelSpaceAfter.disableProperty().bind(toggleSwitchSpaceAfter.disabledProperty());

        labelTextToWrite.disableProperty().bind(fieldTextToWrite.disabledProperty());
        fieldTextToWrite.disableProperty().bind(toggleSwitchEnableWrite.selectedProperty().not());

        labelTextToSpeak.disableProperty().bind(fieldTextToSpeak.disabledProperty());
        fieldTextToSpeak.disableProperty().bind(toggleSwitchEnableSpeak.selectedProperty().not());

        labelTextSpeakOver.disableProperty().bind(toggleSwitchSpeakOver.selectedProperty().not());
        fieldTextSpeakOver.disableProperty().bind(toggleSwitchSpeakOver.selectedProperty().not());

        labelPlayRecordedSound.disableProperty().bind(toggleEnablePlayRecordedSound.selectedProperty().not());
        soundRecordingControl.disableProperty().bind(toggleEnablePlayRecordedSound.selectedProperty().not());

        toggleSwitchSpaceAfter.disableProperty().bind(fieldTextToWrite.disabledProperty());
        bindTextFieldsValues(fieldText, fieldTextToWrite);
        bindTextFieldsValues(fieldTextToWrite, fieldTextToSpeak);
        bindTextFieldsValues(fieldText, fieldTextSpeakOver);
    }

    @Override
    protected void bindBidirectionalContent(T ov, T nv) {
        if (nv != null) {
            fieldTextToWrite.textProperty().bindBidirectional(nv.textToWriteProperty());
            fieldTextToSpeak.textProperty().bindBidirectional(nv.textToSpeakProperty());
            toggleSwitchEnableWrite.selectedProperty().bindBidirectional(nv.enableWriteProperty());
            toggleSwitchSpaceAfter.selectedProperty().bindBidirectional(nv.enableSpaceAfterWriteProperty());
            toggleSwitchEnableSpeak.selectedProperty().bindBidirectional(nv.enableSpeakProperty());
            fieldTextSpeakOver.textProperty().bindBidirectional(nv.textSpeakOnOverProperty());
            toggleSwitchSpeakOver.selectedProperty().bindBidirectional(nv.enableSpeakOnOverProperty());
            toggleEnablePlayRecordedSound.selectedProperty().bindBidirectional(nv.enablePlayRecordedSoundProperty());
            soundRecordingControl.setFileAndDuration(nv.getSoundResourceHolder().filePathProperty().get(), nv.getSoundResourceHolder().durationInSecondProperty().get());
            this.setVvalue(0.0);
        }
    }

    @Override
    protected void unbindBidirectionalContent(T ov, T nv) {
        if (ov != null) {
            fieldTextToWrite.textProperty().unbindBidirectional(ov.textToWriteProperty());
            fieldTextToSpeak.textProperty().unbindBidirectional(ov.textToSpeakProperty());
            fieldTextSpeakOver.textProperty().unbindBidirectional(ov.textSpeakOnOverProperty());
            toggleSwitchEnableWrite.selectedProperty().unbindBidirectional(ov.enableWriteProperty());
            toggleSwitchEnableSpeak.selectedProperty().unbindBidirectional(ov.enableSpeakProperty());
            toggleSwitchSpaceAfter.selectedProperty().unbindBidirectional(ov.enableSpaceAfterWriteProperty());
            toggleSwitchSpeakOver.selectedProperty().unbindBidirectional(ov.enableSpeakOnOverProperty());
            toggleEnablePlayRecordedSound.selectedProperty().unbindBidirectional(ov.enablePlayRecordedSoundProperty());
            if (nv == null) {
                soundRecordingControl.setFileAndDuration(null, -1);
            }
        }
    }

    protected abstract boolean enableWriteFields();

    protected abstract boolean enableOverSpeakFields();

    protected abstract String getTextToSpeakFieldLabelId();
}
