package org.lifecompanion.plugin.translate.model.useaction;

import org.lifecompanion.framework.commons.translation.Translation;

public enum AvailableTranslation {
    EN,
    FR,
    ES,
    RU,
    RO,
    DE,
    IT,
    PT;

    public String getTranslation() {
        return Translation.getText("lc.translate.plugin.lang." + name().toLowerCase());
    }
}
