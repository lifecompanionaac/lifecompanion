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
package org.lifecompanion.model.impl.categorizedelement.useaction.available;

import java.util.Map;

import org.fxmisc.easybind.EasyBind;
import org.jdom2.Element;

import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.impl.configurationcomponent.ImageUseComponentPropertyWrapper;
import org.lifecompanion.model.impl.configurationcomponent.ComponentHolder;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChangeKeyImageAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private StringProperty targetKeyId;
    private ComponentHolder<GridPartKeyComponentI> targetKey;
    private ObjectProperty<ImageElementI> wantedImage;

    public ChangeKeyImageAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 1;
        this.category = DefaultUseActionSubCategories.CHANGE;
        this.nameID = "action.change.key.image.name";
        this.staticDescriptionID = "action.change.key.image.static.description";
        this.configIconPath = "configuration/icon_change_key_image.png";
        this.targetKeyId = new SimpleStringProperty();
        this.wantedImage = new SimpleObjectProperty<>();
        this.targetKey = new ComponentHolder<>(this.targetKeyId, this.parentComponentProperty());
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("action.change.key.image.variable.description", EasyBind
                .select(this.targetKeyProperty()).selectObject(GridPartKeyComponentI::nameProperty).orElse(Translation.getText("key.none.selected"))));
    }

    public ObjectProperty<GridPartKeyComponentI> targetKeyProperty() {
        return this.targetKey.componentProperty();
    }

    public ObjectProperty<ImageElementI> wantedImageProperty() {
        return this.wantedImage;
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI wantedKeyChanged = this.targetKey.componentProperty().get();
        if (wantedKeyChanged != null) {
            wantedKeyChanged.imageVTwoProperty().set(this.wantedImage.get());
        }
    }

    @Override
    public void idsChanged(final Map<String, String> changes) {
        super.idsChanged(changes);
        this.targetKey.idsChanged(changes);
    }

    // XML
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(ChangeKeyImageAction.class, this, elem);
        if (this.wantedImage.get() != null) {
            ImageUseComponentPropertyWrapper.serializeImageUse(this.wantedImage.get(), elem, contextP);
        }
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(ChangeKeyImageAction.class, this, nodeP);
        this.wantedImage.set(ImageUseComponentPropertyWrapper.deserializeImageUseV2(nodeP, contextP));
    }
    //========================================================================
}
