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
package org.lifecompanion.plugin.ppp.events;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.ppp.events.categories.PPPEventSubCategories;
import org.lifecompanion.plugin.ppp.model.AssessmentRecord;
import org.lifecompanion.plugin.ppp.model.AssessmentType;
import org.lifecompanion.plugin.ppp.services.AssessmentService;

import java.util.function.Consumer;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OnAssessmentEndEventGenerator extends BaseUseEventGeneratorImpl {
    public static String formatAssessmentTypeCondition(AssessmentType assessmentType) {
        if (assessmentType == null) {
            return Translation.getText("ppp.plugin.events.assessments.end.fields.assessment_type.all");
        }

        return Translation.getText("ppp.plugin.events.assessments.end.fields.assessment_type.specific",
                assessmentType.getText());
    }

    @XMLGenericProperty(AssessmentType.class)
    private final ObjectProperty<AssessmentType> assessmentTypeCondition;

    private final Consumer<AssessmentRecord> assessmentEndCallback;

    public OnAssessmentEndEventGenerator() {
        super();
        this.parameterizableAction = true;
        this.order = 20;
        this.category = PPPEventSubCategories.ASSESSMENT;
        this.nameID = "ppp.plugin.events.assessments.end.name";
        this.staticDescriptionID = "ppp.plugin.events.assessments.end.description";
        this.assessmentTypeCondition = new SimpleObjectProperty<>();
        this.variableDescriptionProperty().bind(
                TranslationFX.getTextBinding("ppp.plugin.events.assessments.end.variable_description",
                        Bindings.createStringBinding(
                                () -> formatAssessmentTypeCondition(this.assessmentTypeCondition.get()),
                                this.assessmentTypeCondition)));

        this.assessmentEndCallback = (assessment) -> {
            if (this.assessmentTypeCondition.get() != null && this.assessmentTypeCondition.get() != assessment.getAssessmentType()) {
                return;
            }

            this.useEventListener.fireEvent(this, null, null);
        };
    }

    public ObjectProperty<AssessmentType> assessmentTypeConditionProperty() {
        return assessmentTypeCondition;
    }

    @Override
    public String getConfigIconPath() {
        return "events/icon_assessment_start.png";
    }

    // Class part : "Mode start/stop"
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        AssessmentService.INSTANCE.addAssessmentEndListener(this.assessmentEndCallback);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        AssessmentService.INSTANCE.removeAssessmentEndListener(this.assessmentEndCallback);
    }
    //========================================================================

    // Class part : "IO"
    //========================================================================

    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(OnAssessmentEndEventGenerator.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(OnAssessmentEndEventGenerator.class, this, nodeP);
    }

    //========================================================================
}
