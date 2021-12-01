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

package org.lifecompanion.base.data.component.keyoption;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.common.Unbindable;
import org.lifecompanion.base.data.style.impl.ShapeStyleBinder;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProgressDisplayKeyOption extends AbstractKeyOption {

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> progressColor;

    @XMLGenericProperty(ProgressDisplayType.class)
    private final ObjectProperty<ProgressDisplayType> progressDisplayType;

    @XMLGenericProperty(ProgressDisplayMode.class)
    private final ObjectProperty<ProgressDisplayMode> progressDisplayMode;

    public ProgressDisplayKeyOption() {
        super();
        this.optionNameId = "key.option.name.progress.display";
        this.iconName = "icon_type_progress_display.png";
        this.progressDisplayMode = new SimpleObjectProperty<>(ProgressDisplayMode.FILL);
        this.progressDisplayType = new SimpleObjectProperty<>(ProgressDisplayType.HORIZONTAL_BAR);
        this.progressColor = new SimpleObjectProperty<>(Color.rgb(3, 189, 244, 0.5));
    }

    public ObjectProperty<Color> progressColorProperty() {
        return progressColor;
    }

    public ObjectProperty<ProgressDisplayType> progressDisplayTypeProperty() {
        return progressDisplayType;
    }

    public ObjectProperty<ProgressDisplayMode> progressDisplayModeProperty() {
        return progressDisplayMode;
    }

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
    }

    @Override
    public Element serialize(IOContextI context) {
        final Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(ProgressDisplayKeyOption.class, this, node);
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(ProgressDisplayKeyOption.class, this, node);
    }

    private Runnable unbindCurrentProgress;

    public void bindAndShowProgress(ReadOnlyDoubleProperty progressProperty) {
        final GridPartKeyComponentI key = this.attachedKey.get();
        if (progressDisplayType.get() == ProgressDisplayType.CIRCULAR) {
            final DoubleBinding radiusBinding = Bindings.createDoubleBinding(() ->
                            (Math.min(key.layoutWidthProperty().get(), key.layoutHeightProperty().get()) - key.getKeyStyle().strokeSizeProperty().valueAsInt().get() * 2.0) / 2.0
                    , key.layoutWidthProperty(), key.layoutHeightProperty(), key.getKeyStyle().strokeSizeProperty().valueAsInt());

            Arc progressArc = new Arc();
            progressArc.setFill(progressColor.get());
            progressArc.setType(ArcType.ROUND);
            progressArc.radiusXProperty().bind(radiusBinding);
            progressArc.radiusYProperty().bind(radiusBinding);
            progressArc.setStartAngle(90);
            progressArc.layoutXProperty().bind(radiusBinding.add(key.getKeyStyle().strokeSizeProperty().valueAsInt()));
            progressArc.layoutYProperty().bind(radiusBinding.add(key.getKeyStyle().strokeSizeProperty().valueAsInt()));

            Pane arcAndRectContainer = new Pane(progressArc);
            arcAndRectContainer.prefWidthProperty().bind(radiusBinding.multiply(2.0));
            arcAndRectContainer.prefHeightProperty().bind(radiusBinding.multiply(2.0));

            HBox boxContainer = new HBox(arcAndRectContainer);
            boxContainer.setAlignment(Pos.CENTER);

            if (progressDisplayMode.get() == ProgressDisplayMode.FILL) {
                progressArc.lengthProperty().bind(progressProperty.multiply(-360.0));
            } else {
                progressArc.lengthProperty().bind(new SimpleDoubleProperty(1.0).subtract(progressProperty).multiply(-360.0));
            }
            keyViewAddedNode.set(boxContainer);
        }
        // Bars
        else {
            Rectangle progressRectangle = new Rectangle();
            progressRectangle.setFill(progressColor.get());

            // Clip to respect key style
            Rectangle rectangleClip = new Rectangle();
            final Unbindable shapeStyleBinding = ShapeStyleBinder.bindClipComp(rectangleClip, key.layoutWidthProperty(), key.layoutHeightProperty(), key.getKeyStyle());
            progressRectangle.setClip(rectangleClip);

            // Bind width/height on progress
            if (progressDisplayType.get() == ProgressDisplayType.HORIZONTAL_BAR) {
                progressRectangle.layoutXProperty().set(key.getKeyStyle().strokeSizeProperty().valueAsInt().get());
                progressRectangle.layoutYProperty().set(key.getKeyStyle().strokeSizeProperty().valueAsInt().get());
                progressRectangle.heightProperty().bind(key.layoutHeightProperty().subtract(key.getKeyStyle().strokeSizeProperty().valueAsInt().get() * 2.0));
                if (progressDisplayMode.get() == ProgressDisplayMode.FILL) {
                    progressRectangle.widthProperty().bind(key.layoutWidthProperty().subtract(key.getKeyStyle().strokeSizeProperty().valueAsInt().get() * 2.0).multiply(progressProperty));
                } else {
                    progressRectangle.widthProperty().bind(Bindings.multiply(key.layoutWidthProperty().subtract(key.getKeyStyle().strokeSizeProperty().valueAsInt().get() * 2.0), new SimpleDoubleProperty(1.0).subtract(progressProperty)));
                }
            } else {
                progressRectangle.layoutXProperty().set(key.getKeyStyle().strokeSizeProperty().valueAsInt().get());
                progressRectangle.widthProperty().bind(key.layoutWidthProperty().subtract(key.getKeyStyle().strokeSizeProperty().valueAsInt().get() * 2.0));
                if (progressDisplayMode.get() == ProgressDisplayMode.FILL) {
                    progressRectangle.layoutYProperty().bind(key.layoutHeightProperty().multiply(new SimpleDoubleProperty(1.0).subtract(progressProperty)).add(key.getKeyStyle().strokeSizeProperty().valueAsInt()));
                    progressRectangle.heightProperty().bind(key.layoutHeightProperty().multiply(progressProperty).subtract(key.getKeyStyle().strokeSizeProperty().valueAsInt().get() * 2.0));
                } else {
                    progressRectangle.layoutYProperty().bind(key.layoutHeightProperty().multiply(progressProperty).add(key.getKeyStyle().strokeSizeProperty().valueAsInt()));
                    progressRectangle.heightProperty().bind(key.layoutHeightProperty().multiply(new SimpleDoubleProperty(1.0).subtract(progressProperty)).subtract(key.getKeyStyle().strokeSizeProperty().valueAsInt().get() * 2.0));
                }
            }

            this.unbindCurrentProgress = () -> {
                progressRectangle.layoutXProperty().unbind();
                progressRectangle.layoutYProperty().unbind();
                progressRectangle.heightProperty().unbind();
                progressRectangle.widthProperty().unbind();
                shapeStyleBinding.unbind();
            };

            Pane progressContainer = new Pane();
            progressContainer.getChildren().add(progressRectangle);
            keyViewAddedNode.set(progressContainer);
        }
    }

    public void hideProgress() {
        if (unbindCurrentProgress != null) {
            unbindCurrentProgress.run();
            unbindCurrentProgress = null;
        }
        keyViewAddedNode.set(null);
    }


    // CONFIG
    //========================================================================
    public enum ProgressDisplayType {
        VERTICAL_BAR("progress.display.type.vertical.bar"),
        HORIZONTAL_BAR("progress.display.type.horizontal.bar"),
        CIRCULAR("progress.display.type.circular");

        private final String nameId;


        ProgressDisplayType(String nameId) {
            this.nameId = nameId;
        }

        public String getNameId() {
            return nameId;
        }
    }

    public enum ProgressDisplayMode {
        FILL("progress.display.mode.fill"),
        EMPTY("progress.display.mode.empty");

        private final String nameId;


        ProgressDisplayMode(String nameId) {
            this.nameId = nameId;
        }

        public String getNameId() {
            return nameId;
        }
    }
    //========================================================================

}
