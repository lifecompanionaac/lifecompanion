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

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.TreeDisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.TreeDisplayableType;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.style.ShapeCompStyleI;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.lifecompanion.model.api.textcomponent.CachedLineListenerDataI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerLineI;
import org.lifecompanion.model.impl.exception.LCException;

import java.util.List;
import java.util.function.Consumer;

/**
 * Component that display text to user into user interface.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class TextEditorComponent extends RootGraphicComponentBaseImpl implements WriterDisplayerComponentBaseImpl {
    /**
     * Text displayer wrapper
     */
    protected TextDisplayerPropertyWrapper textDisplayerPropertyWrapper;

    /**
     * Create the base for a text editor component
     */
    public TextEditorComponent() {
        super();
        this.textDisplayerPropertyWrapper = new TextDisplayerPropertyWrapper(this);
    }


    // Class part : "Base getter"
    //========================================================================
    @Override
    public ShapeCompStyleI getTextDisplayerShapeStyle() {
        return this.textDisplayerPropertyWrapper.getTextDisplayerShapeStyle();
    }

    @Override
    public TextCompStyleI getTextDisplayerTextStyle() {
        return this.textDisplayerPropertyWrapper.getTextDisplayerTextStyle();
    }
    //========================================================================

    // WRITER IMPL
    //========================================================================
    @Override
    public DoubleProperty imageHeightProperty() {
        return this.textDisplayerPropertyWrapper.imageHeightProperty();
    }

    @Override
    public DoubleProperty lineSpacingProperty() {
        return this.textDisplayerPropertyWrapper.lineSpacingProperty();
    }

    @Override
    public BooleanProperty enableImageProperty() {
        return this.textDisplayerPropertyWrapper.enableImageProperty();
    }

    @Override
    public BooleanProperty enableWordWrapProperty() {
        return this.textDisplayerPropertyWrapper.enableWordWrapProperty();
    }

    @Override
    public List<TextDisplayerLineI> getLastCachedLines() {
        return this.textDisplayerPropertyWrapper.getLastCachedLines();
    }

    //    @Override
    //    public CachedLineListenerDataI getCachedLineUpdateListener() {
    //        return textDisplayerPropertyWrapper.getCachedLinesListener();
    //    }

    @Override
    public CachedLineListenerDataI addCachedLinesUpdateListener(Consumer<List<TextDisplayerLineI>> listener, DoubleBinding maxWithProperty) {
        return textDisplayerPropertyWrapper.addCachedLinesUpdateListener(listener, maxWithProperty);
    }
    //========================================================================

    // Class part : "XML IO"
    //========================================================================

    @Override
    public Element serialize(final IOContextI contextP) {
        Element superSerialization = super.serialize(contextP);
        this.textDisplayerPropertyWrapper.serialize(superSerialization, contextP);
        return superSerialization;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        this.textDisplayerPropertyWrapper.deserialize(nodeP, contextP);
    }
    //========================================================================

    // Class part : "Tree"
    //========================================================================
    @SuppressWarnings("unchecked")
    @Override
    public ObservableList<? extends TreeDisplayableComponentI> getChildrenNode() {
        return null;
    }

    @Override
    public boolean isNodeLeaf() {
        return true;
    }

    @Override
    public TreeDisplayableType getNodeType() {
        return TreeDisplayableType.EDITOR;
    }
    //========================================================================

}
