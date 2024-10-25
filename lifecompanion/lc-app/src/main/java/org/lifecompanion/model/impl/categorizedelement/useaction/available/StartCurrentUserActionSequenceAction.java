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

package org.lifecompanion.model.impl.categorizedelement.useaction.available;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.configurationcomponent.dynamickey.UserActionSequenceController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.UserActionSequenceI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.WordPredictionKeyOption;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.dynamickey.UserActionSequenceListKeyOption;
import org.lifecompanion.model.impl.exception.LCException;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class StartCurrentUserActionSequenceAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

    public StartCurrentUserActionSequenceAction() {
        super(GridPartKeyComponentI.class);
        this.order = 100;
        this.category = DefaultUseActionSubCategories.UA_SEQUENCE_LIST;
        this.nameID = "start.current.user.action.sequence.name";
        this.staticDescriptionID = "start.current.user.action.sequence.description";
        this.configIconPath = "sequence/start_current_sequence.png";
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            if (parentKey.keyOptionProperty().get() instanceof UserActionSequenceListKeyOption userActionSequenceListKeyOption) {
                UserActionSequenceI userActionSequence = userActionSequenceListKeyOption.currentSimplerKeyContentContainerProperty().get();
                if (userActionSequence != null) {
                    UserActionSequenceController.INSTANCE.startSequence(userActionSequence);
                }
            }
        }
    }
}
