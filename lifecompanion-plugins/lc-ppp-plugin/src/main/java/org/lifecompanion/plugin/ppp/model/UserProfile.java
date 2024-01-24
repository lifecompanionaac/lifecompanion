/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.ppp.model;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private String userId;
    private String userName;
    private int baseScore;
    private LocalDate baseScoreAt;
    private List<Action> actions;

    public UserProfile() {
        this.userId = StringUtils.getNewID();
        this.actions = new ArrayList<>(
                List.of(new Action("Antalgique 1 systématique"),
                        new Action("Antalgique 1"),
                        new Action("Antalgique 2 systématique"),
                        new Action("Antalgique 2"),
                        new Action("Changement position"),
                        new Action("Retrait appareillage"),
                        new Action("Surveillance"),
                        new Action("Mise au repos"),
                        new Action("Contact médical"),
                        new Action("Diminution lumière/bruit"),
                        new Action("Attente")
                )
        );
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getBaseScore() {
        return baseScore;
    }

    public void setBaseScore(int baseScore) {
        this.baseScore = baseScore;
    }

    public LocalDate getBaseScoreAt() {
        return baseScoreAt;
    }

    public void setBaseScoreAt(LocalDate baseScoreAt) {
        this.baseScoreAt = baseScoreAt;
    }
}
