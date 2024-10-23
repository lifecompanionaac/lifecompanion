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

import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.configurationcomponent.ComponentHolderById;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.ui.UseModeScene;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.DesktopUtils;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.SnapshotUtils;
import org.lifecompanion.util.pdf.DocumentConfiguration;
import org.lifecompanion.util.pdf.DocumentImagePage;
import org.lifecompanion.util.pdf.PdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SaveConfigToPdfAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveConfigToPdfAction.class);

    private String destinationFolder;

    public SaveConfigToPdfAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.COMPUTER_FEATURES;
        this.nameID = "action.save.config.pdf.name";
        this.order = 10;
        this.staticDescriptionID = "action.save.config.pdf.description";
        this.configIconPath = "computeraccess/icon_print_config_pdf_action.png";
        this.parameterizableAction = true;
        this.destinationFolder = IOUtils.getDefaultDestinationFolder(Translation.getText("default.user.grid.pdf.directory.name")).getPath();
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
            try {
                PDRectangle pageSize = PDRectangle.A4;

                // Prepare output file
                File outputFile = IOUtils.getUserUseModeDestination(destinationFolder, Translation.getText("default.user.grid.pdf.directory.name"), "pdf");

                // Get config component
                UseModeScene scene = (UseModeScene) AppModeController.INSTANCE.getUseModeContext().getStage().getScene();
                File imageFile = IOUtils.getTempFile("config-images", ".png");

                // Snapshot + temp save
                Image image = FXThreadUtils.runOnFXThreadAndWaitFor(() -> SnapshotUtils.executeSnapshot(scene.getConfigurationDisplayer(), pageSize.getHeight() * 2, pageSize.getWidth() * 2, true, 1.0));
                BufferedImage buffImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(buffImage, "png", imageFile);

                final String profileName = ProfileController.INSTANCE.currentProfileProperty().get() != null ? ProfileController.INSTANCE.currentProfileProperty().get().nameProperty().get() : "PROFILE?";
                final String configName = AppModeController.INSTANCE.getUseModeContext().getConfigurationDescription() != null ? AppModeController.INSTANCE.getUseModeContext().getConfigurationDescription().configurationNameProperty().get() : "CONFIGURATION?";

                // Create and save PDF
                LocalDate now = LocalDate.now();
                String fullDate = now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + now.getDayOfMonth() + " " + now.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + now.getYear();
                PdfUtils.createPdfDocument(new DocumentConfiguration(AppModeController.INSTANCE.getUseModeContext().getConfiguration().backgroundColorProperty().get(), pageSize, profileName, configName, "action.save.config.pdf.document.name"),
                        outputFile,
                        List.of(new DocumentImagePage(Translation.getText("action.save.config.pdf.page.name", fullDate, UseVariableController.DATE_ONLY_HOURS_MIN.format(new Date())), imageFile, true))
                );
                LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("save.user.item.in.notification", outputFile.getParentFile().getPath())));
            } catch (Throwable t) {
                LOGGER.warn("Couldn't save config to PDF", t);
            }
        }
    }
}
