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

package org.lifecompanion.ui.common.pane.specific;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.editmode.ProfileConfigSelectionController;
import org.lifecompanion.controller.resource.LCGlyphFont;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.utils.Pair;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DefaultConfigurationListPane extends VBox implements LCViewInitHelper {
    private final Map<ToggleSwitch, Pair<LCConfigurationDescriptionI, File>> defaultConfigurationToggles;
    private GridPane gridPaneDefaultConfigurations;
    private final boolean multiSelectMode;
    private Consumer<Pair<LCConfigurationDescriptionI, File>> onConfigurationSelected;
    private Hyperlink linkSelectAll, linkUnselectAll;

    public DefaultConfigurationListPane(boolean multiSelectMode) {
        defaultConfigurationToggles = new HashMap<>();
        this.multiSelectMode = multiSelectMode;
        initAll();
    }

    @Override
    public void initUI() {
        // Default configuration to add on profile
        Label labelDefaultConfiguration = UIUtils.createTitleLabel("profile.edition.general.default.configuration.title");
        Label labelExplain = new Label(Translation.getText("profile.edition.general.default.configuration.explain"));
        labelExplain.setMinHeight(100.0);
        labelExplain.getStyleClass().add("explain-text");

        linkUnselectAll = new Hyperlink(Translation.getText("profile.edition.general.default.configuration.link.unselect.all"));
        linkSelectAll = new Hyperlink(Translation.getText("profile.edition.general.default.configuration.link.select.all"));
        HBox boxLinks = new HBox(10.0, linkSelectAll, linkUnselectAll);
        boxLinks.setAlignment(Pos.CENTER_RIGHT);
        boxLinks.setManaged(multiSelectMode);
        boxLinks.setVisible(multiSelectMode);

        gridPaneDefaultConfigurations = new GridPane();
        ScrollPane scrollPaneDefaultConfigurations = new ScrollPane(gridPaneDefaultConfigurations);
        scrollPaneDefaultConfigurations.setFitToWidth(true);
        VBox.setVgrow(scrollPaneDefaultConfigurations, Priority.ALWAYS);
        this.setSpacing(5.0);
        this.getChildren().addAll(labelDefaultConfiguration, labelExplain, boxLinks, new Separator(Orientation.HORIZONTAL), scrollPaneDefaultConfigurations);
    }

    @Override
    public void initListener() {
        linkSelectAll.setOnAction(e -> setOnAllToggleSwitches(true));
        linkUnselectAll.setOnAction(e -> setOnAllToggleSwitches(false));
    }

    private void setOnAllToggleSwitches(boolean selected) {
        for (ToggleSwitch toggleSwitch : defaultConfigurationToggles.keySet()) {
            toggleSwitch.setSelected(selected);
        }
    }

    @Override
    public void initBinding() {
    }

    public List<Pair<LCConfigurationDescriptionI, File>> getSelectedDefaultConfigurations() {
        return defaultConfigurationToggles.entrySet().stream().filter(e -> e.getKey().isSelected()).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public void setOnConfigurationSelected(Consumer<Pair<LCConfigurationDescriptionI, File>> callback) {
        this.onConfigurationSelected = callback;
    }

    public void initDefaultConfigurations() {
        if (this.gridPaneDefaultConfigurations.getChildren().isEmpty()) {
            ProfileConfigSelectionController.INSTANCE.getDefaultConfiguration(defaultConfigurations -> {
                int rowIndex = 0;
                for (Pair<LCConfigurationDescriptionI, File> defaultConfiguration : defaultConfigurations) {
                    LCConfigurationDescriptionI configDescription = defaultConfiguration.getLeft();

                    // Config title
                    Label labelTitle = new Label(configDescription.configurationNameProperty().get());
                    labelTitle.getStyleClass().add("default-configuration-title");
                    GridPane.setHgrow(labelTitle, Priority.ALWAYS);

                    // Config author and description
                    Label labelAuthor = new Label(configDescription.configurationAuthorProperty().get());
                    labelAuthor.getStyleClass().add("default-configuration-author");

                    Label labelDescription = new Label(configDescription.configurationDescriptionProperty().get());
                    labelDescription.getStyleClass().add("default-configuration-description");
                    labelDescription.setWrapText(true);
                    GridPane.setMargin(labelDescription, new Insets(0, 0, 20.0, 0));

                    Node selectionNode;
                    if (multiSelectMode) {
                        ToggleSwitch toggleEnableConfiguration = new ToggleSwitch();
                        toggleEnableConfiguration.setSelected(true);
                        toggleEnableConfiguration.setTooltip(UIUtils.createTooltip(Translation.getText("available.default.configuration.tooltip.toggle.add")));
                        defaultConfigurationToggles.put(toggleEnableConfiguration, defaultConfiguration);
                        selectionNode = toggleEnableConfiguration;
                    } else {
                        final Button selectConfigButton = UIUtils.createGraphicButton(LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(20).color(LCGraphicStyle.MAIN_DARK), null);
                        selectConfigButton.setOnAction(e -> {
                            if (this.onConfigurationSelected != null) onConfigurationSelected.accept(defaultConfiguration);
                        });
                        GridPane.setValignment(selectConfigButton, VPos.CENTER);
                        selectionNode = selectConfigButton;
                    }

                    GridPane.setMargin(selectionNode, new Insets(10.0));
                    GridPane.setValignment(selectionNode, VPos.TOP);
                    gridPaneDefaultConfigurations.add(labelTitle, 0, rowIndex);
                    gridPaneDefaultConfigurations.add(labelAuthor, 0, rowIndex + 1);
                    gridPaneDefaultConfigurations.add(labelDescription, 0, rowIndex + 2);
                    gridPaneDefaultConfigurations.add(selectionNode, 1, rowIndex, 1, 3);

                    Tooltip tooltip = UIUtils.createTooltip(Translation.getText("available.default.configuration.preview.image"));
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(500);
                    imageView.setFitHeight(400);
                    imageView.setPreserveRatio(true);
                    imageView.imageProperty().bind(configDescription.configurationImageProperty());
                    tooltip.setContentDisplay(ContentDisplay.TOP);
                    tooltip.setGraphic(imageView);

                    Tooltip.install(labelTitle, tooltip);
                    Tooltip.install(labelAuthor, tooltip);
                    Tooltip.install(labelDescription, tooltip);

                    final EventHandler<MouseEvent> displayTooltip = e -> tooltip.show((Node) e.getSource(), e.getScreenX(), e.getScreenY());
                    labelTitle.setOnMouseClicked(displayTooltip);
                    labelAuthor.setOnMouseClicked(displayTooltip);
                    labelDescription.setOnMouseClicked(displayTooltip);

                    configDescription.requestImageLoad();

                    rowIndex += 4;
                }
            });
        }
    }
}
