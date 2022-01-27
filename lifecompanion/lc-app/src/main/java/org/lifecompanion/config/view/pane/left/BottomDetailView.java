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

package org.lifecompanion.config.view.pane.left;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.control.refacto.AppModeController;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.view.scene.ConfigurationScene;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * Bottom part of the menu, just contains a single button to collapse menu
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class BottomDetailView extends HBox implements LCViewInitHelper {
    private Button buttonCollapse;

    public BottomDetailView() {
        this.initAll();
    }

    @Override
    public void initUI() {
        //Style
        this.getStyleClass().addAll("main-menu-section", "main-menu-section-bottom");
        //Action
        this.buttonCollapse = UIUtils.createTextButtonWithGraphics(null,
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_LEFT).sizeFactor(1).color(LCGraphicStyle.MAIN_PRIMARY),
                "tooltip.main.menu.collapse");
        //Total
        this.getChildren().addAll(this.buttonCollapse);
    }

    @Override
    public void initListener() {
        this.buttonCollapse.setOnAction((me) -> {
            ConfigurationScene scene = (ConfigurationScene) AppModeController.INSTANCE.getEditModeContext().getStage().getScene();
            scene.hideMenu();
        });
    }

    @Override
    public void initBinding() {
    }
}
