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
import org.lifecompanion.plugin.spellgame.utils.SpellGameUtils;

import java.util.Set;

public enum GameStepV2Enum implements GameStepV2 {
    WRITE("spellgame.plugin.game.step.write", StepDisplayMode.HIDDEN) {
        @Override
        public GameStepV2 getStepOnFail() {
            return COPY;
        }
    },
    COPY("spellgame.plugin.game.step.copy", StepDisplayMode.SHOWN) {
        @Override
        public GameStepV2 getStepOnFail() {
            return COPY;
        }

        @Override
        public GameStepV2 getStepOnSuccess() {
            return COPY_TIMER;
        }
    },
    COPY_TIMER("spellgame.plugin.game.step.copy.timer", StepDisplayMode.TIMER) {
        @Override
        public GameStepV2 getStepOnFail() {
            return COPY;
        }

        @Override
        public GameStepV2 getStepOnSuccess() {
            return CHAR_COUNT;
        }
    },
    CHAR_COUNT("spellgame.plugin.game.step.char.count", StepDisplayMode.HIDDEN) {
        @Override
        public GameStepV2 getStepOnFail() {
            return COPY;
        }

        @Override
        public GameStepV2 getStepOnSuccess() {
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

        static final Set<Character> VOWELS = Set.of('a', 'e', 'i', 'o', 'u', 'y');

        @Override
        public GameStepV2 getStepOnFail() {
            return COPY;
        }

        @Override
        public GameStepV2 getStepOnSuccess() {
            return CONSONANT_COUNT;
        }

        @Override
        public boolean checkWord(SpellGamePluginProperties spellGamePluginProperties, String word, String input) {
            // FIXME use count
            return StringUtils.isEquals(SpellGameUtils.withOnly(word, VOWELS), input);
        }

        @Override
        public String getExpectedResult(String word) {
            return SpellGameUtils.withOnly(word, VOWELS);
        }


    }, CONSONANT_COUNT("spellgame.plugin.game.step.consonant.count", StepDisplayMode.HIDDEN) {
        static final Set<Character> CONSONANT = Set.of('b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z');

        @Override
        public GameStepV2 getStepOnFail() {
            return COPY;
        }

        @Override
        public boolean checkWord(SpellGamePluginProperties spellGamePluginProperties, String word, String input) {
            // FIXME use count
            return StringUtils.isEquals(SpellGameUtils.withOnly(word, CONSONANT), input);
        }

        @Override
        public String getExpectedResult(String word) {
            return SpellGameUtils.withOnly(word, CONSONANT);
        }
    };

    private final String translationId;
    private final StepDisplayMode displayMode;

    GameStepV2Enum(String translationId, StepDisplayMode displayMode) {
        this.translationId = translationId;
        this.displayMode = displayMode;
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
        // TODO : accent or not ?
        return StringUtils.isEquals(word, input);
    }

    @Override
    public String getExpectedResult(String word) {
        return word;
    }

    @Override
    public GameStepV2 getStepOnSuccess() {
        return null;
    }

    @Override
    public StepDisplayMode getDisplayMode() {
        return displayMode;
    }


}
