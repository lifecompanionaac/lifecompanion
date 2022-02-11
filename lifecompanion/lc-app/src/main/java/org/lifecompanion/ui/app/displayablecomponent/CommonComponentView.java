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
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.controller.resource.IconManager;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.common.util.UndoRedoTextInputWrapper;
import org.lifecompanion.controller.editaction.BaseComponentAction;
import org.lifecompanion.controller.editaction.BaseComponentAction.ChangeComponentNameAction;
import org.lifecompanion.controller.editaction.UserCompActions.CreateOrUpdateUserComp;
import org.lifecompanion.controller.resource.LCGlyphFont;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.NodeSnapshotCache;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
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
     * Button to create a {@link UserCompDescriptionI}
     */
    private Button buttonSaveComponent;

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
        /**
         * Label use to display informations, or name
         */
        Label labelName = new Label(Translation.getText("component.name.custom"));
        labelDefaultName = new Label();
        labelDefaultName.getStyleClass().add("explain-text");

        this.fieldName = new TextField();
        UIUtils.createAndAttachTooltip(fieldName, "tooltip.explain.component.name.custom");
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

        //Button to save component
        this.buttonSaveComponent = UIUtils.createLeftTextButton(Translation.getText("menu.select.save.component"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.SAVE).size(18).color(LCGraphicStyle.MAIN_DARK),
                "tooltip.menu.select.save.component");
        Separator sep = new Separator(Orientation.HORIZONTAL);

        // View content
        totalBox.getChildren().addAll(this.imageViewComponent, labelDefaultName, labelName, boxFieldName, sep, this.buttonSaveComponent);

        // Button ok (hide stage)
        buttonOk = UIUtils.createLeftTextButton(Translation.getText("image.use.button.ok"),
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.CHECK).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        HBox buttonBox = new HBox(buttonOk);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        BorderPane.setMargin(buttonBox, new Insets(0.0, 0.0, 5.0, 0.0));

        this.setCenter(totalBox);
        this.setBottom(buttonBox);

        this.setPadding(new Insets(10.0));
    }

    @Override
    public void initListener() {
        this.buttonOk.setOnAction(ev -> UIUtils.getSourceWindow(this).hide());
        this.fieldNameWrapper.setListener((oldV, newV) -> {
            if (currentComponent != null) {
                ChangeComponentNameAction textAction = new BaseComponentAction.ChangeComponentNameAction(currentComponent, oldV, newV);
                ConfigActionController.INSTANCE.addAction(textAction);
            }
        });
        this.buttonSaveComponent.setOnAction(ea -> ConfigActionController.INSTANCE.executeAction(new CreateOrUpdateUserComp(currentComponent)));
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
            NodeSnapshotCache.INSTANCE.requestSnapshot(component, -1, -1, (c, img) -> {
                if (c == currentComponent) {
                    imageViewComponent.setImage(img);
                }
            });
            imageViewComponentType.setImage(component.getNodeType().isIconValid() ? IconManager.get(component.getNodeType().getIconPath()) : null);
            this.labelDefaultName.setText(Translation.getText("component.name.custom.default.name", component.defaultNameProperty().get()));
            this.fieldName.textProperty().bindBidirectional(component.userNameProperty());
            this.fieldNameWrapper.clearPreviousValue();
            fieldName.requestFocus();
        }
    }

    public void hide() {
        if (currentComponent != null) {
            NodeSnapshotCache.INSTANCE.cancelRequestSnapshot(currentComponent);
            this.fieldName.textProperty().unbindBidirectional(currentComponent.userNameProperty());
        }
        this.fieldName.clear();
        imageViewComponentType.setImage(null);
        imageViewComponent.setImage(null);
        currentComponent = null;
    }
    //========================================================================
}
