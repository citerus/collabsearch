package se.citerus.collabsearch.store.facades;

import java.io.IOException;
import java.util.List;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Status;

public interface SearchMissionDAO {

	public List<SearchMission> getAllSearchMissions() throws IOException;
	
	public void disconnect();

	public String endMission(String missionId) throws IOException;
	
	public List<Status> getAllStatuses() throws IOException;

	public SearchMission findMission(String missionId) throws IOException;

	public void addFileMetadata(String missionId, FileMetadata metadata) throws IOException;

	public void deleteFileMetadata(String filename, String missionId) throws IOException;
	
	public void addNewSearchMission(SearchMission mission) throws IOException;
	
	public void editExistingMission(SearchMission mission, String missionId) throws IOException;

	public Status findStatus(String statusName) throws IOException;
	
	/*
	 * SearchOperation operations. Will be broken out into SearchOperationDAO.
	 */
	public SearchOperation findOperation(String opId) throws IOException;
	
	public List<SearchOperation> getAllSearchOpsForMission(String missionId) throws IOException;
	
	public void deleteSearchOperation(String searchOpId) throws IOException;
	
	public void editSearchOperation(SearchOperation operation, String missionId) throws IOException;

	public FileMetadata getFileMetadata(String filename, String missionId);

	public void addSearchOperation(SearchOperation operation, String missionId);
	
}
