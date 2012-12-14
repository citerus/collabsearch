package se.citerus.collabsearch.model.exceptions;

@SuppressWarnings("serial")
public class SearchGroupValidationException extends Exception {
	
	public SearchGroupValidationException() {
		super();
	}
	
	public SearchGroupValidationException(String message) {
		super(message);
	}
	
	public SearchGroupValidationException(Throwable cause) {
		super(cause);
	}
	
	public SearchGroupValidationException(String message, Throwable cause) {
		super(message, cause);
	}
	
}

