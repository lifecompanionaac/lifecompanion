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

import javax.mail.Message;

public class MessageToSend {
	private String toAddress, toName;
	private String content;
	private String subject;
	private Message repliedMessage;

	public MessageToSend() {}

	public MessageToSend(MessageToSend messageToSend) {
		this.toAddress = messageToSend.toAddress;
		this.toName = messageToSend.toName;
		this.content = messageToSend.content;
		this.subject = messageToSend.subject;
		this.repliedMessage = messageToSend.repliedMessage;
	}

	public void clear() {
		this.toAddress = null;
		this.toName = null;
		this.content = null;
		this.subject = null;
		this.repliedMessage = null;
	}

	public MessageToSend copy() {
		return new MessageToSend(this);
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Message getRepliedMessage() {
		return repliedMessage;
	}

	public void setRepliedMessage(Message repliedMessage) {
		this.repliedMessage = repliedMessage;
	}

}
