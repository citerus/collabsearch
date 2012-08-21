package se.citerus.collabsearch.api;

import java.util.List;

public interface RestService {

	public SearchOperationDTO[] getAllOps();

	public SearchOperationDTO getSearchOperation(String name);

	void applyForSearchOp(String opName, String name, String email, String tele);

	public SearchOperationDTO[] searchForOps(String title, String location, String date);
}
