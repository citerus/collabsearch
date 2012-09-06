package se.citerus.collabsearch.store.facades;

import java.io.IOException;
import java.util.List;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Status;

public interface SearchOperationDAO {

//	TODO implement the below operations in the SearchOpDAOs
//	public SearchOperation findOperation(String opName, String missionName) throws IOException;
//	
//	public List<SearchOperation> getAllSearchOpsForMission(String missionName) throws IOException;
//	
//	public void deleteSearchOperation(String searchOpName, String missionName) throws IOException;
//	
//	public void addOrModifySearchOperation(SearchOperation operation, String missionName) throws IOException;
	
	public List<Status> getAllSearchOpStatuses() throws IOException;

	public Status getSearchOpStatusByName(String statusName) throws IOException;

	public void disconnect();

	public String endOperation(String opName, String missionName);

	public String getZoneIdByName(String zoneName, String opName);

	public void deleteZone(String zoneId);

	public String getGroupIdByName(String groupName, String opName);

	public void deleteGroup(String groupId);
	
}
