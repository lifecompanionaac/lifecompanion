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

package org.lifecompanion.ui.app.categorizedelement;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;

public class CategorizedContentScene extends Scene {
    private final BorderPane borderPane;

    public CategorizedContentScene() {
        super(new BorderPane());
        this.borderPane = (BorderPane) this.getRoot();
        this.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        SystemVirtualKeyboardController.INSTANCE.registerScene(this);
        SessionStatsController.INSTANCE.registerScene(this);
    }

    public void setContentNode(Node content) {
        this.borderPane.setCenter(content);
    }
}
