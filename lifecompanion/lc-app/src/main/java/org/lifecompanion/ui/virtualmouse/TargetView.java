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

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import org.lifecompanion.controller.virtualmouse.PointingMouseController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TargetView extends Group implements PointingMouseDrawingI {

    private final static double RECTANGLE_WIDTH = 24, RECTANGLE_HEIGHT = 10, RECTANGLE_STROKE = 4.0;

    private final DoubleProperty rectangleWidth;
    private final DoubleProperty rectangleHeight;

    private List<Rectangle> rectangles;

    public TargetView() {
        this.rectangles = new ArrayList<>();
        this.rectangleWidth = new SimpleDoubleProperty();
        this.rectangleHeight = new SimpleDoubleProperty();
        //Create
        Rectangle left = new Rectangle();
        Rectangle right = new Rectangle();
        Rectangle top = new Rectangle();
        Rectangle bottom = new Rectangle();
        //Position
        DoubleBinding rectHeightDivide2 = this.rectangleHeight.divide(2.0);
        DoubleBinding heightDivide2Negate = rectHeightDivide2.negate();
        DoubleBinding heightDivide2SubWidth = heightDivide2Negate.subtract(this.rectangleWidth);
        left.xProperty().bind(heightDivide2SubWidth);
        left.yProperty().bind(heightDivide2Negate);
        right.xProperty().bind(rectHeightDivide2);
        right.yProperty().bind(heightDivide2Negate);
        top.xProperty().bind(heightDivide2Negate);
        top.yProperty().bind(heightDivide2SubWidth);
        bottom.xProperty().bind(heightDivide2Negate);
        bottom.yProperty().bind(rectHeightDivide2);
        //Size
        this.rectangles = Arrays.asList(left, right, top, bottom);
        for (int i = 0; i < this.rectangles.size() / 2; i++) {
            Rectangle rect = this.rectangles.get(i);
            rect.widthProperty().bind(this.rectangleWidth);
            rect.heightProperty().bind(this.rectangleHeight);
        }
        for (int i = this.rectangles.size() / 2; i < this.rectangles.size(); i++) {
            Rectangle rect = this.rectangles.get(i);
            rect.widthProperty().bind(this.rectangleHeight);
            rect.heightProperty().bind(this.rectangleWidth);
        }
        //Add
        this.getChildren().addAll(this.rectangles);
    }

    @Override
    public Node getView() {
        return this;
    }

    @Override
    public void bind(final PointingMouseController mouseController) {
        this.layoutXProperty().bind(mouseController.mouseXProperty());
        this.layoutYProperty().bind(mouseController.mouseYProperty());
        //Size on scale
        this.rectangleWidth.bind(mouseController.sizeScaleProperty().multiply(TargetView.RECTANGLE_WIDTH));
        this.rectangleHeight.bind(mouseController.sizeScaleProperty().multiply(TargetView.RECTANGLE_HEIGHT));
        this.rectangles.forEach((rect) -> {
            rect.fillProperty().bind(mouseController.colorProperty());
            rect.strokeProperty().bind(mouseController.strokeColorProperty());
            rect.strokeWidthProperty().bind(mouseController.sizeScaleProperty().multiply(RECTANGLE_STROKE));
        });
    }

    @Override
    public void unbind() {
        this.layoutXProperty().unbind();
        this.layoutYProperty().unbind();
        this.rectangleWidth.unbind();
        this.rectangleHeight.unbind();
        this.rectangles.forEach((rect) -> {
            rect.fillProperty().unbind();
            rect.strokeProperty().unbind();
            rect.strokeWidthProperty().unbind();
        });
    }

}
