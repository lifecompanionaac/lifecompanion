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

import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Flags.Flag;

public class EmailCellContent {
	private final Message message;
	private final String subject;
	private final Date sentDate;
	private final Address[] from;
	private boolean seen;

	public EmailCellContent(Message message) throws MessagingException {
		this.message = message;
		this.subject = message.getSubject();
		this.sentDate = message.getSentDate();
		this.from = message.getFrom();
		this.seen = message.isSet(Flag.SEEN);
	}

	public EmailCellContent() {
		this.message = null;
		this.subject = null;
		this.sentDate = null;
		this.from = null;
	}

	public boolean isSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public String getSubject() {
		return subject;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public Address[] getFrom() {
		return from;
	}

	public Message getMessage() {
		return message;
	}

}
