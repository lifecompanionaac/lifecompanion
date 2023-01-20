package org.lifecompanion.plugin.ppp.model;

import org.lifecompanion.framework.commons.translation.Translation;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum AssessmentType {
    PPP("ppp-assessments", "ppp.plugin.model.assessments.types.text.ppp", Arrays.asList(
            Question.PPP_CHEERFUL,
            Question.PPP_SOCIABLE,
            Question.PPP_DEPRESSED,
            Question.PPP_CRIED,
            Question.PPP_HARD_TO_CONSOLE,
            Question.PPP_SELF_HARMED,
            Question.PPP_RELUCTANT,
            Question.PPP_DISTURBED_SLEEP,
            Question.PPP_GRIMACED,
            Question.PPP_WORRIED,
            Question.PPP_FRIGHTENED,
            Question.PPP_GROUND_TEETH,
            Question.PPP_RESTLESS,
            Question.PPP_TENSED,
            Question.PPP_FLEXED_INWARDS,
            Question.PPP_TENDED_TO_TOUCH,
            Question.PPP_RESISTED,
            Question.PPP_PULLED_AWAY,
            Question.PPP_TWISTED,
            Question.PPP_INVOLUNTARY_MOVEMENTS)),
    EVS("evs-assessments", "ppp.plugin.model.assessments.types.text.evs",
            Collections.singletonList(Question.EVS_INTENSITY)),
    AUTO_EVS("auto-evs-assessments", "ppp.plugin.model.assessments.types.text.auto_evs",
            Collections.singletonList(Question.EVS_INTENSITY));

    private final String directory;

    private final String textId;

    private final List<Question> questions;

    AssessmentType(final String directory, final String textId, final List<Question> questions) {
        this.directory = directory;
        this.textId = textId;
        this.questions = questions;
    }

    public String getDirectory() {
        return AbstractRecord.DIRECTORY + File.separator + this.directory;
    }

    public String getText() {
        return Translation.getText(this.textId);
    }

    public List<Question> getQuestions() {
        return this.questions;
    }
}
