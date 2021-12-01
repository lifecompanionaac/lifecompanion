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
package org.lifecompanion.base.data.style2.impl;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.fxmisc.easybind.EasyBind;
import org.jdom2.Element;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.style2.definition.StyleChangeUndo;
import org.lifecompanion.api.style2.definition.TextCompStyleI;
import org.lifecompanion.api.style2.property.definition.IntegerStylePropertyI;
import org.lifecompanion.api.style2.property.definition.StylePropertyI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.style2.property.impl.IntegerStyleProperty;
import org.lifecompanion.base.data.style2.property.impl.StyleProperty;
import org.lifecompanion.base.data.style2.property.impl.StylePropertyConverter;
import org.lifecompanion.framework.commons.fx.io.XMLCustomProperty;
import org.lifecompanion.framework.commons.fx.io.XMLIgnoreNullValue;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

public abstract class AbstractTextCompStyle extends AbstractStyle<TextCompStyleI> implements TextCompStyleI {

    @XMLCustomProperty(value = String.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final StylePropertyI<String> fontFamilly;

    @XMLCustomProperty(value = Integer.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final IntegerStyleProperty fontSize;

    @XMLCustomProperty(value = Boolean.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final StylePropertyI<Boolean> italic;

    @XMLCustomProperty(value = Boolean.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final StylePropertyI<Boolean> bold;

    @XMLCustomProperty(value = Boolean.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final StylePropertyI<Boolean> underline;

    @XMLCustomProperty(value = Boolean.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final StylePropertyI<Boolean> upperCase;

    @XMLCustomProperty(value = TextAlignment.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final StylePropertyI<TextAlignment> textAlignment;

    @XMLCustomProperty(value = Color.class, converter = StylePropertyConverter.class)
    @XMLIgnoreNullValue
    private final StylePropertyI<Color> color;

    private transient final ObjectProperty<FontWeight> fontWeight;
    private transient final ObjectProperty<FontPosture> fontPosture;
    private transient final ObjectProperty<Font> font;

    protected AbstractTextCompStyle() {
        super();
        this.fontFamilly = new StyleProperty<>();
        this.fontSize = new IntegerStyleProperty();
        this.italic = new StyleProperty<>();
        this.bold = new StyleProperty<>();
        this.underline = new StyleProperty<>();
        this.textAlignment = new StyleProperty<>();
        this.upperCase = new StyleProperty<>();
        this.color = new StyleProperty<>();
        this.fontWeight = new SimpleObjectProperty<>();
        this.fontPosture = new SimpleObjectProperty<>();
        this.font = new SimpleObjectProperty<>();
        this.createBindings();
    }

    private void createBindings() {
        InvalidationListener updateFontListener = (obs) -> {
            this.updateFont();
        };
        //Add listener to font
        this.fontFamilly.value().addListener(updateFontListener);
        this.fontWeight.addListener(updateFontListener);
        this.fontPosture.addListener(updateFontListener);
        this.fontSize.value().addListener(updateFontListener);
        //Map italic and bold
        this.fontPosture.bind(EasyBind.map(this.italic.value(), (i) -> {
            return LCUtils.isTrue(i) ? FontPosture.ITALIC : FontPosture.REGULAR;
        }));
        this.fontWeight.bind(EasyBind.map(this.bold.value(), (b) -> {
            return LCUtils.isTrue(b) ? FontWeight.BOLD : FontWeight.NORMAL;
        }));
    }

    private void updateFont() {
        this.font
                .set(Font.font(this.fontFamilly.value().getValue(), this.fontWeight.get(), this.fontPosture.get(), this.fontSize.valueAsInt().get()));
    }

    @Override
    public Font deriveFont(final double size) {
        return Font.font(this.fontFamilly.value().getValue(), this.fontWeight.get(), this.fontPosture.get(), size);
    }

    @Override
    public StylePropertyI<String> fontFamilyProperty() {
        return this.fontFamilly;
    }

    @Override
    public IntegerStylePropertyI fontSizeProperty() {
        return this.fontSize;
    }

    @Override
    public StylePropertyI<Boolean> italicProperty() {
        return this.italic;
    }

    @Override
    public StylePropertyI<Boolean> boldProperty() {
        return this.bold;
    }

    @Override
    public StylePropertyI<TextAlignment> textAlignmentProperty() {
        return this.textAlignment;
    }

    @Override
    public StylePropertyI<Color> colorProperty() {
        return this.color;
    }

    @Override
    public StylePropertyI<Boolean> underlineProperty() {
        return this.underline;
    }

    @Override
    public StylePropertyI<Boolean> upperCaseProperty() {
        return this.upperCase;
    }

    @Override
    public ReadOnlyObjectProperty<Font> fontProperty() {
        return this.font;
    }

    @Override
    public void addInvalidationListener(final InvalidationListener invalidationListener) {
        this.font.addListener(invalidationListener);
        this.color.value().addListener(invalidationListener);
        this.textAlignment.value().addListener(invalidationListener);
        this.underline.value().addListener(invalidationListener);
        this.upperCase.value().addListener(invalidationListener);
    }

    @Override
    public void removeInvalidationListener(final InvalidationListener invalidationListener) {
        this.font.removeListener(invalidationListener);
        this.color.value().removeListener(invalidationListener);
        this.textAlignment.value().removeListener(invalidationListener);
        this.underline.value().removeListener(invalidationListener);
        this.upperCase.value().removeListener(invalidationListener);
    }

    @Override
    protected void bindStyle(final TextCompStyleI style) {
        this.bindP(TextCompStyleI::fontFamilyProperty, style);
        this.bindP(TextCompStyleI::fontSizeProperty, style);
        this.bindP(TextCompStyleI::italicProperty, style);
        this.bindP(TextCompStyleI::boldProperty, style);
        this.bindP(TextCompStyleI::textAlignmentProperty, style);
        this.bindP(TextCompStyleI::colorProperty, style);
        this.bindP(TextCompStyleI::underlineProperty, style);
        this.bindP(TextCompStyleI::upperCaseProperty, style);
    }

    @Override
    protected void unbindStyle() {
        this.unbindP(TextCompStyleI::fontFamilyProperty, null);
        this.unbindP(TextCompStyleI::fontSizeProperty, null);
        this.unbindP(TextCompStyleI::italicProperty, null);
        this.unbindP(TextCompStyleI::boldProperty, null);
        this.unbindP(TextCompStyleI::textAlignmentProperty, null);
        this.unbindP(TextCompStyleI::colorProperty, null);
        this.unbindP(TextCompStyleI::underlineProperty, null);
        this.unbindP(TextCompStyleI::upperCaseProperty, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StyleChangeUndo copyChanges(final TextCompStyleI other, final boolean copyIfNull) {
        StyleChangeUndoImpl<TextCompStyleI> undo = (org.lifecompanion.base.data.style2.impl.AbstractStyle.StyleChangeUndoImpl<TextCompStyleI>) super.copyChanges(
                other, copyIfNull);
        this.copyChange(undo, TextCompStyleI::fontFamilyProperty, other, copyIfNull);
        this.copyChange(undo, TextCompStyleI::fontSizeProperty, other, copyIfNull);
        this.copyChange(undo, TextCompStyleI::italicProperty, other, copyIfNull);
        this.copyChange(undo, TextCompStyleI::boldProperty, other, copyIfNull);
        this.copyChange(undo, TextCompStyleI::textAlignmentProperty, other, copyIfNull);
        this.copyChange(undo, TextCompStyleI::colorProperty, other, copyIfNull);
        this.copyChange(undo, TextCompStyleI::underlineProperty, other, copyIfNull);
        this.copyChange(undo, TextCompStyleI::upperCaseProperty, other, copyIfNull);
        return undo;
    }

    @Override
    public Element serialize(final IOContextI context) {
        Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(AbstractTextCompStyle.class, this, node);
        return node;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(AbstractTextCompStyle.class, this, node);
    }
}
