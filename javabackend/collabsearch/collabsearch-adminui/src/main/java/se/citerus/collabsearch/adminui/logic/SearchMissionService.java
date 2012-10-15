package se.citerus.collabsearch.adminui.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearcherInfo;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.store.facades.SearchMissionDAO;
import se.citerus.collabsearch.store.inmemory.SearchMissionDAOInMemory;

public class SearchMissionService { //TODO refactor into spring service
	
	private SearchMissionDAO searchMissionDAL;

	public SearchMissionService() {
		//TODO choose type of DAO by config file
		//searchMissionDAL = new SearchMissionDALMongoDB();
		searchMissionDAL = new SearchMissionDAOInMemory();
	}

	public List<SearchMission> getListOfSearchMissions() throws Exception {
		return searchMissionDAL.getAllSearchMissions();
	}

	public void cleanUp() {
		try {
			searchMissionDAL.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String endMission(String missionId) throws Exception {
		return searchMissionDAL.endMission(missionId);
	}
	
	public List<Status> getListOfStatuses() throws Exception {
		return searchMissionDAL.getAllStatuses();
	}

	public SearchMission getSearchMissionData(String missionId) throws Exception {
		return searchMissionDAL.findMission(missionId);
	}

	public void addOrModifyMission(SearchMission mission, String missionId) throws Exception {
		if (missionId == null) {
			searchMissionDAL.addNewSearchMission(mission);
		} else {
			searchMissionDAL.editExistingMission(mission, missionId);
		}
	}
	
	public void addFileToMission(String missionId, FileMetadata metadata) throws Exception {
		searchMissionDAL.addFileMetadata(missionId, metadata);
	}
	
	public void deleteFile(String filename, String missionId) throws Exception {		
		try {
			FileMetadata metadata = searchMissionDAL.getFileMetadata(filename, missionId);
			File file = new File(metadata.getFilePath());
			file.delete();
			searchMissionDAL.deleteFileMetadata(filename, missionId);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Status getStatusByName(String statusName) throws Exception {
		return searchMissionDAL.findStatus(statusName);
	}

}
