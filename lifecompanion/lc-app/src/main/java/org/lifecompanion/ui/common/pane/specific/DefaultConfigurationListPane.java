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

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.util.javafx.FXControlUtils;

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
        Label labelDefaultConfiguration = FXControlUtils.createTitleLabel("profile.edition.general.default.configuration.title");
        Label labelExplain = new Label(Translation.getText("profile.edition.general.default.configuration.explain"));
        labelExplain.setMinHeight(60.0);
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
        this.setSpacing(8.0);
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


    public void clearConfigurationImages() {
        this.gridPaneDefaultConfigurations.getChildren().clear();
    }

    public void initDefaultConfigurations() {
        final int colSpanImg = 2;
        if (this.gridPaneDefaultConfigurations.getChildren().isEmpty()) {
            ProfileConfigSelectionController.INSTANCE.getDefaultConfiguration(defaultConfigurations -> {
                int rowIndex = 0;
                for (Pair<String, List<Pair<LCConfigurationDescriptionI, File>>> defaultConfigurationList : defaultConfigurations) {

                    Label labelConfigType = new Label(defaultConfigurationList.getLeft());
                    labelConfigType.getStyleClass().addAll("text-h3", "text-fill-gray", "text-label-center");
                    labelConfigType.setMaxWidth(Double.MAX_VALUE);
                    GridPane.setHgrow(labelConfigType, Priority.ALWAYS);
                    GridPane.setMargin(labelConfigType, new Insets(rowIndex == 0 ? 5 : 30, 0, 0, 0));
                    final Separator separatorConfigType = new Separator(Orientation.HORIZONTAL);
                    GridPane.setMargin(separatorConfigType, new Insets(5.0, 30.0, 5.0, 30.0));
                    gridPaneDefaultConfigurations.add(labelConfigType, 0, rowIndex++, colSpanImg + 2, 1);
                    gridPaneDefaultConfigurations.add(separatorConfigType, 0, rowIndex++, colSpanImg + 2, 1);

                    for (Pair<LCConfigurationDescriptionI, File> defaultConfiguration : defaultConfigurationList.getRight()) {
                        LCConfigurationDescriptionI configDescription = defaultConfiguration.getLeft();

                        // Config image
                        ImageView imageViewInList = new ImageView();
                        imageViewInList.setFitWidth(300);
                        imageViewInList.setFitHeight(200);
                        imageViewInList.setPreserveRatio(true);
                        configDescription.requestImageLoad(imageViewInList::setImage);
                        GridPane.setHalignment(imageViewInList, HPos.RIGHT);
                        GridPane.setHgrow(imageViewInList, Priority.ALWAYS);
                        GridPane.setMargin(imageViewInList, new Insets(5.0, 20.0, 5.0, 0.0));

                        // Config title
                        Label labelTitle = new Label(configDescription.configurationNameProperty().get());
                        labelTitle.getStyleClass().addAll("text-fill-primary-dark", "text-h4");
                        GridPane.setHgrow(labelTitle, Priority.ALWAYS);

                        // Config author and description
                        Label labelAuthor = new Label(configDescription.configurationAuthorProperty().get());
                        labelAuthor.getStyleClass().addAll("text-fill-dimgrey", "text-weight-bold");

                        Label labelDescription = new Label(configDescription.configurationDescriptionProperty().get());
                        labelDescription.getStyleClass().add("text-fill-gray");
                        labelDescription.setWrapText(true);
                        labelDescription.prefWidthProperty().bind(gridPaneDefaultConfigurations.widthProperty().multiply(0.60));
                        GridPane.setValignment(labelDescription, VPos.TOP);
                        GridPane.setMargin(labelDescription, new Insets(0, 0, 8.0, 0));

                        Node selectionNode;
                        if (multiSelectMode) {
                            ToggleSwitch toggleEnableConfiguration = new ToggleSwitch();
                            toggleEnableConfiguration.setSelected(true);
                            toggleEnableConfiguration.setTooltip(FXControlUtils.createTooltip(Translation.getText("available.default.configuration.tooltip.toggle.add")));
                            defaultConfigurationToggles.put(toggleEnableConfiguration, defaultConfiguration);
                            selectionNode = toggleEnableConfiguration;
                        } else {
                            final Button selectConfigButton = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(30).color(LCGraphicStyle.MAIN_DARK), null);
                            final EventHandler<Event> eventHandlerSelect = e -> {
                                if (this.onConfigurationSelected != null)
                                    onConfigurationSelected.accept(defaultConfiguration);
                            };
                            selectConfigButton.setOnMouseClicked(eventHandlerSelect);
                            labelTitle.setOnMouseClicked(eventHandlerSelect);
                            labelDescription.setOnMouseClicked(eventHandlerSelect);
                            labelAuthor.setOnMouseClicked(eventHandlerSelect);
                            imageViewInList.setOnMouseClicked(eventHandlerSelect);
                            GridPane.setValignment(selectConfigButton, VPos.CENTER);
                            selectionNode = selectConfigButton;
                        }

                        GridPane.setMargin(selectionNode, new Insets(10.0));
                        GridPane.setValignment(selectionNode, VPos.TOP);
                        gridPaneDefaultConfigurations.add(imageViewInList, 0, rowIndex, colSpanImg, 3);
                        gridPaneDefaultConfigurations.add(labelTitle, colSpanImg, rowIndex);
                        gridPaneDefaultConfigurations.add(labelAuthor, colSpanImg, rowIndex + 1);
                        gridPaneDefaultConfigurations.add(labelDescription, colSpanImg, rowIndex + 2);
                        gridPaneDefaultConfigurations.add(selectionNode, colSpanImg + 1, rowIndex, 1, 3);

                        final Separator separator = new Separator(Orientation.HORIZONTAL);
                        GridPane.setMargin(separator, new Insets(5.0, 30.0, 5.0, 30.0));
                        gridPaneDefaultConfigurations.add(separator, 0, rowIndex + 3, colSpanImg + 2, 1);

                        rowIndex += 5;
                    }
                }
            });
        }
    }
}
