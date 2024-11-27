package org.lifecompanion.model.impl.configurationcomponent.dynamickey;

public interface DynamicLocalFileNodeI {
    boolean isGeneratedChild();
    String getTargetPath();
    void setTargetPath(String path);
}
