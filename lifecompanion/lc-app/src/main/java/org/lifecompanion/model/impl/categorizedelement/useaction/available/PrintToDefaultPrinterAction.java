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

import javafx.print.*;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.WriterDisplayerI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerLineI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.model.impl.textcomponent.TextDisplayerLineHelper;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.ui.configurationcomponent.base.TextDisplayer;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Action to print the editor text on the default action.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PrintToDefaultPrinterAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrintToDefaultPrinterAction.class);

    /**
     * Delay before taking a new print into account (30 seconds)
     */
    // TODO : user should be able to configure it
    private static final long DELAY_BETWEEN_TWO_PRINT_ACTION = 1000 * 30;

    private transient long lastPrint;

    public PrintToDefaultPrinterAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.COMPUTER_FEATURES;
        this.nameID = "action.print.on.default.printer.name";
        this.order = 10;
        this.staticDescriptionID = "action.print.on.default.printer.description";
        this.configIconPath = "computeraccess/icon_print_action.png";
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    // Class part : "Execute"
    // ========================================================================
    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_EXTERNAL_ACTIONS)) {
            if (System.currentTimeMillis() - lastPrint >= DELAY_BETWEEN_TWO_PRINT_ACTION) {
                this.lastPrint = System.currentTimeMillis();
                try {
                    // Get default printer
                    Printer defaultPrinter = Printer.getDefaultPrinter();
                    if (defaultPrinter != null) {
                        // Create layout
                        PageLayout pageLayout = defaultPrinter.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
                        PrinterJob printerJob = PrinterJob.createPrinterJob(defaultPrinter);
                        printerJob.getJobSettings().setJobName("LifeCompanion");
                        if (printerJob != null) {
                            WriterDisplayerI writer = WritingStateController.INSTANCE.getReferencedTextEditor();
                            if (writer != null) {
                                // Initialize a text displayer
                                TextDisplayer textDisplayer = TextDisplayer.toPrint(writer, pageLayout.getPrintableWidth());
                                List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(WritingStateController.INSTANCE,
                                        writer,
                                        writer.getTextDisplayerTextStyle(),
                                        pageLayout.getPrintableWidth());

                                // Compute page count
                                List<List<TextDisplayerLineI>> linesPerPage = new ArrayList<>();
                                List<TextDisplayerLineI> currentPage = createPageAndAddIt(linesPerPage);
                                for (TextDisplayerLineI line : lines) {
                                    double currentPageHeight = currentPage.stream().mapToDouble(l -> getLineHeight(writer, l)).sum();
                                    double lineHeight = getLineHeight(writer, line);
                                    if (currentPageHeight + lineHeight > pageLayout.getPrintableHeight()) {
                                        currentPage = createPageAndAddIt(linesPerPage);
                                    }
                                    currentPage.add(line);
                                }

                                // Try to print each page
                                for (List<TextDisplayerLineI> lineForPage : linesPerPage) {
                                    textDisplayer.manualRepaint(lineForPage);
                                    boolean success = printerJob.printPage(textDisplayer);
                                    LOGGER.info("Page printed successfully : {}", success);
                                }
                                boolean endJob = printerJob.endJob();
                                LOGGER.info("Printer job ended successfully : {}", endJob);
                                if (!endJob) {
                                    lastPrint = 0;
                                }
                                LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("print.use.action.finished"));
                            } else {
                                throw new NullPointerException("No printer job");
                            }
                        }
                    }
                } catch (Throwable e) {
                    PrintToDefaultPrinterAction.LOGGER.error("Error while trying to print", e);
                    LCNotificationController.INSTANCE.showNotification(LCNotification.createError("print.use.action.failed"));
                    lastPrint = 0;
                }
            } else {
                LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("print.use.action.should.wait",
                        (DELAY_BETWEEN_TWO_PRINT_ACTION - (System.currentTimeMillis() - lastPrint)) / 1000)));
                LOGGER.warn("Last print was execute {} ms ago, didn't launch a new print", (System.currentTimeMillis() - lastPrint));
            }
        } else {
            LOGGER.info("Ignored {} action because {} is enabled", this.getClass().getSimpleName(), GlobalRuntimeConfiguration.DISABLE_EXTERNAL_ACTIONS);
        }
    }

    private static List<TextDisplayerLineI> createPageAndAddIt(List<List<TextDisplayerLineI>> linesPerPage) {
        List<TextDisplayerLineI> currentPage = new ArrayList<>();
        linesPerPage.add(currentPage);
        return currentPage;
    }

    private static double getLineHeight(WriterDisplayerI writer, TextDisplayerLineI line) {
        return line.getTextHeight() + line.getImageHeight(writer) + writer.lineSpacingProperty().get();
    }
    // ========================================================================

}
