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

package org.lifecompanion.ui.feedback;

import javafx.animation.FadeTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import org.lifecompanion.controller.feedback.IndicationController;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.style.ShapeStyle;
import org.lifecompanion.model.api.style.StylePropertyI;
import org.lifecompanion.util.LangUtils;
import org.lifecompanion.util.model.ConfigurationComponentLayoutUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class IndicationView extends Group {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndicationView.class);

    private final Rectangle strokeRectangle;
    private final FadeTransition fadeTransition;

    public IndicationView() {
        IndicationController.INSTANCE.setIndicationView(this);
        this.setMouseTransparent(true);
        this.strokeRectangle = new Rectangle();
        this.strokeRectangle.setFill(Color.TRANSPARENT);
        this.strokeRectangle.setOpacity(0.0);
        this.getChildren().add(this.strokeRectangle);
        this.fadeTransition = new FadeTransition(Duration.millis(IndicationController.TRANSITION_TIME_MS), this.strokeRectangle);
    }

    public void dispose() {
        this.fadeTransition.stop();
    }

    public void showFeedback(GridPartKeyComponentI key, Color strokeColor, double strokeSize) {

        fadeTransition.stop();

        Pair<Double, Double> position = ConfigurationComponentLayoutUtils.getConfigurationPosition(key);
        Pair<Double, Double> size = new Pair<>(key.layoutWidthProperty().get(), key.layoutHeightProperty().get());
        StylePropertyI<Number> radiusProp = key.getKeyStyle().shapeRadiusProperty();
        ShapeStyle shapeStyle = key.getKeyStyle().shapeStyleProperty().value().getValue();

        double arcValue = ConfigurationComponentLayoutUtils.computeArcAndStroke(LangUtils.nullToZeroDouble(radiusProp.value().getValue()), size.getKey(), size.getValue(), strokeSize, shapeStyle);

        strokeRectangle.setStrokeWidth(strokeSize);
        strokeRectangle.setStroke(strokeColor);
        strokeRectangle.setArcWidth(arcValue);
        strokeRectangle.setArcHeight(arcValue);
        strokeRectangle.setTranslateX(position.getKey());
        strokeRectangle.setTranslateY(position.getValue());
        strokeRectangle.setWidth(size.getKey());
        strokeRectangle.setHeight(size.getValue());

        if (strokeRectangle.getOpacity() < 1.0) {
            fadeTransition.setToValue(1.0);
            fadeTransition.play();
        }

    }

    public void hideFeedback() {
        fadeTransition.stop();

        if (strokeRectangle.getOpacity() > 0.0) {
            fadeTransition.setToValue(0.0);
            fadeTransition.play();
        }

    }
}
