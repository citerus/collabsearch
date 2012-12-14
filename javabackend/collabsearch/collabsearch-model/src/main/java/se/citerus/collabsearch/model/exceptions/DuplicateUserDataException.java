package se.citerus.collabsearch.model.exceptions;

@SuppressWarnings("serial")
public class DuplicateUserDataException extends Exception {

	public DuplicateUserDataException() {
	}

	public DuplicateUserDataException(String message) {
		super(message);
	}

	public DuplicateUserDataException(Throwable cause) {
		super(cause);
	}

	public DuplicateUserDataException(String message, Throwable cause) {
		super(message, cause);
	}


}
