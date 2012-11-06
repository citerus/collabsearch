package se.citerus.collabsearch.model.exceptions;

public class SearchGroupNotFoundException extends Exception {

	public SearchGroupNotFoundException() {
		super();
	}

	public SearchGroupNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public SearchGroupNotFoundException(String message) {
		super(message);
	}

	public SearchGroupNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
