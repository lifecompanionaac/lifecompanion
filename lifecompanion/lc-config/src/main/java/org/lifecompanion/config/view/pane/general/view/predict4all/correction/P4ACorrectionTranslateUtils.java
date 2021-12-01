/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
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

package org.lifecompanion.config.view.pane.general.view.predict4all.correction;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.predict4all.nlp.Separator;
import org.predict4all.nlp.words.correction.CorrectionRule;
import org.predict4all.nlp.words.correction.CorrectionRuleNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;


//TODO : should detect errors > bidir insert or delete
public class P4ACorrectionTranslateUtils {
    public static String generateTranslate(CorrectionRuleParentNodeView parentView, final CorrectionRuleNode node) {
        StringBuilder translation = new StringBuilder();

        // Check if rule is disabled (direct or by a parent)
        boolean ruleEnable = node.isEnabled();
        if (ruleEnable) {
            while (parentView != null) {
                if (!parentView.node.isEnabled()) {
                    translation.append(Translation.getText("predict4all.rule.translate.rule.disable.rule.parent")).append("\n");
                    break;
                } else {
                    parentView = parentView.parentView;
                }
            }
        } else {
            translation.append(Translation.getText("predict4all.rule.translate.rule.disable.rule.direct")).append("\n");
        }
        CorrectionRule correctionRule = node.getCorrectionRule();
        HashSet<String> errors = new HashSet<>(Arrays.asList(correctionRule.getErrors()));
        HashSet<String> replacements = new HashSet<>(Arrays.asList(correctionRule.getReplacements()));

        // Detect insertion : insert something anywhere in the word
        if (!correctionRule.isBidirectional() && correctionRule.getErrors().length == 1 && StringUtils.isEquals("", correctionRule.getErrors()[0])) {
            translation.append(Translation.getText("predict4all.rule.translate.rule.insert.element",
                    P4ACorrectionTranslateUtils.createStringFromContentArray(replacements, null, "predict4all.rule.translate.rule.or.name")));
        }
        // Detect deletion
        else if (!correctionRule.isBidirectional() && correctionRule.getReplacements().length == 1 && StringUtils.isEquals("", correctionRule.getReplacements()[0])) {
            translation.append(Translation.getText("predict4all.rule.translate.rule.delete.element",
                    P4ACorrectionTranslateUtils.createStringFromContentArray(errors, null, "predict4all.rule.translate.rule.or.name")));
        }
        // Detect confusion
        else if (Arrays.deepEquals(correctionRule.getErrors(), correctionRule.getReplacements())) {
            translation.append(Translation.getText("predict4all.rule.translate.rule.confusion.set", P4ACorrectionTranslateUtils.createStringFromContentArray(errors, null, ", ")));
        }
        // Detect classic rule
        else {
            translation.append(Translation.getText("predict4all.rule.translate.rule.simple.replace",
                    P4ACorrectionTranslateUtils.createStringFromContentArray(errors, e -> !replacements.contains(e), "predict4all.rule.translate.rule.or.name"),
                    P4ACorrectionTranslateUtils.createStringFromContentArray(replacements, e -> !errors.contains(e), "predict4all.rule.translate.rule.or.name")));
            if (correctionRule.isBidirectional()) {
                translation.append("\n").append(Translation.getText("predict4all.rule.translate.rule.simple.replace.bidir"));
            }
        }

        return translation.toString();
    }

    private static String createStringFromContentArray(final HashSet<String> content, final Predicate<String> filter, final String joinTermId) {
        return content.stream().filter(filter != null ? filter : v -> true).map(e -> "\"" + P4ACorrectionTranslateUtils.getConvertedStringSeparator(e) + "\"")
                .collect(Collectors.joining(Translation.getText(joinTermId)));
    }

    public static String getConvertedStringSeparator(final String str) {
        if (str == null || str.length() == 0) {
            return Translation.getText("predict4all.rule.sep.anywhere");
        } else if (str.length() == 1) {
            Separator separatorFor = Separator.getSeparatorFor(str.charAt(0));
            if (separatorFor != null) {
                String id = P4ACorrectionTranslateUtils.SEPARATOR_CHAR_CONVERT.get(separatorFor);
                return id != null ? Translation.getText(id) : str;
            } else {
                return str;
            }
        } else {
            return str;
        }
    }

    @SuppressWarnings("serial")
    private static final Map<Separator, String> SEPARATOR_CHAR_CONVERT = new HashMap<Separator, String>() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {
            this.put(Separator.SPACE, "predict4all.rule.sep.translate.space");
            this.put(Separator.APOSTROPHE, "predict4all.rule.sep.translate.apostrophe");
        }
    };

    // FIXME : (correctionRule.getErrors().length == 1 && correctionRule.getReplacements().length == 1 && correctionRule.isBidirectionnal())
    // Classic rules with bidir on only one char are the same than confusion BUT the displayed element in UI are not the same so don't add this condition yet.
    public static CorrectionCategory getCorrectionCategoryFor(final CorrectionRuleNode node) {
        CorrectionRule correctionRule = node.getCorrectionRule();

        // Detect insertion : insert something anywhere in the word
        if (!correctionRule.isBidirectional() && correctionRule.getErrors().length == 1 && StringUtils.isEquals("", correctionRule.getErrors()[0])) {
            return CorrectionCategory.INSERT;
        }
        // Detect deletion
        else if (!correctionRule.isBidirectional() && correctionRule.getReplacements().length == 1 && StringUtils.isEquals("", correctionRule.getReplacements()[0])) {
            return CorrectionCategory.DELETE;
        }
        // Detect confusion
        else if (Arrays.deepEquals(correctionRule.getErrors(), correctionRule.getReplacements())) {
            return CorrectionCategory.CONFUSION;
        }
        // Detect classic rule
        else {
            return CorrectionCategory.CLASSIC;
        }
    }
}
