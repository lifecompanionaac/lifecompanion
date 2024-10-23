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

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.lifecompanion.controller.configurationcomponent.UseModeWhiteboardController;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.WhiteboardKeyOption;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.SnapshotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WhiteboardSaveImageAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WhiteboardSaveImageAction.class);

    private String destinationFolder;

    public WhiteboardSaveImageAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.WHITEBOARD;
        this.nameID = "action.save.whiteboard.image.name";
        this.order = 10;
        this.staticDescriptionID = "action.save.whiteboard.image.description";
        this.configIconPath = "miscellaneous/icon_whiteboard_save_image.png";
        this.parameterizableAction = true;
        this.destinationFolder = IOUtils.getDefaultDestinationFolder(Translation.getText("default.whiteboard.image.directory.name")).getPath();
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    public String getDestinationFolder() {
        return destinationFolder;
    }

    public void setDestinationFolder(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_EXTERNAL_ACTIONS)) {
            WhiteboardKeyOption keyOption = UseModeWhiteboardController.INSTANCE.getFirstWhiteboardKeyOption();
            if (keyOption != null && keyOption.getDrawingCanvas() != null) {
                try {
                    File outputFile = IOUtils.getUserUseModeDestination(destinationFolder, Translation.getText("default.whiteboard.image.directory.name"), "png");
                    Image image = FXThreadUtils.runOnFXThreadAndWaitFor(() -> SnapshotUtils.executeSnapshot(keyOption.getDrawingCanvas(), 1920, 1080, true, 1.0));
                    BufferedImage buffImage = SwingFXUtils.fromFXImage(image, null);
                    ImageIO.write(buffImage, "png", outputFile);
                    LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("save.user.item.in.notification", outputFile.getParentFile().getPath())));
                } catch (Throwable t) {
                    LOGGER.warn("Couldn't whiteboard to image", t);
                }
            }
        }
    }
}
