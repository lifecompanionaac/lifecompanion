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

package org.lifecompanion.plugin.caaai.ui.keyoption;

import javafx.beans.value.ChangeListener;
import org.lifecompanion.plugin.caaai.model.keyoption.AiSuggestionKeyOption;
import org.lifecompanion.ui.configurationcomponent.editmode.keyoption.BaseKeyOptionConfigView;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.util.binding.EditActionUtils;
import org.lifecompanion.util.javafx.FXControlUtils;

public class SuggestedSentenceKeyOptionConfigView extends BaseKeyOptionConfigView<AiSuggestionKeyOption> {
    private ToggleSwitch toggleExample;
    private ChangeListener<Boolean> changeListenerExample;

    @Override
    public void initUI() {
        super.initUI();
        this.toggleExample = FXControlUtils.createToggleSwitch("caa.ai.plugin.todo", "caa.ai.plugin.todo");
        this.getChildren().add(toggleExample);
    }

    @Override
    public void initListener() {
        super.initListener();
        changeListenerExample = EditActionUtils.createSimpleBinding(this.toggleExample.selectedProperty(), this.model, m -> m.examplePropertyProperty().get(), ChangeExamplePropAction::new);
    }

    @Override
    public Class<AiSuggestionKeyOption> getConfiguredKeyOptionType() {
        return AiSuggestionKeyOption.class;
    }

    @Override
    public void bind(AiSuggestionKeyOption model) {
        this.toggleExample.setSelected(model.examplePropertyProperty().get());
        model.examplePropertyProperty().addListener(changeListenerExample);
    }

    @Override
    public void unbind(AiSuggestionKeyOption model) {
        model.examplePropertyProperty().removeListener(changeListenerExample);
    }
}
