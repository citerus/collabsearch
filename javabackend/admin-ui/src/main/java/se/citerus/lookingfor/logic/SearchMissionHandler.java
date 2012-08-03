package se.citerus.lookingfor.logic;

import java.util.List;

import se.citerus.lookingfor.DAL.SearchMissionDALMongoDB;

public class SearchMissionHandler {
	SearchMissionDALMongoDB searchMissionDAL;
	
	public SearchMissionHandler() {
		searchMissionDAL = new SearchMissionDALMongoDB();
	}

	public List<SearchMission> getListOfSearchMissions() {
		return searchMissionDAL.getAllSearchMissions();
	}
}
