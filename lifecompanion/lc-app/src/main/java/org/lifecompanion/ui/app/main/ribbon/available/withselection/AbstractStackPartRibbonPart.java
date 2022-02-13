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

package org.lifecompanion.ui.app.main.ribbon.available.withselection;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ListCell;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.controller.editaction.GridStackActions;
import org.lifecompanion.controller.editaction.GridStackActions.AddGridInStackAction;
import org.lifecompanion.controller.editaction.GridStackActions.RemoveGridInStackAction;
import org.lifecompanion.controller.editaction.GridStackActions.ShiftDownStackComponent;
import org.lifecompanion.controller.editaction.GridStackActions.ShiftUpStackComponent;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.ui.common.control.generic.OrderModifiableListView;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

/**
 * Part to add, remove, change grid of a stack.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractStackPartRibbonPart extends RibbonBasePart<StackComponentI> implements LCViewInitHelper {

    /**
     * List view to display grid
     */
    private OrderModifiableListView<GridComponentI> modifiableListView;

    /**
     * Change listener that listen displayed changes
     */
    private ChangeListener<GridComponentI> selectedChangeListener;

    public AbstractStackPartRibbonPart() {
        this.initAll();
    }

    @Override
    public void initUI() {
        this.setTitle(Translation.getText("stack.grid.list"));
        //Button on right
        this.modifiableListView = new OrderModifiableListView<>(false);
        this.modifiableListView.setCellFactory(listView -> new GridComponentListCellView());
        FXControlUtils.createAndAttachTooltip(this.modifiableListView.getButtonAdd(), "tooltip.explain.add.grid.stack");
        FXControlUtils.createAndAttachTooltip(this.modifiableListView.getButtonRemove(), "tooltip.explain.delete.grid.stack");
        FXControlUtils.createAndAttachTooltip(this.modifiableListView.getButtonUp(), "tooltip.explain.go.up.stack");
        FXControlUtils.createAndAttachTooltip(this.modifiableListView.getButtonDown(), "tooltip.explain.go.down.stack");
        //Total
        this.setContent(this.modifiableListView);
    }

    @Override
    public void initListener() {
        //Add grid
        this.modifiableListView.getButtonAdd().setOnAction((ae) -> {
            //Do action
            GridStackActions.AddGridInStackAction addAction = new AddGridInStackAction(this.model.get(), false, true);
            ConfigActionController.INSTANCE.executeAction(addAction);
        });
        //Remove selected
        this.modifiableListView.getButtonRemove().setOnAction((ae) -> {
            GridComponentI selected = this.modifiableListView.getSelectedItem();
            GridStackActions.RemoveGridInStackAction removeAction = new RemoveGridInStackAction(this.model.get(), selected);
            ConfigActionController.INSTANCE.executeAction(removeAction);
        });
        //Put selected down
        this.modifiableListView.getButtonDown().setOnAction((ae) -> {
            GridComponentI selected = this.modifiableListView.getSelectedItem();
            ConfigActionController.INSTANCE.executeAction(new ShiftDownStackComponent(this.model.get(), selected));
            this.modifiableListView.select(selected);
        });
        //Put selected up
        this.modifiableListView.getButtonUp().setOnAction((ae) -> {
            GridComponentI selected = this.modifiableListView.getSelectedItem();
            ConfigActionController.INSTANCE.executeAction(new ShiftUpStackComponent(this.model.get(), selected));
            this.modifiableListView.select(selected);
        });
    }

    @Override
    public void initBinding() {
        //Display selected
        this.modifiableListView.selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null && this.model.get() != null) {
                this.model.get().displayedComponentProperty().set(nv);
            }
        });
        //Select displayed
        this.selectedChangeListener = (obs, ov, nv) -> {
            if (nv != null) {
                this.modifiableListView.select(nv);
                this.modifiableListView.scrollTo(nv);
            }
        };
        this.initModelBinding();
    }

    /**
     * This method must bind the wanted selection to this model.
     */
    public abstract void initModelBinding();

    @Override
    public void bind(final StackComponentI component) {
        this.modifiableListView.setItems(component.getComponentList());
        component.displayedComponentProperty().addListener(this.selectedChangeListener);
    }

    @Override
    public void unbind(final StackComponentI modelP) {
        this.modifiableListView.setItems(null);
        modelP.displayedComponentProperty().removeListener(this.selectedChangeListener);
    }

    // Class part : "Cell to display a grid part"
    //========================================================================
    public static class GridComponentListCellView extends ListCell<GridComponentI> {

        @Override
        protected void updateItem(final GridComponentI item, final boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                this.textProperty().unbind();
                this.textProperty().set(null);
            } else {
                this.textProperty().unbind();
                this.textProperty().bind(item.nameProperty());
            }
        }
    }
    //========================================================================

}
