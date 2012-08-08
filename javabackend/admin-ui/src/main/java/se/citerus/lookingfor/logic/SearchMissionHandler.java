package se.citerus.lookingfor.logic;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import se.citerus.lookingfor.DAL.SearchMissionDAL;
import se.citerus.lookingfor.DAL.SearchMissionDALInMemory;
import se.citerus.lookingfor.DAL.SearchMissionDALMongoDB;

public class SearchMissionHandler {
	SearchMissionDAL searchMissionDAL;
	
	public SearchMissionHandler() {
		//searchMissionDAL = new SearchMissionDALMongoDB();
		searchMissionDAL = new SearchMissionDALInMemory();
	}

	public List<SearchMission> getListOfSearchMissions() throws IOException {
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
	
	public List<Status> getListOfStatuses() throws IOException {
		return searchMissionDAL.getAllStatuses();
	}

	public SearchMission getSearchMissionData(String missionName) throws IOException {
		return searchMissionDAL.findMission(missionName);
	}
}
