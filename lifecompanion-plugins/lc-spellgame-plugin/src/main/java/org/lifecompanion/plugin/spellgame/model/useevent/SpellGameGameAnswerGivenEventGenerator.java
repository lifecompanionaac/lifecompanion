package org.lifecompanion.plugin.spellgame.model.useevent;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.plugin.spellgame.controller.SpellGameController;
import org.lifecompanion.plugin.spellgame.model.AnswerTypeEnum;
import org.lifecompanion.plugin.spellgame.model.SpellGameStepResult;

import java.util.List;
import java.util.function.Consumer;

public class SpellGameGameAnswerGivenEventGenerator extends BaseUseEventGeneratorImpl {

    private final Consumer<SpellGameStepResult> listener;

    private final UseVariableDefinitionI answerContentDefinition;

    @XMLGenericProperty(AnswerTypeEnum.class)
    private final ObjectProperty<AnswerTypeEnum> answerFilter;

    public SpellGameGameAnswerGivenEventGenerator() {
        super();
        this.parameterizableAction = true;
        this.order = 0;
        this.category = SpellGameEventSubCategories.GENERAL;
        this.nameID = "spellgame.plugin.event.answer.given.name";
        this.staticDescriptionID = "spellgame.plugin.event.answer.given.description";
        this.configIconPath = "filler_icon.png";
        this.answerFilter = new SimpleObjectProperty<>(AnswerTypeEnum.ALL);

        this.answerContentDefinition = new UseVariableDefinition("SpellGameAnswerContent", "spellgame.plugin.use.variable.event.answer.given.content.var.name",
                "spellgame.plugin.use.variable.event.answer.given.content.var.description", "spellgame.plugin.use.variable.event.answer.given.content.var.example");
        this.generatedVariables.add(this.answerContentDefinition);

        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("spellgame.plugin.event.answer.given.variable.description",
                Bindings.createStringBinding(() -> answerFilter.get() != null ? answerFilter.get().getName() : "", answerFilter)));

        listener = (answer) -> {
            if (AppModeController.INSTANCE.isUseMode()) {
                if (this.answerFilter.get().filter(answer)) {
                    this.useEventListener.fireEvent(this, List.of(new StringUseVariable(this.answerContentDefinition, answer.input())), null);
                }
            }
        };
    }

    public ObjectProperty<AnswerTypeEnum> answerFilterProperty() {
        return answerFilter;
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        SpellGameController.INSTANCE.addAnswerListener(listener);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        SpellGameController.INSTANCE.removeAnswerListener(listener);
    }

    @Override
    public Element serialize(final IOContextI context) {
        final Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(SpellGameGameAnswerGivenEventGenerator.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(SpellGameGameAnswerGivenEventGenerator.class, this, node);
    }
}

