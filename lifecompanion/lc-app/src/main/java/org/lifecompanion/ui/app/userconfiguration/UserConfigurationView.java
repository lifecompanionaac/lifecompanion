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
package org.lifecompanion.ui.app.userconfiguration;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.model.Triple;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.ui.common.pane.generic.AnimatedBorderPane;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.io.task.LoadUserConfigTask;
import org.lifecompanion.controller.io.task.SaveUserConfigTask;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View to display and set {@link UserConfigurationController}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserConfigurationView extends BorderPane implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserConfigurationView.class);
    private final Stage parentStage;
    private AnimatedBorderPane viewContentBorderPane;
    private UserConfigSubmenuI currentView;
    private final List<UserConfigSubmenuI> userConfigTabs;
    private VBox boxMenuLeft;
    private Button buttonOk, buttonCancel;
    private Label labelTitle;
    private Node nodePreviousIndicator;
    private final Map<UserConfigSubmenuI, Label> stepButtons;

    public UserConfigurationView(final Stage parentStage) {
        this.parentStage = parentStage;
        this.stepButtons = new HashMap<>();
        this.userConfigTabs = new ArrayList<>();
        this.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.initAll();
        this.showTab(userConfigTabs.get(0));
    }

    // Class part : "View"
    //========================================================================
    @Override
    public void initUI() {
        // Left part : menu
        boxMenuLeft = new VBox();
        boxMenuLeft.setPrefWidth(200.0);
        boxMenuLeft.setAlignment(Pos.TOP_LEFT);
        boxMenuLeft.getStyleClass().add("general-config-menu-pane");

        //Tabs
        this.addConfigTab(new UIConfigSubmenu());
        this.addConfigTab(new AboutSubmenu());
        this.addConfigTab(new PluginConfigSubmenu());
        this.addConfigTab(new MiscConfigSubmenu());

        // Center top : title and previous button
        Triple<HBox, Label, Node> header = FXControlUtils.createHeader("", e -> this.parentStage.hide());
        labelTitle = header.getMiddle();
        nodePreviousIndicator = header.getRight();
        boxMenuLeft.setPadding(new Insets(50.0, 0.0, 0.0, 0.0));

        // Center main : step view display
        viewContentBorderPane = new AnimatedBorderPane();

        // Center bottom : ok, cancel buttons
        buttonOk = FXControlUtils.createLeftTextButton(Translation.getText("general.configuration.scene.ok.button"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHECK).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        buttonCancel = FXControlUtils.createLeftTextButton(Translation.getText("general.configuration.scene.cancel.button"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TIMES).size(16).color(LCGraphicStyle.SECOND_DARK), null);
        HBox buttonBox = new HBox(buttonCancel, buttonOk);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        BorderPane.setMargin(buttonBox, new Insets(10.0));

        // Center : put it together
        BorderPane borderPaneCenter = new BorderPane();
        borderPaneCenter.setTop(header.getLeft());
        borderPaneCenter.setCenter(viewContentBorderPane);
        borderPaneCenter.setBottom(buttonBox);

        this.setLeft(boxMenuLeft);
        this.setCenter(borderPaneCenter);
    }

    private void addConfigTab(UserConfigSubmenuI configTab) {
        if (configTab != null) {
            this.userConfigTabs.add(configTab);
            BorderPane.setMargin(configTab.getView(), new Insets(20.0));

            // Create menu button
            Label button = new Label(Translation.getText(configTab.getTabTitleId()));
            button.setMaxWidth(Double.MAX_VALUE);
            button.getStyleClass().add("general-config-menu-button");

            stepButtons.put(configTab, button);

            // Listener on button to show step
            button.setOnMouseClicked(m -> this.showTab(configTab));

            // Order buttons from their preferred relative order
            boxMenuLeft.getChildren().add(button);
        }
    }

    public void showTab(UserConfigSubmenuI tab) {
        // Handle previous step
        if (currentView != null) {
            Label stepButton = stepButtons.get(currentView);
            if (stepButton != null) {
                stepButton.getStyleClass().remove("general-config-menu-button-selected");
            }
            currentView = null;
        }
        // Handle new step
        if (tab != null) {
            currentView = tab;
            this.labelTitle.setText(Translation.getText(tab.getTabTitleId()));
            this.nodePreviousIndicator.setVisible(true);
            Label stepButton = stepButtons.get(tab);
            if (stepButton != null) {
                stepButton.getStyleClass().add("general-config-menu-button-selected");
            }
            this.viewContentBorderPane.changeCenter(tab.getView());
        }
    }
    //========================================================================

    @Override
    public void initListener() {
        this.buttonOk.setOnAction(e -> {
            this.userConfigTabs.forEach(UserConfigSubmenuI::updateModel);
            this.parentStage.hide();
            AsyncExecutorController.INSTANCE.addAndExecute(false, false, new SaveUserConfigTask());
        });
        this.buttonCancel.setOnAction(e -> {
            this.userConfigTabs.forEach(UserConfigSubmenuI::cancel);
            this.parentStage.hide();
        });
    }

    // Class part : "Update"
    //========================================================================
    public void showView() {
        //Loading param task
        LoadUserConfigTask loadTask = new LoadUserConfigTask();
        loadTask.setOnSucceeded(event -> {
            this.userConfigTabs.forEach(UserConfigSubmenuI::updateFields);
            this.parentStage.show();
        });
        AsyncExecutorController.INSTANCE.addAndExecute(false, false, loadTask);
    }
    //========================================================================

}
