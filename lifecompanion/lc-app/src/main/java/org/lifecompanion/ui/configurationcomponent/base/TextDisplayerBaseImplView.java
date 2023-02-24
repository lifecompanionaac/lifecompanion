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
package org.lifecompanion.ui.configurationcomponent.base;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.lifecompanion.model.api.configurationcomponent.WriterDisplayerI;
import org.lifecompanion.model.api.style.ShapeCompStyleI;
import org.lifecompanion.model.api.ui.configurationcomponent.ComponentViewI;
import org.lifecompanion.model.api.ui.configurationcomponent.ViewProviderI;
import org.lifecompanion.util.binding.Unbindable;
import org.lifecompanion.model.impl.style.ShapeStyleBinder;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Class that handle the common view component of a {@link WriterDisplayerI}.
 *
 * @param <T> the real displayed {@link WriterDisplayerI} subtype
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class TextDisplayerBaseImplView<T extends WriterDisplayerI> extends Pane implements ComponentViewI<T>, LCViewInitHelper {
    protected ViewProviderI viewProvider;
    protected boolean useCache;

    /**
     * Displayed model
     */
    protected T model;

    /**
     * Scroll for text content
     */
    private ScrollPane scrollText;

    /**
     * Text displayer
     */
    private TextDisplayer textDisplayer;

    private final AtomicReference<Double> wantedScrollPercent;

    public TextDisplayerBaseImplView() {
        wantedScrollPercent = new AtomicReference<Double>();
    }

    @Override
    public void initialize(ViewProviderI viewProvider, boolean useCache, final T componentP) {
        this.viewProvider = viewProvider;
        this.useCache = useCache;
        this.model = componentP;
        this.initAll();
    }

    private Unbindable shapeStyleUnbind, shapeStyleSizeUnbind;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initUI() {
        this.scrollText = new ScrollPane();
        textDisplayer = TextDisplayer.toDisplay(model, widthProperty().subtract(22.0), heightProperty(), this);
        this.scrollText.setContent(textDisplayer);
        this.scrollText.prefHeightProperty().bind(this.heightProperty());
        this.scrollText.setFitToWidth(true);
        this.getChildren().add(this.scrollText);
        this.scrollText.getStyleClass().add("text-displayer-scroll-pane");

        ShapeCompStyleI shapeCompStyle = this.model.getTextDisplayerShapeStyle();
        shapeStyleUnbind = ShapeStyleBinder.bindNode(this, shapeCompStyle);
        shapeStyleSizeUnbind = ShapeStyleBinder.bindNodeSize(this.scrollText, this.model.getTextDisplayerShapeStyle(), this.modelWidthProperty(),
                this.modelHeightProperty());
    }

    @Override
    public void unbindComponentAndChildren() {
        this.textDisplayer.unbindComponentAndChildren();
        shapeStyleUnbind.unbind();
        shapeStyleSizeUnbind.unbind();
        this.model = null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void initListener() {
        this.scrollText.setOnMouseClicked((ea) -> {
            if (ea.getButton() == MouseButton.PRIMARY) {

            } else if (ea.getButton() == MouseButton.SECONDARY) {

            } else {
            }
        });
        this.textDisplayer.heightProperty().addListener((obs, ov, nv) -> {
            final Double wantedScrollValue = wantedScrollPercent.getAndSet(null);
            if (wantedScrollValue != null) {
                this.scrollText.setVvalue(wantedScrollValue);
            }
        });
    }

    /**
     * Set the value of the canvas scroll.</br>
     * The value is immediately set, and planned to be set on next height change (bug fix because height is not immediately correct)
     *
     * @param yPercent scroll y value (0.0 -> 1.0)
     */
    public void updateCaretScroll(final double yPercent) {
        this.scrollText.setVvalue(yPercent);
        this.wantedScrollPercent.set(yPercent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initBinding() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Region getView() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showToFront() {
        this.toFront();
    }

    protected abstract DoubleProperty modelWidthProperty();

    protected abstract DoubleProperty modelHeightProperty();

}
