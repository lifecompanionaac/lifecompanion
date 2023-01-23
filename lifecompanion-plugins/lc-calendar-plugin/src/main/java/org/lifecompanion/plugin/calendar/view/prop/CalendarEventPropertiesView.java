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

package org.lifecompanion.plugin.calendar.view.prop;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.plugin.calendar.model.CalendarEvent;
import org.lifecompanion.plugin.calendar.view.control.SoundAlarmSelectorControl;
import org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.AbstractSimplerKeyContentContainerPropertiesEditionView;
import org.lifecompanion.ui.common.control.generic.DurationPickerControl;
import org.lifecompanion.ui.common.control.generic.SoundRecordingControl;
import org.lifecompanion.ui.common.control.generic.TimePickerControl;
import org.lifecompanion.ui.common.control.specific.selector.UserActionSequenceSelectorControl;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.util.javafx.FXControlUtils;

import static org.lifecompanion.plugin.calendar.view.control.CalendarViewUtils.addSoundRecordingControlListener;
import static org.lifecompanion.plugin.calendar.view.control.CalendarViewUtils.disableIfSelected;

public class CalendarEventPropertiesView extends AbstractSimplerKeyContentContainerPropertiesEditionView<CalendarEvent> {
    private TextField fieldTextOnStart, fieldTextOnFinish, fieldTextOnAlarm;
    private ToggleSwitch toggleEnableTextOnStart, toggleEnableTextOnFinish, toggleEnableAutomaticItem, toggleEnableAtFixedTime, toggleEnableLinkToSequence, toggleEnableTextOnAlarm, toggleLeisureSelection, toggleEnableAutostartWhenPreviousFinished, toggleEnableSoundAlarm, toggleEnableRecordedSoundOnStart, toggleEnableRecordedSoundOnEnd;
    private Label labelTextOnStart, labelTextOnFinish, labelAutomaticItem, labelAtFixedTime, labelLinkedSequence, labelTextOnAlarm, labelLeisureSelection, labelToggleAutostartWhenPreviousFinished, labelSoundAlarm, labelRecordedSoundOnStart, labelRecordedSoundOnEnd;
    private UserActionSequenceSelectorControl fieldLinkedSequence;
    private TimePickerControl pickerFixedTime;
    private DurationPickerControl durationPickerAutomaticItemTimeMs;
    private SoundAlarmSelectorControl soundAlarmSelectorControl;
    private SoundRecordingControl recordedSoundOnStartRecordingControl, recordedSoundOnEndRecordingControl;


    @Override
    protected int addFieldsAfterTextInGeneralPart(GridPane gridPaneConfiguration, int rowIndex, int columnCount) {

        fieldTextOnStart = new TextField();
        labelTextOnStart = new Label(Translation.getText("calendar.plugin.field.text.on.generic"));
        GridPane.setHgrow(fieldTextOnStart, Priority.ALWAYS);
        toggleEnableTextOnStart = new ToggleSwitch();
        toggleEnableTextOnStart.setFocusTraversable(false);
        GridPane.setHalignment(toggleEnableTextOnStart, HPos.RIGHT);

        recordedSoundOnStartRecordingControl = new SoundRecordingControl();
        GridPane.setHalignment(recordedSoundOnStartRecordingControl, HPos.RIGHT);
        labelRecordedSoundOnStart = new Label(Translation.getText("calendar.plugin.field.recorded.sound.on.start"));
        toggleEnableRecordedSoundOnStart = new ToggleSwitch();
        toggleEnableRecordedSoundOnStart.setFocusTraversable(false);


        fieldTextOnFinish = new TextField();
        labelTextOnFinish = new Label(Translation.getText("calendar.plugin.field.text.on.generic"));
        GridPane.setHgrow(fieldTextOnFinish, Priority.ALWAYS);
        toggleEnableTextOnFinish = new ToggleSwitch();
        toggleEnableTextOnFinish.setFocusTraversable(false);
        GridPane.setHalignment(toggleEnableTextOnFinish, HPos.RIGHT);

        recordedSoundOnEndRecordingControl = new SoundRecordingControl();
        GridPane.setHalignment(recordedSoundOnEndRecordingControl, HPos.RIGHT);
        labelRecordedSoundOnEnd = new Label(Translation.getText("calendar.plugin.field.recorded.sound.on.end"));
        toggleEnableRecordedSoundOnEnd = new ToggleSwitch();
        toggleEnableRecordedSoundOnEnd.setFocusTraversable(false);

        fieldTextOnAlarm = new TextField();
        labelTextOnAlarm = new Label(Translation.getText("calendar.plugin.field.text.on.generic"));
        GridPane.setHgrow(fieldTextOnAlarm, Priority.ALWAYS);
        toggleEnableTextOnAlarm = new ToggleSwitch();
        toggleEnableTextOnAlarm.setFocusTraversable(false);
        GridPane.setHalignment(toggleEnableTextOnAlarm, HPos.RIGHT);

        this.durationPickerAutomaticItemTimeMs = new DurationPickerControl();
        this.durationPickerAutomaticItemTimeMs.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHalignment(durationPickerAutomaticItemTimeMs, HPos.RIGHT);
        durationPickerAutomaticItemTimeMs.setMaxWidth(Double.MAX_VALUE);
        labelAutomaticItem = new Label(Translation.getText("calendar.plugin.field.automatic.item"));
        toggleEnableAutomaticItem = new ToggleSwitch();
        GridPane.setHalignment(toggleEnableAutomaticItem, HPos.RIGHT);

        pickerFixedTime = new TimePickerControl();
        pickerFixedTime.setAlignment(Pos.CENTER_RIGHT);
        labelAtFixedTime = new Label(Translation.getText("calendar.plugin.field.at.fixed.time"));
        GridPane.setHgrow(pickerFixedTime, Priority.ALWAYS);
        toggleEnableAtFixedTime = new ToggleSwitch();
        toggleEnableAtFixedTime.setFocusTraversable(false);
        GridPane.setHalignment(toggleEnableAtFixedTime, HPos.RIGHT);

        soundAlarmSelectorControl = new SoundAlarmSelectorControl();
        labelSoundAlarm = new Label(Translation.getText("calendar.plugin.field.sound.alarm"));
        GridPane.setHgrow(soundAlarmSelectorControl, Priority.ALWAYS);
        toggleEnableSoundAlarm = new ToggleSwitch();
        toggleEnableSoundAlarm.setFocusTraversable(false);
        GridPane.setHalignment(toggleEnableSoundAlarm, HPos.RIGHT);

        fieldLinkedSequence = new UserActionSequenceSelectorControl(null);
        labelLinkedSequence = new Label(Translation.getText("calendar.plugin.field.link.sequence"));
        GridPane.setHgrow(fieldLinkedSequence, Priority.ALWAYS);
        toggleEnableLinkToSequence = new ToggleSwitch();
        toggleEnableLinkToSequence.setFocusTraversable(false);
        GridPane.setHalignment(toggleEnableLinkToSequence, HPos.RIGHT);

        labelLeisureSelection = new Label(Translation.getText("calendar.plugin.field.leisure.selection"));
        toggleLeisureSelection = new ToggleSwitch();
        toggleLeisureSelection.setFocusTraversable(false);

        labelToggleAutostartWhenPreviousFinished = new Label(Translation.getText("calendar.plugin.autostart.event.when.previous.finished"));
        toggleEnableAutostartWhenPreviousFinished = new ToggleSwitch();
        toggleEnableAutostartWhenPreviousFinished.setFocusTraversable(false);

        // General config part
        gridPaneConfiguration.add(labelAutomaticItem, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnableAutomaticItem, 1, rowIndex);
        gridPaneConfiguration.add(durationPickerAutomaticItemTimeMs, 2, rowIndex++);

        gridPaneConfiguration.add(labelToggleAutostartWhenPreviousFinished, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnableAutostartWhenPreviousFinished, 1, rowIndex++, columnCount - 1, 1);

        // Linked to external event part
        gridPaneConfiguration.add(FXControlUtils.createTitleLabel(Translation.getText("calendar.plugin.field.part.linked.external")), 0, rowIndex++, columnCount, 1);
        gridPaneConfiguration.add(labelLinkedSequence, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnableLinkToSequence, 1, rowIndex);
        gridPaneConfiguration.add(fieldLinkedSequence, 2, rowIndex++);

        gridPaneConfiguration.add(labelLeisureSelection, 0, rowIndex);
        gridPaneConfiguration.add(toggleLeisureSelection, 1, rowIndex++, columnCount - 1, 1);

        // At fixed time part
        gridPaneConfiguration.add(FXControlUtils.createTitleLabel(Translation.getText("calendar.plugin.field.part.at.fixed.time")), 0, rowIndex++, columnCount, 1);
        gridPaneConfiguration.add(labelAtFixedTime, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnableAtFixedTime, 1, rowIndex);
        gridPaneConfiguration.add(pickerFixedTime, 2, rowIndex++);
        gridPaneConfiguration.add(labelTextOnAlarm, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnableTextOnAlarm, 1, rowIndex);
        gridPaneConfiguration.add(fieldTextOnAlarm, 2, rowIndex++);
        gridPaneConfiguration.add(labelSoundAlarm, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnableSoundAlarm, 1, rowIndex);
        gridPaneConfiguration.add(soundAlarmSelectorControl, 2, rowIndex++);

        // On start part
        gridPaneConfiguration.add(FXControlUtils.createTitleLabel(Translation.getText("calendar.plugin.field.part.on.start")), 0, rowIndex++, columnCount, 1);
        gridPaneConfiguration.add(labelTextOnStart, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnableTextOnStart, 1, rowIndex);
        gridPaneConfiguration.add(fieldTextOnStart, 2, rowIndex++);

        gridPaneConfiguration.add(labelRecordedSoundOnStart, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnableRecordedSoundOnStart, 1, rowIndex);
        gridPaneConfiguration.add(recordedSoundOnStartRecordingControl, 2, rowIndex++);

        // On end part
        gridPaneConfiguration.add(FXControlUtils.createTitleLabel(Translation.getText("calendar.plugin.field.part.on.end")), 0, rowIndex++, columnCount, 1);
        gridPaneConfiguration.add(labelTextOnFinish, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnableTextOnFinish, 1, rowIndex);
        gridPaneConfiguration.add(fieldTextOnFinish, 2, rowIndex++);

        gridPaneConfiguration.add(labelRecordedSoundOnEnd, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnableRecordedSoundOnEnd, 1, rowIndex);
        gridPaneConfiguration.add(recordedSoundOnEndRecordingControl, 2, rowIndex++);

        return rowIndex;
    }

    @Override
    public void initBinding() {
        super.initBinding();

        durationPickerAutomaticItemTimeMs.disableProperty().bind(toggleEnableAutomaticItem.selectedProperty().not());
        labelAutomaticItem.disableProperty().bind(toggleEnableAutomaticItem.selectedProperty().not());

        labelToggleAutostartWhenPreviousFinished.disableProperty().bind(toggleEnableAutostartWhenPreviousFinished.selectedProperty().not());

        labelTextOnStart.disableProperty().bind(toggleEnableTextOnStart.selectedProperty().not());
        fieldTextOnStart.disableProperty().bind(toggleEnableTextOnStart.selectedProperty().not());

        labelTextOnFinish.disableProperty().bind(toggleEnableTextOnFinish.selectedProperty().not());
        fieldTextOnFinish.disableProperty().bind(toggleEnableTextOnFinish.selectedProperty().not());

        labelAtFixedTime.disableProperty().bind(toggleEnableAtFixedTime.selectedProperty().not());
        pickerFixedTime.disableProperty().bind(toggleEnableAtFixedTime.selectedProperty().not());
        fieldTextOnAlarm.disableProperty().bind(toggleEnableTextOnAlarm.selectedProperty().not().or(toggleEnableAtFixedTime.selectedProperty().not()));
        toggleEnableTextOnAlarm.disableProperty().bind(toggleEnableAtFixedTime.selectedProperty().not());
        labelTextOnAlarm.disableProperty().bind(fieldTextOnAlarm.disabledProperty());
        toggleEnableSoundAlarm.disableProperty().bind(toggleEnableAtFixedTime.selectedProperty().not());
        soundAlarmSelectorControl.disableProperty().bind(toggleEnableSoundAlarm.selectedProperty().not().or(toggleEnableAtFixedTime.selectedProperty().not()));
        labelSoundAlarm.disableProperty().bind(soundAlarmSelectorControl.disabledProperty());

        fieldLinkedSequence.disableProperty().bind(toggleEnableLinkToSequence.selectedProperty().not());
        labelLinkedSequence.disableProperty().bind(toggleEnableLinkToSequence.selectedProperty().not());

        labelLeisureSelection.disableProperty().bind(toggleLeisureSelection.selectedProperty().not());

        labelRecordedSoundOnStart.disableProperty().bind(recordedSoundOnStartRecordingControl.disabledProperty());
        recordedSoundOnStartRecordingControl.disableProperty().bind(toggleEnableRecordedSoundOnStart.selectedProperty().not());

        labelRecordedSoundOnEnd.disableProperty().bind(recordedSoundOnEndRecordingControl.disabledProperty());
        recordedSoundOnEndRecordingControl.disableProperty().bind(toggleEnableRecordedSoundOnEnd.selectedProperty().not());
    }


    @Override
    public void initListener() {
        super.initListener();
        this.toggleEnableAutostartWhenPreviousFinished.selectedProperty().addListener(disableIfSelected(toggleEnableAtFixedTime));
        this.toggleEnableAtFixedTime.selectedProperty().addListener(disableIfSelected(toggleEnableAutostartWhenPreviousFinished));
        this.toggleLeisureSelection.selectedProperty().addListener(disableIfSelected(toggleEnableLinkToSequence));
        this.toggleEnableLinkToSequence.selectedProperty().addListener(disableIfSelected(toggleLeisureSelection));
        this.toggleEnableLinkToSequence.selectedProperty().addListener(disableIfSelected(toggleEnableAutomaticItem));
        this.toggleEnableAutomaticItem.selectedProperty().addListener(disableIfSelected(toggleEnableLinkToSequence));
        addSoundRecordingControlListener(selectedNode::get, toggleEnableRecordedSoundOnStart, recordedSoundOnStartRecordingControl, CalendarEvent::getSoundOnStartResourceHolder);
        addSoundRecordingControlListener(selectedNode::get, toggleEnableRecordedSoundOnEnd, recordedSoundOnEndRecordingControl, CalendarEvent::getSoundOnEndResourceHolder);
    }


    @Override
    protected void bindBidirectionalContent(CalendarEvent ov, CalendarEvent nv) {
        if (nv != null) {
            // FIXME : doesn't include current edited sequences
            fieldLinkedSequence.setInputUserActionSequences(AppModeController.INSTANCE.getEditModeContext().getConfiguration().userActionSequencesProperty().get());

            toggleLeisureSelection.selectedProperty().bindBidirectional(nv.enableLeisureSelectionProperty());

            toggleEnableAutostartWhenPreviousFinished.selectedProperty().bindBidirectional(nv.enableAutostartWhenPreviousFinishedProperty());

            fieldTextOnStart.textProperty().bindBidirectional(nv.textOnStartProperty());
            toggleEnableTextOnStart.selectedProperty().bindBidirectional(nv.enableTextOnStartProperty());

            fieldTextOnFinish.textProperty().bindBidirectional(nv.textOnFinishProperty());
            toggleEnableTextOnFinish.selectedProperty().bindBidirectional(nv.enableTextOnFinishProperty());

            durationPickerAutomaticItemTimeMs.durationProperty().bindBidirectional(nv.automaticItemTimeMsProperty());
            durationPickerAutomaticItemTimeMs.tryToPickBestUnit();
            toggleEnableAutomaticItem.selectedProperty().bindBidirectional(nv.enableAutomaticItemProperty());

            toggleEnableAtFixedTime.selectedProperty().bindBidirectional(nv.enableAtFixedTimeProperty());
            pickerFixedTime.hourProperty().bindBidirectional(nv.getFixedTime().hoursProperty());
            pickerFixedTime.minuteProperty().bindBidirectional(nv.getFixedTime().minutesProperty());
            toggleEnableTextOnAlarm.selectedProperty().bindBidirectional(nv.enableTextOnAlarmProperty());
            fieldTextOnAlarm.textProperty().bindBidirectional(nv.textOnAlarmProperty());

            toggleEnableSoundAlarm.selectedProperty().bindBidirectional(nv.enableSoundOnAlarmProperty());
            soundAlarmSelectorControl.valueProperty().bindBidirectional(nv.soundOnAlarmProperty());

            toggleEnableLinkToSequence.selectedProperty().bindBidirectional(nv.enableLinkToSequenceProperty());
            fieldLinkedSequence.selectedSequenceId().bindBidirectional(nv.linkedSequenceIdProperty());

            // Sounds
            toggleEnableRecordedSoundOnStart.selectedProperty().bindBidirectional(nv.enablePlayRecordedSoundPropertyOnStartProperty());
            recordedSoundOnStartRecordingControl.setFileAndDuration(nv.getSoundOnStartResourceHolder().filePathProperty().get(), nv.getSoundOnStartResourceHolder().durationInSecondProperty().get());
            toggleEnableRecordedSoundOnEnd.selectedProperty().bindBidirectional(nv.enablePlayRecordedSoundPropertyOnEndProperty());
            recordedSoundOnEndRecordingControl.setFileAndDuration(nv.getSoundOnEndResourceHolder().filePathProperty().get(), nv.getSoundOnEndResourceHolder().durationInSecondProperty().get());
        }
    }

    @Override
    protected void unbindBidirectionalContent(CalendarEvent ov, CalendarEvent nv) {
        //FIXME : clean field
        if (ov != null) {
            fieldTextOnStart.textProperty().unbindBidirectional(ov.textOnStartProperty());
            toggleEnableTextOnStart.selectedProperty().unbindBidirectional(ov.enableTextOnStartProperty());

            fieldTextOnFinish.textProperty().unbindBidirectional(ov.textOnFinishProperty());
            toggleEnableTextOnFinish.selectedProperty().unbindBidirectional(ov.enableTextOnFinishProperty());

            durationPickerAutomaticItemTimeMs.durationProperty().unbindBidirectional(ov.automaticItemTimeMsProperty());
            toggleEnableAutomaticItem.selectedProperty().unbindBidirectional(ov.enableAutomaticItemProperty());

            toggleEnableAtFixedTime.selectedProperty().unbindBidirectional(ov.enableAtFixedTimeProperty());
            pickerFixedTime.hourProperty().unbindBidirectional(ov.getFixedTime().hoursProperty());
            pickerFixedTime.minuteProperty().unbindBidirectional(ov.getFixedTime().minutesProperty());
            toggleEnableTextOnAlarm.selectedProperty().unbindBidirectional(ov.enableTextOnAlarmProperty());
            fieldTextOnAlarm.textProperty().unbindBidirectional(ov.textOnAlarmProperty());

            toggleEnableLinkToSequence.selectedProperty().unbindBidirectional(ov.enableLinkToSequenceProperty());
            fieldLinkedSequence.selectedSequenceId().unbindBidirectional(ov.linkedSequenceIdProperty());

            toggleLeisureSelection.selectedProperty().unbindBidirectional(ov.enableLeisureSelectionProperty());

            toggleEnableSoundAlarm.selectedProperty().unbindBidirectional(ov.enableSoundOnAlarmProperty());
            soundAlarmSelectorControl.valueProperty().unbindBidirectional(ov.soundOnAlarmProperty());

            toggleEnableAutostartWhenPreviousFinished.selectedProperty().unbindBidirectional(ov.enableAutostartWhenPreviousFinishedProperty());

            fieldLinkedSequence.setInputUserActionSequences(null);

            // Sounds
            toggleEnableRecordedSoundOnStart.selectedProperty().unbindBidirectional(ov.enablePlayRecordedSoundPropertyOnStartProperty());
            toggleEnableRecordedSoundOnEnd.selectedProperty().unbindBidirectional(ov.enablePlayRecordedSoundPropertyOnEndProperty());
            if (nv == null) {
                recordedSoundOnStartRecordingControl.setFileAndDuration(null, -1);
                recordedSoundOnEndRecordingControl.setFileAndDuration(null, -1);
            }
        }
    }
}
