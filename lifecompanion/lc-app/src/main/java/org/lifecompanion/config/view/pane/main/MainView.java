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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.RootGraphicComponentI;
import org.lifecompanion.api.ui.AddTypeEnum;
import org.lifecompanion.api.ui.ViewProviderI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.control.refacto.AppMode;
import org.lifecompanion.base.data.control.refacto.AppModeController;
import org.lifecompanion.config.data.action.impl.GlobalActions;
import org.lifecompanion.config.data.action.impl.LCConfigurationActions;
import org.lifecompanion.config.data.action.impl.OptionActions;
import org.lifecompanion.config.data.action.impl.OptionActions.AddRootComponentAction;
import org.lifecompanion.config.data.component.profile.ProfileConfigSelectionController;
import org.lifecompanion.config.data.component.profile.ProfileConfigStep;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.DragController;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.framework.commons.translation.Translation;
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

    private VBox noConfigurationPlaceholder;

    private Hyperlink linkCreateBlank, linkOpenConfiguration, linkCreateModel, linkImportConfiguration;

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

        final Label labelMessage = new Label(Translation.getText("no.configuration.placeholder.message"));
        labelMessage.getStyleClass().addAll("text-font-size-120", "text-label-center");
        VBox.setMargin(labelMessage, new Insets(0.0, 0.0, 5.0, 0.0));
        linkCreateBlank = createActionLink("no.configuration.placeholder.link.create.blank");
        linkOpenConfiguration = createActionLink("no.configuration.placeholder.link.open");
        linkCreateModel = createActionLink("no.configuration.placeholder.link.create.from.model");
        linkImportConfiguration = createActionLink("no.configuration.placeholder.link.import.configuration");
        noConfigurationPlaceholder = new VBox(6.0, labelMessage, linkOpenConfiguration, linkImportConfiguration, linkCreateModel, linkCreateBlank);
        noConfigurationPlaceholder.setAlignment(Pos.CENTER);
        noConfigurationPlaceholder.prefWidthProperty().bind(scrollcenter.widthProperty().subtract(20.0));

        // Quick actions buttons
        this.buttonResetSelection = this.createQuickActionButton(false, "tooltip.quick.action.clear.selection", FontAwesome.Glyph.REMOVE);
        StackPane.setAlignment(this.buttonResetSelection, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(this.buttonResetSelection, new Insets(0.0, 45.0, 15.0, 0.0));
        this.buttonGoUseMode = this.createQuickActionButton(true, "tooltip.quick.action.go.use.mode", FontAwesome.Glyph.PLAY);
        StackPane.setAlignment(this.buttonGoUseMode, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(this.buttonGoUseMode, new Insets(0, 15.0, 15.0, 0.0));
        this.getChildren().addAll(this.buttonResetSelection, this.buttonGoUseMode);

        displayNoConfigurationMessage();
    }

    private Hyperlink createActionLink(String translation) {
        final Hyperlink hyperlink = new Hyperlink(Translation.getText(translation));
        hyperlink.getStyleClass().add("text-font-size-120");
        return hyperlink;
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
        this.buttonGoUseMode.visibleProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationProperty().isNotNull());
        // On configuration change, display the new configuration
        AppModeController.INSTANCE.getEditModeContext().configurationProperty()
                .addListener((observableP, oldValueP, newValueP) -> {
                    if (oldValueP != null) {
                        oldValueP.clearViewCache();
                        // Fix memory leak : remove previous children
                        final Node previousContent = this.scrollcenter.getContent();
                        if (previousContent instanceof Group) {
                            ((Group) previousContent).getChildren().clear();
                        }
                        this.scrollcenter.setContent(null);
                    }
                    if (newValueP != null) {
                        displayConfiguration(newValueP);
                    } else {
                        displayNoConfigurationMessage();
                    }
                });

    }

    private void displayConfiguration(LCConfigurationI newValueP) {
        Scale scaleTransform = new Scale();
        scaleTransform.xProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationScaleProperty());
        scaleTransform.yProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationScaleProperty());
        final Region viewForNewValue = ViewProviderI.getComponentView(newValueP, AppMode.EDIT).getView();
        Group group = new Group(viewForNewValue);
        viewForNewValue.getTransforms().add(scaleTransform);
        this.scrollcenter.setContent(group);
    }

    private void displayNoConfigurationMessage() {
        scrollcenter.setContent(noConfigurationPlaceholder);
        scrollcenter.setFitToHeight(true);
        scrollcenter.setFitToHeight(true);
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
                    AppModeController.INSTANCE.getEditModeContext().zoomIn();
                } else {
                    AppModeController.INSTANCE.getEditModeContext().zoomOut();
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
                    if (dragged != null && AppModeController.INSTANCE.getEditModeContext().getConfiguration() != null) {
                        double scale = AppModeController.INSTANCE.getEditModeContext().configurationScaleProperty().get();
                        // Center the component
                        dragged.xProperty()
                                .set(Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP,
                                        (contentBounds.getWidth() - viewportBounds.getWidth()) * this.scrollcenter.getHvalue() + ea.getX() * (1.0 / scale)
                                                - dragged.widthProperty().get() / 2.0));
                        dragged.yProperty()
                                .set(Math.max(LCConstant.CONFIG_ROOT_COMPONENT_GAP,
                                        (contentBounds.getHeight() - viewportBounds.getHeight()) * this.scrollcenter.getVvalue()
                                                + ea.getY() * (1.0 / scale) - dragged.heightProperty().get() / 2.0));
                        // Do add
                        OptionActions.AddRootComponentAction action = new AddRootComponentAction(this,
                                AppModeController.INSTANCE.getEditModeContext().configurationProperty().get(), dragged);
                        ConfigActionController.INSTANCE.executeAction(action);
                    }
                    // Remove added
                    DragController.INSTANCE.resetCurrentDraggedComp();
                }
            } catch (Throwable t) {
                LOGGER.error("Problem when dragging a component to main view", t);
            }
        });
        linkCreateBlank.setOnAction(e -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.NewConfigInListAction()));
        linkCreateModel.setOnAction(e -> ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_ADD_FROM_DEFAULT, null, null));
        linkOpenConfiguration.setOnAction(e -> ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_LIST, null, null));
        linkImportConfiguration.setOnAction(LCConfigurationActions.HANDLER_IMPORT_OPEN);
    }
}
