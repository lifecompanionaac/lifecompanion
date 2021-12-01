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
package org.lifecompanion.config.view.pane.tabs.selected.part;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.TilePane;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.api.ui.config.ConfigurationProfileLevelEnum;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.config.data.action.impl.GridActions.ChangeGridHGapAction;
import org.lifecompanion.config.data.action.impl.GridActions.ChangeGridVGapAction;
import org.lifecompanion.config.data.common.LCConfigBindingUtils;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * Pane to configure, the grid gap on the selected component.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridGapRibbonPart extends RibbonBasePart<GridComponentI> implements LCViewInitHelper {
    /**
     * Spinner to configure the gap on grid
     */
    private Spinner<Double> spinnerHGap, spinnerVGap;

    /**
     * Change listener for hgap/vgap properties
     */
    private ChangeListener<Number> changeListenerHGap, changeListenerVGap;

    public GridGapRibbonPart() {
        this.initAll();
    }

    @Override
    public void initUI() {
        this.setTitle(Translation.getText("pane.title.grid.gap"));
        TilePane rows = new TilePane();
        rows.setPrefColumns(1);
        this.spinnerHGap = UIUtils.createDoubleSpinner(1, 150, 2, 1, 75.0);
        UIUtils.createAndAttachTooltip(spinnerHGap, "tooltip.explain.grid.hgap.spinner");
        this.spinnerVGap = UIUtils.createDoubleSpinner(1, 150, 2, 1, 75.0);
        UIUtils.createAndAttachTooltip(spinnerVGap, "tooltip.explain.grid.vgap.spinner");
        Label labelVGap = new Label(Translation.getText("grid.vgap"));
        TilePane.setAlignment(labelVGap, Pos.CENTER);
        Label labelHGap = new Label(Translation.getText("grid.hgap"));
        TilePane.setAlignment(labelHGap, Pos.CENTER);
        rows.getChildren().addAll(labelHGap, this.spinnerHGap, labelVGap, this.spinnerVGap);
        this.setContent(rows);
    }

    /**
     * Set the listener to controls
     */
    @Override
    public void initListener() {
        this.changeListenerHGap = LCConfigBindingUtils.createDoubleSpinnerBinding(this.spinnerHGap, this.model, GridComponentI::hGapProperty,
                ChangeGridHGapAction::new);
        this.changeListenerVGap = LCConfigBindingUtils.createDoubleSpinnerBinding(this.spinnerVGap, this.model, GridComponentI::vGapProperty,
                ChangeGridVGapAction::new);
    }

    @Override
    public void bind(final GridComponentI component) {
        component.hGapProperty().addListener(this.changeListenerHGap);
        component.vGapProperty().addListener(this.changeListenerVGap);
        this.spinnerHGap.getValueFactory().setValue(component.hGapProperty().get());
        this.spinnerVGap.getValueFactory().setValue(component.vGapProperty().get());
    }

    @Override
    public void unbind(final GridComponentI component) {
        component.hGapProperty().removeListener(this.changeListenerHGap);
        component.vGapProperty().removeListener(this.changeListenerVGap);
    }

    @Override
    public void initBinding() {
        SelectionController.INSTANCE.selectedComponentProperty()
                .addListener((observableP, oldValueP, newValueP) -> {
                    if (newValueP instanceof GridComponentI) {
                        this.model.set((GridComponentI) newValueP);
                    } else {
                        this.model.set(null);
                    }
                });
        ConfigUIUtils.bindShowForLevelFrom(this, ConfigurationProfileLevelEnum.NORMAL);
    }
}
