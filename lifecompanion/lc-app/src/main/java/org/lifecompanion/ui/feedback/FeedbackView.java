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

import javafx.animation.*;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import javafx.util.Pair;
import org.lifecompanion.controller.feedback.FeedbackController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.selectionmode.ProgressDrawMode;
import org.lifecompanion.model.api.selectionmode.ScanningDirection;
import org.lifecompanion.model.api.style.ShapeStyle;
import org.lifecompanion.model.api.style.StylePropertyI;
import org.lifecompanion.model.api.ui.configurationcomponent.ViewProviderI;
import org.lifecompanion.model.impl.configurationcomponent.GridPartKeyComponent;
import org.lifecompanion.model.impl.useapi.dto.ShowFeedbackDto;
import org.lifecompanion.ui.configurationcomponent.base.LCConfigurationChildContainerPane;
import org.lifecompanion.util.LangUtils;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentLayoutUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class FeedbackView extends Group {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackView.class);

    private final Rectangle strokeRectangle;

    public FeedbackView() {
        FeedbackController.INSTANCE.setFeedbackView(this);
        this.setMouseTransparent(true);
        this.strokeRectangle = new Rectangle();
        this.strokeRectangle.setFill(Color.TRANSPARENT);
        this.strokeRectangle.setOpacity(0.0);
        this.getChildren().add(this.strokeRectangle);
    }

    public void dispose() {
        FeedbackController.INSTANCE.setFeedbackView(null);
    }

    public void showFeedback(GridPartKeyComponentI key, Color strokeColor, double strokeSize) {

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
        strokeRectangle.setOpacity(1.0);
    }
}
