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

package org.lifecompanion.ui.configurationcomponent.editmode.componentoption;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.RootGraphicComponentI;
import org.lifecompanion.model.api.configurationcomponent.SelectableComponentI;
import org.lifecompanion.model.api.ui.editmode.ConfigOptionComponentI;
import org.lifecompanion.ui.controlsfx.glyphfont.Glyph;
import org.lifecompanion.util.javafx.ColorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Option to select component, add to add some optional buttons to it.<br>
 * The optional buttons are displayed only if component is selected.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ButtonComponentOption extends BaseOptionRegion<SelectableComponentI> implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(ButtonComponentOption.class);
    /**
     * Grid that contains the buttons to display
     */
    private GridPane gridButtons;

    /**
     * Button that allow selection of this component model
     */
    private ToggleButton buttonSelect;

    /**
     * The current row/column in grid add
     */
    private int currentRow, currentColumn;

    /**
     * List of current option in this component
     */
    private final List<ConfigOptionComponentI> options;

    private final Color selectButtonBackgroundColor;

    public ButtonComponentOption(final SelectableComponentI modelP, Color selectButtonBackgroundColor) {
        super(modelP);
        this.selectButtonBackgroundColor = selectButtonBackgroundColor;
        this.options = new ArrayList<>();
        this.initAll();
    }

    @Override
    public void initUI() {
        this.gridButtons = new GridPane();
        this.gridButtons.setPickOnBounds(false);
        this.gridButtons.setHgap(2.0);
        this.gridButtons.setVgap(2.0);
        //Select button
        this.buttonSelect = new ToggleButton();
        ButtonComponentOption.applyComponentOptionButtonStyle(this.buttonSelect, selectButtonBackgroundColor, FontAwesome.Glyph.HAND_ALT_UP);
        this.gridButtons.add(this.buttonSelect, this.currentColumn++, this.currentRow++);
        //Display the grid
        this.getChildren().add(this.gridButtons);
    }

    @Override
    public void initListener() {
        //Select or unselect
        this.buttonSelect.setOnAction((ea) -> {
            if (this.model instanceof DisplayableComponentI) {
                SelectionController.INSTANCE.selectDisplayableComponent((DisplayableComponentI) model, false);
            } else {
                ButtonComponentOption.LOGGER
                        .warn("The current selectable model type in select button option doesn't allow selection on SelectionController");
            }
        });
        if (this.model instanceof RootGraphicComponentI) {
            MoveButtonHelper.install(() -> (RootGraphicComponentI) model, buttonSelect);
        }
    }

    // Class part : "Option"
    //========================================================================
    private void addOption(final ConfigOptionComponentI option, final List<Node> component) {
        for (Node comp : component) {
            this.addOptionUnique(option, comp);
        }
    }

    private void addOptionUnique(final ConfigOptionComponentI option, final Node component) {
        if (option.getOrientation() == Orientation.HORIZONTAL) {
            this.gridButtons.add(component, this.currentColumn++, 0);
        } else if (option.getOrientation() == Orientation.VERTICAL) {
            this.gridButtons.add(component, 0, this.currentRow++);
        }
        //Default : hide
        if (option.hideOnUnselect()) {
            component.setVisible(false);
            component.managedProperty().bind(component.visibleProperty());
            component.mouseTransparentProperty().bind(component.visibleProperty().not());
        }
    }

    public void addOption(final ConfigOptionComponentI option) {
        this.options.add(option);
        this.addOption(option, option.getOptions());
    }

    //========================================================================

    // Class part : "Selection hide/visible"
    //========================================================================
    @Override
    public void initBinding() {
        this.model.selectedProperty().addListener((observableP, oldValueP, newValueP) -> {
            //Change button
            this.buttonSelect.setSelected(newValueP);
            if (oldValueP && !newValueP) {
                this.unselected();
            }
            if (!oldValueP && newValueP) {
                this.selected();
            }
            //To front
            this.getParent().toFront();
        });
    }

    private void selected() {
        //Show option
        for (ConfigOptionComponentI option : this.options) {
            if (option.hideOnUnselect()) {
                List<Node> nodes = option.getOptions();
                for (Node c : nodes) {
                    c.setVisible(true);
                }
            }
        }
    }

    private void unselected() {
        //Hide option
        for (ConfigOptionComponentI option : this.options) {
            if (option.hideOnUnselect()) {
                List<Node> nodes = option.getOptions();
                for (Node c : nodes) {
                    c.setVisible(false);
                }
            }
        }
    }

    //========================================================================

    // Class part : "Style"
    //========================================================================

    /**
     * To set the base component option style on a button
     *
     * @param button the button we want to create the style
     * @param glyph  the icon to show in the button
     */
    public static void applyComponentOptionButtonStyle(final ButtonBase button, Color backgroundColor, final Enum<?> glyph) {
        if (glyph != null) {
            button.setGraphic(GlyphFontHelper.FONT_AWESOME.create(glyph).sizeFactor(1).color(Color.WHITE));
        }
        Circle buttonShape = new Circle(1.0);//Radius is ignored when != 0
        button.setShape(buttonShape);
        button.setCenterShape(true);
        button.getStyleClass().addAll("content-display-graphic-only", "opacity-30", "opacity-60-pressed", "opacity-80-hover", "opacity-40-disabled", "opacity-100-selected");
        button.setStyle("-fx-background-color:" + ColorUtils.toCssColor(backgroundColor));
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);
    }

    /**
     * To set the base on option button
     *
     * @param button
     * @param glyph
     */
    public static void applyButtonBaseStyle(final ButtonBase button, Color backgroundColor, final Enum<?> glyph) {
        applyButtonBaseStyle(button, backgroundColor, glyph, 14, 14, 26);
    }

    public static void applyButtonBaseStyle(final ButtonBase button, Color backgroundColor, final Enum<?> glyph, double iconSize, double circleSize, double prefSize) {
        button.setGraphic(GlyphFontHelper.FONT_AWESOME.create(glyph)
                .size(iconSize)
                .color(Color.WHITE));
        button.setShape(new Circle(circleSize));
        //FIX : set a space for text allow the icon to be correctly displayed
        button.setText(" ");
        button.getStyleClass().addAll("content-display-center", "opacity-60-pressed", "opacity-80-hover", "opacity-40-disabled", "opacity-100-selected");
        button.setStyle("-fx-background-color:" + ColorUtils.toCssColor(backgroundColor));
        //FIX : when adding a group to root, if the pref size is not set, the button is not visible ?
        button.setPrefSize(prefSize, prefSize);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);
    }
    //========================================================================

}
