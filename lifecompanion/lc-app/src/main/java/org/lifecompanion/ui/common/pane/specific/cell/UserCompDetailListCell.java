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
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editaction.PluginActions;
import org.lifecompanion.controller.editaction.UserCompActions.EditUserCompAction;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.DragController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.task.UserCompLoadingTask;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.ui.app.main.usercomponent.UserCompSelectManageView;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * List cell for {@link UserCompDescriptionI}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserCompDetailListCell extends ListCell<UserCompDescriptionI> {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserCompDetailListCell.class);

    private Button buttonEdit, buttonRemove;
    private final ImageView userCompImage;
    private final Label labelName;
    private StackPane stackPane;

    public UserCompDetailListCell(UserCompSelectManageView userCompSelectManageView) {
        this.getStyleClass().addAll("background-transparent", "soft-selection-cell");
        //Base content
        this.labelName = new Label();
        this.labelName.prefWidthProperty().bind(this.widthProperty().subtract(40));
        this.labelName.setWrapText(true);
        this.labelName.setTextAlignment(TextAlignment.CENTER);
        this.labelName.setAlignment(Pos.CENTER);
        this.labelName.getStyleClass().addAll("text-h4", "text-fill-dimgrey");

        this.buttonEdit = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_MATERIAL.create('\uE254').size(22).color(LCGraphicStyle.MAIN_PRIMARY), "tooltip.user.comp.edit");
        this.buttonRemove = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH_ALT).size(23).color(LCGraphicStyle.SECOND_PRIMARY), "tooltip.user.comp.remove");
        VBox boxButtons = new VBox(5.0, buttonEdit, buttonRemove);
        boxButtons.setAlignment(Pos.CENTER_RIGHT);

        this.userCompImage = new ImageView();
        this.userCompImage.setFitHeight(75);
        this.userCompImage.fitWidthProperty().bind(this.widthProperty().subtract(40));
        this.userCompImage.setPreserveRatio(true);
        this.userCompImage.setSmooth(true);

        stackPane = new StackPane(userCompImage, this.labelName, boxButtons);
        StackPane.setAlignment(labelName, Pos.BOTTOM_CENTER);
        stackPane.setPadding(new Insets(2.0));
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        //Double click : edit
        this.setOnMouseClicked(me -> {
            UserCompDescriptionI item = this.getItem();
            if (item != null) {
                userCompSelectManageView.selected(item);
            }
        });
        this.buttonEdit.setOnAction(e -> {
            UserCompDescriptionI item = this.getItem();
            if (item != null) {
                ConfigActionController.INSTANCE.executeAction(new EditUserCompAction(this, item));
            }
        });
        this.buttonRemove.setOnAction(e -> {
            UserCompDescriptionI item = this.getItem();
            if (item != null) {
                userCompSelectManageView.remove(item);
            }
        });
    }

    @Override
    protected void updateItem(final UserCompDescriptionI itemP, final boolean emptyP) {
        super.updateItem(itemP, emptyP);
        if (itemP == null || emptyP) {
            this.userCompImage.imageProperty().unbind();
            this.userCompImage.imageProperty().set(null);
            this.labelName.textProperty().unbind();
            this.setGraphic(null);
        } else {
            itemP.requestImageLoad();
            this.userCompImage.imageProperty().bind(itemP.componentImageProperty());
            this.labelName.textProperty().bind(itemP.nameProperty());
            this.setGraphic(this.stackPane);
        }
    }
}
