/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.ui.common.control.specific.io;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.io.PdfConfig;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.common.pane.specific.cell.SimpleTextListCell;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.DesktopUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.StageUtils;

import static org.lifecompanion.model.impl.constant.LCConstant.URL_PATH_ANDROID_GUIDE;
import static org.lifecompanion.util.javafx.FXControlUtils.createGraphicButton;

public class ExportConfigStage extends Stage implements LCViewInitHelper {

    private static ExportConfigStage instance;

    private ExportConfigStage() {
        initAll();
    }

    public static ExportConfigStage getInstance() {
        if (instance == null) {
            instance = new ExportConfigStage();
        }
        return instance;
    }

    @Override
    public void initUI() {
        // Stage config
        this.setTitle(LCConstant.NAME);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setWidth(600.0);
        this.setHeight(400.0);
        this.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
        StageUtils.fixMaximizedVisualBounds(this);
        this.addEventFilter(KeyEvent.KEY_RELEASED, k -> {
            if (k.getCode() == KeyCode.ESCAPE) {
                k.consume();
                this.hide();
            }
        });

        VBox totalBox = new VBox(5.0);
        totalBox.setPadding(new Insets(5.0, 8.0, 5.0, 8.0));
        final Scene exportConfigScene = new Scene(totalBox);
        exportConfigScene.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        SystemVirtualKeyboardController.INSTANCE.registerScene(exportConfigScene);

        this.setScene(exportConfigScene);

        // CONFIG FILE EXPORT
        Label labelPartExportConfigFile = FXControlUtils.createTitleLabel(Translation.getText("config.export.dialog.part.config.file"));
        final Node exportDesktopConfigFileAction = FXControlUtils.createActionTableEntry("config.export.dialog.export.lcc.button",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.DESKTOP).size(25).color(LCGraphicStyle.MAIN_DARK),
                () -> {
                    this.hide();
                    ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.ExportEditAction(totalBox, false));
                });
        final Node exportMobileConfigFileAction = FXControlUtils.createActionTableEntry("config.export.dialog.export.lccm.button",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TABLET).size(30).color(LCGraphicStyle.MAIN_DARK),
                () -> {
                    this.hide();
                    ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.ExportEditAction(totalBox, true));
                });
        Hyperlink linkOpenMobileDocumentation = new Hyperlink(Translation.getText("config.export.dialog.export.lccm.documentation.link"));
        linkOpenMobileDocumentation.getStyleClass().addAll("padding-0");
        VBox.setMargin(linkOpenMobileDocumentation, new Insets(-25.0, 0, 0, 0));
        linkOpenMobileDocumentation.setOnAction(e -> {
            DesktopUtils.openUrlInDefaultBrowser(InstallationController.INSTANCE.getBuildProperties()
                    .getAppServerUrl() + URL_PATH_ANDROID_GUIDE);
        });

        // PDF EXPORT
        Label labelPartExportPdf = FXControlUtils.createTitleLabel(Translation.getText("config.export.dialog.part.pdf.file"));
        Label labelPdfDescription = new Label(Translation.getText("configuration.selection.print.grids.pdf.configuration.button.description"));
        labelPdfDescription.setWrapText(true);
        Label labelPageSize = new Label(Translation.getText("pdf.config.export.dialog.page.size"));
        labelPageSize.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelPageSize, Priority.ALWAYS);
        ComboBox<PdfPageSize> comboBoxPageSize = new ComboBox<>(FXCollections.observableArrayList(PdfPageSize.values()));
        comboBoxPageSize.setMinWidth(250);
        comboBoxPageSize.setButtonCell(new SimpleTextListCell<>(Enum::name));
        comboBoxPageSize.setCellFactory(lv -> new SimpleTextListCell<>(Enum::name));
        comboBoxPageSize.getSelectionModel().select(PdfPageSize.A4);

        ToggleSwitch toggleSwitchFooterHeader = FXControlUtils.createToggleSwitch("pdf.config.export.dialog.enable.footer.header", null);
        toggleSwitchFooterHeader.setSelected(true);
        GridPane.setMargin(toggleSwitchFooterHeader, new Insets(0, 0, 10.0, 0));

        Button buttonExportPdf = createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.FILE_PDF_ALT).size(25).color(LCGraphicStyle.SECOND_DARK), null);
        buttonExportPdf.setMinWidth(50.0);
        GridPane.setValignment(buttonExportPdf, VPos.TOP);
        buttonExportPdf.setOnAction(e -> {
            this.hide();
            ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.ExportEditGridsToPdfAction(buttonExportPdf, new PdfConfig(comboBoxPageSize.getValue().pdRectangle, toggleSwitchFooterHeader.isSelected())));
        });

        GridPane gridPanePdfPart = new GridPane(GeneralConfigurationStepViewI.GRID_V_GAP, GeneralConfigurationStepViewI.GRID_H_GAP);
        int row = 0;
        gridPanePdfPart.add(labelPageSize, 0, row);
        gridPanePdfPart.add(comboBoxPageSize, 1, row);
        gridPanePdfPart.add(buttonExportPdf, 2, row++, 1, 2);
        gridPanePdfPart.add(toggleSwitchFooterHeader, 0, row++, 2, 1);


        totalBox.getChildren().addAll(labelPartExportConfigFile, exportDesktopConfigFileAction, exportMobileConfigFileAction, linkOpenMobileDocumentation, labelPartExportPdf, labelPdfDescription, gridPanePdfPart);
    }


    enum PdfPageSize {
        LETTER(PDRectangle.LETTER),
        LEGAL(PDRectangle.LEGAL),
        A1(PDRectangle.A1),
        A2(PDRectangle.A2),
        A3(PDRectangle.A3),
        A4(PDRectangle.A4),
        A5(PDRectangle.A5),
        A6(PDRectangle.A6);

        private final PDRectangle pdRectangle;

        PdfPageSize(PDRectangle pdRectangle) {
            this.pdRectangle = pdRectangle;
        }
    }
}

