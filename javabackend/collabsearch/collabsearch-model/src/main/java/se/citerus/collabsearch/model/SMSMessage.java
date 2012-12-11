package se.citerus.collabsearch.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class SMSMessage {
	private static final String NEWLINE = "\n";
	private static final Locale SV_SE = new Locale("sv", "SE");
	
	private final long date;
	private final String location;
	private final String body;
	private final String opName;
	private final String contactInfo;
	
	public SMSMessage(final long date, final String place, final String body,
			final String opName, final String contactInfo) {
		this.date = date;
		this.location = place;
		this.body = body;
		this.opName = opName;
		this.contactInfo = contactInfo;
	}

	public long getDate() {
		return date;
	}

	public String getLocation() {
		return location;
	}

	public String getBody() {
		return body;
	}

	public String getOpName() {
		return opName;
	}

	public String getContactInfo() {
		return contactInfo;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(opName + ": ");
		sb.append(body.endsWith(".") ? body : body.concat(". "));
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, 
				DateFormat.MEDIUM, SV_SE);
		sb.append(df.format(new Date(date)) + ", ");
		sb.append(location + ", ");
		sb.append("Tel: " + contactInfo);
		return sb.toString();
	}
	
	public int getMessageLength() {
		return this.toString().length();
	}
}
