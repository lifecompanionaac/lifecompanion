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
package org.lifecompanion.ui.common.pane.generic;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.lifecompanion.model.impl.style.TextStyleBinder;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A custom label component, where the graphics node inside take the maximum possible place and label text is wrapped when the case become to small.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCLabel extends BorderPane implements TextStyleBinder.TextStyleBindable {
    private final static Logger LOGGER = LoggerFactory.getLogger(LCLabel.class);

    // private static final double MAX_FONT_SIZE = 200.0, FONT_INCREASE = 1.0;
    private static final double MIN_FONT_SIZE = 5.0, FONT_DECREASE = 1.0;
    private static final double GRAPHIC_MARGIN = 2.0;

    private StackPane stackCenter;
    private Label labelText;
    private ObjectProperty<Region> graphic;
    private ObjectProperty<ContentDisplay> textPosition;
    private ObjectProperty<TextCompStyleI> usedTextCompStyle;
    private ObjectProperty<Font> font;
    private BooleanProperty enableAutoFontSizing;

    /**
     * Create a new default
     */
    public LCLabel() {
        this.graphic = new SimpleObjectProperty<>();
        this.usedTextCompStyle = new SimpleObjectProperty<>();
        this.enableAutoFontSizing = new SimpleBooleanProperty(false);
        this.font = new SimpleObjectProperty<>(Font.getDefault());
        this.textPosition = new SimpleObjectProperty<>(ContentDisplay.CENTER);
        this.initGraphics();
    }

    // Class part : "UI base"
    //========================================================================

    /**
     * Initialize UI and its behavior
     */
    private void initGraphics() {
        this.initUI();
        this.initBinding();
    }

    /**
     * Initialize UI components
     */
    private void initUI() {
        //Create label
        this.createLabel();
        //Create center
        this.stackCenter = new StackPane();
        this.stackCenter.setMinSize(0.0, 0.0);
        this.stackCenter.prefWidthProperty().set(Double.MAX_VALUE);
        this.stackCenter.prefHeightProperty().set(Double.MAX_VALUE);
        this.setCenter(this.stackCenter);
        this.stackCenter.getChildren().add(this.labelText);
    }

    /**
     * Create the label displayed into this {@link LCLabel}.
     */
    private void createLabel() {
        //Label that can be invisible
        this.labelText = new Label() {
            @Override
            protected double computeMinHeight(final double arg0P) {
                return 0.0;
            }

            @Override
            protected double computeMinWidth(final double arg0P) {
                return 0.0;
            }
        };
        //Base properties
        this.labelText.wrapTextProperty().set(true);
        //this.labelText.textOverrunProperty().set(OverrunStyle.CLIP); //TODO : check if "..." are ok or if clip is better ?
        this.labelText.contentDisplayProperty().set(ContentDisplay.TEXT_ONLY);
        //Label  size musn't go out of this component size
        this.labelText.maxWidthProperty().bind(this.prefWidthProperty());
        this.labelText.maxHeightProperty().bind(this.prefHeightProperty());
        //Label musn't take space when text is empty
        this.labelText.prefHeightProperty().set(0.0);
        this.labelText.prefWidthProperty().set(0.0);
        this.labelText.textProperty().addListener((o, ov, nv) -> {
            if (StringUtils.isEmpty(nv)) {
                this.labelText.prefHeightProperty().set(0.0);
                this.labelText.prefWidthProperty().set(0.0);
            } else if (StringUtils.isEmpty(ov)) {
                this.labelText.prefHeightProperty().set(Region.USE_COMPUTED_SIZE);
                this.labelText.prefWidthProperty().set(Region.USE_COMPUTED_SIZE);
            }
        });
    }

    /**
     * Initialize binding on UI component
     */
    private void initBinding() {
        //Change the displayed content
        this.graphic.addListener((ChangeListener<Region>) (observableP, oldValueP, newValueP) -> {
            //Remove previous
            if (oldValueP != null) {
                oldValueP.prefWidthProperty().unbind();
                oldValueP.prefHeightProperty().unbind();
                LCLabel.this.stackCenter.getChildren().remove(oldValueP);
            }
            //Add new
            if (newValueP != null) {
                LCLabel.this.stackCenter.getChildren().add(newValueP);
                newValueP.prefWidthProperty().set(Double.MAX_VALUE);
                newValueP.prefHeightProperty().set(Double.MAX_VALUE);
                StackPane.setMargin(newValueP, new Insets(GRAPHIC_MARGIN));
            }
            this.labelText.toFront();
        });
        //Change the content location
        this.textPosition.addListener((ChangeListener<ContentDisplay>) (observableP, oldValueP, newValueP) -> {
            //Remove previous
            switch (oldValueP) {
                case TOP:
                    LCLabel.this.setTop(null);
                    break;
                case BOTTOM:
                    LCLabel.this.setBottom(null);
                    break;
                case RIGHT:
                    LCLabel.this.setRight(null);
                    break;
                case LEFT:
                    LCLabel.this.setLeft(null);
                    break;
                case CENTER:
                    LCLabel.this.stackCenter.getChildren().remove(LCLabel.this.labelText);
                    break;
                default:
                    LCLabel.LOGGER.warn("Given content display in LCLabel is invalid {}", oldValueP);
                    break;
            }
            //Set the new one
            switch (newValueP) {
                case TOP:
                    LCLabel.this.setTop(LCLabel.this.labelText);
                    break;
                case BOTTOM:
                    LCLabel.this.setBottom(LCLabel.this.labelText);
                    break;
                case RIGHT:
                    LCLabel.this.setRight(LCLabel.this.labelText);
                    break;
                case LEFT:
                    LCLabel.this.setLeft(LCLabel.this.labelText);
                    break;
                case CENTER:
                    LCLabel.this.stackCenter.getChildren().add(LCLabel.this.labelText);
                    break;
                default:
                    LCLabel.LOGGER.warn("Given content display in LCLabel is invalid {}", oldValueP);
                    break;
            }
        });
        this.labelText.alignmentProperty().addListener((obs, ov, nv) -> {
            StackPane.setAlignment(this.labelText, nv);
            BorderPane.setAlignment(this.labelText, nv);
        });
        //Get the text in label, and add binding on it
        try {
            this.labelText.fontProperty().bind(Bindings.createObjectBinding(() -> {
                String text = this.labelText.getText();
                Font wantedFont = this.font.get();
                TextCompStyleI textStyle = this.usedTextCompStyle.get();
                if (this.enableAutoFontSizing.get() && textStyle != null && !StringUtils.isBlank(text)) {
                    double wantedSize = wantedFont.getSize();
                    wantedFont = this.usedTextCompStyle.get().deriveFont(wantedSize);
                    Bounds layoutBounds = this.getLayoutBounds();
                    Bounds textBounds = FXUtils.getTextBounds(text, wantedFont);
                    //If the text is large enough
                    if (textBounds.getWidth() < layoutBounds.getWidth() && textBounds.getHeight() < layoutBounds.getHeight()) {
                        return wantedFont;
                        // REMOVED : it's important that the text become smaller in key, but not that the text take all the space
                        // Can be restored if a specific need/parameter enable the text to grow
                        //						Font previousFont = null;
                        //						while (textBounds.getWidth() < layoutBounds.getWidth() && textBounds.getHeight() < layoutBounds.getHeight()
                        //								&& wantedSize < LCLabel.MAX_FONT_SIZE) {
                        //							previousFont = wantedFont;
                        //							wantedSize += LCLabel.FONT_INCREASE;
                        //							wantedFont = textStyle.deriveFont(wantedSize);
                        //							textBounds = LCUtils.getTextBounds(text, wantedFont);
                        //						}
                        //						wantedFont = previousFont;
                    }
                    //If the text is too large
                    else {
                        while ((textBounds.getWidth() > layoutBounds.getWidth() || textBounds.getHeight() > layoutBounds.getHeight())
                                && wantedSize > LCLabel.MIN_FONT_SIZE) {
                            wantedSize -= LCLabel.FONT_DECREASE;
                            wantedFont = textStyle.deriveFont(wantedSize);
                            textBounds = FXUtils.getTextBounds(text, wantedFont);
                        }
                    }
                }
                return wantedFont;
            }, this.font, this.enableAutoFontSizing, this.labelText.textProperty(), this.labelText.layoutBoundsProperty(), this.layoutBoundsProperty()));
        } catch (Throwable t) {
            LCLabel.LOGGER.warn("Couldn't bind the font dynamically in the label, so the label font will be binded to the original font", t);
            this.labelText.fontProperty().bind(this.font);
        }
    }

    //========================================================================

    // Class part : "Override, use to have a component that can be reduce"
    //========================================================================
    @Override
    protected double computeMinWidth(final double arg0P) {
        return 0.0;
    }

    @Override
    protected double computeMinHeight(final double widthP) {
        return 0.0;
    }

    //========================================================================

    // Class part : "Base label properties"
    //========================================================================
    public ObjectProperty<ContentDisplay> contentDisplayProperty() {
        return this.textPosition;
    }

    @Override
    public final ObjectProperty<Font> fontProperty() {
        return this.font;
    }

    public final DoubleProperty lineSpacingProperty() {
        return this.labelText.lineSpacingProperty();
    }

    @Override
    public final ObjectProperty<TextAlignment> textAlignmentProperty() {
        return this.labelText.textAlignmentProperty();
    }

    @Override
    public final ObjectProperty<Paint> textFillProperty() {
        return this.labelText.textFillProperty();
    }

    public final ObjectProperty<OverrunStyle> textOverrunProperty() {
        return this.labelText.textOverrunProperty();
    }

    @Override
    public final BooleanProperty underlineProperty() {
        return this.labelText.underlineProperty();
    }

    public final BooleanProperty wrapTextProperty() {
        return this.labelText.wrapTextProperty();
    }

    public StringProperty textProperty() {
        return this.labelText.textProperty();
    }

    public ObjectProperty<Region> graphicProperty() {
        return this.graphic;
    }

    @Override
    public ObjectProperty<Pos> alignmentProperty() {
        return this.labelText.alignmentProperty();
    }

    @Override
    public ObjectProperty<TextCompStyleI> useTextCompStyleProperty() {
        return this.usedTextCompStyle;
    }

    public BooleanProperty enableAutoFontSizingProperty() {
        return this.enableAutoFontSizing;
    }
    //========================================================================
}
