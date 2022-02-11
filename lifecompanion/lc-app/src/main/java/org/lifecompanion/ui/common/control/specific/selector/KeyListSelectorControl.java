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
package org.lifecompanion.ui.common.control.specific.selector;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.ui.common.pane.specific.cell.KeyListCellHandler;
import org.lifecompanion.ui.common.pane.specific.cell.SimpleKeyListContentListCell;
import org.lifecompanion.ui.common.control.generic.searchcombobox.SearchComboBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;

import java.util.function.Function;

public class KeyListSelectorControl extends VBox implements LCViewInitHelper {

    private Label label;
    private final String labelText;
    private SearchComboBox<KeyListNodeI> searchComboBox;
    private final StringProperty selectedKeylistCategoryId;

    public KeyListSelectorControl(final String labelText) {
        this.labelText = labelText;
        selectedKeylistCategoryId = new SimpleStringProperty();
        this.initAll();
    }

    public StringProperty selectedKeylistCategoryIdProperty() {
        return selectedKeylistCategoryId;
    }

    @Override
    public void initUI() {
        //Create label
        if (labelText != null) {
            this.label = new Label(this.labelText);
        }
        // Search combobox
        searchComboBox = new SearchComboBox<>(lv -> new SimpleKeyListContentListCell(),
                searchText ->
                        StringUtils.isBlank(searchText) ? null : c -> LCUtils.getSimilarityScoreFor(searchText, c, getNameGetterForCategory()) > 0
                , comp -> comp != null ? comp.textProperty().get() : Translation.getText("key.list.selector.control.no.value"),
                searchText -> StringUtils.isBlank(searchText) ? null : (c1, c2) -> Double.compare(
                        LCUtils.getSimilarityScoreFor(searchText, c2, getNameGetterForCategory()),
                        LCUtils.getSimilarityScoreFor(searchText, c1, getNameGetterForCategory())
                ));
        searchComboBox.setFixedCellSize(KeyListCellHandler.CELL_HEIGHT + 5);
        this.setSpacing(5.0);
        if (label != null) {
            this.getChildren().add(label);
        }
        this.getChildren().add(searchComboBox);
    }

    private Function<KeyListNodeI, Pair<String, Double>>[] getNameGetterForCategory() {
        Function<KeyListNodeI, Pair<String, Double>> getter = klc -> Pair.of(klc.textProperty().get(), 1.0);
        return new Function[]{getter};
    }

    public void setTooltipText(String tooltipTextId) {
        Tooltip.install(searchComboBox, UIUtils.createTooltip(tooltipTextId));
    }

    @Override
    public void initBinding() {
        // When selection change in combobox, change in ID
        this.searchComboBox.valueProperty().addListener((obs, ov, nv) -> selectedKeylistCategoryId.set(nv != null ? nv.getID() : null));
        this.selectedKeylistCategoryId.addListener((obs, ov, nv) -> {
            if (nv != null) {
                currentNodeRoot.traverseTreeToBottom(childNode -> {
                    if (StringUtils.isEquals(nv, childNode.getID())) {
                        searchComboBox.valueProperty().set(childNode);
                    }
                });
            } else {
                searchComboBox.valueProperty().set(null);
            }
        });
    }

    private KeyListNodeI currentNodeRoot;

    public void setInputKeyNode(KeyListNodeI node) {
        if (node == null) {
            currentNodeRoot = null;
            searchComboBox.setItems(null);
            searchComboBox.valueProperty().set(null);
        } else {
            // Find root node
            currentNodeRoot = node;
            while (node != null) {
                currentNodeRoot = node;
                node = node.parentProperty().get();
            }
            ObservableList<KeyListNodeI> items = FXCollections.observableArrayList();
            currentNodeRoot.traverseTreeToBottom(childNode -> {
                if (!childNode.isLeafNode()) {
                    items.add(childNode);
                }
            });
            searchComboBox.setItems(items);
        }
    }

}
