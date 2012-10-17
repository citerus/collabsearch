package se.citerus.collabsearch.store.facades;

import java.io.IOException;
import java.util.List;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.store.mongodb.SearchMissionNotFoundException;

public interface SearchMissionDAO {

	public List<SearchMission> getAllSearchMissions() throws IOException;

	public void disconnect() throws Exception;

	public void endMission(String missionId) throws IOException;

	public List<Status> getAllSearchMissionStatuses() throws IOException;

	public SearchMission findMission(String missionId) throws IOException, SearchMissionNotFoundException, Exception;

	public void addFileMetadata(String missionId, FileMetadata metadata)
			throws IOException;

	public FileMetadata getFileMetadata(String filename, String missionId);
	
	public void deleteFileMetadata(String filename, String missionId)
			throws IOException;

	public void addNewSearchMission(SearchMission mission) throws IOException;

	public void editExistingMission(SearchMission mission, String missionId)
			throws IOException;

	public Status findMissionStatusByName(String statusName) throws IOException;

}
