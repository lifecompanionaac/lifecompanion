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
package org.lifecompanion.base.data.useaction.impl.show.movetoc;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.fxmisc.easybind.EasyBind;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.component.utils.ComponentHolder;
import org.lifecompanion.base.data.control.SelectionModeController;
import org.lifecompanion.base.data.control.UserActionController;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Map;

/**
 * Action to change
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MoveToGridAndGoBackAction extends SimpleUseActionImpl<GridPartKeyComponentI> {
    public static final String CANCEL_GO_BACK_KEY = "CANCEL_GO_BACK_ACTION";

    private StringProperty targetGridId;
    private ComponentHolder<GridComponentI> targetGrid;

    public MoveToGridAndGoBackAction() {
        super(GridPartKeyComponentI.class);
        this.order = 1;
        this.category = DefaultUseActionSubCategories.MOVE_TO_COMPLEX;
        this.nameID = "go.to.grid.go.back.next.name";
        this.movingAction = true;
        this.staticDescriptionID = "go.to.grid.go.back.next.static.description";
        this.configIconPath = "show/icon_go_back_after_action.png";
        this.targetGridId = new SimpleStringProperty();
        this.targetGrid = new ComponentHolder<>(this.targetGridId, this.parentComponentProperty());
        this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("go.to.grid.go.back.next.variable.description",
                EasyBind.select(this.targetGridProperty()).selectObject(GridComponentI::nameProperty).orElse(Translation.getText("grid.none.selected"))));

    }

    public ObjectProperty<GridComponentI> targetGridProperty() {
        return this.targetGrid.componentProperty();
    }

    @Override
    public void idsChanged(final Map<String, String> changes) {
        super.idsChanged(changes);
        this.targetGrid.idsChanged(changes);
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            //On next, go back to the current key grid
            UserActionController.INSTANCE.getNextSimpleActionExecutionListener(UseActionEvent.ACTIVATION).add((result) -> {
                GridComponentI gridParent = parentKey.gridParentProperty().get();
                //Return in component, only if the executed action was not a moving action
                if (gridParent != null && !result.isMovingActionExecuted()
                        && !result.executedActionVariables().containsKey(MoveToGridAndGoBackAction.CANCEL_GO_BACK_KEY)) {
                    SelectionModeController.INSTANCE.goToGridPart(gridParent);
                }
            });
            //Go to the wanted grid
            SelectionModeController.INSTANCE.goToGridPart(this.targetGridProperty().get());
        }
    }

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(MoveToGridAndGoBackAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(MoveToGridAndGoBackAction.class, this, nodeP);
    }
    //========================================================================
}
