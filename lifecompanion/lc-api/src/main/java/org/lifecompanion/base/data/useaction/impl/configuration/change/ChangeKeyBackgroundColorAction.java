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
package org.lifecompanion.base.data.useaction.impl.configuration.change;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.fxmisc.easybind.EasyBind;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.component.utils.ComponentHolder;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Map;

public class ChangeKeyBackgroundColorAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private final StringProperty targetKeyId;
    private final ComponentHolder<GridPartKeyComponentI> targetKey;

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> wantedColor;

    private final BooleanProperty restoreParentColor;

    public ChangeKeyBackgroundColorAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 1;
        this.category = DefaultUseActionSubCategories.CHANGE;
        this.nameID = "action.change.key.background.color.name";
        this.staticDescriptionID = "action.change.key.background.color.description";
        this.configIconPath = "configuration/icon_change_key_style.png";
        this.targetKeyId = new SimpleStringProperty();
        this.targetKey = new ComponentHolder<>(this.targetKeyId, this.parentComponentProperty());
        this.wantedColor = new SimpleObjectProperty<>(Color.RED);
        this.restoreParentColor = new SimpleBooleanProperty(false);
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("action.change.key.background.color.variable.description", EasyBind
                .select(this.targetKeyProperty()).selectObject(GridPartKeyComponentI::nameProperty).orElse(Translation.getText("key.none.selected"))));
    }

    public ObjectProperty<GridPartKeyComponentI> targetKeyProperty() {
        return this.targetKey.componentProperty();
    }

    public ObjectProperty<Color> wantedColorProperty() {
        return wantedColor;
    }

    public BooleanProperty restoreParentColorProperty() {
        return restoreParentColor;
    }

    @Override
    public void idsChanged(final Map<String, String> changes) {
        super.idsChanged(changes);
        this.targetKey.idsChanged(changes);
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI wantedKeyChanged = this.targetKey.componentProperty().get();
        if (wantedKeyChanged != null) {
            Platform.runLater(() -> wantedKeyChanged.getKeyStyle().backgroundColorProperty().forced().setValue(restoreParentColor.get() ? null : wantedColor.get()));
        }
    }

    // Class part : "XML"
    //========================================================================

    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(ChangeKeyBackgroundColorAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(ChangeKeyBackgroundColorAction.class, this, nodeP);
    }
    //========================================================================
}
