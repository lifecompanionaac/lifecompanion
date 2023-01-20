package org.lifecompanion.plugin.ppp.model;

import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Arrays;
import java.util.List;

public enum Choice {
    PPP_NOT_AT_ALL("ppp.plugin.model.ppp.choices.text.not_at_all"),
    PPP_A_LITTLE("ppp.plugin.model.ppp.choices.text.a_little"),
    PPP_QUITE_A_LOT("ppp.plugin.model.ppp.choices.text.quite_a_lot"),
    PPP_A_GREAT_DEAL("ppp.plugin.model.ppp.choices.text.a_great_deal"),

    EVS_ABSENT("ppp.plugin.model.evs.choices.text.absent"),
    EVS_LOW("ppp.plugin.model.evs.choices.text.low"),
    EVS_MODERATE("ppp.plugin.model.evs.choices.text.moderate"),
    EVS_INTENSE("ppp.plugin.model.evs.choices.text.intense"),
    EVS_INSUPPORTABLE("ppp.plugin.model.evs.choices.text.insupportable");

    public static final List<Choice> PPP_CHOICES = Arrays.asList(PPP_NOT_AT_ALL, PPP_A_LITTLE,
            PPP_QUITE_A_LOT, PPP_A_GREAT_DEAL);
    public static final List<Choice> EVS_CHOICES = Arrays.asList(EVS_ABSENT, EVS_LOW,
            EVS_MODERATE, EVS_INTENSE, EVS_INSUPPORTABLE);

    private final String textId;

    Choice(String textId) {
        this.textId = textId;
    }

    public String getText() {
        return Translation.getText(this.textId);
    }
}
