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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.plugin.calendar.model.CalendarLeisure;
import org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.AbstractSimplerKeyContentContainerPropertiesEditionView;
import org.lifecompanion.ui.common.control.generic.SoundRecordingControl;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;

import static org.lifecompanion.plugin.calendar.view.control.CalendarViewUtils.addSoundRecordingControlListener;

public class CalendarLeisurePropertiesView extends AbstractSimplerKeyContentContainerPropertiesEditionView<CalendarLeisure> {
    private TextField fieldLeisureSpeech;
    private Label labelLeisureSpeech, labelPlaySoundOnSelection;
    private ToggleSwitch toggleEnableLeisureSpeech, toggleEnablePlaySoundOnSelection;
    private SoundRecordingControl recordedSoundOnSelectionRecordingControl;

    @Override
    protected int addFieldsAfterTextInGeneralPart(GridPane gridPaneConfiguration, int rowIndex, int columnCount) {
        fieldLeisureSpeech = new TextField();
        labelLeisureSpeech = new Label(Translation.getText("calendar.plugin.field.leisure.speech.text"));
        GridPane.setHgrow(fieldLeisureSpeech, Priority.ALWAYS);
        toggleEnableLeisureSpeech = new ToggleSwitch();
        toggleEnableLeisureSpeech.setFocusTraversable(false);
        GridPane.setHalignment(toggleEnableLeisureSpeech, HPos.RIGHT);

        recordedSoundOnSelectionRecordingControl = new SoundRecordingControl();
        GridPane.setHalignment(recordedSoundOnSelectionRecordingControl, HPos.RIGHT);
        labelPlaySoundOnSelection = new Label(Translation.getText("calendar.plugin.field.recorded.sound.on.leisure.selection"));
        toggleEnablePlaySoundOnSelection = new ToggleSwitch();
        toggleEnablePlaySoundOnSelection.setFocusTraversable(false);

        gridPaneConfiguration.add(labelLeisureSpeech, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnableLeisureSpeech, 1, rowIndex);
        gridPaneConfiguration.add(fieldLeisureSpeech, 2, rowIndex++);

        gridPaneConfiguration.add(labelPlaySoundOnSelection, 0, rowIndex);
        gridPaneConfiguration.add(toggleEnablePlaySoundOnSelection, 1, rowIndex);
        gridPaneConfiguration.add(recordedSoundOnSelectionRecordingControl, 2, rowIndex++);

        return rowIndex;
    }

    @Override
    public void initBinding() {
        super.initBinding();
        this.fieldLeisureSpeech.disableProperty().bind(toggleEnableLeisureSpeech.selectedProperty().not());
        this.labelLeisureSpeech.disableProperty().bind(fieldLeisureSpeech.disabledProperty());

        this.recordedSoundOnSelectionRecordingControl.disableProperty().bind(toggleEnablePlaySoundOnSelection.selectedProperty().not());
        this.labelPlaySoundOnSelection.disableProperty().bind(recordedSoundOnSelectionRecordingControl.disabledProperty());
    }

    @Override
    public void initListener() {
        super.initListener();
        addSoundRecordingControlListener(selectedNode::get, toggleEnablePlaySoundOnSelection, recordedSoundOnSelectionRecordingControl, CalendarLeisure::getSoundOnSelectionResourceHolder);
    }

    @Override
    protected void bindBidirectionalContent(CalendarLeisure ov, CalendarLeisure nv) {
        if (nv != null) {
            fieldLeisureSpeech.textProperty().bindBidirectional(nv.leisureSpeechProperty());
            toggleEnableLeisureSpeech.selectedProperty().bindBidirectional(nv.enableLeisureSpeechProperty());

            toggleEnablePlaySoundOnSelection.selectedProperty().bindBidirectional(nv.enablePlayRecordedSoundPropertyOnSelectionProperty());
            recordedSoundOnSelectionRecordingControl.setFileAndDuration(nv.getSoundOnSelectionResourceHolder().filePathProperty().get(), nv.getSoundOnSelectionResourceHolder().durationInSecondProperty().get());
        }
    }

    @Override
    protected void unbindBidirectionalContent(CalendarLeisure ov, CalendarLeisure nv) {
        if (ov != null) {
            fieldLeisureSpeech.textProperty().unbindBidirectional(ov.leisureSpeechProperty());
            toggleEnableLeisureSpeech.selectedProperty().unbindBidirectional(ov.enableLeisureSpeechProperty());

            toggleEnablePlaySoundOnSelection.selectedProperty().unbindBidirectional(ov.enablePlayRecordedSoundPropertyOnSelectionProperty());
            if (nv == null) {
                recordedSoundOnSelectionRecordingControl.setFileAndDuration(null, -1);
            }
        }
    }


}
