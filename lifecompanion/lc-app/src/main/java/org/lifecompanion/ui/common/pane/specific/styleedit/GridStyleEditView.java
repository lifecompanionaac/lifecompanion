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
package org.lifecompanion.ui.common.pane.specific.styleedit;

import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import org.controlsfx.control.ToggleSwitch;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.style.GridCompStyleI;
import org.lifecompanion.util.binding.EditActionUtils;
import org.lifecompanion.util.javafx.FXControlUtils;

public class GridStyleEditView extends AbstractShapeStyleEditView<GridCompStyleI> {
    private Spinner<Integer> spinnerHGap, spinnerVGap;
    private ChangeListener<Number> changeListenerHGap, changeListenerVGap;

    public GridStyleEditView() {
        super(true);
    }

    @Override
    public void initUI() {
        super.initUI();

        this.spinnerHGap = FXControlUtils.createIntSpinner(1, 150, 2, 1, 75.0);
        FXControlUtils.createAndAttachTooltip(spinnerHGap, "tooltip.explain.grid.hgap.spinner");
        GridPane.setHalignment(this.spinnerHGap, HPos.RIGHT);
        this.spinnerVGap = FXControlUtils.createIntSpinner(1, 150, 2, 1, 75.0);
        FXControlUtils.createAndAttachTooltip(spinnerVGap, "tooltip.explain.grid.vgap.spinner");
        GridPane.setHalignment(this.spinnerVGap, HPos.RIGHT);

        this.fieldGrid.add(new Label(Translation.getText("field.grid.key.hgap")), 0, 4);
        this.fieldGrid.add(spinnerHGap, 1, 4);
        this.fieldGrid.add(this.createModifiedIndicator(GridCompStyleI::hGapProperty, spinnerHGap), 2, 4);

        this.fieldGrid.add(new Label(Translation.getText("field.grid.key.vgap")), 0, 5);
        this.fieldGrid.add(spinnerVGap, 1, 5);
        this.fieldGrid.add(this.createModifiedIndicator(GridCompStyleI::vGapProperty, spinnerVGap), 2, 5);
    }

    @Override
    public void initListener() {
        super.initListener();
        if (bindOnModel) {
            this.changeListenerHGap = EditActionUtils.createIntegerSpinnerBinding(this.spinnerHGap, this.model,
                    g -> g.hGapProperty().value(), (m, nv) -> this.createChangePropAction(m.hGapProperty(), nv));
            this.changeListenerVGap = EditActionUtils.createIntegerSpinnerBinding(this.spinnerVGap, this.model,
                    g -> g.vGapProperty().value(), (m, nv) -> this.createChangePropAction(m.vGapProperty(), nv));
        }
    }


    @Override
    public void bind(final GridCompStyleI model) {
        super.bind(model);
        if (bindOnModel) {
            this.spinnerHGap.getValueFactory().setValue(model.hGapProperty().value().getValue().intValue());
            this.spinnerVGap.getValueFactory().setValue(model.vGapProperty().value().getValue().intValue());
            model.hGapProperty().value().addListener(this.changeListenerHGap);
            model.vGapProperty().value().addListener(this.changeListenerVGap);
        }
    }

    @Override
    public void unbind(final GridCompStyleI model) {
        super.unbind(model);
        if (bindOnModel) {
            model.hGapProperty().value().removeListener(this.changeListenerHGap);
            model.vGapProperty().value().removeListener(this.changeListenerVGap);
        }
    }
}
