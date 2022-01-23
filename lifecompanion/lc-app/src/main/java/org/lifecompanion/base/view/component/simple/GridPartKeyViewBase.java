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
package org.lifecompanion.base.view.component.simple;

import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import org.lifecompanion.api.style2.definition.KeyCompStyleI;
import org.lifecompanion.api.ui.ComponentViewI;
import org.lifecompanion.api.ui.ViewProviderI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.common.Unbindable;
import org.lifecompanion.base.data.component.simple.GridPartKeyComponent;
import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.base.data.style.impl.ShapeStyleBinder;
import org.lifecompanion.base.data.style.impl.TextStyleBinder;
import org.lifecompanion.base.view.reusable.ImageViewPane;
import org.lifecompanion.base.view.reusable.LCLabel;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

/**
 * Base displayer for a Key component
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridPartKeyViewBase extends Pane implements ComponentViewI<GridPartKeyComponent>, LCViewInitHelper {
    protected GridPartKeyComponent model;
    protected LCLabel labelContent;
    protected ImageView keyImageView;

    private Unbindable shapeStyleUnbind, shapeStyleClipUnbind, textStyleUnbind;

    public GridPartKeyViewBase() {
    }

    @Override
    public void initUI() {
        this.labelContent = new LCLabel();
        this.getChildren().add(this.labelContent);
    }

    @Override
    public void initBinding() {
        //Position and size binding
        this.prefWidthProperty().bind(this.model.layoutWidthProperty());
        this.prefHeightProperty().bind(this.model.layoutHeightProperty());
        this.layoutXProperty().bind(this.model.layoutXProperty());
        this.layoutYProperty().bind(this.model.layoutYProperty());

        KeyCompStyleI keyStyle = this.model.getKeyStyle();
        this.labelContent.layoutXProperty().bind(keyStyle.strokeSizeProperty().valueAsInt());
        this.labelContent.layoutYProperty().bind(keyStyle.strokeSizeProperty().valueAsInt());
        this.labelContent.prefWidthProperty()
                .bind(this.model.layoutWidthProperty().subtract(keyStyle.strokeSizeProperty().valueAsInt().multiply(2.0)));
        this.labelContent.prefHeightProperty()
                .bind(this.model.layoutHeightProperty().subtract(keyStyle.strokeSizeProperty().valueAsInt().multiply(2.0)));
        this.labelContent.enableAutoFontSizingProperty().bind(keyStyle.autoFontSizeProperty().value());
        textStyleUnbind = TextStyleBinder.bindTextStyleBindableComp(this.labelContent, this.model.getKeyTextStyle());

        //Position
        this.labelContent.contentDisplayProperty().bind(this.model.textPositionProperty());
        //Label content (uppercase, capitalize when needed)
        this.labelContent.textProperty().bind(Bindings.createStringBinding(() -> {
                    String text = this.model.textContentProperty().get();
                    // Upper case because of the style
                    if (LCUtils.isTrue(this.model.getKeyTextStyle().upperCaseProperty().value().getValue()))
                        return StringUtils.toUpperCase(text);
                    // Upper case/capitalize because of WrittingController
                    if (this.model.isTextContentWritten()) {
                        if (WritingStateController.INSTANCE.capitalizeNextProperty().get()) {
                            return StringUtils.capitalize(text);
                        } else if (WritingStateController.INSTANCE.upperCaseProperty().get()) {
                            return StringUtils.toUpperCase(text);
                        }
                    }
                    return text;
                }, this.model.textContentProperty(), WritingStateController.INSTANCE.capitalizeNextProperty(), WritingStateController.INSTANCE.upperCaseProperty(),
                this.model.getKeyTextStyle().upperCaseProperty().value()));

        //Bind the image
        keyImageView = new ImageView();
        keyImageView.setSmooth(true);
        keyImageView.preserveRatioProperty().bind(this.model.preserveRatioProperty());
        keyImageView.rotateProperty().bind(this.model.rotateProperty());
        keyImageView.viewportProperty().bind(this.model.viewportProperty());
        ImageViewPane keyImageViewWrapper = new ImageViewPane(keyImageView);
        keyImageView.imageProperty().bind(Bindings.createObjectBinding(() -> {
                    Image img = this.model.loadedImageProperty().get();
                    return img == null || !this.model.enableReplaceColorProperty().get() ? img
                            : LCUtils.replaceColorInImage(img, this.model.colorToReplaceProperty().get(), this.model.replacingColorProperty().get(),
                            this.model.replaceColorThresholdProperty().get());
                }, this.model.loadedImageProperty(), this.model.enableReplaceColorProperty(), this.model.colorToReplaceProperty(),
                this.model.replacingColorProperty(), this.model.replaceColorThresholdProperty()));
        this.labelContent.graphicProperty().set(keyImageViewWrapper);

        //Bind style
        shapeStyleUnbind = ShapeStyleBinder.bindNode(this, keyStyle);
        Rectangle rectangleClip = new Rectangle();
        shapeStyleClipUnbind = ShapeStyleBinder.bindClipComp(rectangleClip, this.model.layoutWidthProperty(), this.model.layoutHeightProperty(), keyStyle);
        this.labelContent.setClip(rectangleClip);

        // Shadow effect
        // TODO : enable via style property ?
        //        final DropShadow dropShadow = new DropShadow();
        //        dropShadow.setRadius(7.0);
        //        dropShadow.setColor(Color.DARKGRAY);
        //        this.setEffect(dropShadow);
    }

    @Override
    public Region getView() {
        return this;
    }

    @Override
    public void initialize(ViewProviderI viewProvider, boolean useCache, final GridPartKeyComponent componentP) {
        this.model = componentP;
        this.initAll();
    }

    @Override
    public void unbindComponentAndChildren() {
        this.prefWidthProperty().unbind();
        this.prefHeightProperty().unbind();
        this.layoutXProperty().unbind();
        this.layoutYProperty().unbind();

        this.labelContent.layoutXProperty().unbind();
        this.labelContent.layoutYProperty().unbind();
        this.labelContent.prefWidthProperty().unbind();
        this.labelContent.prefHeightProperty().unbind();
        this.labelContent.enableAutoFontSizingProperty().unbind();
        textStyleUnbind.unbind();

        this.labelContent.contentDisplayProperty().unbind();
        this.labelContent.textProperty().unbind();

        //Bind the image
        keyImageView.preserveRatioProperty().unbind();
        keyImageView.rotateProperty().unbind();
        keyImageView.viewportProperty().unbind();
        LCUtils.unbindAndSetNull(keyImageView.imageProperty());

        //Bind style
        shapeStyleUnbind.unbind();
        shapeStyleClipUnbind.unbind();

        this.model = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showToFront() {
        this.toFront();
    }
}
