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
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;

/**
 * Action to save current typed text to a file.
 *
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
        this.destinationFolder = IOUtils.getDefaultDestinationFolder(Translation.getText("default.user.text.directory.name")).getPath();
        this.variableDescriptionProperty().set(this.getStaticDescription());
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
        if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_EXTERNAL_ACTIONS)) {
            try {
                File outputFile = IOUtils.getUserUseModeDestination(destinationFolder, Translation.getText("default.user.text.directory.name"), "txt");
                // Save text content
                String currentText = WritingStateController.INSTANCE.currentTextProperty().get();
                try (PrintWriter pw = new PrintWriter(outputFile)) {
                    pw.println(currentText);
                }
                LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("save.user.item.in.notification",outputFile.getParentFile().getPath())));
                LOGGER.info("User text will saved to {}", outputFile.getAbsolutePath());
            } catch (Throwable t) {
                LOGGER.warn("Couldn't save user text", t);
            }
        } else {
            LOGGER.info("Ignored {} action because {} is enabled", this.getClass().getSimpleName(), GlobalRuntimeConfiguration.DISABLE_EXTERNAL_ACTIONS);
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
