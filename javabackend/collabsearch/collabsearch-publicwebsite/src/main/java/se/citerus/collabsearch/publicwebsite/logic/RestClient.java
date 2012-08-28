package se.citerus.collabsearch.publicwebsite.logic;

import javax.ws.rs.core.Response;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;
import se.citerus.collabsearch.model.interfaces.RestService;

public class RestClient implements RestService {
	
	public RestClient() {
		//establish connection to server address
	}

	public SearchOperationIntro[] getAllOps() {
		return null;
	}

	public SearchOperation getSearchOperationByName(String name) {
		return null;
	}

	public Response applyForSearchOp(String opName, String name, String email,
			String tele) {
		return null;
	}

	public SearchOperationIntro[] searchForOps(String title, String location,
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
