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
package org.lifecompanion.config.view.pane.general.view.predict4all;

import javafx.scene.control.ListCell;
import org.predict4all.nlp.words.model.Word;

public class P4AWordListCell extends ListCell<Word> {
    private final P4ADictionaryConfigurationView dictionaryConfigurationView;

    public P4AWordListCell(final P4ADictionaryConfigurationView dictionaryConfigurationView) {
        super();
        this.dictionaryConfigurationView = dictionaryConfigurationView;
    }

    @Override
    protected void updateItem(final Word item, final boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            this.setText(null);
        } else {
            if (item.isForceInvalid()) {
                this.setStyle("-fx-text-fill: red");
            } else if (item.isValidToBePredicted(this.dictionaryConfigurationView.getPredictorModelDto().getPredictionParameter())) {
                this.setStyle(null);
            } else {
                this.setStyle("-fx-text-fill: orange");
            }
            this.setText(item.getWord());
        }
    }

}
