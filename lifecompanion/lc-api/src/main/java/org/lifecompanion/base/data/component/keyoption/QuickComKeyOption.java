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

package org.lifecompanion.base.data.component.keyoption;

import org.jdom2.Element;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.useaction.impl.text.write.WriteAndSpeakTextAction;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

/**
 * Key option to provide a quick way to create a key with written text (spoken or not)
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class QuickComKeyOption extends AbstractKeyOption {

	private final StringProperty textToWrite;
	private final StringProperty textToSpeak;
	private final BooleanProperty enableSpeak;
	private final BooleanProperty addSpace;

	/**
	 * Action to write and speak the text
	 */
	private WriteAndSpeakTextAction writeAndSpeakAction;

	/**
	 * To listen for key text content change
	 */
	private final ChangeListener<String> changeListenerKeyContent;

	public QuickComKeyOption() {
		super();
		this.optionNameId = "key.option.name.quick.communication";
		this.iconName = "icon_type_quick_communication.png";
		this.textToWrite = new SimpleStringProperty("");
		this.textToSpeak = new SimpleStringProperty("");
		this.enableSpeak = new SimpleBooleanProperty(true);
		this.addSpace = new SimpleBooleanProperty(true);
		//If both text are equals, text to speak is text to write
		this.textToWrite.addListener((obs, ov, nv) -> {
			if (StringUtils.isEquals(ov, this.textToSpeak.get())) {
				this.textToSpeak.set(nv);
			}
		});
		this.changeListenerKeyContent = (obs, ov, nv) -> {
			if (StringUtils.isEquals(ov, this.textToWrite.get())) {
				this.textToWrite.set(nv);
			}
		};
	}

	@Override
	public void attachToImpl(final GridPartKeyComponentI key) {
		//Get the existing action, or create new one
		this.writeAndSpeakAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, WriteAndSpeakTextAction.class);
		if (this.writeAndSpeakAction == null) {
			this.writeAndSpeakAction = new WriteAndSpeakTextAction();
			key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).add(this.writeAndSpeakAction);
		}
		this.writeAndSpeakAction.attachedToKeyOptionProperty().set(true);
		// Fix #125 : if text to write is empty, this action will set the text to write on deserialize.
		// Text to write is now set in AddQuickCommunicationKey
		// setTextToWriteToKeyContent();
		key.textContentProperty().addListener(this.changeListenerKeyContent);
		//Bind space
		this.writeAndSpeakAction.addSpaceProperty().bind(this.addSpace);
		this.writeAndSpeakAction.textToSpeakProperty().bind(this.textToSpeak);
		this.writeAndSpeakAction.textToWriteProperty().bind(this.textToWrite);
		this.writeAndSpeakAction.enableSpeakProperty().bind(this.enableSpeak);
	}

	@Override
	public void keyNewlyAttached() {
		super.keyNewlyAttached();
		//Bind content (and set text if the text to write is empty)
		GridPartKeyComponentI key = this.attachedKey.get();
		if (key != null && StringUtils.isBlank(this.textToWrite.get())) {
			this.textToWrite.set(key.textContentProperty().get());
		}
	}

	@Override
	public void detachFromImpl(final GridPartKeyComponentI key) {
		key.getActionManager().componentActions().get(UseActionEvent.ACTIVATION).remove(this.writeAndSpeakAction);
		key.textContentProperty().removeListener(this.changeListenerKeyContent);
	}

	public StringProperty textToWriteProperty() {
		return this.textToWrite;
	}

	public StringProperty textToSpeakProperty() {
		return this.textToSpeak;
	}

	public BooleanProperty enableSpeakProperty() {
		return this.enableSpeak;
	}

	public BooleanProperty addSpaceProperty() {
		return this.addSpace;
	}

	@Override
	public Element serialize(final IOContextI context) {
		Element elem = super.serialize(context);
		XMLObjectSerializer.serializeInto(QuickComKeyOption.class, this, elem);
		return elem;
	}

	@Override
	public void deserialize(final Element node, final IOContextI context) throws LCException {
		super.deserialize(node, context);
		XMLObjectSerializer.deserializeInto(QuickComKeyOption.class, this, node);
	}

}
