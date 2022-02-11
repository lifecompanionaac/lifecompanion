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

package org.lifecompanion.model.impl.categorizedelement.useaction;

import org.lifecompanion.model.api.categorizedelement.useaction.SimpleUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base implementation for simple use action.<br>
 * This is done to provide a faster way to implement actions.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class SimpleUseActionImpl<T extends UseActionTriggerComponentI> extends BaseUseActionImpl<T> implements SimpleUseActionI<T> {

	public SimpleUseActionImpl(final Class<T> allowedParentP) {
		super(allowedParentP);
	}

	private final static Logger LOGGER = LoggerFactory.getLogger(SimpleUseActionImpl.class);

	@Override
	public void eventStarts(final UseActionEvent eventTypeP) {
		SimpleUseActionImpl.LOGGER.info("Ignore a event start of type {} on action {} because it's a simple use action.", eventTypeP,
				this.getClass().getSimpleName());
	}

	@Override
	public void eventEnds(final UseActionEvent eventTypeP) {
		SimpleUseActionImpl.LOGGER.info("Ignore a event end of type {} on action {} because it's a simple use action.", eventTypeP,
				this.getClass().getSimpleName());
	}

	@Override
	public boolean isSimple() {
		return true;
	}
}
