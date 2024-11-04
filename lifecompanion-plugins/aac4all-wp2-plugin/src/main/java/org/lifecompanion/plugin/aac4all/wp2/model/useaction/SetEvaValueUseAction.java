/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.aac4all.wp2.model.useaction;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionManagerI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.aac4all.wp2.controller.AAC4AllWp2EvaluationController;

import java.util.Map;


public class SetEvaValueUseAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {


    @XMLGenericProperty(EvaCategoryType.class)
    private final ObjectProperty<EvaCategoryType> evaCategoryType;

    @XMLGenericProperty(EvaScoreType.class)
    private final ObjectProperty<EvaScoreType> evaScoreType;

    public SetEvaValueUseAction() {
        super(UseActionTriggerComponentI.class);
        this.category = AAC4AllWp2SubCategories.TODO;
        this.order = 1;
        //this.evaCategorie = new SimpleStringProperty("");
        //this.evaScore = new SimpleIntegerProperty();
        this.evaScoreType = new SimpleObjectProperty<>();
        this.evaCategoryType = new SimpleObjectProperty<>();

        this.nameID = "todo-eva";
        this.staticDescriptionID = "todo";
        this.configIconPath = "filler_icon_32px.png";
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("action.simple.set.eva.value.variable.description", this.evaCategoryType, this.evaScoreType));

    }

    public EvaCategoryType getEvaCategoryType() {
        return evaCategoryType.get();
    }

    public ObjectProperty<EvaCategoryType> evaCategoryTypeProperty() {
        return evaCategoryType;
    }

    public void setEvaCategoryType(EvaCategoryType evaCategoryType) {
        this.evaCategoryType.set(evaCategoryType);
    }

    public EvaScoreType getEvaScoreType() {
        return evaScoreType.get();
    }

    public ObjectProperty<EvaScoreType> evaScoreTypeProperty() {
        return evaScoreType;
    }

    public void setEvaScoreType(EvaScoreType evaScoreType) {
        this.evaScoreType.set(evaScoreType);
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        if (this.evaCategoryType.get() != null) {
            if (evaCategoryType.get() == EvaCategoryType.FATIGUE_POST) {
                if (evaScoreType.get() != null) {
                    updateColor();
                    AAC4AllWp2EvaluationController.INSTANCE.setEvaFatigueScore(evaScoreType.get().getScore());}

            } else if (evaCategoryType.get() == EvaCategoryType.SATISFACTION) {
                if (evaScoreType.get() != null) {
                updateColor();
                AAC4AllWp2EvaluationController.INSTANCE.setEvaSatisfactionScore(evaScoreType.get().getScore());}

            } else if (evaCategoryType.get()== EvaCategoryType.FATIGUE_INIT) {
                if (evaScoreType.get() != null) {
                updateColor();
                AAC4AllWp2EvaluationController.INSTANCE.setEvaFatigueInitScore(evaScoreType.get().getScore());}
            }
        }
    }

    private void updateColor() {
        emptyAllColors();
        setColorOn(parentComponentProperty().get(), Color.CYAN);
    }

    /*  même méthode que dans AAC4AllWp2EvaluationController   */
    public void emptyAllColors() {
        UseActionTriggerComponentI parentComp = parentComponentProperty().get();
        if (parentComp != null && parentComp.configurationParentProperty().get() != null) {
            LCConfigurationI configuration = parentComp.configurationParentProperty().get();
            configuration.getAllComponent().values().stream().filter(c -> c instanceof UseActionTriggerComponentI).forEach(c -> {
                UseActionManagerI actionManager = ((UseActionTriggerComponentI) c).getActionManager();
                SetEvaValueUseAction setEvaValueUseAction = actionManager.getFirstActionOfType(UseActionEvent.ACTIVATION, SetEvaValueUseAction.class);
                if (setEvaValueUseAction != null && setEvaValueUseAction.getEvaCategoryType() == this.getEvaCategoryType() && c != parentComp) {
                    setColorOn(c, null);
                }
            });
        }
    }

    private void setColorOn(Object comp, Color color) {
        if (comp instanceof GridPartKeyComponentI) {
            ((GridPartKeyComponentI) comp).getKeyStyle().backgroundColorProperty().forced().setValue(color);
        }
    }


    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(SetEvaValueUseAction.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(SetEvaValueUseAction.class, this, nodeP);
    }

}
