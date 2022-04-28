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
import org.lifecompanion.util.binding.BindingUtils;
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

    private final ImageView userCompImage;

    public UserCompDetailListCell(Consumer<UserCompDescriptionI> directSelectionCallback) {
        this.setWrapText(true);
        this.setTextAlignment(TextAlignment.CENTER);
        this.setAlignment(Pos.CENTER);
        this.prefWidthProperty().bind(widthProperty().subtract(20));
        this.setWrapText(true);

        this.userCompImage = new ImageView();
        this.userCompImage.setFitHeight(70);
        this.userCompImage.fitWidthProperty().bind(this.widthProperty().subtract(20));
        this.userCompImage.setPreserveRatio(true);
        this.userCompImage.setSmooth(true);

        this.setContentDisplay(ContentDisplay.TOP);
        this.setGraphicTextGap(6);

        this.setOnMouseClicked(me -> {
            UserCompDescriptionI item = this.getItem();
            if (me.getClickCount() > 1 && item != null) {
                directSelectionCallback.accept(item);
            }
        });
    }

    @Override
    protected void updateItem(final UserCompDescriptionI itemP, final boolean emptyP) {
        super.updateItem(itemP, emptyP);
        if (itemP == null || emptyP) {
            BindingUtils.unbindAndSetNull(userCompImage.imageProperty());
            BindingUtils.unbindAndSetNull(textProperty());
            this.setGraphic(null);
        } else {
            itemP.requestImageLoad();
            this.userCompImage.imageProperty().bind(itemP.componentImageProperty());
            this.textProperty().bind(itemP.nameProperty());
            this.setGraphic(userCompImage);
        }
    }
}
