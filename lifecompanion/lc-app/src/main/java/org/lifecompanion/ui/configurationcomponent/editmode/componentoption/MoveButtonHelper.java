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

package org.lifecompanion.ui.configurationcomponent.editmode.componentoption;

import javafx.scene.control.ButtonBase;
import org.lifecompanion.model.api.configurationcomponent.MovableComponentI;
import org.lifecompanion.model.api.configurationcomponent.ResizableComponentI;
import org.lifecompanion.util.model.PositionSize;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.controller.editaction.OptionActions;
import org.lifecompanion.controller.editmode.ConfigActionController;

import java.util.function.Supplier;

public class MoveButtonHelper<T extends MovableComponentI & ResizableComponentI> {
    private double sdX, sdY;
    private PositionSize initialMoveState;
    private final Supplier<T> modelSupplier;

    private MoveButtonHelper(Supplier<T> modelSupplier, ButtonBase buttonWhereMoveOptionShouldBeAdded) {
        this.modelSupplier = modelSupplier;
        buttonWhereMoveOptionShouldBeAdded.setOnMousePressed(me -> {
            this.startMove();
            this.sdX = me.getX();
            this.sdY = me.getY();
        });
        buttonWhereMoveOptionShouldBeAdded.setOnMouseDragged(me -> {
            double dx = me.getX() - this.sdX;
            double dy = me.getY() - this.sdY;
            this.modelSupplier.get().xProperty().set(Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP, this.modelSupplier.get().xProperty().get() + dx));
            this.modelSupplier.get().yProperty().set(Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP, this.modelSupplier.get().yProperty().get() + dy));
        });
        buttonWhereMoveOptionShouldBeAdded.setOnMouseReleased((me) -> this.endMove());
    }

    /**
     * Called when a move start
     */
    private void startMove() {
        this.initialMoveState = PositionSize.create(this.modelSupplier.get());
        this.modelSupplier.get().movingProperty().set(true);
    }

    /**
     * Called when a move end
     */
    private void endMove() {
        this.modelSupplier.get().movingProperty().set(false);
        final PositionSize endMoveState = PositionSize.create(this.modelSupplier.get());
        if (!endMoveState.equals(initialMoveState)) {
            OptionActions.MoveAction<T> action = new OptionActions.MoveAction<>(this.initialMoveState, endMoveState, this.modelSupplier.get());
            ConfigActionController.INSTANCE.addAction(action);
        }
    }

    public static <K extends MovableComponentI & ResizableComponentI> void install(BaseOption<K> baseOption, ButtonBase buttonWhereMoveOptionShouldBeAdded) {
        new MoveButtonHelper<>(() -> baseOption.model, buttonWhereMoveOptionShouldBeAdded);
    }

    public static <K extends MovableComponentI & ResizableComponentI> void install(Supplier<K> modelSupplier, ButtonBase buttonWhereMoveOptionShouldBeAdded) {
        new MoveButtonHelper<>(modelSupplier, buttonWhereMoveOptionShouldBeAdded);
    }
}
