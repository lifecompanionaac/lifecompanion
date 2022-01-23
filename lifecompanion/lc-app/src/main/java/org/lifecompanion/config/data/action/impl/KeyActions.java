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

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import org.lifecompanion.api.action.definition.UndoRedoActionI;
import org.lifecompanion.api.component.definition.DisplayableComponentI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.ImageUseComponentI;
import org.lifecompanion.api.component.definition.keyoption.KeyOptionI;
import org.lifecompanion.api.component.definition.simplercomp.SimplerKeyContentContainerI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.image2.ImageElementI;
import org.lifecompanion.api.style2.definition.GridStyleUserI;
import org.lifecompanion.api.style2.definition.KeyStyleUserI;
import org.lifecompanion.api.style2.definition.StyleChangeUndo;
import org.lifecompanion.api.style2.definition.TextDisplayerStyleUserI;
import org.lifecompanion.base.data.action.definition.BasePropertyChangeAction;
import org.lifecompanion.base.data.common.PositionSize;
import org.lifecompanion.base.data.component.simple.GridPartKeyComponent;
import org.lifecompanion.config.data.control.ComponentActionController;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.SelectionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * All actions relative to a key.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class KeyActions {

    public static final KeyCombination KEY_COMBINATION_COPY_STYLE = new KeyCodeCombination(KeyCode.C, KeyCodeCombination.SHORTCUT_DOWN,
            KeyCodeCombination.SHIFT_DOWN);
    public static final KeyCombination KEY_COMBINATION_PASTE_STYLE = new KeyCodeCombination(KeyCode.V, KeyCodeCombination.SHORTCUT_DOWN,
            KeyCodeCombination.SHIFT_DOWN);

    public static final EventHandler<ActionEvent> HANDLER_COPY_STYLE = (ea) -> {
        if (!SelectionController.INSTANCE.getSelectedKeys().isEmpty()) {
            ComponentActionController.INSTANCE.styleCopySourceProperty().set(SelectionController.INSTANCE.getSelectedKeys().get(0));
        }
    };

    public static final EventHandler<ActionEvent> HANDLER_PASTE_STYLE = (ea) -> {
        ConfigActionController.INSTANCE.executeAction(
                new ChangeStyleOnComponents(
                        ComponentActionController.INSTANCE.styleCopySourceProperty().get(),
                        SelectionController.INSTANCE.getSelectedKeys().isEmpty() ? Arrays.asList(SelectionController.INSTANCE.selectedComponentBothProperty().get()) : new ArrayList<>(SelectionController.INSTANCE.getSelectedKeys())
                ));
    };

    /**
     * When the key text change
     */
    public static class SetTextAction extends BasePropertyChangeAction<String> {

        public SetTextAction(final GridPartKeyComponent keyP, final String oldValueP, final String newValueP) {
            super(keyP.textContentProperty(), oldValueP, newValueP);
        }

        @Override
        public String getNameID() {
            return "action.key.change.text";
        }
    }

    /**
     * To change the key content display
     */
    public static class ChangeTextPositionAction extends BasePropertyChangeAction<ContentDisplay> {

        public ChangeTextPositionAction(final GridPartKeyComponentI keyP, final ContentDisplay display) {
            super(keyP.textPositionProperty(), display);
        }

        @Override
        public String getNameID() {
            return "action.key.text.position.change";
        }

    }

    public static class ChangeMultiTextPositionAction implements UndoRedoActionI {
        private List<ChangeTextPositionAction> actions;

        public ChangeMultiTextPositionAction(final List<GridPartKeyComponentI> keys, final ContentDisplay display) {
            actions = keys.stream().map(p -> new ChangeTextPositionAction(p, display)).collect(Collectors.toList());
        }

        @Override
        public void doAction() throws LCException {
            for (ChangeTextPositionAction action : actions) {
                action.doAction();
            }
        }

        @Override
        public void undoAction() throws LCException {
            for (ChangeTextPositionAction action : actions) {
                action.undoAction();
            }
        }

        @Override
        public void redoAction() throws LCException {
            for (ChangeTextPositionAction action : actions) {
                action.redoAction();
            }
        }

        @Override
        public String getNameID() {
            return "action.key.text.position.change";
        }
    }

    /**
     * To change the image
     */
    public static class ChangeImageAction extends BasePropertyChangeAction<ImageElementI> {
        private ImageUseComponentI imageUseComponent;
        private PositionSize savedViewport;
        private double previousRotate;
        private boolean previousEnableColorReplace;
        private boolean previousPreserveRatio;
        private ContentDisplay previousContentDisplay;

        public ChangeImageAction(final ImageUseComponentI keyP, final ImageElementI wantedImageP) {
            super(keyP.imageVTwoProperty(), wantedImageP);
            this.imageUseComponent = keyP;
        }

        @Override
        public void doAction() throws LCException {
            // Disable image parameter on image change
            if (this.imageUseComponent.useViewPortProperty().get()) {
                this.savedViewport = PositionSize.create(this.imageUseComponent);
                this.imageUseComponent.useViewPortProperty().set(false);
            }
            previousRotate = this.imageUseComponent.rotateProperty().get();
            previousEnableColorReplace = this.imageUseComponent.enableReplaceColorProperty().get();
            previousPreserveRatio = this.imageUseComponent.preserveRatioProperty().get();
            // Set default
            this.imageUseComponent.rotateProperty().set(0.0);
            this.imageUseComponent.enableReplaceColorProperty().set(false);
            this.imageUseComponent.preserveRatioProperty().set(true);

            // Automatic text position : when it's a key and a image is set (and wasn't set before)
            changeTextPositionIfPossible(SimplerKeyContentContainerI.class, imageUseComponent, SimplerKeyContentContainerI::textPositionProperty);
            changeTextPositionIfPossible(GridPartKeyComponentI.class, imageUseComponent, GridPartKeyComponentI::textPositionProperty);

            super.doAction();
        }

        private <T extends ImageUseComponentI> void changeTextPositionIfPossible(Class<T> type, Object element, Function<T, ObjectProperty<ContentDisplay>> textPositionPropGetter) {
            if (type.isAssignableFrom(element.getClass())) {
                final ObjectProperty<ContentDisplay> textPositionProp = textPositionPropGetter.apply((T) element);
                if (wantedValue != null && ((ImageUseComponentI) element).imageVTwoProperty().get() == null && textPositionProp.get() == ContentDisplay.CENTER && !textPositionProp.isBound()) {
                    previousContentDisplay = textPositionProp.get();
                    textPositionProp.set(ContentDisplay.BOTTOM);
                }
            }
        }

        @Override
        public void undoAction() throws LCException {
            super.undoAction();

            // Restore image parameters
            if (this.savedViewport != null) {
                this.imageUseComponent.useViewPortProperty().set(true);
                this.savedViewport.setPositionAndSizeOn(this.imageUseComponent);
            }
            this.imageUseComponent.rotateProperty().set(previousRotate);
            this.imageUseComponent.enableReplaceColorProperty().set(previousEnableColorReplace);
            this.imageUseComponent.preserveRatioProperty().set(previousPreserveRatio);

            // Restore text position on key
            if (this.imageUseComponent instanceof GridPartKeyComponentI && previousContentDisplay != null) {
                GridPartKeyComponentI key = (GridPartKeyComponentI) this.imageUseComponent;
                if (!key.textPositionProperty().isBound()) {
                    key.textPositionProperty().set(previousContentDisplay);
                }
            }
        }

        @Override
        public String getNameID() {
            return "action.key.image.change";
        }

    }

    /**
     * To change the image rotate
     */
    public static class ChangeImageRotateAction extends BasePropertyChangeAction<Number> {

        public ChangeImageRotateAction(final ImageUseComponentI keyP, final Number wantedImageP) {
            super(keyP.rotateProperty(), wantedImageP);
        }

        @Override
        public String getNameID() {
            return "action.key.image.change.rotate";
        }

    }

    public static class ChangeEnableReplaceColorAction extends BasePropertyChangeAction<Boolean> {

        public ChangeEnableReplaceColorAction(final ImageUseComponentI keyP, final Boolean wantedValue) {
            super(keyP.enableReplaceColorProperty(), wantedValue);
        }

        @Override
        public String getNameID() {
            return "action.image.change.enable.replace.color";
        }

    }

    public static class ChangeToReplaceColorAction extends BasePropertyChangeAction<Color> {

        public ChangeToReplaceColorAction(final ImageUseComponentI keyP, final Color wantedValue) {
            super(keyP.colorToReplaceProperty(), wantedValue);
        }

        @Override
        public String getNameID() {
            return "action.image.change.to.replace.color";
        }

    }

    public static class ChangeReplacingColorAction extends BasePropertyChangeAction<Color> {

        public ChangeReplacingColorAction(final ImageUseComponentI keyP, final Color wantedValue) {
            super(keyP.replacingColorProperty(), wantedValue);
        }

        @Override
        public String getNameID() {
            return "action.image.change.replacing.color";
        }

    }

    public static class ChangeReplaceColorThresholdAction extends BasePropertyChangeAction<Number> {

        public ChangeReplaceColorThresholdAction(final ImageUseComponentI keyP, final Number wantedValue) {
            super(keyP.replaceColorThresholdProperty(), wantedValue);
        }

        @Override
        public String getNameID() {
            return "action.key.image.change.color.replace.threshold";
        }

    }

    /**
     * To change preserve ratio
     */
    public static class ChangePreserveRatioAction extends BasePropertyChangeAction<Boolean> {

        public ChangePreserveRatioAction(final ImageUseComponentI keyP, final Boolean wantedValueP) {
            super(keyP.preserveRatioProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.key.image.change.preserve.ratio";
        }

    }

    public static class ChangeUseViewportAction extends BasePropertyChangeAction<Boolean> {

        private ImageUseComponentI key;
        private PositionSize savedViewport;

        public ChangeUseViewportAction(final ImageUseComponentI keyP, final Boolean wantedValueP) {
            super(keyP.useViewPortProperty(), wantedValueP);
            this.key = keyP;
        }

        @Override
        public String getNameID() {
            return "action.key.image.change.viewport";
        }

        @Override
        public void doAction() throws LCException {
            if (this.key.useViewPortProperty().get()) {
                this.savedViewport = PositionSize.create(this.key);
            }
            super.doAction();
        }

        @Override
        public void undoAction() throws LCException {
            super.undoAction();
            if (this.savedViewport != null && this.key.useViewPortProperty().get()) {
                this.savedViewport.setPositionAndSizeOn(this.key);
            }
        }
    }

    /**
     * To change the view port
     */
    public static class ChangeViewportAction implements UndoRedoActionI {

        private ImageUseComponentI imageUseComponent;
        private PositionSize initState, finalState;

        public ChangeViewportAction(final ImageUseComponentI imageUseComponent, final PositionSize initState, final PositionSize finalState) {
            this.imageUseComponent = imageUseComponent;
            this.initState = initState;
            this.finalState = finalState;
        }

        @Override
        public void doAction() throws LCException {
            this.finalState.setPositionAndSizeOn(this.imageUseComponent);
        }

        @Override
        public void undoAction() throws LCException {
            this.initState.setPositionAndSizeOn(this.imageUseComponent);
        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }

        @Override
        public String getNameID() {
            return "action.key.image.change.viewport.value";
        }

    }

    /**
     * TODO : check SetKeyOptionsAction and merge/use
     */
    public static class ChangeMultiKeyOptionAction implements UndoRedoActionI {
        private static final Logger LOGGER = LoggerFactory.getLogger(ChangeMultiKeyOptionAction.class);

        private final List<ChangeKeyOptionAction> actions;

        public ChangeMultiKeyOptionAction(final List<GridPartKeyComponentI> keys, final Class<? extends KeyOptionI> wantedValueP) {
            actions = keys.stream().map(p -> {
                try {
                    return new ChangeKeyOptionAction(p, wantedValueP.getConstructor().newInstance());
                } catch (Exception e) {
                    LOGGER.error("Couldn't create key option from {}", wantedValueP, e);
                }
                return null;
            }).collect(Collectors.toList());
        }

        @Override
        public void doAction() throws LCException {
            for (ChangeKeyOptionAction action : actions) {
                action.doAction();
            }
        }

        @Override
        public void undoAction() throws LCException {
            for (ChangeKeyOptionAction action : actions) {
                action.undoAction();
            }
        }

        @Override
        public void redoAction() throws LCException {
            for (ChangeKeyOptionAction action : actions) {
                action.redoAction();
            }
        }

        @Override
        public String getNameID() {
            return "action.key.change.option";
        }
    }

    public static class ChangeKeyOptionAction implements UndoRedoActionI {
        private final GridPartKeyComponentI key;
        private KeyOptionI previous;
        private final KeyOptionI wanted;
        private String savedText;
        private ImageElementI savedImage;

        public ChangeKeyOptionAction(final GridPartKeyComponentI keyP, final KeyOptionI wantedValueP) {
            this.key = keyP;
            this.wanted = wantedValueP;
        }

        @Override
        public String getNameID() {
            return "action.key.change.option";
        }

        @Override
        public void doAction() throws LCException {
            this.previous = this.key.keyOptionProperty().get();
            this.savedText = this.key.textContentProperty().get();
            this.savedImage = this.key.imageVTwoProperty().get();
            this.key.changeKeyOption(this.wanted, true);
        }

        @Override
        public void undoAction() throws LCException {
            this.key.changeKeyOption(this.previous, false);
            // Restore if possible only (because some option may bind content)
            if (!this.key.textContentProperty().isBound()) {
                this.key.textContentProperty().set(this.savedText);
            }
            if (!this.key.imageVTwoProperty().isBound()) {
                this.key.imageVTwoProperty().set(this.savedImage);
            }
        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }
    }


    public static class ChangeStyleOnComponents implements UndoRedoActionI {
        private final DisplayableComponentI sourceComponent;
        private final List<DisplayableComponentI> destinationComp;
        private final List<StyleChangeUndo> changes;

        public ChangeStyleOnComponents(DisplayableComponentI sourceComponent, List<DisplayableComponentI> destinationComp) {
            this.sourceComponent = sourceComponent;
            this.destinationComp = destinationComp;
            this.changes = new ArrayList<>();
        }

        @Override
        public void doAction() throws LCException {
            changes.clear();
            StyleContainer sourceContainer = sourceComponent != null ? new StyleContainer(sourceComponent) : null;
            for (DisplayableComponentI destComp : destinationComp) {
                changes.addAll(new StyleContainer(destComp).copyFrom(sourceContainer));
            }
        }

        @Override
        public void undoAction() throws LCException {
            for (StyleChangeUndo change : this.changes) {
                change.undo();
            }
        }

        @Override
        public void redoAction() throws LCException {
            doAction();
        }

        @Override
        public String getNameID() {
            return "paste.key.style.action";
        }

        private static class StyleContainer {
            private final KeyStyleUserI keyStyleUser;
            private final GridStyleUserI gridStyleUser;
            private final TextDisplayerStyleUserI textDisplayerStyleUser;

            public StyleContainer(DisplayableComponentI displayableComponent) {
                keyStyleUser = displayableComponent instanceof KeyStyleUserI ? (KeyStyleUserI) displayableComponent : null;
                gridStyleUser = displayableComponent instanceof GridStyleUserI ? (GridStyleUserI) displayableComponent : null;
                textDisplayerStyleUser = displayableComponent instanceof TextDisplayerStyleUserI ? (TextDisplayerStyleUserI) displayableComponent : null;
            }

            public List<StyleChangeUndo> copyFrom(StyleContainer source) {
                List<StyleChangeUndo> changes = new ArrayList<>(10);
                if (keyStyleUser != null && (source == null || source.keyStyleUser != null)) {
                    changes.add(keyStyleUser.getKeyStyle().copyChanges(source != null ? source.keyStyleUser.getKeyStyle() : null, source == null));
                    changes.add(keyStyleUser.getKeyTextStyle().copyChanges(source != null ? source.keyStyleUser.getKeyTextStyle() : null, source == null));
                }
                if (gridStyleUser != null && (source == null || source.gridStyleUser != null)) {
                    changes.add(gridStyleUser.getGridShapeStyle().copyChanges(source != null ? source.gridStyleUser.getGridShapeStyle() : null, source == null));
                }
                if (textDisplayerStyleUser != null && (source == null || source.textDisplayerStyleUser != null)) {
                    changes.add(textDisplayerStyleUser.getTextDisplayerShapeStyle().copyChanges(source != null ? source.textDisplayerStyleUser.getTextDisplayerShapeStyle() : null, source == null));
                    changes.add(textDisplayerStyleUser.getTextDisplayerTextStyle().copyChanges(source != null ? source.textDisplayerStyleUser.getTextDisplayerTextStyle() : null, source == null));
                }
                return changes;
            }

        }
    }

    public static class ClearStyleOnComponents extends ChangeStyleOnComponents {

        public ClearStyleOnComponents(List<DisplayableComponentI> destinationComp) {
            super(null, destinationComp);
        }

        @Override
        public String getNameID() {
            return "delete.key.style.changes.action";
        }
    }

}
