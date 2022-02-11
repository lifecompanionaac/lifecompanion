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

package org.lifecompanion.ui.configurationcomponent.usemode;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.ui.configurationcomponent.base.GridPartKeyViewBase;

public class GridPartKeyViewUse extends GridPartKeyViewBase {

    private ChangeListener<KeyOptionI> keyOptionChanged;
    private final ChangeListener<Region> changeListenerKeyOptionAddedNode;

    public GridPartKeyViewUse() {
        super();
        changeListenerKeyOptionAddedNode = (obs, ov, nv) -> {
            if (ov != null) {
                ov.prefWidthProperty().unbind();
                ov.prefHeightProperty().unbind();
                this.getChildren().remove(ov);
            }
            if (nv != null) {
                nv.prefWidthProperty().bind(prefWidthProperty());
                nv.prefHeightProperty().bind(prefHeightProperty());
                this.getChildren().add(nv);
            }
        };
    }

    @Override
    public void initListener() {
        super.initListener();
        EventHandler<? super MouseEvent> mouseEventListener = (me) -> {
            SelectionModeController.INSTANCE.fireMouseEventOn(this.model, me);
        };
        this.setOnMouseEntered(mouseEventListener);
        this.setOnMouseExited(mouseEventListener);
        this.setOnMousePressed(mouseEventListener);
        this.setOnMouseReleased(mouseEventListener);
        this.setOnMouseMoved(mouseEventListener);
    }

    @Override
    public void initBinding() {
        super.initBinding();
        // On key option change, bind added node
        keyOptionChanged = (obs, ov, nv) -> {
            if (ov != null) {
                ov.keyViewAddedNodeProperty().removeListener(changeListenerKeyOptionAddedNode);
                this.changeListenerKeyOptionAddedNode.changed(null, ov.keyViewAddedNodeProperty().get(), null);
            }
            if (nv != null) {
                changeListenerKeyOptionAddedNode.changed(null, null, nv.keyViewAddedNodeProperty().get());
                nv.keyViewAddedNodeProperty().addListener(changeListenerKeyOptionAddedNode);
            }
        };
        keyOptionChanged.changed(null, null, model.keyOptionProperty().get());
        this.model.keyOptionProperty().addListener(keyOptionChanged);
    }

    @Override
    public void unbindComponentAndChildren() {
        final KeyOptionI prevValue = model.keyOptionProperty().get();
        this.model.keyOptionProperty().removeListener(keyOptionChanged);
        keyOptionChanged.changed(model.keyOptionProperty(), prevValue, null);
        super.unbindComponentAndChildren();
    }
}
