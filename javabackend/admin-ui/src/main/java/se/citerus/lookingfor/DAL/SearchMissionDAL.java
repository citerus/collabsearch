package se.citerus.lookingfor.DAL;

import java.io.IOException;
import java.util.List;

import se.citerus.lookingfor.logic.FileMetadata;
import se.citerus.lookingfor.logic.SearchMission;
import se.citerus.lookingfor.logic.SearchOperation;
import se.citerus.lookingfor.logic.Status;

public interface SearchMissionDAL {

	public List<SearchMission> getAllSearchMissions() throws IOException;
	
	public void disconnect();

	public void endMission(String missionName) throws IOException;
	
	public void addOrModifyMission(SearchMission mission) throws IOException;

	public List<SearchOperation> getAllSearchOpsForMission(String missionName) throws IOException;
	
	public List<Status> getAllStatuses() throws IOException;

	public SearchMission findMission(String missionName) throws IOException;

	public SearchOperation findOperation(String opName, String missionName) throws IOException;

	public void addFileMetadata(String missionName, FileMetadata metadata) throws IOException;

	public void deleteFileMetadata(String filename, String missionName) throws IOException;

	public void deleteSearchOperation(String searchOpName, String missionName) throws IOException;

	public void addOrModifySearchOperation(SearchOperation operation, String missionName) throws IOException;

	public Status findStatus(String statusName) throws IOException;
}
