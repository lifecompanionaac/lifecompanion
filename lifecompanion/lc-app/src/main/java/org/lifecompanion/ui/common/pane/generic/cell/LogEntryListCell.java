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

package org.lifecompanion.ui.common.pane.generic.cell;

import org.lifecompanion.model.impl.devmode.LogEntry;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public class LogEntryListCell extends ListCell<LogEntry> {
	private static final Font FONT = Font.font("Consolas", 11);

	public LogEntryListCell() {
		this.setFont(LogEntryListCell.FONT);
	}

	@Override
	protected void updateItem(final LogEntry item, final boolean empty) {
		super.updateItem(item, empty);
		if (item != null || !empty) {
			this.setText(item.getFormattedMessage());
			this.setTextFill(this.getColorForEntry(item));
		} else {
			this.setText(null);
		}
	}

	private Paint getColorForEntry(final LogEntry item) {
		return item.getLevel() != null ? item.getLevel().getColor() : Color.BLACK;
	}

}
