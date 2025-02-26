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
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.model.impl.io.PdfConfig;
import org.lifecompanion.model.impl.selectionmode.SelectionModeEnum;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.common.pane.specific.cell.SimpleTextListCell;
import org.lifecompanion.ui.common.pane.specific.cell.TitleAndDescriptionListCell;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.model.CountingMap;
import org.lifecompanion.util.model.ImageDictionaryUtils;

public class PdfConfigDialog extends Dialog<PdfConfig> implements LCViewInitHelper {

    public PdfConfigDialog() {
        initAll();
    }

    @Override
    public void initUI() {
        // Dialog config
        this.setTitle(LCConstant.NAME);
        this.initStyle(StageStyle.UTILITY);
        this.setWidth(400);
        this.setHeight(250);

        // Content
        Label labelPageSize = new Label(Translation.getText("pdf.config.export.dialog.page.size"));
        labelPageSize.getStyleClass().addAll("text-weight-bold");
        ComboBox<PdfPageSize> comboBoxPageSize = new ComboBox<>(FXCollections.observableArrayList(PdfPageSize.values()));
        comboBoxPageSize.setPrefWidth(380);
        comboBoxPageSize.setButtonCell(new SimpleTextListCell<>(Enum::name));
        comboBoxPageSize.setCellFactory(lv -> new SimpleTextListCell<>(Enum::name));
        comboBoxPageSize.getSelectionModel().select(PdfPageSize.A4);

        ToggleSwitch toggleSwitchFooterHeader = FXControlUtils.createToggleSwitch("pdf.config.export.dialog.enable.footer.header", null);
        toggleSwitchFooterHeader.setSelected(true);
        GridPane.setMargin(toggleSwitchFooterHeader, new Insets(0, 0, 10.0, 0));

        GridPane gridPaneContent = new GridPane();
        gridPaneContent.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        gridPaneContent.setVgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        int row = 0;
        gridPaneContent.add(labelPageSize, 0, row++);
        gridPaneContent.add(comboBoxPageSize, 0, row++);
        gridPaneContent.add(toggleSwitchFooterHeader, 0, row++);

        // Dialog content
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        this.getDialogPane().setHeaderText(Translation.getText("pdf.config.export.dialog.header"));
        this.getDialogPane().setContent(gridPaneContent);
        this.getDialogPane().getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? new PdfConfig(comboBoxPageSize.getValue().pdRectangle, toggleSwitchFooterHeader.isSelected()) : null);
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

