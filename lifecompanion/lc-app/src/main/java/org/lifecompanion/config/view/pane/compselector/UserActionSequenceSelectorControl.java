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
package org.lifecompanion.config.view.pane.compselector;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.lifecompanion.api.component.definition.simplercomp.UserActionSequenceI;
import org.lifecompanion.api.component.definition.simplercomp.UserActionSequencesI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.config.view.pane.general.view.simplercomp.useractionsequence.UserActionSequenceListCell;
import org.lifecompanion.config.view.reusable.searchcombobox.SearchComboBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;

import java.util.function.Function;

public class UserActionSequenceSelectorControl extends VBox implements LCViewInitHelper {

    private Label label;
    private final String labelText;
    private SearchComboBox<UserActionSequenceI> searchComboBox;
    private final StringProperty selectedSequenceId;

    public UserActionSequenceSelectorControl(final String labelText) {
        this.labelText = labelText;
        selectedSequenceId = new SimpleStringProperty();
        this.initAll();
    }

    public StringProperty selectedSequenceId() {
        return selectedSequenceId;
    }

    @Override
    public void initUI() {
        //Create label
        if (labelText != null) {
            this.label = new Label(this.labelText);
        }
        // Search combobox
        searchComboBox = new SearchComboBox<>(
                lv -> new UserActionSequenceListCell(),
                searchText -> StringUtils.isBlank(searchText) ? null : c -> LCUtils.getSimilarityScoreFor(searchText, c, getNameGetterForCategory()) > 0
                , comp -> comp != null ? comp.nameProperty().get() : Translation.getText("key.list.selector.control.no.value"),
                searchText -> StringUtils.isBlank(searchText) ? null : (c1, c2) -> Double.compare(
                        LCUtils.getSimilarityScoreFor(searchText, c2, getNameGetterForCategory()),
                        LCUtils.getSimilarityScoreFor(searchText, c1, getNameGetterForCategory())
                ));
        searchComboBox.setFixedCellSize(35.0);

        this.setSpacing(5.0);
        if (label != null) {
            this.getChildren().add(label);
        }
        this.getChildren().add(searchComboBox);
        //this.setMaxHeight(SimpleKeyListContentListCell.CELL_HEIGHT);
    }

    private Function<UserActionSequenceI, Pair<String, Double>>[] getNameGetterForCategory() {
        Function<UserActionSequenceI, Pair<String, Double>> getter = klc -> Pair.of(klc.nameProperty().get(), 1.0);
        return new Function[]{getter};
    }

    public void setTooltipText(String tooltipTextId) {
        Tooltip.install(searchComboBox, UIUtils.createTooltip(tooltipTextId));
    }

    @Override
    public void initBinding() {
        this.searchComboBox.valueProperty().addListener((obs, ov, nv) -> selectedSequenceId.set(nv != null ? nv.getID() : null));
        this.selectedSequenceId.addListener((obs, ov, nv) -> {
            if (nv != null) {
                searchComboBox.valueProperty().set(currentActionSequences.getUserActionSequences().stream().filter(s -> StringUtils.isEquals(s.getID(), nv)).findAny().orElse(null));
            } else {
                searchComboBox.valueProperty().set(null);
            }
        });
    }

    private UserActionSequencesI currentActionSequences;

    public void setInputUserActionSequences(UserActionSequencesI userActionSequences) {
        this.currentActionSequences = userActionSequences;
        if (userActionSequences == null) {
            searchComboBox.setItems(null);
            searchComboBox.valueProperty().set(null);
        } else {
            searchComboBox.setItems(userActionSequences.getUserActionSequences());
        }
    }
}
