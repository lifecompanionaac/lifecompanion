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
package org.lifecompanion.ui.configurationcomponent.editmode;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.BasicKeyOption;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.ui.editmode.AddTypeEnum;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.controlsfx.glyphfont.Glyph;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.dynamickey.KeyListNodeKeyOption;
import org.lifecompanion.model.impl.configurationcomponent.GridPartKeyComponent;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.configurationcomponent.dynamickey.KeyListController;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.MoveToGridAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.MoveToKeyAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.MoveToGridAndGoBackAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.NextPageAndLoopInStackAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.NextPageInStackAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.PreviousPageInStackAction;
import org.lifecompanion.ui.configurationcomponent.base.GridPartKeyViewBase;
import org.lifecompanion.controller.editaction.GridActions;
import org.lifecompanion.controller.editaction.GridActions.InverseKeyAction;
import org.lifecompanion.controller.editaction.GridActions.SetComponentAction;
import org.lifecompanion.controller.editaction.KeyActions.ChangeImageAction;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.DragController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.ui.configurationcomponent.editmode.componentoption.ButtonComponentOption;
import org.lifecompanion.ui.configurationcomponent.editmode.componentoption.SelectableOption;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Configuration view for key component.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridPartKeyViewConfig extends GridPartKeyViewBase {
    private final static Logger LOGGER = LoggerFactory.getLogger(GridPartKeyViewConfig.class);
    private ImageView imageViewOptionType;
    private Button buttonSimulateActions;
    private Glyph warningGlyphNoAction;

    @Override
    public void initUI() {
        super.initUI();
        //Select option
        SelectableOption<GridPartKeyComponent> selectableOption = new SelectableOption<>(this.model);
        this.getChildren().add(selectableOption);
        selectableOption.bindSize(this);
        //Key option
        this.imageViewOptionType = new ImageView();
        this.imageViewOptionType.setTranslateX(2.0);
        this.imageViewOptionType.setTranslateY(2.0);
        this.getChildren().add(this.imageViewOptionType);
        Consumer<KeyOptionI> keyOptionChanged = (option) -> {
            //Get the enum for the value
            if (option != null) {
                if (option.getIconUrl() != null) {
                    this.imageViewOptionType.setImage(IconHelper.get(option.getIconUrl(), 16, 16, true, true));
                } else {
                    this.imageViewOptionType.setImage(null);
                }
            }
            //Remove the icon
            else {
                this.imageViewOptionType.setImage(null);
            }
        };
        this.model.keyOptionProperty().addListener((obs, ov, nv) -> {
            keyOptionChanged.accept(nv);
        });
        keyOptionChanged.accept(this.model.keyOptionProperty().get());

        warningGlyphNoAction = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.WARNING).size(12).color(LCGraphicStyle.THIRD_DARK);
        warningGlyphNoAction.setOpacity(0.8);
        Tooltip tooltipWarning = FXControlUtils.createTooltip(Translation.getText(
                "tooltip.warning.no.action.on.key.edit.mode"));
        tooltipWarning.setShowDelay(Duration.millis(0));
        Tooltip.install(warningGlyphNoAction, tooltipWarning);
        warningGlyphNoAction.translateXProperty().bind(widthProperty().subtract(15.0));
        this.getChildren().add(warningGlyphNoAction);

        // Execute move action
        this.buttonSimulateActions = new Button();
        ButtonComponentOption.applyButtonBaseStyle(this.buttonSimulateActions, LCGraphicStyle.SECOND_DARK, FontAwesome.Glyph.SHARE);
        this.buttonSimulateActions.translateXProperty().bind(widthProperty().subtract(buttonSimulateActions.getPrefWidth() + 5));
        this.getChildren().add(this.buttonSimulateActions);
    }

    @Override
    public void initBinding() {
        super.initBinding();
        InvalidationListener invalidationListenerMoveAction = inv -> {
            this.buttonSimulateActions.setVisible(
                    // Key list actions
                    KeyListController.INSTANCE.isKeySimulatedAsKeyListActions(this.model)
                            // Classic move actions
                            || this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, MoveToGridAction.class) != null
                            || this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, MoveToKeyAction.class) != null
                            || this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextPageInStackAction.class) != null
                            || this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, PreviousPageInStackAction.class) != null
                            || this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextPageAndLoopInStackAction.class) != null
                            || this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, MoveToGridAndGoBackAction.class) != null
            );
        };
        invalidationListenerMoveAction.invalidated(null);
        this.model.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).addListener(invalidationListenerMoveAction);
        this.model.getActionManager().componentActions().get(UseActionEvent.OVER).addListener(invalidationListenerMoveAction);
        model.keyOptionProperty().addListener(invalidationListenerMoveAction);
        final ChangeListener<KeyOptionI> keyOptionChangeListener = (obs, ov, nv) -> {
            if (ov instanceof KeyListNodeKeyOption) {
                ((KeyListNodeKeyOption) ov).currentSimplerKeyContentContainerProperty().removeListener(invalidationListenerMoveAction);
            }
            if (nv instanceof KeyListNodeKeyOption) {
                ((KeyListNodeKeyOption) nv).currentSimplerKeyContentContainerProperty().addListener(invalidationListenerMoveAction);
            }
        };
        keyOptionChangeListener.changed(null, null, model.keyOptionProperty().get());
        model.keyOptionProperty().addListener(keyOptionChangeListener);

        warningGlyphNoAction.visibleProperty().bind(
                Bindings.createBooleanBinding(
                        () -> model.keyOptionProperty().get() instanceof BasicKeyOption && !model.getActionManager().containsActions(),
                        this.model.getActionManager().componentActions().get(UseActionEvent.ACTIVATION),
                        this.model.getActionManager().componentActions().get(UseActionEvent.OVER),
                        this.model.keyOptionProperty()
                )
        );
    }

    @Override
    public void initListener() {
        super.initListener();
        //Selection
        this.setOnMouseClicked((ea) -> {
            //On alt down : try to execute move actions
            if (ea.isAltDown()) {
                this.executeActionSimulations();
            }
            //Select key
            else {
                SelectionController.INSTANCE.selectKeyComponent(this.model, false, ea.isShortcutDown(), ea.isShiftDown());
                this.toFront();
            }
        });
        //On mouse entered/exited, update show possible selected / update loaded image (if needed)
        this.setOnMouseEntered((ea) -> this.model.showPossibleSelectedProperty().set(true));
        this.setOnMouseExited((ea) -> this.model.showPossibleSelectedProperty().set(false));

        //Drag over key : accept image and components
        this.setOnDragOver((ea) -> {
            if (isCopyingRawImagesFromComputer(ea) || DragController.INSTANCE.currentDraggedKeyProperty().get() != null) {
                ea.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        });
        this.setOnDragDropped((ea) -> {
            //Accept other key
            GridPartKeyComponentI dragged = DragController.INSTANCE.currentDraggedKeyProperty().get();
            if (dragged != null) {
                ConfigActionController.INSTANCE.executeAction(new InverseKeyAction(this.model, dragged));
                DragController.INSTANCE.currentDraggedKeyProperty().set(null);
            }
            //Accept raw images (if change image is enabled)
            if (isCopyingRawImagesFromComputer(ea) && !this.model.keyOptionProperty().get().disableImageProperty().get()) {
                Optional<File> firstValidImage = ea.getDragboard().getFiles().stream().filter(IOUtils::isSupportedImage).findFirst();
                if (firstValidImage.isPresent()) {
                    try {
                        if (this.model.keyOptionProperty().get() == null || !this.model.keyOptionProperty().get().disableImageProperty().get()) {
                            ImageElementI imageElement = ImageDictionaries.INSTANCE.getOrAddToUserImagesDictionary(firstValidImage.get());
                            ConfigActionController.INSTANCE.executeAction(new ChangeImageAction(this.model, imageElement, false));
                        }
                    } catch (Exception e) {
                        LOGGER.warn("Couldn't add dragged image to gallery", e);
                    }
                }
            }

        });
        //On drag start : drag the key
        this.setOnDragDetected((ea) -> {
            Dragboard dragboard = this.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            //Tale a snapshot of the key as dragged image
            content.putImage(this.snapshot(null, null));
            dragboard.setContent(content);
            //Set the dragged key
            DragController.INSTANCE.currentDraggedKeyProperty().set(this.model);
        });
        this.buttonSimulateActions.setOnAction(e -> executeActionSimulations());
    }

    private boolean isCopyingRawImagesFromComputer(DragEvent ea) {
        return ea.getTransferMode() == TransferMode.MOVE
                || ea.getTransferMode() == TransferMode.COPY && ea.getDragboard() != null && ea.getDragboard().getFiles() != null
                && !ea.getDragboard().getFiles().isEmpty() && ea.getDragboard().getFiles().stream().anyMatch(IOUtils::isSupportedImage);
    }

    /**
     * On alt shortcut : try to simulation actions
     */
    private void executeActionSimulations() {
        if (KeyListController.INSTANCE.isKeySimulatedAsKeyListActions(model)) {
            if (KeyListController.INSTANCE.simulateKeyListKeyActions(model)) {
                return;
            }
        }
        MoveToGridAction moveGrid = this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, MoveToGridAction.class);
        MoveToKeyAction moveKey = this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, MoveToKeyAction.class);
        NextPageInStackAction nextPageStackAction = this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextPageInStackAction.class);
        NextPageAndLoopInStackAction nextPageAndLoopInStackAction = this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextPageAndLoopInStackAction.class);
        PreviousPageInStackAction previousPageAction = this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, PreviousPageInStackAction.class);
        MoveToGridAndGoBackAction moveToGridAndGoBackAction = this.model.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, MoveToGridAndGoBackAction.class);
        if (moveGrid != null) {
            GridComponentI targetGrid = moveGrid.targetGridProperty().get();
            if (targetGrid != null) {
                SelectionController.INSTANCE.selectDisplayableComponent(targetGrid, true);
            }
        } else if (moveKey != null) {
            GridPartKeyComponentI targetKey = moveKey.targetKeyProperty().get();
            if (targetKey != null) {
                SelectionController.INSTANCE.selectKeyComponent(targetKey, true, false, false);
            }
        } else if (nextPageStackAction != null) {
            StackComponentI targetStack = nextPageStackAction.changedPageParentStackProperty().get();
            if (targetStack != null) {
                GridComponentI nextComponent = targetStack.getNextComponent();
                if (nextComponent != null) {
                    SelectionController.INSTANCE.selectDisplayableComponent(nextComponent, true);
                }
            }
        } else if (previousPageAction != null) {
            StackComponentI targetStack = previousPageAction.changedPageParentStackProperty().get();
            if (targetStack != null) {
                GridComponentI previousComponent = targetStack.getPreviousComponent();
                if (previousComponent != null) {
                    SelectionController.INSTANCE.selectDisplayableComponent(previousComponent, true);
                }
            }
        } else if (nextPageAndLoopInStackAction != null) {
            StackComponentI targetStack = nextPageAndLoopInStackAction.changedPageParentStackProperty().get();
            if (targetStack != null) {
                GridComponentI nextComponent = targetStack.getNextComponent();
                if (nextComponent != null) {
                    SelectionController.INSTANCE.selectDisplayableComponent(nextComponent, true);
                } else if (!targetStack.getComponentList().isEmpty()) {
                    SelectionController.INSTANCE.selectDisplayableComponent(targetStack.getComponentList().get(0), true);
                }
            }
        } else if (moveToGridAndGoBackAction != null) {
            final GridComponentI targetGrid = moveToGridAndGoBackAction.targetGridProperty().get();
            if (targetGrid != null) {
                SelectionController.INSTANCE.selectDisplayableComponent(targetGrid, true);
            }
        }
    }
}
