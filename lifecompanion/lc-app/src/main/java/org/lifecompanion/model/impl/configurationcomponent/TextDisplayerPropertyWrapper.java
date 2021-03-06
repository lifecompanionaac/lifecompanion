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
package org.lifecompanion.model.impl.configurationcomponent;

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.WriterDisplayerI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.style.ShapeCompStyleI;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.lifecompanion.model.api.textcomponent.CachedLineListenerDataI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerLineI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.style.StyleSerialializer;
import org.lifecompanion.model.impl.style.TextDisplayerShapeCompStyle;
import org.lifecompanion.model.impl.style.TextDisplayerTextCompStyle;
import org.lifecompanion.model.impl.textcomponent.CachedLineListenerData;
import org.lifecompanion.model.impl.textcomponent.TextDisplayerLineHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class is a simple wrapper for class that implements {@link WriterDisplayerI}, to avoid theses class to have to instantiate every properties.<br>
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class TextDisplayerPropertyWrapper {
    private final DoubleProperty imageHeight;
    private final DoubleProperty lineSpacing;
    private final BooleanProperty enableImage;
    private final BooleanProperty enableWordWrap;

    private final ShapeCompStyleI textDisplayerShapeStyle;
    private final TextCompStyleI textDisplayerTextStyle;

    private final WriterDisplayerI textDisplayerComponent;

    private final Set<CachedLineListenerDataI> cachedLinesListeners;
    private List<TextDisplayerLineI> cachedLines;

    public TextDisplayerPropertyWrapper(final WriterDisplayerI textDisplayerComponent) {
        this.textDisplayerComponent = textDisplayerComponent;
        cachedLinesListeners = new HashSet<>();
        this.imageHeight = new SimpleDoubleProperty(80.0);
        this.lineSpacing = new SimpleDoubleProperty(2.0);
        this.enableImage = new SimpleBooleanProperty(false);
        this.enableWordWrap = new SimpleBooleanProperty(true);
        this.textDisplayerShapeStyle = new TextDisplayerShapeCompStyle();
        this.textDisplayerTextStyle = new TextDisplayerTextCompStyle();

        // This invalidation listener is called on graphics changes (this displayer config) or on global text changed (added via bind method)
        InvalidationListener invalidationListenerForWritingState = inv -> {
            cachedLinesListeners.forEach(listener -> {
                List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(WritingStateController.INSTANCE, this.textDisplayerComponent, getTextDisplayerTextStyle(), listener.maxWidthProperty().get());
                cachedLines = lines;
                listener.getListener().accept(lines);
            });
        };
        this.enableImageProperty().addListener(invalidationListenerForWritingState);
        this.lineSpacingProperty().addListener(invalidationListenerForWritingState);
        this.imageHeightProperty().addListener(invalidationListenerForWritingState);
        this.enableWordWrapProperty().addListener(invalidationListenerForWritingState);
        this.getTextDisplayerTextStyle().addInvalidationListener(invalidationListenerForWritingState);

        textDisplayerComponent.configurationParentProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                this.getTextDisplayerTextStyle().parentComponentStyleProperty().set(nv.getTextDisplayerTextStyle());
                this.getTextDisplayerShapeStyle().parentComponentStyleProperty().set(nv.getTextDisplayerShapeStyle());
            } else {
                this.getTextDisplayerTextStyle().parentComponentStyleProperty().set(null);
                this.getTextDisplayerShapeStyle().parentComponentStyleProperty().set(null);
            }
        });

        // Bind to global state (with weak listeners : listener is still in memory thanks to previous bindings)
        WritingStateController.INSTANCE.currentTextProperty().addListener(new WeakInvalidationListener(invalidationListenerForWritingState));
        WritingStateController.INSTANCE.caretPosition().addListener(new WeakInvalidationListener(invalidationListenerForWritingState));
    }

    // Class part : "Text displayer"
    //========================================================================
    public DoubleProperty imageHeightProperty() {
        return this.imageHeight;
    }

    public DoubleProperty lineSpacingProperty() {
        return this.lineSpacing;
    }

    public BooleanProperty enableImageProperty() {
        return this.enableImage;
    }

    public BooleanProperty enableWordWrapProperty() {
        return this.enableWordWrap;
    }

    public ShapeCompStyleI getTextDisplayerShapeStyle() {
        return this.textDisplayerShapeStyle;
    }

    public TextCompStyleI getTextDisplayerTextStyle() {
        return this.textDisplayerTextStyle;
    }

    public List<TextDisplayerLineI> getLastCachedLines() {
        return cachedLines;
    }
    //========================================================================

    // IO
    //========================================================================
    public void serialize(final Element element, final IOContextI contextP) {
        XMLObjectSerializer.serializeInto(TextDisplayerPropertyWrapper.class, this, element);
        StyleSerialializer.serializeTextDisplayerStyle(textDisplayerComponent, element, contextP);
    }

    public void deserialize(final Element element, final IOContextI contextP) throws LCException {
        XMLObjectSerializer.deserializeInto(TextDisplayerPropertyWrapper.class, this, element);
        StyleSerialializer.deserializeTextDisplayerStyle(textDisplayerComponent, element, contextP);
    }
    //========================================================================

    // LINE UPDATE LISTENER
    //========================================================================
    public CachedLineListenerDataI addCachedLinesUpdateListener(Consumer<List<TextDisplayerLineI>> listener, DoubleBinding maxWithProperty) {
        InvalidationListener wil = i -> {
            List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(WritingStateController.INSTANCE, this.textDisplayerComponent, getTextDisplayerTextStyle(), maxWithProperty.get());
            cachedLines = lines;
            listener.accept(lines);
        };
        final CachedLineListenerData cachedLineListenerData = new CachedLineListenerData(listener, maxWithProperty, wil, listenerDataThis -> {
            maxWithProperty.removeListener(wil);
            cachedLinesListeners.remove(listenerDataThis);
        });
        this.cachedLinesListeners.add(cachedLineListenerData);
        maxWithProperty.addListener(wil);

        return cachedLineListenerData;
    }
    //========================================================================

}
