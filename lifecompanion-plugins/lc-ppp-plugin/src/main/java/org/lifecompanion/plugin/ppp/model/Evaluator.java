package org.lifecompanion.plugin.ppp.model;

import org.lifecompanion.framework.commons.translation.Translation;

public class Evaluator {
    private final EvaluatorType type;
    private final String name;
    private String localization;

    public Evaluator(EvaluatorType type, String name) {
        this.type = type;
        this.name = name;
    }

    public EvaluatorType getType() {
        return this.type;
    }

    public String getName() {
        if (this.name == null) {
            return this.buildNameWithLocalization(this.type.getText());
        }

        return Translation.getText("ppp.plugin.model.evaluators.name",
                this.buildNameWithLocalization(this.name), this.type.getText());
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    private String buildNameWithLocalization(String name) {
        if (this.localization == null) {
            return name;
        }

        return Translation.getText("ppp.plugin.model.evaluators.name_with_localization", name, this.localization);
    }
}
