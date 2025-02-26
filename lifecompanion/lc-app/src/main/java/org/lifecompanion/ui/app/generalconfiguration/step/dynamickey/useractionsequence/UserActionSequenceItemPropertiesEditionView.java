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

package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.useractionsequence;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequenceItemI;
import org.lifecompanion.ui.common.control.generic.DurationPickerControl;
import org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.AbstractSimplerKeyActionContainerPropertiesEditionView;
import org.lifecompanion.framework.commons.translation.Translation;


public class UserActionSequenceItemPropertiesEditionView extends AbstractSimplerKeyActionContainerPropertiesEditionView<UserActionSequenceItemI> {

    private ToggleSwitch toggleSubItem;
    private ToggleSwitch toggleAutomaticItem;
    private DurationPickerControl durationPickerAutomaticItemTimeMs;

    @Override
    protected int addFieldsAfterTextInGeneralPart(GridPane gridPaneConfiguration, int rowIndex, final int columnCount) {
        rowIndex = super.addFieldsAfterTextInGeneralPart(gridPaneConfiguration, rowIndex, columnCount);

        // Sub item
        Label labelSubItem = new Label(Translation.getText("sequence.configuration.item.sub.item"));
        labelSubItem.setMaxWidth(200.0);
        labelSubItem.setWrapText(true);
        Label labelSubItemToggle = new Label(Translation.getText("sequence.configuration.item.sub.item.toggle"));
        toggleSubItem = new ToggleSwitch();
        toggleSubItem.setFocusTraversable(false);
        GridPane.setHalignment(toggleSubItem, HPos.RIGHT);

//        gridPaneConfiguration.add(labelSubItem, 0, rowIndex);
//        gridPaneConfiguration.add(toggleSubItem, 1, rowIndex);
//        gridPaneConfiguration.add(labelSubItemToggle, 2, rowIndex++);

        // Spinner auto time
        durationPickerAutomaticItemTimeMs = new DurationPickerControl();
        durationPickerAutomaticItemTimeMs.setAlignment(Pos.CENTER_LEFT);
        Label labelAutomaticTime = new Label(Translation.getText("sequence.configuration.item.auto.time"));
        toggleAutomaticItem = new ToggleSwitch();
        GridPane.setHalignment(toggleAutomaticItem, HPos.RIGHT);
        gridPaneConfiguration.add(labelAutomaticTime, 0, rowIndex);
        gridPaneConfiguration.add(toggleAutomaticItem, 1, rowIndex);
        gridPaneConfiguration.add(durationPickerAutomaticItemTimeMs, 2, rowIndex++);

        this.setFitToHeight(true);

        return rowIndex;
    }

    @Override
    public void initBinding() {
        super.initBinding();
        this.durationPickerAutomaticItemTimeMs.disableProperty().bind(toggleAutomaticItem.selectedProperty().not());
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    @Override
    protected void bindBidirectionalContent(UserActionSequenceItemI ov, UserActionSequenceItemI nv) {
        super.bindBidirectionalContent(ov, nv);
        if (nv != null) {
            toggleSubItem.selectedProperty().bindBidirectional(nv.subItemProperty());
            toggleAutomaticItem.selectedProperty().bindBidirectional(nv.enableAutomaticItemProperty());
            durationPickerAutomaticItemTimeMs.durationProperty().bindBidirectional(nv.automaticItemTimeMsProperty());
            durationPickerAutomaticItemTimeMs.tryToPickBestUnit();
        }
    }

    @Override
    protected void unbindBidirectionalContent(UserActionSequenceItemI ov, UserActionSequenceItemI nv) {
        super.unbindBidirectionalContent(ov, nv);
        if (ov != null) {
            toggleSubItem.selectedProperty().unbindBidirectional(ov.subItemProperty());
            toggleAutomaticItem.selectedProperty().unbindBidirectional(ov.enableAutomaticItemProperty());
            durationPickerAutomaticItemTimeMs.durationProperty().unbindBidirectional(ov.automaticItemTimeMsProperty());
        }
    }

    @Override
    protected boolean enableWriteFields() {
        return false;
    }

    @Override
    protected boolean enableOverSpeakFields() {
        return false;
    }

    @Override
    protected String getTextToSpeakFieldLabelId() {
        return "sequence.configuration.view.field.text.to.speak";
    }


}
