package org.lifecompanion.model.api.useapi;

public enum GlobalRuntimeConfigurationType {
    COMMAND_LINE("-"),
    JAVA_PROPERTY("-D");

    private final String prefix;

    GlobalRuntimeConfigurationType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
