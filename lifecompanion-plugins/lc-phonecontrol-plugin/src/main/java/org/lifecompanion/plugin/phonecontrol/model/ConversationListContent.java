package org.lifecompanion.plugin.phonecontrol.model;

public class ConversationListContent {
	private final String phoneNumber;
	private final String contactName;
	private final String lastSMS;
	private boolean isSeen;
	private boolean isSendByMe;

	public ConversationListContent(String phoneNumber, String contactName, String lastSMS, boolean isSeen, boolean isSendByMe) {
		this.phoneNumber = phoneNumber;
		this.contactName = contactName;
		this.lastSMS = lastSMS;
		this.isSeen = isSeen;
		this.isSendByMe = isSendByMe;
	}

	public ConversationListContent() {
		this.phoneNumber = null;
		this.contactName = null;
		this.lastSMS = null;
		this.isSeen = false;
		this.isSendByMe = false;
	}

	// Getters
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getContactName() {
		return contactName;
	}

	public String getLastSMS() {
		return lastSMS;
	}

	public boolean isSeen() {
		return isSeen;
	}

	public boolean isSendByMe() {
		return isSendByMe;
	}

	// Setters
	public void setIsSeen(boolean isSeen) {
		this.isSeen = isSeen;
	}

	@Override
	public String toString() {
		String ret = "";
		if (phoneNumber != null) {
			ret = contactName + "\n";
			if (isSendByMe) {
				ret += "Vous : ";
			} else {
				ret += "Reçu : ";
			}
			ret += lastSMS;
		}
		return ret;
	}
}
