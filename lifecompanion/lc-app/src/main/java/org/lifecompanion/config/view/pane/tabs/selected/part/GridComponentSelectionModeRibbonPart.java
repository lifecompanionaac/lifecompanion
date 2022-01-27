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
package org.lifecompanion.config.view.pane.tabs.selected.part;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.api.component.definition.SelectionModeUserI;
import org.lifecompanion.base.data.common.Triple;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.control.refacto.AppModeController;
import org.lifecompanion.base.view.reusable.AnimatedBorderPane;
import org.lifecompanion.config.data.action.impl.SelectionModeActions;
import org.lifecompanion.config.data.common.LCConfigBindingUtils;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.pane.selection.SelectionModeMainParamView;
import org.lifecompanion.config.view.pane.selection.SelectionModeSuppParamView;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.Optional;

/**
 * // TODO - it should be transferred some day with the new selection mode config stage > this is not used a lot !
 * Ribbon part to change the selection mode
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridComponentSelectionModeRibbonPart extends RibbonBasePart<SelectionModeUserI> implements LCViewInitHelper {

    private static final double DIALOG_WIDTH = 600.0, DIALOG_HEIGHT = 600.0;

    /**
     * Toggle to enable/disable parent selection mode use
     */
    private ToggleSwitch toggleEnableParentSelectionMode;

    private AnimatedBorderPane configStageAnimatedBorderPane;
    private Dialog<ButtonType> configStageDialogPane;

    private Button buttonOpenSelectionModeConfiguration;

    private SelectionModeMainParamView selectionModeMainParamView;
    private SelectionModeSuppParamView selectionModeSuppParamView;

    /**
     * Change listener for parent mode
     */
    private ChangeListener<Boolean> changeListenerUseParentMode;


    public GridComponentSelectionModeRibbonPart() {
        this.initAll();
    }

    @Override
    public void initUI() {
        selectionModeMainParamView = new SelectionModeMainParamView(e -> {
            selectionModeSuppParamView.setSelectedSelectionMode(selectionModeMainParamView.getSelectedSelectionMode());
            configStageAnimatedBorderPane.changeCenter(selectionModeSuppParamView);
        });
        selectionModeMainParamView.setPrefWidth(DIALOG_WIDTH - 10.0);
        selectionModeMainParamView.setPrefHeight(DIALOG_HEIGHT - 60.0);
        selectionModeSuppParamView = new SelectionModeSuppParamView();
        selectionModeSuppParamView.setPrefWidth(DIALOG_WIDTH - 10.0);

        this.buttonOpenSelectionModeConfiguration = UIUtils.createTextButtonWithGraphics(Translation.getText("selection.mode.use.configuration.grid.config.button"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).sizeFactor(2).color(LCGraphicStyle.MAIN_DARK), "selection.mode.use.configuration.grid.config.button");
        buttonOpenSelectionModeConfiguration.setTextAlignment(TextAlignment.CENTER);

        this.toggleEnableParentSelectionMode = ConfigUIUtils.createToggleSwitch("selection.mode.use.configuration.mode",
                "tooltip.explain.parent.selection.mode.grid");

        VBox boxElements = new VBox(10.0, toggleEnableParentSelectionMode, new Separator(Orientation.HORIZONTAL), buttonOpenSelectionModeConfiguration);
        boxElements.setAlignment(Pos.CENTER);

        this.setTitle(Translation.getText("pane.title.selection.mode"));
        this.setContent(boxElements);
    }

    @Override
    public void initListener() {
        this.changeListenerUseParentMode = LCConfigBindingUtils.createSimpleBinding(this.toggleEnableParentSelectionMode.selectedProperty(),
                this.model, m -> m.useParentSelectionModeProperty().get(), (m, e) -> new SelectionModeActions.ChangeUseParentAction(AppModeController.INSTANCE.getEditModeContext().configurationProperty().get(), m, e));
        this.buttonOpenSelectionModeConfiguration.setOnAction(e -> {
            showConfigStage();
        });
    }


    private void showConfigStage() {
        initConfigStage();

        // Bind this model to configurations views
        selectionModeMainParamView.modelProperty().set(model.get());
        selectionModeSuppParamView.modelProperty().set(model.get());

        // Show dialog with first view
        configStageAnimatedBorderPane.setEnableTransition(false);
        configStageAnimatedBorderPane.changeCenter(selectionModeMainParamView);
        configStageAnimatedBorderPane.setEnableTransition(true);
        Optional<ButtonType> buttonType = configStageDialogPane.showAndWait();

        // Save on OK only
        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            selectionModeMainParamView.saveChanges();
            selectionModeSuppParamView.saveChanges();
        }

        // Unbind/clear models
        selectionModeMainParamView.modelProperty().set(null);
        selectionModeSuppParamView.modelProperty().set(null);
    }

    private void initConfigStage() {
        if (configStageDialogPane == null) {
            configStageDialogPane = ConfigUIUtils.createAlert(buttonOpenSelectionModeConfiguration, null);
            configStageDialogPane.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
            configStageDialogPane.getDialogPane().getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
            configStageDialogPane.getDialogPane().getStyleClass().addAll("selection-mode-config-dialog");
            configStageDialogPane.setWidth(DIALOG_WIDTH);
            configStageDialogPane.setWidth(DIALOG_HEIGHT);

            configStageAnimatedBorderPane = new AnimatedBorderPane();
            configStageDialogPane.getDialogPane().setContent(configStageAnimatedBorderPane);

            Triple<HBox, Label, Node> header = ConfigUIUtils.createHeader("Mode de sélection pour la grille", previous -> configStageAnimatedBorderPane.changeCenter(selectionModeMainParamView));
            header.getRight().visibleProperty().bind(configStageAnimatedBorderPane.centerProperty().isEqualTo(selectionModeSuppParamView));
            configStageAnimatedBorderPane.setTop(header.getLeft());
        }
    }

    @Override
    public void initBinding() {
        SelectionController.INSTANCE.selectedComponentProperty()
                .addListener((observableP, oldValueP, newValueP) -> {
                    if (newValueP instanceof SelectionModeUserI) {
                        this.model.set((SelectionModeUserI) newValueP);
                    } else {
                        this.model.set(null);
                    }
                });
        buttonOpenSelectionModeConfiguration.disableProperty().bind(toggleEnableParentSelectionMode.selectedProperty());
    }

    @Override
    public void bind(final SelectionModeUserI model) {
        this.toggleEnableParentSelectionMode.setSelected(model.useParentSelectionModeProperty().get());
        model.useParentSelectionModeProperty().addListener(this.changeListenerUseParentMode);
    }

    @Override
    public void unbind(final SelectionModeUserI model) {
        model.useParentSelectionModeProperty().removeListener(this.changeListenerUseParentMode);
    }
}
