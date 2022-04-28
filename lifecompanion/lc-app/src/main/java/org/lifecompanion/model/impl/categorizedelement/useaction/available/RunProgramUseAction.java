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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (StringUtils.isNotBlank(programPath.get())) {
            try {
                List<String> cmds = new ArrayList<>(Collections.singletonList(programPath.get()));
                if (StringUtils.isNotBlank(programArgs.get())) {
                    Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
                    Matcher regexMatcher = regex.matcher(programArgs.get());
                    while (regexMatcher.find()) {
                        if (regexMatcher.group(1) != null) {
                            cmds.add(UseVariableController.INSTANCE.createText(regexMatcher.group(1), variables));
                        } else if (regexMatcher.group(2) != null) {
                            cmds.add(UseVariableController.INSTANCE.createText(regexMatcher.group(2), variables));
                        } else {
                            cmds.add(UseVariableController.INSTANCE.createText(regexMatcher.group(), variables));
                        }
                    }
                }
                File logDir = new File(System.getProperty("java.io.tmpdir") + "/LifeCompanion/logs/run-program-action/" + System.currentTimeMillis());
                logDir.mkdirs();
                LOGGER.info("Will try to run process with command : {}", cmds);
                new ProcessBuilder()
                        .command(cmds)
                        .redirectOutput(new File(logDir + "/out.txt"))
                        .redirectError(new File(logDir + "/err.txt"))
                        .start();
                LOGGER.info("Process {} was launched successfully", cmds);
            } catch (Exception e) {
                LOGGER.error("Couldn't start process {} with args {}", programPath.get(), programArgs, e);
            }
        }
    }

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
}
