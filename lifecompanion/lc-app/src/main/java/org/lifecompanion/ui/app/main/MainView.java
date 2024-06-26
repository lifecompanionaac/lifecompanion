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
package org.lifecompanion.ui.app.main;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
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
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.editaction.GlobalActions;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.editaction.OptionActions;
import org.lifecompanion.controller.editaction.OptionActions.AddRootComponentAction;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.DragController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigStep;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.RootGraphicComponentI;
import org.lifecompanion.model.api.ui.configurationcomponent.ViewProviderI;
import org.lifecompanion.model.api.ui.editmode.AddTypeEnum;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.javafx.FXControlUtils;
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

        final Label labelMessage1 = new Label(Translation.getText("no.configuration.placeholder.message1"));
        labelMessage1.getStyleClass().addAll("text-font-size-120", "text-label-center", "text-weight-bold");
        final Label labelMessage2 = new Label(Translation.getText("no.configuration.placeholder.message2"));
        labelMessage2.getStyleClass().addAll("text-font-size-120", "text-label-center");
        VBox.setMargin(labelMessage2, new Insets(0.0, 0.0, 8.0, 0.0));
        linkCreateBlank = createActionLink("no.configuration.placeholder.link.create.blank");
        linkOpenConfiguration = createActionLink("no.configuration.placeholder.link.open");
        linkCreateModel = createActionLink("no.configuration.placeholder.link.create.from.model");
        linkImportConfiguration = createActionLink("no.configuration.placeholder.link.import.configuration");
        noConfigurationPlaceholder = new VBox(4.0, labelMessage1, labelMessage2, linkOpenConfiguration, linkImportConfiguration, linkCreateModel, linkCreateBlank);
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
        Button button = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(glyph).size(12).color(Color.WHITE), tooltipId);
        Circle buttonShape = new Circle(1.0);// Radius is ignored when != 0
        button.setShape(buttonShape);
        button.setCenterShape(true);
        button.getStyleClass().addAll("opacity-90", "hidden-disabled");
        button.setStyle("-fx-background-color: " + (primary ? "-fx-main-dark" : "-fx-second-dark"));
        return button;
    }

    /**
     * Init binding with the model
     */
    @Override
    public void initBinding() {
        this.buttonResetSelection.disableProperty().bind(SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().isNull());
        this.buttonGoUseMode.visibleProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationProperty().isNotNull());
        // On configuration change, display the new configuration
        AppModeController.INSTANCE.getEditModeContext().configurationProperty().addListener((observableP, oldValueP, newValueP) -> {
            if (oldValueP != null) {
                oldValueP.clearAllComponentViewCache();
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
        final Region viewForNewValue = ViewProviderI.getOrCreateViewComponentFor(newValueP, AppMode.EDIT).getView();
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
        linkCreateBlank.setOnAction(e -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.NewEditInListAction()));
        linkCreateModel.setOnAction(e -> ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_ADD_FROM_DEFAULT, null, null));
        linkOpenConfiguration.setOnAction(e -> ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_LIST, null, null));
        linkImportConfiguration.setOnAction(LCConfigurationActions.HANDLER_IMPORT_OPEN);
    }
}
