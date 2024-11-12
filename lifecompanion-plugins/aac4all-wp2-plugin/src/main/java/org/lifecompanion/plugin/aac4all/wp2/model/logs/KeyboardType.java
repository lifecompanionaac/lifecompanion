package org.lifecompanion.plugin.aac4all.wp2.model.logs;

import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.model.impl.selectionmode.HorizontalDirectKeyScanSelectionMode;
import org.lifecompanion.model.impl.selectionmode.RowColumnScanSelectionMode;

public enum KeyboardType {
    REOLOC_L("Clavier RéoLocLigne", "RéoLocL", RowColumnScanSelectionMode.class),
    REOLOC_G("Clavier RéoLocGlobale", "RéoLocG", RowColumnScanSelectionMode.class),
    STATIC("Clavier Statique", "Statique", RowColumnScanSelectionMode.class),
    CUR_STA("Clavier CurSta", "CurSta", HorizontalDirectKeyScanSelectionMode.class),
    DY_LIN("Clavier DyLin", "DyLin", HorizontalDirectKeyScanSelectionMode.class);

    private final String gridName;
    private final String translationId;
    private final Class<? extends SelectionModeI> selectionMode;

    KeyboardType(String gridName, String translationId, Class<? extends SelectionModeI> selectionMode) {
        this.gridName = gridName;
        this.translationId = translationId;
        this.selectionMode = selectionMode;
    }

    public String getGridName() {
        return gridName;
    }

    public String getTranslationId() {
        return translationId;
    }

    public Class<? extends SelectionModeI> getSelectionMode() {
        return selectionMode;
    }
}
