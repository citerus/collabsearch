package se.citerus.lookingfor.logic;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import se.citerus.lookingfor.DAL.SearchMissionDAL;
import se.citerus.lookingfor.DAL.SearchMissionDALInMemory;
import se.citerus.lookingfor.DAL.SearchMissionDALMongoDB;

public class SearchMissionHandler {
	private SearchMissionDAL searchMissionDAL;
	
	public SearchMissionHandler() {
		//searchMissionDAL = new SearchMissionDALMongoDB();
		searchMissionDAL = new SearchMissionDALInMemory();
	}

	public List<SearchMission> getListOfSearchMissions() throws Exception {
		return searchMissionDAL.getAllSearchMissions();
	}

	public void cleanUp() {
		searchMissionDAL.disconnect();
	}

	public void endMission(String name) throws Exception {
		searchMissionDAL.endMission(name);
	}

	public List<SearchOperation> getListOfSearchOps(String missionName) {
		try {
			return searchMissionDAL.getAllSearchOpsForMission(missionName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Status> getListOfStatuses() throws Exception {
		return searchMissionDAL.getAllStatuses();
	}

	public SearchMission getSearchMissionData(String missionName) throws Exception {
		return searchMissionDAL.findMission(missionName);
	}

	public SearchOperation getSearchOp(String name, String missionName) throws Exception {
		return searchMissionDAL.findOperation(name, missionName);
	}

	public void editMission(SearchMission mission) throws Exception {
		searchMissionDAL.addOrModifyMission(mission);
	}

	public void addFileToMission(String mission, FileMetadata metadata) throws Exception {
		searchMissionDAL.addFileMetadata(mission, metadata);
	}

	public void deleteFile(String filename, String missionName) throws Exception {
		searchMissionDAL.deleteFileMetadata(filename, missionName);
	}
}
