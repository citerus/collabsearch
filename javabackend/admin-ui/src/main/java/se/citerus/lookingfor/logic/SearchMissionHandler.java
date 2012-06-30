package se.citerus.lookingfor.logic;

import java.util.List;

import se.citerus.lookingfor.DAL.SearchMissionDAL;

public class SearchMissionHandler {
	SearchMissionDAL searchMissionDAL;
	
	public SearchMissionHandler() {
		searchMissionDAL = new SearchMissionDAL();
	}

	public List<SearchMission> getListOfSearchMissions() {
		return searchMissionDAL.getAllSearchMissions();
	}
}
