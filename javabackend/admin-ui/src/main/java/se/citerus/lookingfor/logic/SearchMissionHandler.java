package se.citerus.lookingfor.logic;

import java.io.IOException;
import java.util.List;

import se.citerus.lookingfor.DAL.SearchMissionDAL;
import se.citerus.lookingfor.DAL.SearchMissionDALMongoDB;

public class SearchMissionHandler {
	SearchMissionDAL searchMissionDAL;
	
	public SearchMissionHandler() {
		searchMissionDAL = new SearchMissionDALMongoDB();
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
}
