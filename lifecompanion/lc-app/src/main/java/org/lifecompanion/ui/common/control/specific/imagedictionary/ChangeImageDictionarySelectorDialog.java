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

package org.lifecompanion.ui.common.control.specific.imagedictionary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.common.pane.specific.cell.SimpleTextListCell;
import org.lifecompanion.ui.common.pane.specific.cell.TitleAndDescriptionListCell;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.model.CountingMap;
import org.lifecompanion.util.model.ImageDictionaryUtils;

public class ChangeImageDictionarySelectorDialog extends Dialog<Pair<ImageDictionaryI, ImageDictionaryI>> implements LCViewInitHelper {


    private final LCConfigurationI configuration;

    public ChangeImageDictionarySelectorDialog(LCConfigurationI configuration) {
        this.configuration = configuration;
        initAll();
    }


    @Override
    public void initUI() {
        // Init data
        ObservableList<ImageDictionaryI> dictionaryList = FXCollections.observableArrayList(ImageDictionaries.INSTANCE.getDictionaries());
        CountingMap<ImageDictionaryI> counts = new CountingMap<>();
        ImageDictionaryUtils.forEachImageUseComponentWithImage(configuration, (comp, image) -> {
            if (image.getDictionary() != null)
                counts.increment(image.getDictionary());
        });

        // Dialog config
        this.setTitle(LCConstant.NAME);
        this.initStyle(StageStyle.UTILITY);
        this.setWidth(500);
        this.setHeight(370);

        // Content
        Label labelExplain = new Label(Translation.getText("image.dictionary.change.dictionary.explain"));
        labelExplain.getStyleClass().addAll("text-font-italic", "text-fill-gray", "text-wrap-enabled");

        Label labelSource = new Label(Translation.getText("image.dictionary.change.dictionary.source"));
        labelSource.getStyleClass().addAll("text-weight-bold");
        ComboBox<ImageDictionaryI> comboBoxSource = createCombobox(dictionaryList, counts);

        ToggleSwitch toggleSwitchNoSourceFilter = FXControlUtils.createToggleSwitch("image.dictionary.change.dictionary.no.source.filter", null);
        GridPane.setMargin(toggleSwitchNoSourceFilter, new Insets(0, 0, 10.0, 0));
        labelSource.disableProperty().bind(comboBoxSource.disabledProperty());
        comboBoxSource.disableProperty().bind(toggleSwitchNoSourceFilter.selectedProperty());

        Label labelDestination = new Label(Translation.getText("image.dictionary.change.dictionary.destination"));
        labelDestination.getStyleClass().addAll("text-weight-bold");
        ComboBox<ImageDictionaryI> comboBoxReplacing = createCombobox(dictionaryList, null);

        GridPane gridPaneContent = new GridPane();
        gridPaneContent.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        gridPaneContent.setVgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        int row = 0;
        gridPaneContent.add(labelExplain, 0, row++);
        gridPaneContent.add(labelSource, 0, row++);
        gridPaneContent.add(comboBoxSource, 0, row++);
        gridPaneContent.add(toggleSwitchNoSourceFilter, 0, row++);
        gridPaneContent.add(labelDestination, 0, row++);
        gridPaneContent.add(comboBoxReplacing, 0, row++);

        // Dialog content
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        this.getDialogPane().setHeaderText(Translation.getText("image.dictionary.change.dictionary.header"));
        this.getDialogPane().setContent(gridPaneContent);
        this.getDialogPane().getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? new Pair<>(toggleSwitchNoSourceFilter.isSelected() ? null : comboBoxSource.getValue(),
                comboBoxReplacing.getValue()) : null);
    }

    private static ComboBox<ImageDictionaryI> createCombobox(ObservableList<ImageDictionaryI> dictionaryList, CountingMap<ImageDictionaryI> counts) {
        ComboBox<ImageDictionaryI> comboBox = new ComboBox<>(dictionaryList);
        comboBox.setPrefWidth(485);
        comboBox.setButtonCell(new ImageDictionaryListCell());
        comboBox.setCellFactory(counts != null ? lv -> new ImageDictionaryWithCountListCell(counts) : lv -> new ImageDictionaryListCell());
        return comboBox;
    }

    private static class ImageDictionaryWithCountListCell extends TitleAndDescriptionListCell<ImageDictionaryI> {
        public ImageDictionaryWithCountListCell(CountingMap<ImageDictionaryI> counts) {
            super(ImageDictionaryI::getName, d -> Translation.getText("image.dictionary.change.dictionary.usage.count", counts.getCount(d)));
        }
    }

    private static class ImageDictionaryListCell extends SimpleTextListCell<ImageDictionaryI> {
        public ImageDictionaryListCell() {
            super(ImageDictionaryI::getName);
        }
    }
}

