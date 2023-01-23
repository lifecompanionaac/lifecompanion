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
package org.lifecompanion.ui.app.categorizedelement;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.categorizedelement.CategorizedConfigurationViewI;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementI;
import org.lifecompanion.model.api.categorizedelement.MainCategoryI;
import org.lifecompanion.model.api.categorizedelement.SubCategoryI;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.model.Triple;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.common.pane.generic.AnimatedBorderPane;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractCategorizedMainView<V extends CategorizedElementI<T>, T extends SubCategoryI<K, V>, K extends MainCategoryI<T>>
        extends AnimatedBorderPane implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractCategorizedMainView.class);

    /**
     * View to display main categories
     */
    private AbstractMainCategoriesView<K> mainCategoryView;

    /**
     * Button to open a the search
     */
    private Button buttonSearch;

    /**
     * Button to cancel (close) or to save the current modifications
     */
    private Button buttonCancel, buttonOk;

    /**
     * Property that is on true if search result are currently display
     */
    private final BooleanProperty searchResultDisplayed;

    /**
     * Map that contains a sub category view for each main category to display
     */
    private final Map<K, AbstractSubCategoriesView<V, T, K>> subCategoryViews;

    /**
     * Map that cache every configuration view for action type
     */
    private final Map<Class<? extends V>, CategorizedConfigurationViewI<V>> useActionConfigurationView;

    /**
     * Currently display action configuration view
     */
    private CategorizedConfigurationViewI<V> currentActionConfigurationView;

    /**
     * View to search action
     */
    private AbstractCategorizedSearchView<V> searchView;

    /**
     * The callback that must be called, when this view is close by cancel/ok button
     */
    private Consumer<V> endEditCallback;

    /**
     * Callback when a action is selected in grid
     */
    private Consumer<V> actionSelectedCallback;

    /**
     * Callback when a category is selected
     */
    private Consumer<K> categorySelectionCallback;

    /**
     * Current action
     */
    private ObjectProperty<V> currentAction;

    /**
     * Current selected category
     */
    private ObjectProperty<K> currentCategory;

    /**
     * Insets that every component in center should have
     */
    private Insets centerInsets;

    /**
     * Current mode for this view (edit or add ?)
     */
    private ObjectProperty<CategorizedElementMainViewMode> currentMode;

    private Label labelTitle;
    private Node nodePreviousIndicator;

    /**
     * Create the gallery to display actions.
     */
    public AbstractCategorizedMainView() {
        this.subCategoryViews = new HashMap<>();
        this.useActionConfigurationView = new HashMap<>();
        this.searchResultDisplayed = new SimpleBooleanProperty(false);
        this.currentAction = new SimpleObjectProperty<>();
        this.currentCategory = new SimpleObjectProperty<>();
        this.currentMode = new SimpleObjectProperty<>();
        this.initAll();
    }

    // Class part : "Public API"
    //========================================================================
    public void editElement(final V action, final Consumer<V> endEditCallbackP) {
        this.setEnableTransition(false);
        this.endEditCallback = endEditCallbackP;
        this.currentMode.set(CategorizedElementMainViewMode.EDIT);
        this.displayElementConfigurationView(action);
        this.currentCategory.set(null);
        this.setEnableTransition(true);
    }

    public void addElement(final Consumer<V> endEditCallbackP) {
        this.setEnableTransition(false);
        this.endEditCallback = endEditCallbackP;
        this.currentMode.set(CategorizedElementMainViewMode.ADD);
        this.displayMainCategories();
        this.setEnableTransition(true);
    }
    //========================================================================

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        //Top : button and title
        this.buttonSearch = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.SEARCH).sizeFactor(1).color(LCGraphicStyle.LC_WHITE),
                this.getSearchButtonTooltipID());

        Triple<HBox, Label, Node> header = FXControlUtils.createHeader("", m -> previous());
        header.getLeft().getChildren().add(buttonSearch);
        labelTitle = header.getMiddle();
        nodePreviousIndicator = header.getRight();

        this.setTop(header.getLeft());

        //Bottom : button to cancel, or save
        this.buttonCancel = FXControlUtils.createTextButtonWithGraphics(Translation.getText("action.view.cancel"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TIMES).size(16).color(LCGraphicStyle.SECOND_PRIMARY),
                this.getButtonCancelTooltipID());
        this.buttonOk = FXControlUtils.createTextButtonWithGraphics("",
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHECK).size(16).color(LCGraphicStyle.MAIN_PRIMARY), this.getButtonOkTooltipID());
        HBox boxBottom = new HBox(this.buttonCancel, this.buttonOk);
        boxBottom.setAlignment(Pos.CENTER_RIGHT);
        this.setBottom(boxBottom);

        //Listener defined each because we need to create the search view with it
        this.actionSelectedCallback = this::displayElementConfigurationView;
        this.categorySelectionCallback = this::displayMainCategory;

        //Main category grid view
        this.mainCategoryView = this.createMainCategoriesView(this.categorySelectionCallback);
        this.centerInsets = new Insets(5.0, 6.0, 5.0, 6.0);
        BorderPane.setMargin(this.mainCategoryView, this.centerInsets);

        //Search actions
        this.searchView = this.createSearchView(this.actionSelectedCallback);
        BorderPane.setMargin(this.searchView, this.centerInsets);

        //Default display
        this.displayMainCategories();
    }

    @Override
    public void initListener() {
        //On ok : ends edit and save/add action
        this.buttonOk.setOnAction((ae) -> {
            //End current edit
            if (this.currentActionConfigurationView != null) {
                this.currentActionConfigurationView.editEnds(this.currentAction.get());
                if (this.endEditCallback != null) {
                    this.endEditCallback.accept(this.currentAction.get());
                }
            }
            this.currentAction.set(null);
        });
        //On cancel : just unselect and hide
        this.buttonCancel.setOnAction((ae) -> {
            if (this.currentActionConfigurationView != null) {
                this.currentActionConfigurationView.editCancelled(this.currentAction.get());
            }
            if (this.endEditCallback != null) {
                this.endEditCallback.accept(null);
            }
            this.currentAction.set(null);
        });
        //Search : display search result
        this.buttonSearch.setOnAction((ae) -> this.displaySearch());
    }

    private void previous() {
        //Search displayed
        if (this.searchResultDisplayed.get()) {
            this.displayMainCategories();
        }
        //Action displayed and selected from a category
        else if (this.currentAction.get() != null && this.currentCategory.get() != null) {
            this.displayMainCategory(this.currentCategory.get());
        }
        //Action displayed but no category, end callback
        else if (this.currentAction.get() != null && this.endEditCallback != null) {
            this.endEditCallback.accept(null);
        }
        //Just a category displayed
        else if (this.currentCategory.get() != null) {
            this.displayMainCategories();
        }
        //Just the main category list
        else if (this.endEditCallback != null) {
            this.endEditCallback.accept(null);
        }
    }
    //========================================================================

    // Class part : "Binding"
    //========================================================================
    @Override
    public void initBinding() {
        this.buttonOk.textProperty().bind(Bindings.createStringBinding(() -> {
            if (this.currentMode.get() == CategorizedElementMainViewMode.ADD) {
                return Translation.getText("action.view.add");
            } else if (this.currentMode.get() == CategorizedElementMainViewMode.EDIT) {
                return Translation.getText("action.view.save");
            } else {
                return "unknown.action.config.mode";
            }
        }, this.currentMode));
        //Never show previous in Edit mode
        this.nodePreviousIndicator.visibleProperty().bind(this.currentMode.isNotEqualTo(CategorizedElementMainViewMode.EDIT));
        nodePreviousIndicator.managedProperty().bind(nodePreviousIndicator.visibleProperty());
        //Display search only on first page
        this.buttonSearch.visibleProperty()
                .bind(this.currentAction.isNull().and(this.currentCategory.isNull()).and(this.searchResultDisplayed.not()));
        this.buttonSearch.managedProperty().bind(this.buttonSearch.visibleProperty());
        //Confirm/cancel button
        this.buttonCancel.visibleProperty().bind(this.currentAction.isNotNull());
        this.buttonOk.visibleProperty().bind(this.currentAction.isNotNull());
        this.buttonOk.managedProperty().bind(this.buttonOk.visibleProperty());
        this.buttonCancel.managedProperty().bind(this.buttonCancel.visibleProperty());
    }
    //========================================================================

    // Class part : "Change display"
    //========================================================================
    private void displayMainCategories() {
        this.labelTitle.setText(Translation.getText("title.main.categories"));
        this.changeCenter(this.mainCategoryView);
        this.searchResultDisplayed.set(false);
        this.currentCategory.set(null);
        this.currentAction.set(null);
    }

    private void displaySearch() {
        this.labelTitle.setText(this.getSearchTitle());
        this.changeCenter(this.searchView);
        this.searchResultDisplayed.set(true);
        this.currentAction.set(null);
        this.currentCategory.set(null);
    }

    protected abstract String getSearchTitle();

    /**
     * Display the given use action in the view
     *
     * @param useAction the action that is edited/created
     */
    @SuppressWarnings("unchecked")
    private void displayElementConfigurationView(final V useAction) {
        AbstractCategorizedMainView.LOGGER.info("Will display action \"{}\", action is parametrizable {}", useAction.getName(),
                useAction.isParameterizableElement());
        this.searchResultDisplayed.set(false);
        this.currentAction.set(useAction);
        //Check parametrizable
        if (useAction.isParameterizableElement()) {
            this.labelTitle.setText(useAction.getName());
            //Create configuration view when needed
            if (!this.useActionConfigurationView.containsKey(useAction.getClass())) {
                AbstractCategorizedMainView.LOGGER.info("There is no existing configuration view for {}, try to create a new one",
                        useAction.getClass());
                CategorizedConfigurationViewI<V> actionConfigurationView = this.getConfigurationViewFor(useAction);
                BorderPane.setMargin(actionConfigurationView.getConfigurationView(), this.centerInsets);
                this.useActionConfigurationView.put((Class<V>) useAction.getClass(), actionConfigurationView);
            }
            //Display and start edit
            this.currentActionConfigurationView = this.useActionConfigurationView.get(useAction.getClass());
            this.callEditStartOn(this.currentActionConfigurationView, useAction);
            this.changeCenter(this.currentActionConfigurationView.getConfigurationView());
        } else {
            //no parameters, directly end edits
            if (this.endEditCallback != null) {
                this.endEditCallback.accept(useAction);
            }
            this.currentAction.set(null);
        }
    }

    /**
     * Display the given main category in the view
     *
     * @param mainCategory the main category to show
     */
    private void displayMainCategory(final K mainCategory) {
        this.labelTitle.setText(mainCategory.getName());
        this.searchResultDisplayed.set(false);
        this.currentCategory.set(mainCategory);
        this.currentAction.set(null);
        this.searchResultDisplayed.set(false);
        //Create when needed
        if (!this.subCategoryViews.containsKey(mainCategory)) {
            AbstractSubCategoriesView<V, T, K> viewForCategory = this.createSubCategoriesView(mainCategory, this.actionSelectedCallback);
            BorderPane.setMargin(viewForCategory, this.centerInsets);
            this.subCategoryViews.put(mainCategory, viewForCategory);
        }
        //Show
        this.changeCenter(this.subCategoryViews.get(mainCategory));
    }

    public void clearSelection() {
        displayMainCategories();
    }
    //========================================================================

    // Class part : "Enum"
    //========================================================================

    /**
     * Represent the mode to display the good configuration.<br>
     * Edit : to edit a already added action, Add : to add an action to the selected component
     */
    public enum CategorizedElementMainViewMode {
        EDIT, ADD;
    }
    //========================================================================

    // Class part : "Subclass implementation"
    //========================================================================
    public abstract AbstractMainCategoriesView<K> createMainCategoriesView(Consumer<K> categorySelectionCallback);

    public abstract AbstractSubCategoriesView<V, T, K> createSubCategoriesView(K mainCategory, Consumer<V> actionSelectedCallback);

    public abstract CategorizedConfigurationViewI<V> getConfigurationViewFor(V value);

    public abstract AbstractCategorizedSearchView<V> createSearchView(Consumer<V> actionSelectedCallback);

    public abstract void callEditStartOn(CategorizedConfigurationViewI<V> configView, V value);

    protected abstract String getButtonOkTooltipID();

    protected abstract String getButtonCancelTooltipID();

    protected abstract String getSearchButtonTooltipID();
    //========================================================================

}
