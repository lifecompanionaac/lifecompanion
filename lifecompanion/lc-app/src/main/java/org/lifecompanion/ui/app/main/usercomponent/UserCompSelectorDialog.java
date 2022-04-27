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

package org.lifecompanion.ui.app.main.usercomponent;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.StageStyle;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.impl.constant.LCConstant;

public class UserCompSelectorDialog extends Dialog<UserCompDescriptionI> implements LCViewInitHelper {
    public static final double USERCOMP_DIALOG_WIDTH = 600, USERCOMP_DIALOG_HEIGHT = 500;

    private static UserCompSelectorDialog instance;

    private UserCompSelectManageView userCompSelectManageView;

    private UserCompSelectorDialog() {
        initAll();
    }

    public static UserCompSelectorDialog getInstance() {
        if (instance == null) {
            instance = new UserCompSelectorDialog();
        }
        return instance;
    }

    @Override
    public void initUI() {
        // Dialog config
        this.setTitle(LCConstant.NAME);
        this.initStyle(StageStyle.UTILITY);

        // Content
        userCompSelectManageView = new UserCompSelectManageView();

        // Dialog content
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        this.getDialogPane().setContent(userCompSelectManageView);
        this.getDialogPane().getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? userCompSelectManageView.getSelectedUserCompDescription() : null);
    }

    @Override
    public void initListener() {
        this.setOnHidden(e -> userCompSelectManageView.unbind());
    }

    public UserCompDescriptionI showWithFilter(Class<? extends DisplayableComponentI> type) {
        if (this.getResult() != null) this.setResult(null);
        this.userCompSelectManageView.bindAndFilterOn(type, this::setResult);
        return this.showAndWait().orElse(null);
    }
}

