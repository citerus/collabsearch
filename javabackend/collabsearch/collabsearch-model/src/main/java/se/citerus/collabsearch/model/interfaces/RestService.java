package se.citerus.collabsearch.model.interfaces;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;

/**
 * And interface required be implemented by both clients and servers acessing the collabsearch RESTful web service.
 * @author Ola Rende
 */
public interface RestService {

	public SearchOperationIntro[] getAllOps();

	public SearchOperation getSearchOperation(String name);

	void applyForSearchOp(String opName, String name, String email, String tele);

	public SearchOperation[] searchForOps(String title, String location, String date);
}
