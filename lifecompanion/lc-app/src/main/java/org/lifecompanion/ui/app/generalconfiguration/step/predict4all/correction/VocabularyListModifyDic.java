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

package org.lifecompanion.ui.app.generalconfiguration.step.predict4all.correction;

public enum VocabularyListModifyDic {
    PRIORIZE_1("predict4all.rule.word.list.1", "http://www-annexe.ia76.ac-rouen.fr/evaluation/documents/lexique_maternelle_eduscol.pdf",
            "mots_enfants_1_rouen.txt", true), //
    PRIORIZE_2("predict4all.rule.word.list.2", "http://eduscol.education.fr/cid50486/liste-de-frequence-lexicale.html", "mots_enfants_2_eduscol.txt",
            true), //
    PRIORIZE_3("predict4all.rule.word.list.3", "http://eduscol.education.fr/cid50486/liste-de-frequence-lexicale.html",
            "mots_enfants_3_wiktionary.txt", true), //
    DEPRIORIZE_1("predict4all.rule.word.list.4", "http://golfes-dombre.nuxit.net/mots-rares/a.html", "mots_exclure_rares.txt", false),
    ;

    private final String nameId;
    private final String link;
    private final String fileName;
    private final boolean priorize;

    private VocabularyListModifyDic(final String nameId, final String link, final String fileName, final boolean priorize) {
        this.nameId = nameId;
        this.link = link;
        this.fileName = fileName;
        this.priorize = priorize;
    }

    public String getNameId() {
        return this.nameId;
    }

    public String getLink() {
        return this.link;
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean isPriorize() {
        return this.priorize;
    }
}
