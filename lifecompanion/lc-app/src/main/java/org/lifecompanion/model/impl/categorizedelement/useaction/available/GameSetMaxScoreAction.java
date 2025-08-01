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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.gaming.GamingFrameworkController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GameSetMaxScoreAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final IntegerProperty score;

    public GameSetMaxScoreAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.GAMING_FRAMEWORK;
        this.score = new SimpleIntegerProperty(10);
        this.nameID = "use.action.game.set.max.score.name";
        this.staticDescriptionID = "use.action.game.set.max.score.description";
        this.configIconPath = "miscellaneous/icon_game_set_max_score.png";
        this.parameterizableAction = true;
        this.variableDescriptionProperty().set(getStaticDescription());
        this.allowSystems = SystemType.allExpectMobile();
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GamingFrameworkController.INSTANCE.setMaxScore(this.score.get());
    }

    public IntegerProperty scoreProperty() {
        return this.score;
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        return XMLObjectSerializer.serializeInto(GameSetMaxScoreAction.class, this, super.serialize(contextP));
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(GameSetMaxScoreAction.class, this, nodeP);
    }
}
