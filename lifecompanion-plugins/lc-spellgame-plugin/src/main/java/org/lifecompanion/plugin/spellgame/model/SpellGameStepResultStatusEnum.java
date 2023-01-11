package org.lifecompanion.plugin.spellgame.model;

public enum SpellGameStepResultStatusEnum {
    UNDONE("text-secondary"),
    SUCCESS("text-success"),
    FAILED("text-danger");

    private final String cssClass;

    SpellGameStepResultStatusEnum(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssClass() {
        return cssClass;
    }
}
