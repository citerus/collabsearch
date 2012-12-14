package se.citerus.collabsearch.model.exceptions;

@SuppressWarnings("serial")
public class NotImplementedException extends Exception {

	public NotImplementedException() {
		super();
	}

	public NotImplementedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotImplementedException(String message) {
		super(message);
	}

	public NotImplementedException(Throwable cause) {
		super(cause);
	}

}
