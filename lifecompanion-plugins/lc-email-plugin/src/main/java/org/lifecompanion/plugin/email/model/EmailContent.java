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

package org.lifecompanion.plugin.email.model;

import java.util.List;

import javax.mail.Message;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EmailContent {
	private final String fullText;
	private final List<String> texts;
	private final Message message;
	private int index;
	private final StringProperty currentText;

	public EmailContent(Message message, final String fullText, List<String> texts) {
		super();
		this.message = message;
		this.texts = texts;
		this.fullText = fullText;
		currentText = new SimpleStringProperty();
		this.updateContent();
	}

	public StringProperty currentTextProperty() {
		return this.currentText;
	}

	public List<String> getTexts() {
		return texts;
	}

	public Message getMessage() {
		return message;
	}

	public String getFullText() {
		return fullText;
	}

	public void nextItem() {
		if (index + 1 < texts.size())
			index++;
		this.updateContent();
	}

	public void previousItem() {
		if (index > 0)
			index--;
		this.updateContent();
	}

	private void updateContent() {
		Platform.runLater(() -> this.currentText.set(index >= 0 && index < texts.size() ? texts.get(index) : null));
	}
}
