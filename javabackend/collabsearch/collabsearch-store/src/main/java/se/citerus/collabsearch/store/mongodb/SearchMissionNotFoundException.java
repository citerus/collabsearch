package se.citerus.collabsearch.store.mongodb;

public class SearchMissionNotFoundException extends Exception {

	public SearchMissionNotFoundException() {
		super();
	}

	public SearchMissionNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public SearchMissionNotFoundException(String message) {
		super(message);
	}

	public SearchMissionNotFoundException(Throwable cause) {
		super(cause);
	}

}
