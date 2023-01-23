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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

/**
 * Dump {@link Message} implementation to flag that the inbox is loading.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class DumpFlagMessage extends Message {

	public DumpFlagMessage() {
		super();
	}

	@Override
	public int getSize() throws MessagingException {
		return 0;
	}

	@Override
	public int getLineCount() throws MessagingException {
		return 0;
	}

	@Override
	public String getContentType() throws MessagingException {
		return null;
	}

	@Override
	public boolean isMimeType(String mimeType) throws MessagingException {
		return false;
	}

	@Override
	public String getDisposition() throws MessagingException {
		return null;
	}

	@Override
	public void setDisposition(String disposition) throws MessagingException {}

	@Override
	public String getDescription() throws MessagingException {
		return null;
	}

	@Override
	public void setDescription(String description) throws MessagingException {}

	@Override
	public String getFileName() throws MessagingException {
		return null;
	}

	@Override
	public void setFileName(String filename) throws MessagingException {}

	@Override
	public InputStream getInputStream() throws IOException, MessagingException {
		return null;
	}

	@Override
	public DataHandler getDataHandler() throws MessagingException {
		return null;
	}

	@Override
	public Object getContent() throws IOException, MessagingException {
		return null;
	}

	@Override
	public void setDataHandler(DataHandler dh) throws MessagingException {}

	@Override
	public void setContent(Object obj, String type) throws MessagingException {}

	@Override
	public void setText(String text) throws MessagingException {}

	@Override
	public void setContent(Multipart mp) throws MessagingException {}

	@Override
	public void writeTo(OutputStream os) throws IOException, MessagingException {}

	@Override
	public String[] getHeader(String header_name) throws MessagingException {
		return null;
	}

	@Override
	public void setHeader(String header_name, String header_value) throws MessagingException {}

	@Override
	public void addHeader(String header_name, String header_value) throws MessagingException {}

	@Override
	public void removeHeader(String header_name) throws MessagingException {}

	@Override
	public Enumeration<Header> getAllHeaders() throws MessagingException {
		return null;
	}

	@Override
	public Enumeration<Header> getMatchingHeaders(String[] header_names) throws MessagingException {
		return null;
	}

	@Override
	public Enumeration<Header> getNonMatchingHeaders(String[] header_names) throws MessagingException {
		return null;
	}

	@Override
	public Address[] getFrom() throws MessagingException {

		return null;
	}

	@Override
	public void setFrom() throws MessagingException {}

	@Override
	public void setFrom(Address address) throws MessagingException {}

	@Override
	public void addFrom(Address[] addresses) throws MessagingException {}

	@Override
	public Address[] getRecipients(RecipientType type) throws MessagingException {
		return null;
	}

	@Override
	public void setRecipients(RecipientType type, Address[] addresses) throws MessagingException {}

	@Override
	public void addRecipients(RecipientType type, Address[] addresses) throws MessagingException {}

	@Override
	public String getSubject() throws MessagingException {
		return null;
	}

	@Override
	public void setSubject(String subject) throws MessagingException {}

	@Override
	public Date getSentDate() throws MessagingException {
		return null;
	}

	@Override
	public void setSentDate(Date date) throws MessagingException {}

	@Override
	public Date getReceivedDate() throws MessagingException {
		return null;
	}

	@Override
	public Flags getFlags() throws MessagingException {
		return null;
	}

	@Override
	public void setFlags(Flags flag, boolean set) throws MessagingException {}

	@Override
	public Message reply(boolean replyToAll) throws MessagingException {
		return null;
	}

	@Override
	public void saveChanges() throws MessagingException {}

}
