package se.citerus.collabsearch.model.interfaces;

import javax.ws.rs.core.Response;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;
import se.citerus.collabsearch.model.StringArrayWrapper;

/**
 * And interface required be implemented by both clients and servers acessing the collabsearch RESTful web service.
 * @author Ola Rende
 */
public interface RestService {

	public SearchOperationIntro[] getAllOps() throws Exception;

	public SearchOperation getSearchOperationById(String name) throws Exception;

	public Response applyForSearchOp(String opName, String name, String email, String tele) throws Exception;

	public SearchOperationIntro[] searchForOps(String title, String location, String startDate, String endDate) throws Exception;
	
	public StringArrayWrapper[] getAllLocations() throws Exception;
	
	public StringArrayWrapper[] getAllTitles() throws Exception;
}
