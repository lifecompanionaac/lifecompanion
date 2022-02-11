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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RunProgramUseAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RunProgramUseAction.class);

	private StringProperty programPath;
	private StringProperty programArgs;

	public RunProgramUseAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.COMPUTER_FEATURES;
		this.nameID = "action.run.program.name";
		this.order = 5;
		this.staticDescriptionID = "action.run.program.description";
		this.configIconPath = "computeraccess/icon_run_program.png";
		this.parameterizableAction = true;
		programArgs = new SimpleStringProperty();
		programPath = new SimpleStringProperty();
		this.variableDescriptionProperty().set(getStaticDescription());
	}

	public StringProperty programPathProperty() {
		return programPath;
	}

	public StringProperty programArgsProperty() {
		return programArgs;
	}

	// Class part : "Execute"
	// ========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		if (StringUtils.isNotBlank(programPath.get())){
			try {
				List<String> cmds = new ArrayList<>(Arrays.asList(programPath.get()));
				if (StringUtils.isNotBlank(programArgs.get())) {
					cmds.add(UseVariableController.INSTANCE.createText(this.programArgs.get(), variables));
				}
				new ProcessBuilder().command(cmds).start();
				LOGGER.info("Process {} was launched successfully", cmds);
			} catch (Exception e) {
				LOGGER.error("Couldn't start process {} with args {}", programPath.get(), programArgs, e);
			}
		}
	}
	// ========================================================================

	// Class part : "XML"
	//========================================================================
	@Override
	public Element serialize(final IOContextI contextP) {
		Element elem = super.serialize(contextP);
		XMLObjectSerializer.serializeInto(RunProgramUseAction.class, this, elem);
		return elem;
	}

	@Override
	public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
		super.deserialize(nodeP, contextP);
		XMLObjectSerializer.deserializeInto(RunProgramUseAction.class, this, nodeP);
	}
	//========================================================================

}
