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

	public void endMission(String missionName) throws IOException;

	public List<SearchOperation> getAllSearchOpsForMission(String missionName) throws IOException;
	
	public List<Status> getAllStatuses() throws IOException;

	public SearchMission findMission(String missionName) throws IOException;

	public SearchOperation findOperation(String opName, String missionName) throws IOException;

	public void addFileMetadata(String missionName, FileMetadata metadata) throws IOException;

	public void deleteFileMetadata(String filename, String missionName) throws IOException;

	public void deleteSearchOperation(String searchOpName, String missionName) throws IOException;

	public void addOrModifySearchOperation(SearchOperation operation, String missionName) throws IOException;
	
	public void addNewSearchMission(SearchMission mission) throws IOException;
	
	public void editExistingMission(SearchMission mission, String missionName) throws IOException;

	public Status findStatus(String statusName) throws IOException;
}
