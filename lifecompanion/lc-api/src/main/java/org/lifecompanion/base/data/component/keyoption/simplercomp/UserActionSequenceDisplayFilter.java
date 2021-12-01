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

package org.lifecompanion.base.data.component.keyoption.simplercomp;

import org.lifecompanion.api.component.definition.simplercomp.UserActionSequenceItemI;

import java.util.function.Predicate;

public enum UserActionSequenceDisplayFilter {
    BOTH("user.action.display.filter.both", i -> true),
    ONLY_SUB("user.action.display.filter.only.sub", i -> i.subItemProperty().get()),
    ONLY_NOT_SUB("user.action.display.filter.only.not.sub", i -> !i.subItemProperty().get());

    private final String nameId;
    private final Predicate<UserActionSequenceItemI> filter;

    UserActionSequenceDisplayFilter(String nameId, Predicate<UserActionSequenceItemI> filter) {
        this.nameId = nameId;
        this.filter = filter;
    }

    public boolean filter(UserActionSequenceItemI item) {
        return filter.test(item);
    }

    public String getNameId() {
        return nameId;
    }
}
