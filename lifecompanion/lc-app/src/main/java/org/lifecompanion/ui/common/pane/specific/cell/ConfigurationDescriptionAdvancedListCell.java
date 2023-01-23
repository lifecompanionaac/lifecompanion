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
package org.lifecompanion.ui.common.pane.specific.cell;

import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigStep;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.function.Consumer;

/**
 * List cell to display a configuration description.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ConfigurationDescriptionAdvancedListCell extends ListCell<LCConfigurationDescriptionI> implements LCViewInitHelper {
    /**
     * Image view to see configuration preview
     */
    private ImageView configurationImage;

    private Label labelConfigName;
    private Label labelConfigDescription;
    private Label labelConfigAuthor;
    private Label labelConfigDate;
    private GridPane gridPaneContent;
    private Button buttonSelect, buttonEdit;
    private Consumer<LCConfigurationDescriptionI> selectionCallback;

    public ConfigurationDescriptionAdvancedListCell(Consumer<LCConfigurationDescriptionI> selectionCallback) {
        this.selectionCallback = selectionCallback;
        this.initAll();
    }


    @Override
    public void initUI() {
        this.getStyleClass().add("list-cell-selection-disabled");
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        //Title
        this.labelConfigName = new Label();
        this.labelConfigName.getStyleClass().add("configuration-and-profile-title");
        this.labelConfigName.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelConfigName, Priority.ALWAYS);

        // Center : information and buttons
        this.labelConfigAuthor = new Label();
        this.labelConfigAuthor.setStyle("-fx-text-fill: gray");
        GridPane.setHgrow(labelConfigAuthor, Priority.ALWAYS);

        labelConfigDescription = new Label();
        labelConfigDescription.setWrapText(true);
        labelConfigDescription.setMaxWidth(450.0);
        this.labelConfigDescription.setStyle("-fx-text-fill: gray");
        GridPane.setHgrow(labelConfigDescription, Priority.ALWAYS);
        GridPane.setVgrow(labelConfigDescription, Priority.ALWAYS);

        this.labelConfigDate = new Label();
        this.labelConfigDate.setStyle("-fx-text-fill: gray");
        GridPane.setHgrow(labelConfigDate, Priority.ALWAYS);

        GridPane gridPaneConfigInfo = new GridPane();
        gridPaneConfigInfo.setVgap(5.0);
        gridPaneConfigInfo.setHgap(10.0);
        gridPaneConfigInfo.add(labelConfigDescription, 0, 0, 2, 1);
        gridPaneConfigInfo.add(new Label(Translation.getText("configuration.description.cell.author.field")), 0, 1);
        gridPaneConfigInfo.add(labelConfigAuthor, 1, 1);
        gridPaneConfigInfo.add(new Label(Translation.getText("configuration.description.cell.date.field")), 0, 2);
        gridPaneConfigInfo.add(labelConfigDate, 1, 2);
        GridPane.setHgrow(gridPaneConfigInfo, Priority.ALWAYS);


        // Button to select/edit
        this.buttonSelect = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(20).color(LCGraphicStyle.MAIN_DARK),
                "tooltip.config.list.select");
        this.buttonEdit = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).size(20).color(LCGraphicStyle.MAIN_DARK),
                "tooltip.config.list.edit");
        HBox boxButtons = new HBox(10.0, buttonSelect, buttonEdit);
        boxButtons.setAlignment(Pos.CENTER);
        GridPane.setVgrow(boxButtons, Priority.ALWAYS);

        //Label style and positions
        this.configurationImage = new ImageView();
        this.configurationImage.setFitHeight(120.0);
        this.configurationImage.setFitWidth(150.0);
        this.configurationImage.setPreserveRatio(true);
        this.configurationImage.setSmooth(true);
        GridPane.setHalignment(configurationImage, HPos.CENTER);
        GridPane.setValignment(configurationImage, VPos.CENTER);

        gridPaneContent = new GridPane();
        gridPaneContent.setVgap(5.0);
        gridPaneContent.setHgap(5.0);
        gridPaneContent.add(configurationImage, 0, 0, 1, 2);
        gridPaneContent.add(labelConfigName, 1, 0, 2, 1);
        gridPaneContent.add(gridPaneConfigInfo, 1, 1);
        gridPaneContent.add(boxButtons, 2, 1, 1, 1);
        gridPaneContent.getColumnConstraints().addAll(new ColumnConstraints(160.0));
        gridPaneContent.setPadding(new Insets(0.0, 5.0, 5.0, 5.0));
    }

    @Override
    public void initListener() {
        this.setOnMouseClicked((me) -> {
            if (this.selectionCallback != null && me.getClickCount() > 1) {
                this.selectionCallback.accept(this.getItem());
            }
        });
        this.buttonSelect.setOnAction(e -> {
            if (this.getItem() != null) {
                this.selectionCallback.accept(this.getItem());
            }
        });
        this.buttonEdit.setOnAction(e -> {
            if (this.getItem() != null) {
                ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_EDIT, ProfileConfigStep.CONFIGURATION_LIST, this.getItem());
            }
        });
    }

    @Override
    protected void updateItem(final LCConfigurationDescriptionI itemP, final boolean emptyP) {
        super.updateItem(itemP, emptyP);
        if (itemP == null || emptyP) {
            this.configurationImage.setImage(null);
            this.labelConfigName.textProperty().unbind();
            this.labelConfigDescription.textProperty().unbind();
            this.labelConfigDate.textProperty().unbind();
            this.labelConfigAuthor.textProperty().unbind();
            this.setGraphic(null);
        } else {
            itemP.requestImageLoad(image -> {
                if (itemP == getItem()) {
                    this.configurationImage.setImage(image);
                }
            });
            this.labelConfigName.textProperty().bind(Bindings.createStringBinding(
                    () -> itemP.configurationNameProperty().get() + (itemP.launchInUseModeProperty().get() ? (" " + Translation.getText("configuration.launch.in.use.mode.indicator.text")) : ""),
                    itemP.configurationNameProperty(), itemP.launchInUseModeProperty()));
            this.labelConfigDescription.textProperty().bind(
                    Bindings.createStringBinding(() ->
                                    StringUtils.isBlank(itemP.configurationDescriptionProperty().get()) ?
                                            Translation.getText("configuration.empty.description.filler") : itemP.configurationDescriptionProperty().get(), itemP.configurationDescriptionProperty(),
                            itemP.configurationDescriptionProperty()));
            this.labelConfigDate.textProperty().bind(
                    Bindings.createStringBinding(() -> StringUtils.dateToStringDateWithHour(itemP.configurationLastDateProperty().get())
                            , itemP.configurationLastDateProperty()));
            this.labelConfigAuthor.textProperty().bind(itemP.configurationAuthorProperty());
            this.setGraphic(this.gridPaneContent);
        }
    }

}
