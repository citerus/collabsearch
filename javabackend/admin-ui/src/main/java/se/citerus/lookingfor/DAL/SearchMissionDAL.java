package se.citerus.lookingfor.DAL;

import java.io.IOException;
import java.util.List;

import se.citerus.lookingfor.logic.SearchMission;

public interface SearchMissionDAL {

	public List<SearchMission> getAllSearchMissions() throws IOException;
	
	public void disconnect();
	
}
