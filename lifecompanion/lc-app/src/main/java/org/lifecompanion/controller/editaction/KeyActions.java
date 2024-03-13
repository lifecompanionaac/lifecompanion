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
package org.lifecompanion.controller.editaction;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.lifecompanion.controller.editmode.ComponentActionController;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.SimplerKeyContentContainerI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.style.*;
import org.lifecompanion.model.impl.editaction.BasePropertyChangeAction;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.lifecompanion.util.model.PositionSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
                        SelectionController.INSTANCE.getSelectedKeys().isEmpty() ? Arrays.asList(SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().get()) : new ArrayList<>(
                                SelectionController.INSTANCE.getSelectedKeys())
                ));
    };

    /**
     * When the key text change
     */
    public static class SetTextAction extends BasePropertyChangeAction<String> {

        public SetTextAction(final GridPartKeyComponentI keyP, final String oldValueP, final String newValueP) {
            super(keyP.textContentProperty(), oldValueP, newValueP);
        }

        public SetTextAction(final GridPartKeyComponentI keyP, final String newValueP) {
            super(keyP.textContentProperty(), newValueP);
        }

        @Override
        public String getNameID() {
            return "action.key.change.text";
        }
    }

    /**
     * To change the image
     */
    public static class ChangeImageAction extends BasePropertyChangeAction<ImageElementI> {
        private ImageUseComponentI imageUseComponent;
        private PositionSize savedViewport;
        private double previousRotate;
        private double previousScaleX;
        private double previousScaleY;
        private Boolean previousColourToGrey;
        private boolean previousEnableColorReplace;
        private boolean previousPreserveRatio;
        private Runnable textPositionUndoKey, textPositionUndoKeyContent;
        private boolean autoSelected, previousAutoSelected;

        public ChangeImageAction(final ImageUseComponentI keyP, final ImageElementI wantedImageP, boolean autoSelected) {
            super(keyP.imageVTwoProperty(), wantedImageP);
            this.imageUseComponent = keyP;
            this.autoSelected = autoSelected;
        }

        @Override
        public void doAction() throws LCException {
            // Disable image parameter on image change
            if (this.imageUseComponent.useViewPortProperty().get()) {
                this.savedViewport = PositionSize.create(this.imageUseComponent);
                this.imageUseComponent.useViewPortProperty().set(false);
            }
            previousRotate = this.imageUseComponent.rotateProperty().get();
            previousScaleX = this.imageUseComponent.scaleXProperty().get();
            previousScaleY = this.imageUseComponent.scaleYProperty().get();
            previousColourToGrey = this.imageUseComponent.colourToGreyProperty().get();
            previousEnableColorReplace = this.imageUseComponent.enableReplaceColorProperty().get();
            previousPreserveRatio = this.imageUseComponent.preserveRatioProperty().get();
            previousAutoSelected = this.imageUseComponent.imageAutomaticallySelectedProperty().get();

            // Set default
            this.imageUseComponent.rotateProperty().set(0.0);
            this.imageUseComponent.scaleXProperty().set(1.0);
            this.imageUseComponent.scaleYProperty().set(1.0);
            this.imageUseComponent.colourToGreyProperty().set(false);
            this.imageUseComponent.enableReplaceColorProperty().set(false);
            this.imageUseComponent.preserveRatioProperty().set(true);

            // Automatic text position : when it's a key and a image is set (and wasn't set before)
            textPositionUndoKey = changeTextPositionIfPossible(
                    imageUseComponent,
                    GridPartKeyComponentI.class,
                    wantedValue,
                    k -> k.imageVTwoProperty().get(),
                    k -> k.getKeyStyle().textPositionProperty().value().getValue(),
                    (k, v) -> k.getKeyStyle().textPositionProperty().selected().setValue(v),
                    k -> !k.getKeyStyle().textPositionProperty().selected().isBound()
            );
            textPositionUndoKeyContent = changeTextPositionIfPossible(
                    imageUseComponent,
                    SimplerKeyContentContainerI.class,
                    wantedValue,
                    k -> k.imageVTwoProperty().get(),
                    k -> k.textPositionProperty().getValue(),
                    (k, v) -> k.textPositionProperty().setValue(v),
                    k -> !k.textPositionProperty().isBound()
            );

            this.imageUseComponent.imageAutomaticallySelectedProperty().set(autoSelected);

            super.doAction();
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
            this.imageUseComponent.scaleXProperty().set(previousScaleX);
            this.imageUseComponent.scaleYProperty().set(previousScaleY);
            this.imageUseComponent.colourToGreyProperty().set(previousColourToGrey);
            this.imageUseComponent.enableReplaceColorProperty().set(previousEnableColorReplace);
            this.imageUseComponent.preserveRatioProperty().set(previousPreserveRatio);
            this.imageUseComponent.imageAutomaticallySelectedProperty().set(previousAutoSelected);

            // Restore text position on key
            runIfNotNull(textPositionUndoKey);
            runIfNotNull(textPositionUndoKeyContent);
        }

        @Override
        public String getNameID() {
            return "action.key.image.change";
        }

    }

    public static void installImageAutoSelect(TextInputControl textField, Supplier<ImageUseComponentI> modelGetter) {
        textField.setOnKeyTyped(event -> {
            String keyText = textField.getText();
            ImageUseComponentI imageUseComponent = modelGetter.get();
            if (UserConfigurationController.INSTANCE.autoSelectImagesProperty().get() && imageUseComponent != null && (imageUseComponent.imageVTwoProperty()
                    .get() == null || imageUseComponent.imageAutomaticallySelectedProperty().get())) {
                ThreadUtils.debounce(600, "auto-image-select", () -> {
                    List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> searchResult = ImageDictionaries.INSTANCE.searchImage(keyText,
                            false,
                            ConfigurationComponentUtils.SIMILARITY_START_WITH);
                    if (!searchResult.isEmpty()) {
                        Pair<ImageDictionaryI, List<List<ImageElementI>>> firstDictResult = searchResult.get(0);
                        if (!CollectionUtils.isEmpty(firstDictResult.getValue())) {
                            List<ImageElementI> firstImages = firstDictResult.getValue().get(0);
                            if (!CollectionUtils.isEmpty(firstImages)) {
                                ImageElementI imageElementI = firstImages.get(0);
                                if (modelGetter.get() == imageUseComponent && (imageUseComponent.imageVTwoProperty().get() == null || imageUseComponent.imageAutomaticallySelectedProperty().get())) {
                                    KeyActions.ChangeImageAction changeImageAction = new KeyActions.ChangeImageAction(imageUseComponent, imageElementI, true);
                                    FXThreadUtils.runOnFXThread(() -> ConfigActionController.INSTANCE.executeAction(changeImageAction));
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public static class ChangeVideoAction extends BasePropertyChangeAction<VideoElementI> {
        private Runnable textPositionUndoKey, textPositionUndoKeyContent;
        private final VideoUseComponentI videoUseComponent;

        public ChangeVideoAction(final VideoUseComponentI videoUseComponent, VideoElementI wantedValueP) {
            super(videoUseComponent.videoProperty(), wantedValueP);
            this.videoUseComponent = videoUseComponent;
        }

        @Override
        public void doAction() throws LCException {
            textPositionUndoKey = changeTextPositionIfPossible(
                    videoUseComponent,
                    GridPartKeyComponentI.class,
                    wantedValue,
                    k -> k.videoProperty().get(),
                    k -> k.getKeyStyle().textPositionProperty().value().getValue(),
                    (k, v) -> k.getKeyStyle().textPositionProperty().selected().setValue(v),
                    k -> !k.getKeyStyle().textPositionProperty().selected().isBound()
            );
            textPositionUndoKeyContent = changeTextPositionIfPossible(
                    videoUseComponent,
                    SimplerKeyContentContainerI.class,
                    wantedValue,
                    k -> k.videoProperty().get(),
                    k -> k.textPositionProperty().getValue(),
                    (k, v) -> k.textPositionProperty().setValue(v),
                    k -> !k.textPositionProperty().isBound()
            );
            super.doAction();
        }

        @Override
        public void undoAction() throws LCException {
            super.undoAction();
            runIfNotNull(textPositionUndoKey);
            runIfNotNull(textPositionUndoKeyContent);
        }

        @Override
        public String getNameID() {
            return null;
        }
    }

    private static void runIfNotNull(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    private static <T extends ImageUseComponentI> Runnable changeTextPositionIfPossible(Object component, Class<T> type,
                                                                                        Object wantedValue,
                                                                                        Function<T, ?> previousValueGetter,
                                                                                        Function<T, TextPosition> textPositionGetter,
                                                                                        BiConsumer<T, TextPosition> textPositionSetter,
                                                                                        Predicate<T> propertyChecker) {
        if (type.isAssignableFrom(component.getClass())) {
            TextPosition currentPos = textPositionGetter.apply((T) component);
            // Set to bottom when setting a new value
            if (wantedValue != null && previousValueGetter.apply((T) component) == null && (currentPos == null || currentPos == TextPosition.CENTER) && propertyChecker.test((T) component)) {
                Runnable textPositionUndo = () -> {
                    if (propertyChecker.test((T) component)) {
                        textPositionSetter.accept((T) component, currentPos);
                    }
                };
                textPositionSetter.accept((T) component, TextPosition.BOTTOM);
                return textPositionUndo;
            }
            // Set to center when deleting a previous value
            if (wantedValue == null && previousValueGetter.apply((T) component) != null && (currentPos == null || currentPos != TextPosition.CENTER) && propertyChecker.test((T) component)) {
                Runnable textPositionUndo = () -> {
                    if (propertyChecker.test((T) component)) {
                        textPositionSetter.accept((T) component, currentPos);
                    }
                };
                textPositionSetter.accept((T) component, TextPosition.CENTER);
                return textPositionUndo;
            }
        }
        return null;
    }

    public static class ChangeVideoDisplayMode extends BasePropertyChangeAction<VideoDisplayMode> {

        public ChangeVideoDisplayMode(final VideoUseComponentI videoUseComponent, VideoDisplayMode wantedValueP) {
            super(videoUseComponent.videoDisplayModeProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return null;
        }
    }

    public static class ChangeVideoPlayMode extends BasePropertyChangeAction<VideoPlayMode> {

        public ChangeVideoPlayMode(final VideoUseComponentI videoUseComponent, VideoPlayMode wantedValueP) {
            super(videoUseComponent.videoPlayModeProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return null;
        }
    }

    public static class ChangeVideoMute extends BasePropertyChangeAction<Boolean> {

        public ChangeVideoMute(final VideoUseComponentI videoUseComponent, Boolean wantedValueP) {
            super(videoUseComponent.muteVideoProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return null;
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

        public static class FlipImageHorizontalAction extends BasePropertyChangeAction<Number> {
        public FlipImageHorizontalAction(final ImageUseComponentI keyP, final Number wantedFlipP) {
            super(keyP.scaleYProperty(), wantedFlipP);
        }
        @Override
        public String getNameID() {
            return "action.key.image.change.flip.horizontal";
        }
    }

    public static class FlipImageVerticalAction extends BasePropertyChangeAction<Number> {

        public FlipImageVerticalAction(final ImageUseComponentI keyP, final Number wantedFlipP) {
            super(keyP.scaleXProperty(), wantedFlipP);
        }

        @Override
        public String getNameID() {
            return "action.key.image.change.flip.vertical";
        }
    }

    public static class colourToGreyAction extends BasePropertyChangeAction<Boolean> {

        public colourToGreyAction(final ImageUseComponentI keyP, Boolean wantedFlipP) {
            super(keyP.colourToGreyProperty(), wantedFlipP);
        }

        @Override
        public String getNameID() {
            return "action.key.image.change.colour.to.grey";
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

    public static class ChangeRemoveBackground extends BasePropertyChangeAction<Boolean> {

        public ChangeRemoveBackground(final ImageUseComponentI keyP, final boolean wantedValue) {
            super(keyP.enableRemoveBackgroundProperty(), wantedValue);
        }

        @Override
        public String getNameID() {
            return "action.key.image.change.remove.background";
        }
    }

     public static class ChangeRemoveBackgroundThresholdAction extends BasePropertyChangeAction<Number> {

        public ChangeRemoveBackgroundThresholdAction(final ImageUseComponentI keyP, final Number wantedValue) {
            super(keyP.replaceRemoveBackgroundThresholdProperty(), wantedValue);
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

    public static class ChangeMultiKeyOptionAction extends UndoRedoActions.MultiActionWrapperAction {
        private static final Logger LOGGER = LoggerFactory.getLogger(ChangeMultiKeyOptionAction.class);

        public ChangeMultiKeyOptionAction(final List<GridPartKeyComponentI> keys, final Class<? extends KeyOptionI> wantedValueP) {
            super("action.key.change.option", keys.stream().map(p -> {
                try {
                    return new ChangeKeyOptionAction(p, wantedValueP.getConstructor().newInstance());
                } catch (Exception e) {
                    LOGGER.error("Couldn't create key option from {}", wantedValueP, e);
                }
                return null;
            }).collect(Collectors.toList()));
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
