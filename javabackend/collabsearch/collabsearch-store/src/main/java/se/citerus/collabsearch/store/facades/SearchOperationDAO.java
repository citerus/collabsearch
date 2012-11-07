package se.citerus.collabsearch.store.facades;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchFinding;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.model.SearchZone;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.exceptions.SearchGroupNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchOperationNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchZoneNotFoundException;

public interface SearchOperationDAO {

	public SearchOperationWrapper[] getAllSearchOps() throws IOException;

	public List<Status> getAllSearchOpStatuses() throws IOException;

	public Status getSearchOpStatus(String statusName) throws IOException;

	public String endOperation(String opName) throws SearchOperationNotFoundException, IOException;

	public void deleteZone(String zoneId) throws IOException;

	public void deleteGroup(String groupId) throws IOException;

	public SearchOperation getSearchOpById(String name) throws IOException;

	public void assignUserToSearchOp(String opName, String name, String email,
			String tele) throws IOException, SearchOperationNotFoundException;

	public SearchOperationWrapper[] getSearchOpsByFilter(String title,
			String location, String startDate, String endDate)
			throws IOException;

	public String[] getAllOpLocations() throws IOException;

	public String[] getAllOpTitles() throws IOException;

	public SearchZone getZoneById(String zoneId) throws IOException, SearchZoneNotFoundException;

	public void editZone(String zoneId, SearchZone zone) throws IOException;

	public String createZone(String opId, SearchZone zone) throws IOException;

	public SearchOperation findOperation(String opId) throws IOException, SearchOperationNotFoundException;

	public void deleteSearchOperation(String searchOpId) throws IOException, SearchOperationNotFoundException;

	public void editSearchOperation(SearchOperation operation, String missionId)
			throws IOException;

	public String createSearchOperation(SearchOperation operation, String missionId)
			throws IOException;

	public SearchGroup getSearchGroup(String groupId) throws IOException, SearchGroupNotFoundException;

	public Map<String, String> getUsersForSearchOp(String opId)
			throws IOException;

	public String addSearchGroup(SearchGroup group, String opId)
			throws IOException;

	public void editSearchGroup(SearchGroup group, String opId)
			throws IOException;

	public void setDebugDB(String dbName);

}
