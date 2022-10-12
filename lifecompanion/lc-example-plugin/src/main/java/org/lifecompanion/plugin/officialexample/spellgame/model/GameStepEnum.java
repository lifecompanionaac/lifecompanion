/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lifecompanion.plugin.officialexample.spellgame.model;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

public enum GameStepEnum implements GameStep {
    WRITE("example.plugin.game.step.instruction.write", false),
    COPY("example.plugin.game.step.instruction.copy", true),
    CHAR_COUNT("example.plugin.game.step.instruction.char.count", true) {
        @Override
        public boolean checkWord(String word, String input) {
            try {
                return word.length() == Integer.parseInt(input);
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
    //    BACKWARD("example.plugin.game.step.instruction.backward"),
    //    ONE_ON_TWO("example.plugin.game.step.instruction.one.on.two"),
    //    VOWEL("example.plugin.game.step.instruction.vowel"),
    //    CONSONANT("example.plugin.game.step.instruction.consonant");
    // at least 1 point per step
    ;
    private final String instructionId;
    private final boolean wordDisplayOnStep;

    GameStepEnum(String instructionId, boolean wordDisplayOnStep) {
        this.instructionId = instructionId;
        this.wordDisplayOnStep = wordDisplayOnStep;
    }

    @Override
    public boolean isWordDisplayOnStep() {
        return wordDisplayOnStep;
    }

    @Override
    public String getInstruction(String word) {
        return Translation.getText(instructionId, word);
    }

    @Override
    public boolean checkWord(String word, String input) {
        return StringUtils.isEquals(word, input);
    }


}
