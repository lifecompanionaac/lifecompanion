package org.lifecompanion.plugin.aac4all.wp2.model.logs;

public enum KeyboardType {
    REOLOC_L("Clavier RéoLocLigne", "RéoLocL"),
    REOLOC_G("Clavier RéoLocGlobale", "RéoLocG"),
    STATIC("Clavier Statique", "Statique"),
    CUR_STA("Clavier CurSta", "TODO CurSta"),
    DY_LIN("Clavier DyLin", "TODO DyLin");

    private final String gridName;
    private final String translationId;

    KeyboardType(String gridName, String translationId) {
        this.gridName = gridName;
        this.translationId = translationId;
    }

    public String getGridName() {
        return gridName;
    }

    public String getTranslationId() {
        return translationId;
    }
}
