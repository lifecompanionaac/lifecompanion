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

package org.lifecompanion.api.component.definition.useaction;

import org.lifecompanion.api.component.definition.eventaction.MainCategoryI;

/**
 * Represent the category for a use action.<br>
 * Category doesn't have any use in use mode, but are very useful in configuration mode.<br>
 * Each main category define its own {@link UseActionSubCategoryI} that are directly used by use action.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface UseActionMainCategoryI extends MainCategoryI<UseActionSubCategoryI> {}
