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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;

import java.util.HashSet;
import java.util.Set;

/**
 * Stage to display categorized content content.<br>
 * This stage is shared between different views to avoid create a stage per view.
 *
 * @author Mathieu THEBAUD
 */
public class CategorizedContentStage extends Stage {
    private static CategorizedContentStage instance;

    private final CategorizedContentScene categorizedContentScene;
    private final Set<Runnable> onHiddenListeners;

    private CategorizedContentStage() {
        this.setTitle(LCConstant.NAME);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setWidth(AbstractCategorizedListManageView.STAGE_WIDTH);
        this.setHeight(AbstractCategorizedListManageView.STAGE_HEIGHT);
        this.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.onHiddenListeners = new HashSet<>();
        this.categorizedContentScene = new CategorizedContentScene();
        this.setScene(categorizedContentScene);
        this.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
        this.setOnHidden(e -> onHiddenListeners.forEach(Runnable::run));
    }

    public static CategorizedContentStage getInstance() {
        if (instance == null) {
            instance = new CategorizedContentStage();
        }
        return instance;
    }

    public CategorizedContentScene getCategorizedContentScene() {
        return categorizedContentScene;
    }

    public void prepareAndShow(Node source, Node content, Runnable onHiddenListener) {
        onHiddenListeners.add(onHiddenListener);
        if (this.getOwner() == null) {
            this.initOwner(UIUtils.getSourceWindow(source));
        }
        this.getCategorizedContentScene().setContentNode(content);
        this.show();
    }
}
