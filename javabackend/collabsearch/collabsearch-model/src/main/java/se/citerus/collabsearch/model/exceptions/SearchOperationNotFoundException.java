package se.citerus.collabsearch.model.exceptions;

@SuppressWarnings("serial")
public class SearchOperationNotFoundException extends Exception {

	public SearchOperationNotFoundException() {
		super();
	}

	public SearchOperationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public SearchOperationNotFoundException(String message) {
		super(message);
	}

	public SearchOperationNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
