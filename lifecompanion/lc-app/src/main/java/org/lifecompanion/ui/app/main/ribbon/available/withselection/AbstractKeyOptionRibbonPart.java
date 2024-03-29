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
package org.lifecompanion.ui.app.main.ribbon.available.withselection;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionConfigurationViewI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AvailableKeyOptionManager;
import org.lifecompanion.controller.editaction.KeyActions;
import org.lifecompanion.ui.common.pane.specific.cell.KeyOptionDetailledListCell;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.model.GridPartKeyCollectionPropertyHolder;
import org.lifecompanion.util.model.GridPartKeyPropertyChangeListener;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.ui.common.pane.specific.cell.KeyOptionListCell;
import org.lifecompanion.ui.configurationcomponent.editmode.keyoption.KeyOptionViewProvider;
import org.lifecompanion.ui.app.main.ribbon.available.withselection.style.MultiKeyHelper;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Part to modify the key option
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class AbstractKeyOptionRibbonPart extends RibbonBasePart<GridPartKeyComponentI> implements LCViewInitHelper {

    /**
     * The selected key option for the key
     */
    private ComboBox<KeyOptionI> comboKeyOptionType;

    /**
     * Pane that display key option configuration view (if there is one)
     */
    private BorderPane paneOptionConfigurationView;

    private Map<Class<? extends KeyOptionI>, KeyOptionConfigurationViewI<KeyOptionI>> keyOptionConfigurationViews;

    private KeyOptionConfigurationViewI<KeyOptionI> displayedOptionConfiguration;

    private final GridPartKeyPropertyChangeListener<GridPartKeyComponentI, KeyOptionI, Class<? extends KeyOptionI>> keyOptionProperty;
    private final GridPartKeyCollectionPropertyHolder selectedKeyProperties;
    private final boolean multiSelection;

    protected AbstractKeyOptionRibbonPart(boolean multiSelection) {
        this.multiSelection = multiSelection;
        this.keyOptionConfigurationViews = new HashMap<>();
        selectedKeyProperties = new GridPartKeyCollectionPropertyHolder(SelectionController.INSTANCE.getSelectedKeys(), Arrays.asList(
                keyOptionProperty = new GridPartKeyPropertyChangeListener<>(GridPartKeyComponentI::keyOptionProperty, v -> v != null ? v.getClass() : null)
        ));
        this.initAll();
    }

    @Override
    public void initUI() {
        //Base
        this.setTitle(Translation.getText("pane.title.key.option.title"));
        VBox rows = new VBox();
        rows.setAlignment(Pos.CENTER);

        //Key option
        this.comboKeyOptionType = new ComboBox<>(AvailableKeyOptionManager.INSTANCE.getKeyOptions());
        this.comboKeyOptionType.setCellFactory((lv) -> new KeyOptionDetailledListCell());
        this.comboKeyOptionType.setButtonCell(new KeyOptionListCell());
        this.comboKeyOptionType.setMaxWidth(180);
        FXControlUtils.createAndAttachTooltip(comboKeyOptionType, "tooltip.explain.select.key.option");
        Separator separator = new Separator(Orientation.HORIZONTAL);
        VBox.setMargin(separator, new Insets(5, 0, 5, 0));

        //Option configuration
        this.paneOptionConfigurationView = new BorderPane();
        separator.visibleProperty().bind(this.paneOptionConfigurationView.visibleProperty());
        VBox.setVgrow(this.paneOptionConfigurationView, Priority.SOMETIMES);

        //Add
        rows.getChildren().addAll(this.comboKeyOptionType, separator, this.paneOptionConfigurationView);
        this.setContent(rows);
    }

    private ChangeListener<KeyOptionI> changeListenerForModelKeyOption;

    @Override
    public void initListener() {
        changeListenerForModelKeyOption = (obs, ov, nv) -> this.updateConfigurationView(nv);

        BiConsumer<ComboBox<KeyOptionI>, Class<? extends KeyOptionI>> fieldValueSetter = (cb, keyOption) ->
                cb.getItems().stream().filter(v -> v != null && v.getClass().equals(keyOption)).findFirst().ifPresent(cb::setValue);
        MultiKeyHelper.initMultiKeyConfigActionListener(this.comboKeyOptionType,
                ComboBoxBase::setOnAction,
                cb -> cb.getValue() != null ? cb.getValue().getClass() : null,
                fieldValueSetter,
                KeyActions.ChangeMultiKeyOptionAction::new,
                this.keyOptionProperty);
    }

    @Override
    public void initBinding() {
        this.model.bind(SelectionController.INSTANCE.selectedKeyHelperProperty());
    }

    @Override
    public void bind(final GridPartKeyComponentI component) {
        this.updateConfigurationView(component.keyOptionProperty().get()); // when key change, we also change the binded configuration
        component.keyOptionProperty().addListener(changeListenerForModelKeyOption);
    }

    @Override
    public void unbind(final GridPartKeyComponentI component) {
        this.unbindDisplayedOptionConfiguration();
        component.keyOptionProperty().removeListener(changeListenerForModelKeyOption);
    }

    // Class part : "Option configuration view"
    //========================================================================
    private void updateConfigurationView(final KeyOptionI option) {
        if (option == null || multiSelection) {
            this.hideOptionConfigurationView();
        } else {
            //Get the option configuration for key option type
            if (!this.keyOptionConfigurationViews.containsKey(option.getClass())) {
                KeyOptionConfigurationViewI<KeyOptionI> configurationView = KeyOptionViewProvider.INSTANCE.getConfigurationViewFor(option.getClass());
                if (configurationView != null) {
                    this.keyOptionConfigurationViews.put(option.getClass(), configurationView);
                }
            }
            //Unbind currently displayed option
            this.unbindDisplayedOptionConfiguration();
            //Display
            KeyOptionConfigurationViewI<KeyOptionI> configurationView = this.keyOptionConfigurationViews.get(option.getClass());
            if (configurationView != null) {
                this.displayedOptionConfiguration = configurationView;
                this.displayedOptionConfiguration.optionProperty().set(this.model.get().keyOptionProperty().get());
                Region node = configurationView.getConfigurationView();
                BorderPane.setAlignment(node,Pos.CENTER);
                this.paneOptionConfigurationView.setCenter(node);
                this.paneOptionConfigurationView.setVisible(true);
            } else {
                this.hideOptionConfigurationView();
            }

        }
    }

    private void hideOptionConfigurationView() {
        this.unbindDisplayedOptionConfiguration();
        this.paneOptionConfigurationView.setCenter(null);
        this.paneOptionConfigurationView.setVisible(false);
    }

    private void unbindDisplayedOptionConfiguration() {
        if (this.displayedOptionConfiguration != null) {
            this.displayedOptionConfiguration.optionProperty().set(null);
            this.displayedOptionConfiguration = null;
        }
    }
    //========================================================================

}
