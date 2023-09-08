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
package org.lifecompanion.ui.app.displayablecomponent;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.editaction.BaseComponentAction;
import org.lifecompanion.controller.editaction.BaseComponentAction.ChangeComponentNameAction;
import org.lifecompanion.controller.editaction.UserCompActions.CreateOrUpdateUserComp;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.DisplayableComponentSnapshotController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.common.util.UndoRedoTextInputWrapper;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Common base for a lot of selected component.<br>
 * Show selected component.
 * <p>
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CommonComponentView extends BorderPane implements LCViewInitHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonComponentView.class);

    private final static double COMP_IMAGE_HEIGHT = 150.0;

    /**
     * Field to display/edit component name
     */
    private TextField fieldName;

    /**
     * To display current component
     */
    private ImageView imageViewComponent;

    /**
     * To display component type icon
     */
    private ImageView imageViewComponentType;

    /**
     * Button to hide the stage
     */
    private Button buttonOk;

    private Label labelDefaultName;

    /**
     * Container for name field
     */
    private UndoRedoTextInputWrapper fieldNameWrapper;


    public CommonComponentView() {
        this.initAll();
    }

    @Override
    public void initUI() {
        VBox totalBox = new VBox(6.0);
        totalBox.setAlignment(Pos.CENTER);
        //Name
        Label labelName = new Label(Translation.getText("component.name.custom"));
        labelDefaultName = new Label();
        labelDefaultName.getStyleClass().addAll("text-font-italic", "text-fill-gray", "text-wrap-enabled");

        this.fieldName = new TextField();
        FXControlUtils.createAndAttachTooltip(fieldName, "tooltip.explain.component.name.custom");
        this.fieldNameWrapper = new UndoRedoTextInputWrapper(this.fieldName, ConfigActionController.INSTANCE.undoRedoEnabled());
        HBox.setHgrow(fieldName, Priority.ALWAYS);

        imageViewComponentType = new ImageView();

        HBox boxFieldName = new HBox(10.0, imageViewComponentType, fieldName);
        boxFieldName.setAlignment(Pos.CENTER);

        this.imageViewComponent = new ImageView();
        this.imageViewComponent.setFitHeight(COMP_IMAGE_HEIGHT);
        this.imageViewComponent.fitWidthProperty().bind(widthProperty().subtract(20.0));
        this.imageViewComponent.setPreserveRatio(true);
        this.imageViewComponent.setSmooth(true);

        // View content
        totalBox.getChildren().addAll(this.imageViewComponent, labelDefaultName, labelName, boxFieldName);

        // Button ok (hide stage)
        buttonOk = FXControlUtils.createLeftTextButton(Translation.getText("image.use.button.ok"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHECK).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        HBox buttonBox = new HBox(buttonOk);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        BorderPane.setMargin(buttonBox, new Insets(0.0, 0.0, 5.0, 0.0));

        this.setCenter(totalBox);
        this.setBottom(buttonBox);

        this.setPadding(new Insets(10.0));
    }

    @Override
    public void initListener() {
        this.buttonOk.setOnAction(ev -> FXUtils.getSourceWindow(this).hide());
        this.fieldNameWrapper.setListener((oldV, newV) -> {
            if (currentComponent != null) {
                ChangeComponentNameAction textAction = new BaseComponentAction.ChangeComponentNameAction(currentComponent, oldV, newV);
                ConfigActionController.INSTANCE.addAction(textAction);
            }
        });
        this.fieldNameWrapper.getTextControl().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                buttonOk.fire();
            }
        });
    }

    // Class part : "Bind/unbind"
    //========================================================================
    private DisplayableComponentI currentComponent;

    public void show(final DisplayableComponentI component) {
        currentComponent = component;
        if (component != null) {
            DisplayableComponentSnapshotController.INSTANCE.requestSnapshotAsync(component, false, -1, -1, (c, img) -> {
                if (c == currentComponent) {
                    imageViewComponent.setImage(img);
                }
            });
            imageViewComponentType.setImage(component.getNodeType().isIconValid() ? IconHelper.get(component.getNodeType().getIconPath()) : null);
            this.labelDefaultName.setText(Translation.getText("component.name.custom.default.name", component.defaultNameProperty().get()));
            this.fieldName.textProperty().bindBidirectional(component.userNameProperty());
            this.fieldNameWrapper.clearPreviousValue();
            fieldName.selectAll();
            fieldName.requestFocus();
        }
    }

    public void hide() {
        if (currentComponent != null) {
            DisplayableComponentSnapshotController.INSTANCE.cancelRequestSnapshot(currentComponent);
            this.fieldName.textProperty().unbindBidirectional(currentComponent.userNameProperty());
        }
        this.fieldName.clear();
        imageViewComponentType.setImage(null);
        imageViewComponent.setImage(null);
        currentComponent = null;
    }
    //========================================================================
}
