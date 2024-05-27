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
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementI;
import org.lifecompanion.model.api.categorizedelement.MainCategoryI;
import org.lifecompanion.model.api.categorizedelement.SubCategoryI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.common.pane.generic.BaseConfigurationViewBorderPane;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.ui.common.pane.specific.cell.AbstractCategorizedElementListCellView;
import org.lifecompanion.ui.app.categorizedelement.useaction.UseActionListManageView;
import org.lifecompanion.ui.common.control.generic.OrderModifiableListView;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public abstract class AbstractCategorizedListManageView<M, V extends CategorizedElementI<T>, T extends SubCategoryI<K, V>, K extends MainCategoryI<T>>
        extends BaseConfigurationViewBorderPane<M> implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(UseActionListManageView.class);
    //public static final double MAX_USEACTION_LIST_WIDTH = 350;
    public static final double STAGE_WIDTH = 520, STAGE_HEIGHT = 500;

    /**
     * If we need to display an empty list, or a simple button when there is no action
     */
    private final boolean alwaysDisplay;

    /**
     * Button to show the list when the list is empty ad {@link #alwaysDisplay} is on false
     */
    private Button buttonAddElementWhenEmpty;

    /**
     * List that contains every action
     */
    private OrderModifiableListView<V> elementListView;

    /**
     * Pane to display the list
     */
    private BorderPane paneList;

    /**
     * View to select and configure actions
     */
    private AbstractCategorizedMainView<V, T, K> categorizedElementMainView;

    private final BooleanProperty orderListButtonVisible;

    private final Pos rightOrLeftButton;

    private Runnable clearSelectionRunnable;

    /**
     * Create a component to manager action
     *
     * @param alwaysDisplayP if we need to display this component when the action list is empty
     */
    public AbstractCategorizedListManageView(final boolean alwaysDisplayP, final boolean skipInitAll, final Pos rightOrLeftButton) {
        this.alwaysDisplay = alwaysDisplayP;
        this.orderListButtonVisible = new SimpleBooleanProperty(true);
        this.rightOrLeftButton = rightOrLeftButton;
        if (!skipInitAll) {
            this.initAll();
        }
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        //Button when empty
        this.buttonAddElementWhenEmpty = FXControlUtils.createTextButtonWithGraphics(this.getAddWhenEmptyButtonText(),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).sizeFactor(2).color(LCGraphicStyle.MAIN_DARK),
                "tooltip.add.action.empty");
        this.buttonAddElementWhenEmpty.setTextAlignment(TextAlignment.CENTER);

        //List view to display action
        this.elementListView = new OrderModifiableListView<>(true, this.rightOrLeftButton);
        this.elementListView.setCellFactory(listView -> this.createCategorizedListCellView(listView, (source, item) -> {
            if (this.checkEditPossible(source, item)) {
                this.prepareEditAction(item);
                this.showCategorizedContentStage();
            }
        }));
        this.elementListView.setPrefSize(400.0, 130);
        this.elementListView.getButtonModify().setVisible(true);
        //Border pane
        this.paneList = new BorderPane();
        this.paneList.setCenter(this.elementListView);

        //Base for this tab (title with event type)
        if (this.alwaysDisplay) {
            this.showNotEmptyContent();
        } else {
            this.showEmptyContent();
        }

        //Use action view
        this.categorizedElementMainView = this.createCategorizedMainView();
        this.categorizedElementMainView.setPrefSize(AbstractCategorizedListManageView.STAGE_WIDTH, AbstractCategorizedListManageView.STAGE_HEIGHT);
        BorderPane.setAlignment(categorizedElementMainView, Pos.CENTER);
    }

    private void showEmptyContent() {
        if (this.getCenter() != this.buttonAddElementWhenEmpty) {
            this.setCenter(this.buttonAddElementWhenEmpty);
        }
    }

    private void showNotEmptyContent() {
        if (this.getCenter() != this.paneList) {
            this.setCenter(this.paneList);
        }
    }

    public void clearSelection() {
        categorizedElementMainView.clearSelection();
    }

    @Override
    public void initListener() {
        clearSelectionRunnable = this::clearSelection;
        this.buttonAddElementWhenEmpty.setOnAction((ea) -> {
            this.showNotEmptyContent();
            this.addButtonClic(buttonAddElementWhenEmpty);//Fire add clic
        });
        //On add : show action view
        this.elementListView.getButtonAdd().setOnAction((ea) -> {
            this.addButtonClic(this.elementListView.getButtonAdd());
        });
        this.elementListView.getButtonRemove().setOnAction((ea) -> {
            if (this.model.get() != null) {
                V selectedElement = this.elementListView.getSelectedItem();
                if (selectedElement != null) {
                    ConfigActionController.INSTANCE.executeAction(this.createRemoveAction(this.elementListView.getButtonRemove(), this.model.get(), selectedElement));
                }
            }
        });
        this.elementListView.getButtonDown().setOnAction((ae) -> {
            V selected = this.elementListView.getSelectedItem();
            if (selected != null) {
                ConfigActionController.INSTANCE.executeAction(this.createShiftDownAction(this.model.get(), selected));
                this.elementListView.select(selected);
                this.elementListView.scrollTo(selected);
            }
        });
        //Put selected up
        this.elementListView.getButtonUp().setOnAction((ae) -> {
            V selected = this.elementListView.getSelectedItem();
            if (selected != null) {
                ConfigActionController.INSTANCE.executeAction(this.createShiftUpAction(this.model.get(), selected));
                this.elementListView.select(selected);
                this.elementListView.scrollTo(selected);
            }
        });
        this.elementListView.getButtonModify().setOnAction(ae -> {
            V item = this.elementListView.getSelectedItem();
            if (item != null) {
                if (this.checkEditPossible(this.elementListView.getButtonModify(), item)) {
                    this.prepareEditAction(item);
                    this.showCategorizedContentStage();
                }
            }
        });
    }

    private void addButtonClic(Node source) {
        this.prepareAdd(source);
        this.showCategorizedContentStage();
    }

    /**
     * Show the popover on clic, or hide if already showing
     */
    public void showCategorizedContentStage() {
        CategorizedContentStage.getInstance().prepareAndShow(this, categorizedElementMainView, clearSelectionRunnable);
    }

    //========================================================================

    // Class part : "Logic part : add/edit/remove/change action"
    //========================================================================

    /**
     * Prepare the add action of a use action
     */
    private void prepareAdd(Node source) {
        this.checkBeforeAdd(source);
        this.categorizedElementMainView.addElement((addedElement) -> {
            if (addedElement != null && this.model.get() != null) {
                ConfigActionController.INSTANCE.executeAction(this.createAddAction(source, this.model.get(), addedElement));
            }
            CategorizedContentStage.getInstance().hide();
            if (addedElement != null) {
                this.elementListView.select(addedElement);
            }
        });
    }

    /**
     * Prepare the edit action on the given use action
     *
     * @param action the use action to edit
     */
    public void prepareEditAction(final V action) {
        this.categorizedElementMainView.editElement(action, (editedElement) -> {
            if (editedElement != null) {
                if (editActionForHistoryOnly()) {
                    ConfigActionController.INSTANCE.addAction(this.createEditAction(editedElement), true);//Trace action (but cannot be undo)
                } else {
                    ConfigActionController.INSTANCE.executeAction(this.createEditAction(editedElement));
                }
            }
            CategorizedContentStage.getInstance().hide();
        });
    }

    protected boolean editActionForHistoryOnly() {
        return true;
    }

    protected void checkBeforeAdd(Node source){}

    public ReadOnlyObjectProperty<V> selectedItemProperty() {
        return this.elementListView.selectedItemProperty();
    }

    public BooleanProperty orderListButtonVisibleProperty() {
        return this.orderListButtonVisible;
    }

    public void setElementListViewPrefSize(double prefWidth, double prefHeight) {
        this.elementListView.setPrefSize(prefWidth, prefHeight);
    }
    //========================================================================

    // Class part : "Model binding"
    //========================================================================
    @Override
    public void bind(final M modelP) {
        //Get list
        ObservableList<V> actionList = this.getContentFromModel(modelP);
        if (actionList != null) {
            //Show the wanted content
            if (!this.alwaysDisplay && actionList.isEmpty()) {
                this.showEmptyContent();
            } else {
                this.showNotEmptyContent();
            }
            //Bind the list
            this.elementListView.setItems(actionList);
        } else {
            AbstractCategorizedListManageView.LOGGER.warn(
                    "An element list was created on the component type {} but there is no element list on this component instance",
                    modelP.getClass().getSimpleName());
        }
    }

    @Override
    public void unbind(final M modelP) {
        this.elementListView.setItems(null);
    }

    @Override
    public void initBinding() {
        //If actions are added in background, should display the list
        this.elementListView.listEmptyProperty().addListener((obs, ov, nv) -> {
            if (!nv) {
                this.showNotEmptyContent();
            }
        });
        //Visibility
        this.elementListView.getButtonDown().visibleProperty().bind(this.orderListButtonVisible);
        this.elementListView.getButtonDown().managedProperty().bind(this.orderListButtonVisible);
        this.elementListView.getButtonUp().visibleProperty().bind(this.orderListButtonVisible);
        this.elementListView.getButtonUp().managedProperty().bind(this.orderListButtonVisible);

        this.elementListView.getButtonModify().disableProperty().bind(Bindings.createBooleanBinding(() -> {
            V item = this.elementListView.selectedItemProperty().get();
            return item == null || !item.isParameterizableElement();
        }, this.elementListView.selectedItemProperty()));
    }

    public ObjectProperty<M> modelProperty() {
        return this.model;
    }
    //========================================================================

    // Class part : "Subclass impl"
    //========================================================================
    protected abstract AbstractCategorizedMainView<V, T, K> createCategorizedMainView();

    protected abstract AbstractCategorizedElementListCellView<V> createCategorizedListCellView(ListView<V> listView, BiConsumer<Node, V> selectionCallback);

    protected abstract BaseEditActionI createEditAction(V editedElement);

    protected abstract BaseEditActionI createAddAction(Node source, M model, V addedElement);

    protected abstract BaseEditActionI createRemoveAction(Node source, M model, V removedElement);

    protected abstract BaseEditActionI createShiftUpAction(M model, V element);

    protected abstract BaseEditActionI createShiftDownAction(M model, V element);

    protected abstract ObservableList<V> getContentFromModel(M model);

    protected abstract String getAddWhenEmptyButtonText();

    protected abstract boolean checkEditPossible(Node source, V item);
    //========================================================================

}
