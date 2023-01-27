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

import java.util.Map;

import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PutUserTextInClipboardAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PutUserTextInClipboardAction.class);

	public PutUserTextInClipboardAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.COMPUTER_FEATURES;
		this.nameID = "action.put.user.text.to.clipboard.name";
		this.order = -5;
		this.staticDescriptionID = "action.put.user.text.to.clipboard.static.description";
		this.configIconPath = "computeraccess/icon_put_editor_to_clipboard.png";
		this.parameterizableAction = false;
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	// Class part : "Execute"
	// ========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		try {
			String currentText = WritingStateController.INSTANCE.currentTextProperty().get();
			final ClipboardContent content = new ClipboardContent();
			content.putString(currentText);
			FXThreadUtils.runOnFXThread(() -> Clipboard.getSystemClipboard().setContent(content));
		} catch (Throwable t) {
			LOGGER.warn("Couldn't copy user text top clipboard", t);
		}
	}
	// ========================================================================

}
