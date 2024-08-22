/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.caaai.model.keyoption;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.style.ShapeStyleBinder;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.binding.Unbindable;
import org.lifecompanion.util.javafx.FXThreadUtils;


public class RecordedVolumeIndicatorKeyOption extends AbstractKeyOption {

    private Runnable unbindCurrentProgress;

    public RecordedVolumeIndicatorKeyOption() {
        super();
        this.optionNameId = "caa.ai.plugin.todo.volume";
        this.optionDescriptionId = "caa.ai.plugin.todo";
        this.iconName = "filler_icon_32px.png";
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
    }

    @Override
    protected void attachToImpl(GridPartKeyComponentI gridPartKeyComponentI) {
        this.keyViewAddedNodeProperty().set(null);
    }

    @Override
    public Element serialize(IOContextI context) {
        final Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(RecordedVolumeIndicatorKeyOption.class, this, node);
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(RecordedVolumeIndicatorKeyOption.class, this, node);
    }

    public void showVolume(ReadOnlyDoubleProperty volume) {
        FXThreadUtils.runOnFXThread(() -> this.keyViewAddedNodeProperty().set(createVolumeIndicatorView(attachedKey.get(), volume)));
    }

    public void hideVolume() {
        if (unbindCurrentProgress != null) {
            unbindCurrentProgress.run();
            unbindCurrentProgress = null;
        }
        FXThreadUtils.runOnFXThread(() -> this.keyViewAddedNodeProperty().set(null));
    }

    long lastUpdate;
    double sumFromLast;
    int countFromLast;
    private static final long UPDATE_DELAY = 500;

    public Pane createVolumeIndicatorView(GridPartKeyComponentI key, ReadOnlyDoubleProperty volume) {
        Circle circle = new Circle();
        circle.setFill(Color.rgb(80, 167, 186, 0.58));

        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        ChangeListener<Number> volumeListener = (obs, ov, nv) -> {
            if (System.currentTimeMillis() - lastUpdate > UPDATE_DELAY) {
                timeline.stop();
                double endValue = (Math.min(key.layoutHeightProperty().get(), key.layoutWidthProperty().get()) / 2.0) * (sumFromLast / countFromLast);
                if (Double.isFinite(endValue)) {
                    timeline.getKeyFrames()
                            .setAll(new KeyFrame(Duration.millis(UPDATE_DELAY * 0.8),
                                    new KeyValue(circle.radiusProperty(),
                                            endValue,
                                            Interpolator.EASE_BOTH)));
                }
                timeline.play();
                lastUpdate = System.currentTimeMillis();
                sumFromLast = 0.0;
                countFromLast = 0;
            } else {
                sumFromLast += nv.doubleValue();
                countFromLast++;
            }
        };
        volume.addListener(volumeListener);
        circle.centerXProperty().bind(key.layoutWidthProperty().divide(2.0).subtract(key.getKeyStyle().strokeSizeProperty().valueAsInt().get() * 2.0));
        circle.centerYProperty().bind(key.layoutHeightProperty().divide(2.0).subtract(key.getKeyStyle().strokeSizeProperty().valueAsInt().get() * 2.0));
        this.unbindCurrentProgress = () -> {
            timeline.stop();
            volume.removeListener(volumeListener);
            circle.centerXProperty().unbind();
            circle.centerYProperty().unbind();
        };
        return new Pane(circle);
    }
}
