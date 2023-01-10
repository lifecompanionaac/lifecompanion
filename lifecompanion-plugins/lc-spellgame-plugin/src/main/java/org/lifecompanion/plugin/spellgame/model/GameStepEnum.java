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

package org.lifecompanion.plugin.spellgame.model;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

public enum GameStepEnum implements GameStep {
    WRITE("spellgame.plugin.game.step.instruction.write", false),
    COPY("spellgame.plugin.game.step.instruction.copy", true),
    CHAR_COUNT("spellgame.plugin.game.step.instruction.char.count", true) {
        @Override
        public boolean checkWord(String word, String input) {
            try {
                return word.length() == Integer.parseInt(input);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public String getExpectedResult(String word) {
            return "" + StringUtils.safeLength(word);
        }
    },
    BACKWARD("spellgame.plugin.game.step.instruction.backward", true) {
        @Override
        public boolean checkWord(String word, String input) {
            return StringUtils.isEquals(StringUtils.reverse(word), input);
        }

        @Override
        public String getExpectedResult(String word) {
            return StringUtils.reverse(word);
        }
    }

    //    ONE_ON_TWO("spellgame.plugin.game.step.instruction.one.on.two"),
    //    VOWEL("spellgame.plugin.game.step.instruction.vowel"),
    //    CONSONANT("spellgame.plugin.game.step.instruction.consonant");
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

    @Override
    public String getExpectedResult(String word) {
        return word;
    }


}
