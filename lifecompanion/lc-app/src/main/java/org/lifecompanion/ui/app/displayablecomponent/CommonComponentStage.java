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

package org.lifecompanion.ui.app.displayablecomponent;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;

public class CommonComponentStage extends Stage {
    private static CommonComponentStage instance;

    private final CommonComponentView commonComponentView;

    public CommonComponentStage() {
        this.setTitle(LCConstant.NAME);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setWidth(400);
        this.setHeight(400.0);
        this.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        commonComponentView = new CommonComponentView();
        commonComponentView.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        final Scene commonComponentScene = new Scene(commonComponentView);
        SystemVirtualKeyboardController.INSTANCE.registerScene(commonComponentScene);
        SessionStatsController.INSTANCE.registerScene(commonComponentScene);
        this.setScene(commonComponentScene);
        this.addEventFilter(KeyEvent.KEY_RELEASED, k -> {
            if (k.getCode() == KeyCode.ESCAPE) {
                k.consume();
                this.hide();
            }
        });
        this.setOnShown(e -> commonComponentView.show(SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().get()));
        this.setOnHiding(e -> commonComponentView.hide());
        this.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
    }

    public static CommonComponentStage getInstance() {
        if (instance == null) {
            instance = new CommonComponentStage();
        }
        return instance;
    }
}
