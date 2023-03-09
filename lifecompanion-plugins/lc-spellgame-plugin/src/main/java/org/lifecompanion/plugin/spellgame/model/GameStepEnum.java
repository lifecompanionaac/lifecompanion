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
import org.lifecompanion.plugin.spellgame.SpellGamePluginProperties;

import java.util.Set;

public enum GameStepEnum implements GameStep {
    WRITE("spellgame.plugin.game.step.write", StepDisplayMode.HIDDEN) {
        @Override
        public GameStep getStepOnFail() {
            return COPY;
        }
    },
    COPY("spellgame.plugin.game.step.copy", StepDisplayMode.SHOWN) {
        @Override
        public GameStep getStepOnFail() {
            return COPY;
        }

        @Override
        public GameStep getStepOnSuccess() {
            return COPY_TIMER;
        }
    },
    COPY_TIMER("spellgame.plugin.game.step.copy.timer", StepDisplayMode.TIMER) {
        @Override
        public GameStep getStepOnFail() {
            return COPY;
        }

        @Override
        public GameStep getStepOnSuccess() {
            return CHAR_COUNT;
        }
    },
    CHAR_COUNT("spellgame.plugin.game.step.char.count", StepDisplayMode.HIDDEN) {
        @Override
        public GameStep getStepOnFail() {
            return COPY;
        }

        @Override
        public GameStep getStepOnSuccess() {
            return VOWEL_COUNT;
        }

        @Override
        public boolean checkWord(SpellGamePluginProperties spellGamePluginProperties, String word, String input) {
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

    VOWEL_COUNT("spellgame.plugin.game.step.vowel.count", StepDisplayMode.HIDDEN) {
        @Override
        public GameStep getStepOnFail() {
            return COPY;
        }

        @Override
        public GameStep getStepOnSuccess() {
            return CONSONANT_COUNT;
        }

        @Override
        public boolean checkWord(SpellGamePluginProperties spellGamePluginProperties, String word, String input) {
            try {
                return getLengthWithout(StringUtils.stripAccents(word), CONSONANT) == Integer.parseInt(input);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public String getExpectedResult(String word) {
            return "" + getLengthWithout(StringUtils.stripAccents(word), CONSONANT);
        }


    }, CONSONANT_COUNT("spellgame.plugin.game.step.consonant.count", StepDisplayMode.HIDDEN) {
        @Override
        public GameStep getStepOnFail() {
            return COPY;
        }

        @Override
        public boolean checkWord(SpellGamePluginProperties spellGamePluginProperties, String word, String input) {
            try {
                return getLengthWithout(StringUtils.stripAccents(word), VOWELS) == Integer.parseInt(input);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public String getExpectedResult(String word) {
            return "" + getLengthWithout(StringUtils.stripAccents(word), VOWELS);
        }
    };

    private static final Set<Character> CONSONANT = Set.of('b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z');
    private static final Set<Character> VOWELS = Set.of('a', 'e', 'i', 'o', 'u', 'y');

    private final String translationId;
    private final StepDisplayMode displayMode;

    GameStepEnum(String translationId, StepDisplayMode displayMode) {
        this.translationId = translationId;
        this.displayMode = displayMode;
    }

    public int getLengthWithout(String word, Set<Character> characters) {
        for (Character character : characters) {
            word = word.replace(String.valueOf(character), "");
        }
        return word.length();
    }


    @Override
    public String getName() {
        return Translation.getText(translationId + ".name");
    }

    @Override
    public String getInstruction(String word) {
        return Translation.getText(translationId + ".instruction.speech", word);
    }

    @Override
    public String getGeneralInstruction() {
        return Translation.getText(translationId + ".instruction.general");
    }

    @Override
    public boolean checkWord(SpellGamePluginProperties spellGamePluginProperties, String word, String input) {
        return StringUtils.isEquals(word, input);
    }

    @Override
    public String getExpectedResult(String word) {
        return word;
    }

    @Override
    public GameStep getStepOnSuccess() {
        return null;
    }

    @Override
    public StepDisplayMode getDisplayMode() {
        return displayMode;
    }


}
