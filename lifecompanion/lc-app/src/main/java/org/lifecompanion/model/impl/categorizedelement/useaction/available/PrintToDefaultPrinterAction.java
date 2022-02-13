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
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.lifecompanion.model.api.configurationcomponent.WriterDisplayerI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.util.LangUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                        // Dirty fix : always print only one page, but with the current text flow we can't compute the real wrapped height
                        Node toPrint = this.getNodeToPrint(pageLayout.getPrintableWidth());
                        if (toPrint != null) {
                            boolean success = printerJob.printPage(toPrint);
                            boolean endJob = printerJob.endJob();
                            PrintToDefaultPrinterAction.LOGGER.info("Printing job end : success {}, finished {}", success, endJob);
                            if (!success) {
                                lastPrint = 0;
                            }
                        }
                        // TODO : find a way to notify user that printing started
                    }
                }
            } catch (Throwable e) {
                PrintToDefaultPrinterAction.LOGGER.error("Error while trying to print", e);
                lastPrint = 0;
            }
        } else {
            LOGGER.warn("Last print was execute {} ms ago, didn't launch a new print", (System.currentTimeMillis() - lastPrint));
        }
    }
    // ========================================================================

    // Class part : "Utils"
    // ========================================================================
    private Node getNodeToPrint(final double width) {
        WriterDisplayerI writer = WritingStateController.INSTANCE.getReferencedTextEditor();
        if (writer != null) {
            final String text = WritingStateController.INSTANCE.currentTextProperty().get();
            Text textEntry = new Text(
                    LangUtils.isTrue(writer.getTextDisplayerTextStyle().upperCaseProperty().value().getValue()) ? StringUtils.toUpperCase(text) : text);
            TextCompStyleI textStyle = writer.getTextDisplayerTextStyle();
            textEntry.setFont(textStyle.fontProperty().get());
            textEntry.setFill(textStyle.colorProperty().value().getValue());

            TextFlow textFlow = new TextFlow(textEntry);
            textFlow.setLineSpacing(writer.lineSpacingProperty().get());
            textFlow.setPrefWidth(width);

            return textFlow;
        }
        return null;
    }
    // ========================================================================
}
