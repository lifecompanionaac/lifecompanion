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
    WRITE("example.plugin.game.step.instruction.write"),
    COPY("example.plugin.game.step.instruction.copy"),
    CHAR_COUNT("game.step.instruction.char.count") {
        @Override
        public boolean checkWord(String word, String cleanInput) {
            try {
                return word.length() == Integer.parseInt(cleanInput);
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
//    BACKWARD("example.plugin.game.step.instruction.backward"),
//    ONE_ON_TWO("example.plugin.game.step.instruction.one.on.two"),
//    VOWEL("example.plugin.game.step.instruction.vowel"),
//    CONSONANT("example.plugin.game.step.instruction.consonant");
    ;
    private final String instructionId;

    GameStepEnum(String instructionId) {
        this.instructionId = instructionId;
    }

    @Override
    public String getInstruction() {
        return Translation.getText(instructionId);
    }

    @Override
    public boolean checkWord(String word, String cleanInput) {
        return StringUtils.isEquals(word, cleanInput);
    }
}
