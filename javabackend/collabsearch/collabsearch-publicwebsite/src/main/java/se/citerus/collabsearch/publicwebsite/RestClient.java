package se.citerus.collabsearch.publicwebsite;

import se.citerus.collabsearch.model.SearchOperationDTO;
import se.citerus.collabsearch.model.SearchOperationIntro;
import se.citerus.collabsearch.model.interfaces.RestService;

public class RestClient implements RestService {
	
	public RestClient() {
		//establish connection to server address
	}

	public SearchOperationIntro[] getAllOps() {
		return null;
	}

	public SearchOperationDTO getSearchOperation(String name) {
		return null;
	}

	public void applyForSearchOp(String opName, String name, String email,
			String tele) {
	}

	public SearchOperationDTO[] searchForOps(String title, String location,
			String date) {
		return null;
	}
	
	/** Mockup WebAppException to be used before the addition of Jersey. **/
	@SuppressWarnings("serial")
	public class WebApplicationException extends Exception {
		public WebApplicationException(String message) {
			super(message);
		}
	}
}
