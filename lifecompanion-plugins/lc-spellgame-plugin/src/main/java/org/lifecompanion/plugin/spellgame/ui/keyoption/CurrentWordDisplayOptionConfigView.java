/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.spellgame.ui.keyoption;

import javafx.beans.value.ChangeListener;
import org.lifecompanion.plugin.spellgame.model.keyoption.CurrentWordDisplayKeyOption;
import org.lifecompanion.ui.configurationcomponent.editmode.keyoption.BaseKeyOptionConfigView;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.util.binding.EditActionUtils;
import org.lifecompanion.util.javafx.FXControlUtils;

public class CurrentWordDisplayOptionConfigView extends BaseKeyOptionConfigView<CurrentWordDisplayKeyOption> {
    private ToggleSwitch toggleShowVisualFeedbackOnAnswer;
    private ChangeListener<Boolean> changeListenerShowVisualFeedbackOnAnswer;

    @Override
    public void initUI() {
        super.initUI();
        this.toggleShowVisualFeedbackOnAnswer = FXControlUtils.createToggleSwitch("spellgame.plugin.config.view.field.show.visual.feedback.key",
                "spellgame.plugin.config.view.field.show.visual.feedback.key.tooltip");
        this.getChildren().add(toggleShowVisualFeedbackOnAnswer);
    }

    @Override
    public void initListener() {
        super.initListener();
        changeListenerShowVisualFeedbackOnAnswer = EditActionUtils.createSimpleBinding(
                this.toggleShowVisualFeedbackOnAnswer.selectedProperty(),
                this.model,
                m -> m.showVisualFeedbackOnAnswerProperty().get(),
                ChangeCurrentWordDisplayShowVisualFeedbackAction::new
        );
    }

    @Override
    public Class<CurrentWordDisplayKeyOption> getConfiguredKeyOptionType() {
        return CurrentWordDisplayKeyOption.class;
    }

    @Override
    public void bind(CurrentWordDisplayKeyOption model) {
        this.toggleShowVisualFeedbackOnAnswer.setSelected(model.showVisualFeedbackOnAnswerProperty().get());
        model.showVisualFeedbackOnAnswerProperty().addListener(changeListenerShowVisualFeedbackOnAnswer);
    }

    @Override
    public void unbind(CurrentWordDisplayKeyOption model) {
        model.showVisualFeedbackOnAnswerProperty().removeListener(changeListenerShowVisualFeedbackOnAnswer);
    }
}
