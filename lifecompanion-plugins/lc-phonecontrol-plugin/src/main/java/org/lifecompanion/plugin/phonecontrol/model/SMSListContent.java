package org.lifecompanion.plugin.phonecontrol.model;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SMSListContent {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
	private final String phoneNumber;
	private final String phoneNumberOrContactName;
	private final String SMS;
	private final String sentDate;
	private final boolean sendByMe;

	public SMSListContent(String phoneNumber, String phoneNumberOrContactName, String SMS, String sentDate, boolean sendByMe) {
		this.phoneNumber = phoneNumber;
		this.phoneNumberOrContactName = phoneNumberOrContactName;
		this.SMS = SMS;

		if (sentDate != null) {
			this.sentDate = this.formatSMSDate(sentDate);
		} else {
			this.sentDate = null;
		}

		this.sendByMe = sendByMe;
	}

	public SMSListContent() {
		this.phoneNumber = null;
		this.phoneNumberOrContactName = null;
		this.SMS = null;
		this.sentDate = null;
		this.sendByMe = false;
	}

	// Getters
	public String getphoneNumber() {
		return phoneNumber;
	}

	public String getphoneNumberOrContactName() {
		return phoneNumberOrContactName;
	}

	public String getSMS() {
		return SMS;
	}

	public String getSentDate() {
		return sentDate;
	}

	public boolean isSendByMe() {
		return sendByMe;
	}

	/**
	 * Format the date of the SMS to a more readable format
	 * @param smsDate The date of the SMS : "dd-MM-yyyy HH:mm:ss"
	 * @return The date of the SMS in a more readable format
	 */
	private String formatSMSDate(String smsDate) {
		try {
			Date date = SMSListContent.dateFormat.parse(smsDate);
			long currentTime = System.currentTimeMillis();
			long timeDifference = currentTime - date.getTime();

			long seconds = timeDifference / 1000;
			long minutes = seconds / 60;
			long hours = minutes / 60;
			long days = hours / 24;
			long weeks = days / 7;
			long months = weeks / 4;
			long years = months / 12;

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int smsDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			calendar.setTimeInMillis(currentTime);
			int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

			if (seconds < 60) {
				return "A l'instant";
			} else if (currentDayOfWeek == smsDayOfWeek && days < 2) {
				return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
			} else if ((currentDayOfWeek - smsDayOfWeek == 1 || currentDayOfWeek - smsDayOfWeek == -6) && days < 3) {
				return "Hier à " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
			} else if (days < 7) {
				String[] daysOfWeek = { "", "Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi" };

				return daysOfWeek[smsDayOfWeek] + " à " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
			} else if (weeks < 4) {
				return "Il y a " + weeks + " semaines";
			} else if (months < 12) {
				return "Il y a " + months + " mois";
			} else {
				return "Il y a " + years + " années";
			}
		} catch (ParseException e) {
			e.printStackTrace();

			return null;
		}
	}

	@Override
	public String toString() {
		return SMS + "\nDate : " + sentDate;
	}
}
