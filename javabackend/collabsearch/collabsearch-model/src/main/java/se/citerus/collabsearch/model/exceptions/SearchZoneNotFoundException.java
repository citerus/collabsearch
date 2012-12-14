package se.citerus.collabsearch.model.exceptions;

@SuppressWarnings("serial")
public class SearchZoneNotFoundException extends Exception {

	public SearchZoneNotFoundException() {
	}

	public SearchZoneNotFoundException(String message) {
		super(message);
	}

	public SearchZoneNotFoundException(Throwable cause) {
		super(cause);
	}

	public SearchZoneNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
