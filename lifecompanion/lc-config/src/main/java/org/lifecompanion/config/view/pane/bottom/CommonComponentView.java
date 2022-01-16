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
package org.lifecompanion.config.view.pane.bottom;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import org.controlsfx.glyphfont.FontAwesome;
import org.fxmisc.easybind.EasyBind;
import org.lifecompanion.api.component.definition.DisplayableComponentI;
import org.lifecompanion.api.component.definition.usercomp.UserCompDescriptionI;
import org.lifecompanion.api.ui.ComponentViewI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.view.reusable.UndoRedoTextInputWrapper;
import org.lifecompanion.base.view.reusable.impl.BaseConfigurationViewBorderPane;
import org.lifecompanion.config.data.action.impl.BaseComponentAction;
import org.lifecompanion.config.data.action.impl.BaseComponentAction.ChangeComponentNameAction;
import org.lifecompanion.config.data.action.impl.UserCompActions.CreateOrUpdateUserComp;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Common base for a lot of selected component.<br>
 * Show selected component.
 *
 * // FIXME : update component only on dialog display
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CommonComponentView extends BaseConfigurationViewBorderPane<DisplayableComponentI> implements LCViewInitHelper {
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

    /**
     * Label use to display informations, or name
     */
    private Label labelName;

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
        this.labelName = new Label(Translation.getText("component.name.custom"));
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
        totalBox.getChildren().addAll(this.imageViewComponent, labelDefaultName, this.labelName, boxFieldName, sep, this.buttonSaveComponent);

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
            if (this.model.get() != null) {
                ChangeComponentNameAction textAction = new BaseComponentAction.ChangeComponentNameAction(this.model.get(), oldV, newV);
                ConfigActionController.INSTANCE.addAction(textAction);
            }
        });
        this.buttonSaveComponent.setOnAction(ea -> ConfigActionController.INSTANCE.executeAction(new CreateOrUpdateUserComp(this.model.get())));
        this.fieldNameWrapper.getTextControl().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                buttonOk.fire();
            }
        });
    }

    @Override
    public void initBinding() {
        model.bind(SelectionController.INSTANCE.selectedComponentBothProperty());
        model.addListener((obs, ov, nv) -> updateComponentImage(nv));
        labelDefaultName.textProperty().bind(TranslationFX.getTextBinding("component.name.custom.default.name", EasyBind.select(model).selectObject(DisplayableComponentI::defaultNameProperty)));
    }

    private void updateComponentImage(DisplayableComponentI nv) {
        if (nv != null && AppController.INSTANCE.getViewForCurrentMode(nv) != null) {
            imageViewComponentType.setImage(nv.getNodeType().isIconValid() ? IconManager.get(nv.getNodeType().getIconPath()) : null);
            final ComponentViewI<?> viewForCurrentMode1 = AppController.INSTANCE.getViewForCurrentMode(nv);
            Region itemView = viewForCurrentMode1.getView();
            try {
                this.imageViewComponent.setImage(UIUtils.takeNodeSnapshot(itemView, -1, -1));
            } catch (Throwable t) {
                LOGGER.warn("Impossible to take a component snapshot for component {}", nv.nameProperty().get(), t);
                imageViewComponent.setImage(null);
            }
        } else {
            imageViewComponent.setImage(null);
            imageViewComponentType.setImage(null);
        }
    }

    void requestFieldNameFocus() {
        fieldName.requestFocus();
    }

    // Class part : "Bind/unbind"
    //========================================================================
    @Override
    public void bind(final DisplayableComponentI modelP) {
        updateComponentImage(modelP);
        //Bind name
        this.fieldName.textProperty().bindBidirectional(modelP.userNameProperty());
        this.fieldNameWrapper.clearPreviousValue();
    }

    @Override
    public void unbind(final DisplayableComponentI modelP) {
        this.fieldName.textProperty().unbindBidirectional(modelP.userNameProperty());
        this.fieldName.clear();
    }
    //========================================================================
}
