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

package org.lifecompanion.config.data.action.impl;

import org.lifecompanion.base.data.action.definition.BaseGridChangeAction;
import org.lifecompanion.api.component.definition.SpanModifiableComponentI;
import org.lifecompanion.api.component.definition.grid.ComponentGridI;
import org.lifecompanion.api.exception.LCException;

/**
 * Class that keep all expand/collapse action
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ExpandCollapseActions {
	/**
	 * Base action for all expand/collapse actions
	 */
	private abstract static class BaseGridChange extends BaseGridChangeAction {
		protected SpanModifiableComponentI component;

		public BaseGridChange(final SpanModifiableComponentI componentP, final ComponentGridI gridP) {
			super(gridP);
			this.component = componentP;
		}
	}

	/**
	 * Expand left
	 */
	public static class ExpandLeftAction extends BaseGridChange {
		public ExpandLeftAction(final SpanModifiableComponentI componentP, final ComponentGridI gridP) {
			super(componentP, gridP);
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.component.expandLeft();
		}

		@Override
		public String getNameID() {
			return "action.expand.left";
		}
	}

	/**
	 * Collpase left
	 */
	public static class CollapseLeftAction extends BaseGridChange {
		public CollapseLeftAction(final SpanModifiableComponentI componentP, final ComponentGridI gridP) {
			super(componentP, gridP);
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.component.collapseLeft();
		}

		@Override
		public String getNameID() {
			return "action.collapse.left";
		}

	}

	/**
	 * Collapse right
	 */
	public static class CollapseRightAction extends BaseGridChange {
		public CollapseRightAction(final SpanModifiableComponentI componentP, final ComponentGridI gridP) {
			super(componentP, gridP);
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.component.collapseRight();
		}

		@Override
		public String getNameID() {
			return "action.collapse.right";
		}
	}

	/**
	 * Expand right
	 */
	public static class ExpandRightAction extends BaseGridChange {
		public ExpandRightAction(final SpanModifiableComponentI componentP, final ComponentGridI gridP) {
			super(componentP, gridP);
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.component.expandRight();
		}

		@Override
		public String getNameID() {
			return "action.expand.right";
		}
	}

	/**
	 * Expand top
	 */
	public static class ExpandTopAction extends BaseGridChange {
		public ExpandTopAction(final SpanModifiableComponentI componentP, final ComponentGridI gridP) {
			super(componentP, gridP);
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.component.expandTop();
		}

		@Override
		public String getNameID() {
			return "action.expand.top";
		}
	}

	/**
	 * Collapse top
	 */
	public static class CollapseTopAction extends BaseGridChange {
		public CollapseTopAction(final SpanModifiableComponentI componentP, final ComponentGridI gridP) {
			super(componentP, gridP);
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.component.collapseTop();
		}

		@Override
		public String getNameID() {
			return "action.collapse.top";
		}
	}

	/**
	 * Expand bottom
	 */
	public static class ExpandBottomAction extends BaseGridChange {
		public ExpandBottomAction(final SpanModifiableComponentI componentP, final ComponentGridI gridP) {
			super(componentP, gridP);
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.component.expandBottom();
		}

		@Override
		public String getNameID() {
			return "action.expand.bottom";
		}
	}

	/**
	 * Collapse bottom
	 */
	public static class CollapseBottomAction extends BaseGridChange {
		public CollapseBottomAction(final SpanModifiableComponentI componentP, final ComponentGridI gridP) {
			super(componentP, gridP);
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.component.collapseBottom();
		}

		@Override
		public String getNameID() {
			return "action.collapse.bottom";
		}
	}
}
