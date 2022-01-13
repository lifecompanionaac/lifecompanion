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
package org.lifecompanion.config.view.pane.main;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.api.component.definition.RootGraphicComponentI;
import org.lifecompanion.api.mode.AppMode;
import org.lifecompanion.api.ui.AddTypeEnum;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.config.data.action.impl.GlobalActions;
import org.lifecompanion.config.data.action.impl.OptionActions;
import org.lifecompanion.config.data.action.impl.OptionActions.AddRootComponentAction;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.DragController;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Software main pane content.<br>
 * Contains a scroll pane that contains the current configuration display.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MainView extends StackPane implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(MainView.class);

    /**
     * The scroll to view the current configuration
     */
    private ScrollPane scrollcenter;

    /**
     * Quick action button
     */
    private Button buttonResetSelection, buttonGoUseMode;

    /**
     * Create a main pane
     */
    public MainView() {
        this.initAll();
    }

    /**
     * Init UI components
     */
    @Override
    public void initUI() {
        this.scrollcenter = new ScrollPane();
        this.scrollcenter.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        this.scrollcenter.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        this.scrollcenter.prefWidthProperty().bind(this.widthProperty());
        this.scrollcenter.prefHeightProperty().bind(this.heightProperty());
        this.getChildren().add(this.scrollcenter);
        StackPane.setAlignment(this.scrollcenter, Pos.CENTER);

        // Quick actions buttons
        this.buttonResetSelection = this.createQuickActionButton(false, "tooltip.quick.action.clear.selection", FontAwesome.Glyph.REMOVE);
        StackPane.setAlignment(this.buttonResetSelection, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(this.buttonResetSelection, new Insets(0.0, 45.0, 15.0, 0.0));
        this.buttonGoUseMode = this.createQuickActionButton(true, "tooltip.quick.action.go.use.mode", FontAwesome.Glyph.PLAY);
        StackPane.setAlignment(this.buttonGoUseMode, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(this.buttonGoUseMode, new Insets(0, 15.0, 15.0, 0.0));
        this.getChildren().addAll(this.buttonResetSelection, this.buttonGoUseMode);
    }

    private Button createQuickActionButton(final boolean primary, final String tooltipId, final Enum<?> glyph) {
        Button button = UIUtils.createGraphicButton(LCGlyphFont.FONT_AWESOME.create(glyph).size(12).color(Color.WHITE), tooltipId);
        Circle buttonShape = new Circle(1.0);// Radius is ignored when != 0
        button.setShape(buttonShape);
        button.setCenterShape(true);
        button.getStyleClass().addAll("quick-action-button-base", primary ? "quick-action-button-primary" : "quick-action-button-secondary");
        button.setPrefSize(12.0, 12.0);
        return button;
    }

    /**
     * Init binding with the model
     */
    @Override
    public void initBinding() {
        this.buttonResetSelection.disableProperty().bind(SelectionController.INSTANCE.selectedComponentBothProperty().isNull());
        // On configuration change, display the new configuration
        AppController.INSTANCE.currentConfigConfigurationProperty()
                .addListener((observableP, oldValueP, newValueP) -> {
                    if (oldValueP != null) {
                        // Fix memory leak : remove previous children
                        final Node previousContent = this.scrollcenter.getContent();
                        if (previousContent instanceof Group) {
                            ((Group) previousContent).getChildren().clear();
                        }
                        this.scrollcenter.setContent(null);
                    }
                    if (newValueP != null) {
                        Scale scaleTransform = new Scale();
                        scaleTransform.xProperty().bind(AppController.INSTANCE.configurationScaleProperty());
                        scaleTransform.yProperty().bind(AppController.INSTANCE.configurationScaleProperty());
                        final Region viewForNewValue = newValueP.getDisplay(AppController.INSTANCE.getViewProvider(AppMode.CONFIG), true).getView();
                        Group group = new Group(viewForNewValue);
                        viewForNewValue.getTransforms().add(scaleTransform);
                        this.scrollcenter.setContent(group);
                    }
                });
    }

    /**
     * Init listener for this component
     */
    @Override
    public void initListener() {
        // Clear selection
        this.buttonResetSelection.setOnAction(a -> SelectionController.INSTANCE.clearSelection());
        this.buttonGoUseMode.setOnAction(GlobalActions.HANDLER_GO_USE_MODE);
        // Drag over : accept some object only
        this.scrollcenter.setOnDragOver((ea) -> {
            if (DragController.INSTANCE.isDragShouldBeAcceptedOn(AddTypeEnum.ROOT, false)) {
                ea.acceptTransferModes(TransferMode.ANY);
            }
        });
        this.addEventFilter(ScrollEvent.SCROLL, scrollEvent -> {
            if (scrollEvent.isShortcutDown()) {
                scrollEvent.consume();
                if (scrollEvent.getDeltaY() > 0) {
                    AppController.INSTANCE.zoomIn();
                } else {
                    AppController.INSTANCE.zoomOut();
                }
            }
        });
        // Get the dragged object
        this.scrollcenter.setOnDragDropped((ea) -> {
            try {
                Bounds viewportBounds = this.scrollcenter.getViewportBounds();
                Bounds contentBounds = this.scrollcenter.getContent().getBoundsInParent();
                if (DragController.INSTANCE.isDragComponentIsPresentOn(AddTypeEnum.ROOT)) {
                    RootGraphicComponentI dragged = DragController.INSTANCE.createNewCompFor(AddTypeEnum.ROOT);
                    RootGraphicComponentI component = dragged;
                    double scale = AppController.INSTANCE.configurationScaleProperty().get();
                    // Center the component
                    component.xProperty()
                            .set(Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP,
                                    (contentBounds.getWidth() - viewportBounds.getWidth()) * this.scrollcenter.getHvalue() + ea.getX() * (1.0 / scale)
                                            - component.widthProperty().get() / 2.0));
                    component.yProperty()
                            .set(Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP,
                                    (contentBounds.getHeight() - viewportBounds.getHeight()) * this.scrollcenter.getVvalue()
                                            + ea.getY() * (1.0 / scale) - component.heightProperty().get() / 2.0));
                    // Do add
                    OptionActions.AddRootComponentAction action = new AddRootComponentAction(this,
                            AppController.INSTANCE.currentConfigConfigurationProperty().get(), component);
                    ConfigActionController.INSTANCE.executeAction(action);

                    // Remove added
                    DragController.INSTANCE.resetCurrentDraggedComp();
                }
            } catch (Throwable t) {
                LOGGER.error("Problem when dragging a component to main view", t);
            }
        });
    }
}
