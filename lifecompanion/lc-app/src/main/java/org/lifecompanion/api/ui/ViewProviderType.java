package org.lifecompanion.api.ui;

public enum ViewProviderType {
    CONFIG, USE;

    private final int cacheIndex;

    ViewProviderType() {
        this.cacheIndex = this.ordinal();
    }

    public int getCacheIndex() {
        return cacheIndex;
    }
}
