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
package org.lifecompanion.model.impl.style;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import org.fxmisc.easybind.EasyBind;
import org.lifecompanion.model.api.style.AbstractShapeCompStyleI;
import org.lifecompanion.util.binding.Unbindable;

/**
 * Class to bind rendering part to shape style.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ShapeStyleBinder {
    // Class part : "STYLE 2"
    //========================================================================
    public static <T extends AbstractShapeCompStyleI<?>> Unbindable bindNodeSize(final Region node, final T style, final DoubleProperty modelWidth,
                                                                                 final DoubleProperty modelHeight) {
        NumberBinding layoutBinding = Bindings.max(style.strokeSizeProperty().valueAsInt(), style.shapeRadiusProperty().valueAsInt().divide(2.0));
        node.layoutXProperty().bind(layoutBinding);
        node.layoutYProperty().bind(layoutBinding);
        NumberBinding borderBinding = Bindings.max(style.strokeSizeProperty().valueAsInt().multiply(2.0), style.shapeRadiusProperty().valueAsInt());
        node.prefWidthProperty().bind(modelWidth.subtract(borderBinding));
        node.prefHeightProperty().bind(modelHeight.subtract(borderBinding));
        return () -> {
            node.layoutXProperty().unbind();
            node.layoutYProperty().unbind();
            node.prefWidthProperty().unbind();
            node.prefHeightProperty().unbind();
        };
    }

    public static <T extends AbstractShapeCompStyleI<?>> Unbindable bindArcSizeComp(final Rectangle rect, final T style, final ReadOnlyDoubleProperty width,
                                                                                    final ReadOnlyDoubleProperty height, final ObservableNumberValue strokeProperty, final int strokeCoef) {
        NumberBinding arcSizeBinding;
        //Because round border stop increase if radius>height/width (divide per 2.0 because rectangle use diameter and not radius)
        if (strokeCoef < 0) {
            arcSizeBinding = Bindings.min(Bindings.min(height.divide(2.0), width.divide(2.0)), style.shapeRadiusProperty().valueAsInt())
                    .subtract(strokeProperty).multiply(2.0);
        } else {
            arcSizeBinding = Bindings.min(Bindings.min(height.divide(2.0), width.divide(2.0)), style.shapeRadiusProperty().valueAsInt())
                    .add(strokeProperty).multiply(2.0);
        }
        rect.arcWidthProperty().bind(arcSizeBinding);
        rect.arcHeightProperty().bind(arcSizeBinding);
        return () -> {
            rect.arcWidthProperty().unbind();
            rect.arcHeightProperty().unbind();
        };
    }

    public static <T extends AbstractShapeCompStyleI<?>> Unbindable bindClipComp(final Rectangle rect, final ReadOnlyDoubleProperty width,
                                                                                 final ReadOnlyDoubleProperty height, final T style) {
        //Unbind
        rect.xProperty().unbind();
        rect.yProperty().unbind();
        rect.arcWidthProperty().unbind();
        rect.arcHeightProperty().unbind();
        rect.widthProperty().unbind();
        rect.heightProperty().unbind();
        //Bind
        DoubleBinding strokeSizeMulti = style.strokeSizeProperty().valueAsInt().multiply(2.0);
        rect.widthProperty().bind(width.subtract(strokeSizeMulti));
        rect.heightProperty().bind(height.subtract(strokeSizeMulti));
        Unbindable unbindableArcSizeComp = ShapeStyleBinder.bindArcSizeComp(rect, style, width, height, style.strokeSizeProperty().valueAsInt(), -1);

        return () -> {
            rect.widthProperty().unbind();
            rect.heightProperty().unbind();
            unbindableArcSizeComp.unbind();
        };
    }

    public static Unbindable bindNode(final Node region, final AbstractShapeCompStyleI<?> style) {
        region.styleProperty().bind(style.cssStyleProperty());
        return () -> region.styleProperty().unbind();
    }

    public static Unbindable bindNodeCmp(final Node region, final ObjectProperty<? extends AbstractShapeCompStyleI<?>> styleProperty) {
        region.styleProperty().bind(EasyBind.select(styleProperty).selectObject(AbstractShapeCompStyleI::cssStyleProperty));
        return () -> region.styleProperty().unbind();
    }
    //========================================================================

}
