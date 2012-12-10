package se.citerus.collabsearch.model;

public class SMSMessage {
	private static final String NEWLINE = "\n";
	
	private final String date;
	private final String location;
	private final String body;
	private final String opName;
	private final String contactInfo;
	
	public SMSMessage(final String date, final String place, final String body,
			final String opName, final String contactInfo) {
		this.date = date;
		this.location = place;
		this.body = body;
		this.opName = opName;
		this.contactInfo = contactInfo;
	}

	public String getDate() {
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
		sb.append(opName + NEWLINE);
		sb.append(body + NEWLINE);
		sb.append("Tid: " + date + NEWLINE);
		sb.append("Ort: " + location + NEWLINE);
		sb.append("Kontakt: " + contactInfo);
		return sb.toString();
	}
	
	public int getMessageLength() {
		return this.toString().length();
	}
}
