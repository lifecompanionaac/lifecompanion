package org.lifecompanion.plugin.ppp.services;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.plugin.ppp.model.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomDataService {
    public static void generateRandomDataFor(LCConfigurationI configuration, UserProfile profile) {
        Random random = new Random();
        int generated = random.nextInt(200, 500);
        while (generated-- > 0) {

            EvaluatorType evaluatorType = EvaluatorType.values()[random.nextInt(EvaluatorType.values().length)];
            Evaluator evaluator = new Evaluator(evaluatorType, "Evaluator " + random.nextInt(10));

            JsonRecordI record;
            if (random.nextInt(10) <= 8) {
                // Type and init
                AssessmentType type = AssessmentType.values()[random.nextInt(AssessmentType.values().length)];
                AssessmentRecord assessmentRecord = new AssessmentRecord(type, evaluator);
                assessmentRecord.setComment("Comment first line\nComment second line\nComment third line");

                // Answer questions
                for (Question question : type.getQuestions()) {
                    List<Choice> choices = new ArrayList<>(question.getChoicesScoresMap().keySet());
                    assessmentRecord.addAnswer(question, choices.get(random.nextInt(choices.size())));
                }
                record = assessmentRecord;
            } else {
                List<Action> actions = profile.getActions();
                record = new ActionRecord(evaluator, actions.get(random.nextInt(actions.size())));
            }

            // Change date and save
            record.setRecordedAt(
                    ZonedDateTime.now().minusDays(random.nextInt(25))
                            .withHour(random.nextInt(8, 23))
                            .withMinute(random.nextInt(0, 60))
            );
            RecordsService.INSTANCE.save(configuration, profile, record, false);
        }
    }
}