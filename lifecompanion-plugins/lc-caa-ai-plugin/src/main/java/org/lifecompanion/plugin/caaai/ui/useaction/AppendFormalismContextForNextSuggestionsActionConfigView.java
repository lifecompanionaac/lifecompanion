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

package org.lifecompanion.plugin.caaai.ui.useaction;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.plugin.caaai.model.FormalismAiContextValue;
import org.lifecompanion.plugin.caaai.model.useaction.AppendFormalismContextForNextSuggestionsAction;
import org.lifecompanion.plugin.caaai.ui.useaction.common.AppendContextForNextSuggestionsActionConfigView;

public class AppendFormalismContextForNextSuggestionsActionConfigView extends AppendContextForNextSuggestionsActionConfigView<FormalismAiContextValue, AppendFormalismContextForNextSuggestionsAction> {

    @Override
    public Class<AppendFormalismContextForNextSuggestionsAction> getConfiguredActionType() {
        return AppendFormalismContextForNextSuggestionsAction.class;
    }

    @Override
    protected String getComboBoxLabel() {
        return Translation.getText("caa.ai.plugin.actions.append_formalism_context_for_next_suggestions.config.context");
    }

    @Override
    protected FormalismAiContextValue[] getComboBoxValues() {
        return FormalismAiContextValue.values();
    }
}
