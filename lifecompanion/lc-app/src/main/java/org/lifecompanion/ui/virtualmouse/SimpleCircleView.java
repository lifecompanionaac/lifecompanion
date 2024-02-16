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

package org.lifecompanion.ui.virtualmouse;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import org.lifecompanion.controller.virtualmouse.DirectionalMouseController;

import java.util.Arrays;
import java.util.List;

public class SimpleCircleView extends Group implements DirectionalMouseDrawingI {

    private final static double BASE_CIRCLE_RADIUS = 40.0, BASE_CIRCLE_STROKE = 10.0, BASE_EXT_INT_STROKE = 4.0;

    private final Circle baseCircle, extCircle, intCircle;
    private final List<Circle> circles;

    public SimpleCircleView() {

        baseCircle = new Circle();
        baseCircle.setFill(Color.TRANSPARENT);
        baseCircle.setStrokeType(StrokeType.INSIDE);

        extCircle = new Circle();
        extCircle.setFill(Color.TRANSPARENT);
        extCircle.setStrokeType(StrokeType.OUTSIDE);

        intCircle = new Circle();
        intCircle.setFill(Color.TRANSPARENT);
        intCircle.setStrokeType(StrokeType.INSIDE);

        circles = Arrays.asList(baseCircle, extCircle, intCircle);
        this.getChildren().addAll(circles);
    }

    @Override
    public Node getView() {
        return this;
    }

    @Override
    public void bind(final DirectionalMouseController mouseController) {
        this.layoutXProperty().bind(mouseController.mouseXProperty());
        this.layoutYProperty().bind(mouseController.mouseYProperty());

        extCircle.strokeProperty().bind(mouseController.strokeColorProperty());
        extCircle.radiusProperty().bind(baseCircle.radiusProperty());
        extCircle.strokeWidthProperty().bind(mouseController.sizeScaleProperty().multiply(SimpleCircleView.BASE_EXT_INT_STROKE));

        intCircle.strokeProperty().bind(mouseController.strokeColorProperty());
        intCircle.radiusProperty().bind(baseCircle.radiusProperty().subtract(baseCircle.strokeWidthProperty()));
        intCircle.strokeWidthProperty().bind(mouseController.sizeScaleProperty().multiply(SimpleCircleView.BASE_EXT_INT_STROKE));

        baseCircle.strokeProperty().bind(mouseController.colorProperty());
        baseCircle.radiusProperty().bind(mouseController.sizeScaleProperty().multiply(SimpleCircleView.BASE_CIRCLE_RADIUS));
        baseCircle.strokeWidthProperty().bind(mouseController.sizeScaleProperty().multiply(SimpleCircleView.BASE_CIRCLE_STROKE));

    }

    @Override
    public void unbind() {
        this.layoutXProperty().unbind();
        this.layoutYProperty().unbind();
        for (Circle circle : circles) {
            circle.strokeProperty().unbind();
            circle.radiusProperty().unbind();
            circle.strokeWidthProperty().unbind();
        }
    }

}
