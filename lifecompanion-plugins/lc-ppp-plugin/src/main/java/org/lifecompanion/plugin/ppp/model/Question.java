package org.lifecompanion.plugin.ppp.model;

import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Map;

public enum Question {
    PPP_CHEERFUL("ppp.plugin.model.ppp.questions.text.cheerful",
            Map.of(Choice.PPP_NOT_AT_ALL, 3, Choice.PPP_A_LITTLE, 2, Choice.PPP_QUITE_A_LOT, 1, Choice.PPP_A_GREAT_DEAL, 0)),
    PPP_SOCIABLE("ppp.plugin.model.ppp.questions.text.sociable",
            Map.of(Choice.PPP_NOT_AT_ALL, 3, Choice.PPP_A_LITTLE, 2, Choice.PPP_QUITE_A_LOT, 1, Choice.PPP_A_GREAT_DEAL, 0)),
    PPP_DEPRESSED("ppp.plugin.model.ppp.questions.text.depressed",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_CRIED("ppp.plugin.model.ppp.questions.text.cried",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_HARD_TO_CONSOLE("ppp.plugin.model.ppp.questions.text.hard_to_console",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_SELF_HARMED("ppp.plugin.model.ppp.questions.text.self_harmed",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_RELUCTANT("ppp.plugin.model.ppp.questions.text.reluctant",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_DISTURBED_SLEEP("ppp.plugin.model.ppp.questions.text.disturbed_sleep",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_GRIMACED("ppp.plugin.model.ppp.questions.text.grimaced",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_WORRIED("ppp.plugin.model.ppp.questions.text.worried",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_FRIGHTENED("ppp.plugin.model.ppp.questions.text.frightened",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_GROUND_TEETH("ppp.plugin.model.ppp.questions.text.ground_teeth",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_RESTLESS("ppp.plugin.model.ppp.questions.text.restless",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_TENSED("ppp.plugin.model.ppp.questions.text.tensed",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_FLEXED_INWARDS("ppp.plugin.model.ppp.questions.text.flexed_inwards",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_TENDED_TO_TOUCH("ppp.plugin.model.ppp.questions.text.tended_to_touch",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_RESISTED("ppp.plugin.model.ppp.questions.text.resisted",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_PULLED_AWAY("ppp.plugin.model.ppp.questions.text.pulled_away",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_TWISTED("ppp.plugin.model.ppp.questions.text.twisted",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),
    PPP_INVOLUNTARY_MOVEMENTS("ppp.plugin.model.ppp.questions.text.involuntary_movements",
            Map.of(Choice.PPP_NOT_AT_ALL, 0, Choice.PPP_A_LITTLE, 1, Choice.PPP_QUITE_A_LOT, 2, Choice.PPP_A_GREAT_DEAL, 3)),

    EVS_INTENSITY("ppp.plugin.model.evs.questions.text.intensity", Map.of(Choice.EVS_ABSENT, 0, Choice.EVS_LOW, 1,
            Choice.EVS_MODERATE, 2, Choice.EVS_INTENSE, 3, Choice.EVS_INSUPPORTABLE, 4));

    private final String textId;

    private final Map<Choice, Integer> choicesScoresMap;

    Question(String textId, Map<Choice, Integer> choicesScoresMap) {
        this.textId = textId;
        this.choicesScoresMap = choicesScoresMap;
    }

    public String getText() {
        return Translation.getText(this.textId);
    }

    public Map<Choice, Integer> getChoicesScoresMap() {
        return this.choicesScoresMap;
    }

    @Override
    public String toString() {
        return this.getText();
    }
}
