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

package org.lifecompanion.ui.common.pane.generic;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class FittedViewPane extends Region {
    private final ObjectProperty<FittedViewI> fittedView = new SimpleObjectProperty<>();

    private final InvalidationListener invalidationListener;

    public FittedViewPane(final FittedViewI fittedView) {
        this.invalidationListener = (inv) -> this.requestLayout();
        this.fittedView.addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.rotateProperty().removeListener(this.invalidationListener);
                ov.viewportProperty().removeListener(this.invalidationListener);
                FittedViewPane.this.getChildren().remove(ov.getNode());
            }
            if (nv != null) {
                nv.rotateProperty().addListener(this.invalidationListener);
                nv.viewportProperty().addListener(this.invalidationListener);
                FittedViewPane.this.getChildren().add(nv.getNode());
            }
            this.requestLayout();
        });
        this.fittedView.set(fittedView);

    }

    @Override
    protected void layoutChildren() {
        FittedViewI view = this.fittedView.get();
        if (view != null) {
            double ivWidth = Math.max(1, this.getWidth());
            double ivHeight = Math.max(1, this.getHeight());
            double rotation = view.rotateProperty().get();
            if (Math.abs(rotation / 90.0 % 2) == 1.0) {
                view.setFitWidth(ivHeight);
                view.setFitHeight(ivWidth);
            } else {
                view.setFitWidth(ivWidth);
                view.setFitHeight(ivHeight);
            }
            this.layoutInArea(view.getNode(), 0, 0, ivWidth, ivHeight, 0, HPos.CENTER, VPos.CENTER);
        }
        super.layoutChildren();
    }

    @Override
    protected double computeMinHeight(final double h) {
        return 1.0;
    }

    @Override
    protected double computeMinWidth(final double w) {
        return 1.0;
    }

    public FittedViewI getFittedView() {
        return fittedView.get();
    }
}
