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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.configurationcomponent.DynamicKeyFillController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.configurationcomponent.ComponentHolderById;
import org.lifecompanion.model.impl.exception.LCException;

import java.util.Map;

public class ClearSelectedDynamicKeyFillAction extends SimpleUseActionImpl<GridPartKeyComponentI> {
    @SuppressWarnings("FieldCanBeLocal")
    private final StringProperty targetKeyId;
    private final ComponentHolderById<GridPartKeyComponentI> targetKey;

    @SuppressWarnings("FieldCanBeLocal")
    public ClearSelectedDynamicKeyFillAction() {
        super(GridPartKeyComponentI.class);
        this.order = 50;
        this.parameterizableAction = true;
        this.category = DefaultUseActionSubCategories.DYNAMIC_KEYS;
        this.nameID = "use.action.clear.selected.dynamic.key.fill.name";
        this.staticDescriptionID = "use.action.clear.selected.dynamic.key.fill.description";
        this.configIconPath = "configuration/icon_clear_selected_key_fill.png";
        this.targetKeyId = new SimpleStringProperty();
        this.targetKey = new ComponentHolderById<>(this.targetKeyId, this.parentComponentProperty());
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    public ReadOnlyObjectProperty<GridPartKeyComponentI> targetKeyProperty() {
        return this.targetKey.componentProperty();
    }

    public StringProperty targetKeyIdProperty() {
        return this.targetKey.componentIdProperty();
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI targetKeyComp = this.targetKey.componentProperty().get();
        if (targetKeyComp != null) {
            DynamicKeyFillController.INSTANCE.clearFillOn(targetKeyComp);
        }
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        return XMLObjectSerializer.serializeInto(ClearSelectedDynamicKeyFillAction.class, this, super.serialize(contextP));
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(ClearSelectedDynamicKeyFillAction.class, this, nodeP);
    }
}
