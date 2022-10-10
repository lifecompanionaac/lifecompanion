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

package org.lifecompanion.plugin.officialexample.spellgame.keyoption;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AbstractKeyOption;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.ProgressDisplayKeyOption;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.FXUtils;

public class CurrentWordDisplayKeyOption extends AbstractKeyOption {

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> progressColor;

    @XMLGenericProperty(ProgressDisplayKeyOption.ProgressDisplayType.class)
    private final ObjectProperty<ProgressDisplayKeyOption.ProgressDisplayType> progressDisplayType;

    @XMLGenericProperty(ProgressDisplayKeyOption.ProgressDisplayMode.class)
    private final ObjectProperty<ProgressDisplayKeyOption.ProgressDisplayMode> progressDisplayMode;

    private final StringProperty currentWord;

    public CurrentWordDisplayKeyOption() {
        super();
        this.disableTextContent.set(true);
        this.optionNameId = "example.plugin.current.word.key.option.name";
        this.iconName = "icon_type_progress_display.png";
        this.progressDisplayMode = new SimpleObjectProperty<>(ProgressDisplayKeyOption.ProgressDisplayMode.FILL);
        this.progressDisplayType = new SimpleObjectProperty<>(ProgressDisplayKeyOption.ProgressDisplayType.HORIZONTAL_BAR);
        this.progressColor = new SimpleObjectProperty<>(Color.rgb(3, 189, 244, 0.5));
        currentWord = new SimpleStringProperty();
    }

    public ObjectProperty<Color> progressColorProperty() {
        return progressColor;
    }

    public ObjectProperty<ProgressDisplayKeyOption.ProgressDisplayType> progressDisplayTypeProperty() {
        return progressDisplayType;
    }

    public ObjectProperty<ProgressDisplayKeyOption.ProgressDisplayMode> progressDisplayModeProperty() {
        return progressDisplayMode;
    }

    @Override
    public void attachToImpl(final GridPartKeyComponentI key) {
        key.textContentProperty().set(null);
    }

    @Override
    public void detachFromImpl(final GridPartKeyComponentI key) {
    }

    @Override
    public Element serialize(IOContextI context) {
        final Element node = super.serialize(context);
        XMLObjectSerializer.serializeInto(CurrentWordDisplayKeyOption.class, this, node);
        return node;
    }

    @Override
    public void deserialize(Element node, IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(CurrentWordDisplayKeyOption.class, this, node);
    }

    public void showWord(String word) {
        FXThreadUtils.runOnFXThread(() -> {
            final GridPartKeyComponentI key = this.attachedKey.get();
            if (key != null) {
                key.textContentProperty().set(word);
            }
        });
    }

    public void hideWord() {
        FXThreadUtils.runOnFXThread(() -> {
            final GridPartKeyComponentI key = this.attachedKey.get();
            if (key != null) {
                key.textContentProperty().set(null);
            }
        });
    }
}
