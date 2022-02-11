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

package org.lifecompanion.model.impl.categorizedelement.useaction.available;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.virtualkeyboard.VirtualKeyboardController;
import org.lifecompanion.model.impl.categorizedelement.useaction.RepeatActionBaseImpl;

/**
 * Action that should have the same effect as the suppr key on a keyboard.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class DeleteNextCharAndRepeatAction extends RepeatActionBaseImpl<UseActionTriggerComponentI> {

	public DeleteNextCharAndRepeatAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.DELETE_TEXT;
		this.nameID = "action.delete.next.char.and.repeat.name";
		this.staticDescriptionID = "action.delete.next.char.and.repeat.description";
		this.variableDescriptionProperty().set(this.getStaticDescription());
		this.configIconPath = "text/icon_delete_next_char_and_repeat.png";
		this.parameterizableAction = false;
		this.order = 6;
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	protected void executeFirstBeforeRepeat(final UseActionEvent eventType) {
		WritingStateController.INSTANCE.removeNextChar(WritingEventSource.USER_ACTIONS);
	}

	@Override
	protected void executeOnRepeat(final UseActionEvent eventType) {
		WritingStateController.INSTANCE.removeNextChar(WritingEventSource.USER_ACTIONS);
	}

	@Override
	protected void repeatEnded(final UseActionEvent eventType) {}

	//TODO : allow user to configure delays
	@Override
	protected long getDelayBeforeRepeatStartMillis() {
		return VirtualKeyboardController.DELAY_BEFORE_REPEAT_KEY_START;
	}

	@Override
	protected long getDelayBetweenEachRepeatMillis() {
		return VirtualKeyboardController.DELAY_REPEAT_KEY_HOLD;
	}
	//========================================================================
}
