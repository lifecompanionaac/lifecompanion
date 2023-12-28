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

package org.lifecompanion.ui.common.control.specific.imagedictionary;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.api.configurationcomponent.VideoUseComponentI;
import org.lifecompanion.model.impl.configurationcomponent.VideoElement;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.fxmisc.easybind.EasyBind;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.SimplerKeyContentContainerI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.common.pane.generic.BaseConfigurationViewBorderPane;
import org.lifecompanion.controller.editaction.KeyActions;
import org.lifecompanion.util.binding.EditActionUtils;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

public class ImageUseComponentSelectorControl extends BaseConfigurationViewBorderPane<ImageUseComponentI> implements LCViewInitHelper {

    /**
     * Button show parameters
     */
    private Button buttonParameters;

    /**
     * Change listener for the image
     */
    private ChangeListener<ImageElementI> changeListenerImage;

    private ChangeListener<VideoElementI> changeListenerVideo;

    /**
     * Image selector control
     */
    private Image2SelectorControl imageSelectorControl;

    /**
     * Property to disable image selection
     */
    private final BooleanProperty disableImageSelection;

    public ImageUseComponentSelectorControl() {
        this.disableImageSelection = new SimpleBooleanProperty(false);
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        //Create buttons
        this.buttonParameters = FXControlUtils.createLeftTextButton(Translation.getText("image.use.show.advanced.parameters"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.GEARS).size(18).color(LCGraphicStyle.MAIN_PRIMARY),
                "tooltip.image.use.show.advanced.parameters");
        BorderPane.setAlignment(buttonParameters, Pos.CENTER);

        //Image selector control
        this.imageSelectorControl = new Image2SelectorControl();

        //Image
        this.setCenter(this.imageSelectorControl);
        this.setBottom(this.buttonParameters);
    }

    @Override
    public void initListener() {
        //Disable remove when there is no image
        this.buttonParameters.disableProperty().bind(this.disableImageSelection.or(this.imageSelectorControl.selectedImageProperty().isNull()));
        this.imageSelectorControl.disableImageSelectionProperty().bind(this.disableImageSelection);
        this.imageSelectorControl.setDefaultSearchTextSupplier(() -> {
            final ImageUseComponentI imageUseComponent = model.get();
            if (imageUseComponent != null) {
                if (imageUseComponent instanceof GridPartKeyComponentI)
                    return ((GridPartKeyComponentI) imageUseComponent).textContentProperty().get();
                if (imageUseComponent instanceof SimplerKeyContentContainerI)
                    return ((SimplerKeyContentContainerI) imageUseComponent).textProperty().get();
            }
            return null;
        });
        //Actions
        this.buttonParameters.setOnAction((ev) -> {
            ImageUseComponentConfigurationStage.getInstance().prepareAndShow(model.get());
        });
    }
    //========================================================================

    // BINDING
    //========================================================================
    public ObjectProperty<ImageUseComponentI> modelProperty() {
        return this.model;
    }

    @Override
    public void initBinding() {
        this.changeListenerImage = EditActionUtils.createSimpleBinding(this.imageSelectorControl.selectedImageProperty(), this.model,
                m -> m.imageVTwoProperty().get(), KeyActions.ChangeImageAction::new);
        this.changeListenerVideo = EditActionUtils.createSimpleBinding(this.imageSelectorControl.selectedVideoProperty(), this.model,
                m -> m.videoProperty().get(), KeyActions.ChangeVideoAction::new);
    }


    @Override
    public void bind(final ImageUseComponentI model) {
        //Disable image selection
        if (model instanceof GridPartKeyComponentI) {
            GridPartKeyComponentI key = (GridPartKeyComponentI) model;
            this.disableImageSelection.bind(EasyBind.select(key.keyOptionProperty()).selectObject(KeyOptionI::disableImageProperty));
        } else {
            this.disableImageSelection.set(false);
        }
        this.imageSelectorControl.selectedImageProperty().set(model.imageVTwoProperty().get());
        this.imageSelectorControl.selectedVideoProperty().set(model.videoProperty().get());
        this.imageSelectorControl.imageUseComponentProperty().set(model);
        model.imageVTwoProperty().addListener(this.changeListenerImage);
    }

    @Override
    public void unbind(final ImageUseComponentI model) {
        this.disableImageSelection.unbind();
        this.imageSelectorControl.imageUseComponentProperty().set(null);
        model.imageVTwoProperty().removeListener(this.changeListenerImage);
    }

    @Override
    protected void clearFieldsAfterUnbind() {
        this.imageSelectorControl.selectedImageProperty().set(null);
    }
    //========================================================================

}