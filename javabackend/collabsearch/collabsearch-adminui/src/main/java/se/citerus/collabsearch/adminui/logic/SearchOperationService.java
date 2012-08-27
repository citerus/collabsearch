package se.citerus.collabsearch.adminui.logic;

import java.util.List;

import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;
import se.citerus.collabsearch.store.inmemory.SearchOperationDAOInMemory;

public class SearchOperationService { //TODO refactor into spring service
	
	private SearchOperationDAO searchOperationDAO;
	
	public SearchOperationService() {
		//TODO choose type of DAO by config file
		//searchOperationDAO = new SearchOperationDAOMongoDB();
		searchOperationDAO = new SearchOperationDAOInMemory();
	}
	
	public List<Status> getAllSearchOpStatuses() throws Exception {
		return searchOperationDAO.getAllSearchOpStatuses();
	}

	public Status getSearchOpStatusByName(String opName) throws Exception {
		return searchOperationDAO.getSearchOpStatusByName(opName);
	}

	public void cleanUp() {
		searchOperationDAO.disconnect();
	}
}
