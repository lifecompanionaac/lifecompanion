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

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.jdom2.Element;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.base.data.control.refacto.ProfileController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;

/**
 * Action to save current typed text to a file.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SaveUserTextAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveUserTextAction.class);

	private static final SimpleDateFormat DATE_FORMAT_FILENAME = new SimpleDateFormat("dd-MM-yyyy_HH-mm");

	/**
	 * Destination folder for saved text
	 */
	private String destinationFolder;

	public SaveUserTextAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.COMPUTER_FEATURES;
		this.nameID = "action.save.user.editor.text.to.txt.file.name";
		this.order = 0;
		this.staticDescriptionID = "action.save.user.editor.text.to.txt.file.static.description";
		this.configIconPath = "computeraccess/icon_save_editor_to_file.png";
		this.parameterizableAction = true;
		this.variableDescriptionProperty().set(this.getStaticDescription());
		this.checkDestinationFolder();
	}

	private void checkDestinationFolder() {
		if (StringUtils.isBlank(destinationFolder)) {
			this.destinationFolder = System.getProperty("user.home") + File.separator + Translation.getText("default.user.text.directory.name")
					+ File.separator;
		}
	}

	public String getDestinationFolder() {
		return destinationFolder;
	}

	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	// Class part : "Execute"
	// ========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		try {
			// Generate file name and path
			StringBuilder fileName = new StringBuilder(DATE_FORMAT_FILENAME.format(new Date()));

			LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
			if (profile != null) {
				fileName.append("_").append(profile.nameProperty().get());
			}
			LCConfigurationDescriptionI configDescription = AppModeController.INSTANCE.getEditModeContext().configurationDescriptionProperty().get();
			if (configDescription != null) {
				fileName.append("_").append(LCUtils.getValidFileName(configDescription.configurationNameProperty().get()));
			}
			fileName.append(".txt");
			this.checkDestinationFolder();
			File outputFile = new File(this.destinationFolder + File.separator + fileName.toString());
			outputFile.getParentFile().mkdirs();
			// Save text content
			String currentText = WritingStateController.INSTANCE.currentTextProperty().get();
			try (PrintWriter pw = new PrintWriter(outputFile)) {
				pw.println(currentText);
			}
			LOGGER.info("User text will saved to {}", outputFile.getAbsolutePath());
		} catch (Throwable t) {
			LOGGER.warn("Couldn't save user text", t);
		}
	}
	// ========================================================================

	// Class part : "XML"
	//========================================================================
	@Override
	public Element serialize(final IOContextI contextP) {
		Element elem = super.serialize(contextP);
		XMLObjectSerializer.serializeInto(SaveUserTextAction.class, this, elem);
		return elem;
	}

	@Override
	public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
		super.deserialize(nodeP, contextP);
		XMLObjectSerializer.deserializeInto(SaveUserTextAction.class, this, nodeP);
	}
	//========================================================================

}
