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

package org.lifecompanion.base.data.style.impl;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.monadic.MonadicBinding;
import org.lifecompanion.api.style2.definition.TextCompStyleI;
import org.lifecompanion.base.data.common.Unbindable;

/**
 * Usefull class to bind view component to their text style properties.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class TextStyleBinder {

    //Just a tool class
    private TextStyleBinder() {
    }

    // FIXME : replace with Unbindable
    public static void unbindLabeled(final Labeled label) {
        label.fontProperty().unbind();
        label.textFillProperty().unbind();
        label.underlineProperty().unbind();
        label.textAlignmentProperty().unbind();
        label.alignmentProperty().unbind();
    }

    public static interface TextStyleBindable {
        public ObjectProperty<Font> fontProperty();

        public ObjectProperty<Paint> textFillProperty();

        public BooleanProperty underlineProperty();

        public ObjectProperty<TextAlignment> textAlignmentProperty();

        public ObjectProperty<Pos> alignmentProperty();

        public ObjectProperty<TextCompStyleI> useTextCompStyleProperty();
    }

    // Class part : "NEW"
    //========================================================================
    public static Unbindable bindTextStyleBindableComp(final TextStyleBindable textStyleBindable, final TextCompStyleI style) {
        textStyleBindable.fontProperty().bind(style.fontProperty());
        textStyleBindable.textFillProperty().bind(style.colorProperty().value());
        textStyleBindable.underlineProperty().bind(style.underlineProperty().value());
        textStyleBindable.textAlignmentProperty().bind(style.textAlignmentProperty().value());
        textStyleBindable.alignmentProperty().bind(TextStyleBinder.createAlignmentBinding(style.textAlignmentProperty().value()));
        textStyleBindable.useTextCompStyleProperty().set(style);
        return () -> {
            textStyleBindable.fontProperty().unbind();
            textStyleBindable.textFillProperty().unbind();
            textStyleBindable.underlineProperty().unbind();
            textStyleBindable.textAlignmentProperty().unbind();
            textStyleBindable.alignmentProperty().unbind();
        };
    }

    public static void bindLabeledComp(final Labeled label, final ObjectProperty<TextCompStyleI> styleProperty) {
        TextStyleBinder.bindLabeledComp(label, styleProperty, true);
    }

    public static void bindLabeledComp(final Labeled label, final ObjectProperty<TextCompStyleI> styleProperty, final boolean bindFill) {
        label.fontProperty().bind(EasyBind.select(styleProperty).selectObject(TextCompStyleI::fontProperty).orElse(Font.getDefault()));
        label.underlineProperty().bind(EasyBind.select(styleProperty).selectObject(t -> t.underlineProperty().value()));
        MonadicBinding<TextAlignment> textAlignBinding = EasyBind.select(styleProperty).selectObject(t -> t.textAlignmentProperty().value());
        label.textAlignmentProperty().bind(textAlignBinding);
        label.alignmentProperty().bind(TextStyleBinder.createAlignmentBinding(textAlignBinding));
        if (bindFill) {
            label.textFillProperty().bind(EasyBind.select(styleProperty).selectObject(t -> t.colorProperty().value()));
        }
    }

    private static ObjectBinding<Pos> createAlignmentBinding(final ObservableValue<TextAlignment> textAlignment) {
        return Bindings.createObjectBinding(() -> {
            TextAlignment textAlign = textAlignment.getValue();
            if (textAlign == TextAlignment.LEFT) {
                return Pos.CENTER_LEFT;
            } else if (textAlign == TextAlignment.RIGHT) {
                return Pos.CENTER_RIGHT;
            } else {
                return Pos.CENTER;
            }
        }, textAlignment);
    }
    //========================================================================

}
