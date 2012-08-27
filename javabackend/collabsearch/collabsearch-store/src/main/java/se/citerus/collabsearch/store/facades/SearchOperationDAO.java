package se.citerus.collabsearch.store.facades;

import java.io.IOException;
import java.util.List;

import se.citerus.collabsearch.model.Status;

public interface SearchOperationDAO {

	public List<Status> getAllSearchOpStatuses() throws IOException;

	public Status getSearchOpStatusByName(String statusName) throws IOException;

	public void disconnect();
	
}
