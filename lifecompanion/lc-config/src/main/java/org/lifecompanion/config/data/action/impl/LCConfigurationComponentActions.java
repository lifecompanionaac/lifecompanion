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
package org.lifecompanion.config.data.action.impl;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.api.action.definition.UndoRedoActionI;
import org.lifecompanion.api.component.definition.*;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.base.data.action.definition.BasePropertyChangeAction;
import org.lifecompanion.base.data.common.PositionSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Action relative to the configuration component.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCConfigurationComponentActions {
    private static final Logger LOGGER = LoggerFactory.getLogger(LCConfigurationComponentActions.class);

    // Class part : "Grid"
    //========================================================================

    /**
     * To enable the grid on configuration
     */
    public static class EnableGridOnConfigurationAction extends BaseConfigurationLayoutChangeAction implements UndoRedoActionI {

        public EnableGridOnConfigurationAction(final LCConfigurationI configurationP) {
            super(configurationP);
        }

        @Override
        public void doAction() throws LCException {
            this.saveLayout();
            this.configuration.useGridProperty().set(true);
        }

        @Override
        public String getNameID() {
            return "action.grid.enable";
        }

        @Override
        public void undoAction() throws LCException {
            this.configuration.useGridProperty().set(false);
            this.restoreLayout();
        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }
    }

    /**
     * To disable the grid on configuration
     */
    public static class DisableGridOnConfigurationAction extends BaseConfigurationLayoutChangeAction implements UndoRedoActionI {

        public DisableGridOnConfigurationAction(final LCConfigurationI configurationP) {
            super(configurationP);
        }

        @Override
        public void doAction() throws LCException {
            this.configuration.useGridProperty().set(false);
        }

        @Override
        public String getNameID() {
            return "action.grid.disable";
        }

        @Override
        public void undoAction() throws LCException {
            this.configuration.useGridProperty().set(true);
        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }
    }

    /**
     * To change the grid size on configuration
     */
    public static class ChangeGridSizeConfigurationAction extends BaseConfigurationLayoutChangeAction implements UndoRedoActionI {
        private int previous, current;

        public ChangeGridSizeConfigurationAction(final LCConfigurationI configurationP, final int currentP) {
            super(configurationP);
            this.current = currentP;
        }

        @Override
        public void doAction() throws LCException {
            this.saveLayout();
            this.previous = this.configuration.gridSizeProperty().get();
            this.configuration.gridSizeProperty().set(this.current);
        }

        @Override
        public String getNameID() {
            return "action.change.grid.size";
        }

        @Override
        public void undoAction() throws LCException {
            this.configuration.gridSizeProperty().set(this.previous);
            this.restoreLayout();
        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }
    }

    /**
     * Action to change the first selected component
     */
    public static class ChangeFirstComponentAction extends BasePropertyChangeAction<GridComponentI> implements UndoRedoActionI {

        public ChangeFirstComponentAction(final LCConfigurationI configuration, final GridComponentI wantedValueP) {
            super(configuration.firstSelectionPartProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.first.selected.component";
        }
    }

    public static class ChangeConfigBackgroundColorAction extends BasePropertyChangeAction<Color> implements UndoRedoActionI {

        public ChangeConfigBackgroundColorAction(final LCConfigurationI configuration, final Color wantedValueP) {
            super(configuration.backgroundColorProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.config.background.color";
        }
    }

    public static class ChangeKeepRatioProperty extends BasePropertyChangeAction<Boolean> implements UndoRedoActionI {
        public ChangeKeepRatioProperty(final LCConfigurationI configuration, final Boolean wantedValueP) {
            super(configuration.keepConfigurationRatioProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.config.keep.ratio";
        }
    }

    public static class ChangeVirtualKeyboardAction extends BasePropertyChangeAction<Boolean> implements UndoRedoActionI {

        public ChangeVirtualKeyboardAction(final LCConfigurationI configuration, final Boolean wantedValueP) {
            super(configuration.virtualKeyboardProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.config.virtual.keyboard";
        }
    }

    public static class ChangeSecuredConfigurationModedAction extends BasePropertyChangeAction<Boolean> implements UndoRedoActionI {

        public ChangeSecuredConfigurationModedAction(final LCConfigurationI configuration, final Boolean wantedValueP) {
            super(configuration.securedConfigurationModeProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.config.secured.configuration.mode";
        }
    }
    //========================================================================

    // Class part : "Size"
    //========================================================================
    public static class ChangeFramePositionOnLaunchAction extends BasePropertyChangeAction<FramePosition> implements UndoRedoActionI {

        public ChangeFramePositionOnLaunchAction(final LCConfigurationI configuration, final FramePosition wantedValueP) {
            super(configuration.framePositionOnLaunchProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.configuration.frame.position.on.launch";
        }
    }

    public static class ChangeFrameOpacityAction extends BasePropertyChangeAction<Number> implements UndoRedoActionI {

        public ChangeFrameOpacityAction(final LCConfigurationI configuration, final Number wantedValueP) {
            super(configuration.frameOpacityProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.config.frame.opacity";
        }
    }

    public static class ChangeMouseColorAction extends BasePropertyChangeAction<Color> implements UndoRedoActionI {

        public ChangeMouseColorAction(final LCConfigurationI configuration, final Color wantedValueP) {
            super(configuration.getVirtualMouseParameters().mouseColorProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.virtual.mouse.color";
        }
    }

    public static class ChangeMouseSizeAction extends BasePropertyChangeAction<Number> implements UndoRedoActionI {

        public ChangeMouseSizeAction(final LCConfigurationI configuration, final Number wantedValueP) {
            super(configuration.getVirtualMouseParameters().mouseSizeProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.virtual.mouse.size";
        }
    }

    public static class ChangeMouseSpeedAction extends BasePropertyChangeAction<Number> implements UndoRedoActionI {

        public ChangeMouseSpeedAction(final LCConfigurationI configuration, final Number wantedValueP) {
            super(configuration.getVirtualMouseParameters().mouseSpeedProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.virtual.mouse.speed";
        }
    }

    public static class ChangeMouseDrawingAction extends BasePropertyChangeAction<VirtualMouseDrawing> implements UndoRedoActionI {

        public ChangeMouseDrawingAction(final LCConfigurationI configuration, final VirtualMouseDrawing wantedValueP) {
            super(configuration.getVirtualMouseParameters().mouseDrawingProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.virtual.mouse.draw";
        }
    }

    public static class ChangePredictionCharSpace extends BasePropertyChangeAction<String> implements UndoRedoActionI {

        public ChangePredictionCharSpace(final LCConfigurationI configuration, final String oldValueP, final String wantedValueP) {
            super(configuration.getPredictionParameters().charPredictionSpaceCharProperty(), oldValueP, wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.prediction.char.space";
        }
    }

    public static class ChangeWordPredictorId extends BasePropertyChangeAction<String> implements UndoRedoActionI {

        public ChangeWordPredictorId(final LCConfigurationI configuration, final String wantedValueP) {
            super(configuration.getPredictionParameters().selectedWordPredictorIdProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.word.predictor.id";
        }
    }

    public static class ChangeCharPredictorId extends BasePropertyChangeAction<String> implements UndoRedoActionI {

        public ChangeCharPredictorId(final LCConfigurationI configuration, final String wantedValueP) {
            super(configuration.getPredictionParameters().selectedCharPredictorIdProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.change.char.predictor.id";
        }
    }
    //========================================================================

    // Class part : "Abstract"
    //========================================================================

    /**
     * Helper class for action to use configuration component
     */
    public abstract static class BaseConfigurationAction {
        protected LCConfigurationI configuration;

        public BaseConfigurationAction(final LCConfigurationI configurationP) {
            this.configuration = configurationP;
        }
    }

    /**
     * Helper class for action that change the configuration layout of children
     */
    public abstract static class BaseConfigurationLayoutChangeAction extends BaseConfigurationAction {
        private Map<RootGraphicComponentI, PositionSize> savedLayout;

        public BaseConfigurationLayoutChangeAction(final LCConfigurationI configurationP) {
            super(configurationP);
            this.savedLayout = new HashMap<>();
        }

        protected void saveLayout() {
            this.savedLayout.clear();
            ObservableList<RootGraphicComponentI> children = this.configuration.getChildren();
            for (RootGraphicComponentI component : children) {
                this.savedLayout.put(component, PositionSize.create(component));
            }
        }

        protected void restoreLayout() {
            ObservableList<RootGraphicComponentI> children = this.configuration.getChildren();
            for (RootGraphicComponentI component : children) {
                PositionSize positionSize = this.savedLayout.get(component);
                if (positionSize != null) {
                    positionSize.setPositionAndSizeOn(component);
                } else {
                    LCConfigurationComponentActions.LOGGER.warn("Didn't find any saved position/size for a configuration child : {}",
                            component.getID());
                }
            }
        }
    }
    //========================================================================
}
