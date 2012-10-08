package se.citerus.collabsearch.store.facades;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.model.SearchZone;
import se.citerus.collabsearch.model.SearcherInfo;
import se.citerus.collabsearch.model.Status;

public interface SearchMissionDAO {

	public List<SearchMission> getAllSearchMissions() throws IOException;

	public void disconnect() throws Exception;

	public String endMission(String missionId) throws IOException;

	public List<Status> getAllStatuses() throws IOException;

	public SearchMission findMission(String missionId) throws IOException;

	public void addFileMetadata(String missionId, FileMetadata metadata)
			throws IOException;

	public void deleteFileMetadata(String filename, String missionId)
			throws IOException;

	public void addNewSearchMission(SearchMission mission) throws IOException;

	public void editExistingMission(SearchMission mission, String missionId)
			throws IOException;

	public Status findStatus(String statusName) throws IOException;

	/*
	 * SearchOperation operations. Will be broken out into SearchOperationDAO.
	 */
	public SearchOperation findOperation(String opId) throws IOException;

	public List<SearchOperation> getAllSearchOpsForMission(String missionId)
			throws IOException;

	public void deleteSearchOperation(String searchOpId) throws IOException;

	public void editSearchOperation(SearchOperation operation, String missionId)
			throws IOException;

	public FileMetadata getFileMetadata(String filename, String missionId);

	public void addSearchOperation(SearchOperation operation, String missionId)
			throws IOException;

	public SearchGroup getSearchGroup(String groupId) throws IOException;

	public Map<String, String> getUsersForSearchOp(String opId)
			throws IOException;

	public void addSearchGroup(SearchGroup group, String opId)
			throws IOException;

	public void editSearchGroup(SearchGroup group, String opId)
			throws IOException;

	public SearchOperationWrapper[] getAllSearchOps() throws IOException;

	public List<Status> getAllSearchOpStatuses() throws IOException;

	public Status getSearchOpStatusByName(String statusName) throws IOException;

	public String endOperation(String opName);

	public void deleteZone(String zoneId);

	public void deleteGroup(String groupId);

	public SearchOperation getSearchOpById(String name) throws IOException;

	public void assignUserToSearchOp(String opName, String name, String email,
			String tele) throws IOException;

	public SearchOperationWrapper[] getSearchOpsByFilter(String title,
			String location, String startDate, String endDate)
			throws IOException;

	public String[] getAllOpLocations();

	public String[] getAllOpTitles();

	public SearchZone getZoneById(String zoneId);
}
