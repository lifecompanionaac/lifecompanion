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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.common.pane.specific.ProfileIconView;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigStep;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.function.Consumer;

/**
 * A list cell to display profile
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProfileAdvancedListCell extends ListCell<LCProfileI> implements LCViewInitHelper {
    private ProfileIconView iconView;
    private Label labelProfileName;
    private Label labelConfigSize;
    private HBox boxContent;
    private Button buttonSelect, buttonEdit;
    private Consumer<LCProfileI> selectionCallback;

    /**
     * Create a new profile cell
     */
    public ProfileAdvancedListCell(final Consumer<LCProfileI> selectionCallback) {
        this.selectionCallback = selectionCallback;
        this.initAll();
    }

    @Override
    protected void updateItem(final LCProfileI itemP, final boolean emptyP) {
        super.updateItem(itemP, emptyP);
        if (itemP == null || emptyP) {
            this.setGraphic(null);
            this.labelProfileName.textProperty().unbind();
            this.labelConfigSize.textProperty().unbind();
            this.setGraphic(null);
        } else {
            this.setGraphic(this.boxContent);
            this.labelProfileName.textProperty().bind(itemP.nameProperty());
            this.labelConfigSize.textProperty().bind(TranslationFX.getTextBinding("profile.config.count", itemP.configurationCountProperty()));
        }
    }

    @Override
    public void initUI() {
        this.getStyleClass().add("list-cell-selection-disabled");

        //Base content
        this.boxContent = new HBox(10.0);
        this.labelProfileName = new Label();
        this.labelProfileName.getStyleClass().add("configuration-and-profile-title");
        this.labelConfigSize = new Label();
        this.labelConfigSize.setStyle("-fx-text-fill: gray");
        this.iconView = new ProfileIconView();
        this.iconView.setIconSizeFactor(1.3);

        // Button to select/edit
        this.buttonSelect = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(20).color(LCGraphicStyle.MAIN_DARK),
                "tooltip.profile.list.select");
        this.buttonEdit = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).size(20).color(LCGraphicStyle.MAIN_DARK),
                "tooltip.profile.list.edit");

        //Label style and positions
        VBox boxLabel = new VBox(this.labelProfileName, this.labelConfigSize);
        boxLabel.setAlignment(Pos.TOP_LEFT);
        VBox.setMargin(this.labelConfigSize, new Insets(0, 0, 10, 6.0));

        //Add
        this.boxContent.setAlignment(Pos.CENTER);
        this.labelProfileName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(boxLabel, Priority.ALWAYS);

        //Global
        this.boxContent.getChildren().addAll(this.iconView, boxLabel, this.buttonSelect, this.buttonEdit);
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.iconView.profileProperty().bind(this.itemProperty());
    }

    @Override
    public void initListener() {
        this.setOnMouseClicked((me) -> {
            if (this.selectionCallback != null && me.getClickCount() > 1 && this.getItem() != null) {
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
                ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_EDIT, ProfileConfigStep.PROFILE_LIST, this.getItem());
            }
        });
    }
}
