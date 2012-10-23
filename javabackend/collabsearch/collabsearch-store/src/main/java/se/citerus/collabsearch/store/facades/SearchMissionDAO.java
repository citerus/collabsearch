package se.citerus.collabsearch.store.facades;

import java.io.IOException;
import java.util.List;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.exceptions.SearchMissionNotFoundException;

public interface SearchMissionDAO {

	/** Returns a list of all available search missions. */
	public List<SearchMission> getAllSearchMissions() throws IOException;

	public void disconnect() throws Exception;

	public void endMission(String missionId) throws IOException;

	public List<Status> getAllSearchMissionStatuses() throws IOException;

	public SearchMission findMission(String missionId) throws IOException, SearchMissionNotFoundException, Exception;

	public void addFileMetadata(String missionId, FileMetadata metadata)
			throws IOException;

	public FileMetadata getFileMetadata(String filename, String missionId) throws IOException;
	
	public void deleteFileMetadata(String filename, String missionId)
			throws IOException;

	public String createSearchMission(SearchMission mission) throws IOException;

	public void editSearchMission(SearchMission mission, String missionId)
			throws IOException;

	public Status findMissionStatusByName(String statusName) throws IOException;

}
