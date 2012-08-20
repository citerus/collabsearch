package se.citerus.collabsearch.api;

import java.util.List;

public interface RestService {

	public List<SearchOperationDTO> getAllOps();

	public SearchOperationDTO getSearchOperation(String name);
}
