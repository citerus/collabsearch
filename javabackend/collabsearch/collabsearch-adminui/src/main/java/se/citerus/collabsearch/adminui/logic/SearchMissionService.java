package se.citerus.collabsearch.adminui.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearcherInfo;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.store.facades.SearchMissionDAO;
import se.citerus.collabsearch.store.inmemory.SearchMissionDAOInMemory;

public class SearchMissionService { //TODO refactor into spring service
	
	private SearchMissionDAO searchMissionDAO;

	public SearchMissionService() {
		//TODO choose type of DAO by config file
		//searchMissionDAL = new SearchMissionDALMongoDB();
		searchMissionDAO = new SearchMissionDAOInMemory();
	}

	public List<SearchMission> getListOfSearchMissions() throws Exception {
		return searchMissionDAO.getAllSearchMissions();
	}

	public void cleanUp() {
		try {
			searchMissionDAO.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void endMission(String missionId) throws Exception {
		Validate.notNull(missionId);
		searchMissionDAO.endMission(missionId);
	}
	
	public List<Status> getListOfStatuses() throws Exception {
		return searchMissionDAO.getAllSearchMissionStatuses();
	}

	public SearchMission getSearchMissionData(String missionId) throws Exception {
		Validate.notNull(missionId);
		return searchMissionDAO.findMission(missionId);
	}

	public void addOrModifyMission(SearchMission mission, String missionId) throws Exception {
		//break into two methods
		if (missionId == null) {
			//create mission obj here instead
			searchMissionDAO.addNewSearchMission(mission);
		} else {
			//create mission obj here instead
			searchMissionDAO.editExistingMission(mission, missionId);
		}
	}
	
	public void addFileToMission(String missionId, FileMetadata metadata) throws Exception {
		Validate.notNull(missionId);
		Validate.notNull(metadata);
		searchMissionDAO.addFileMetadata(missionId, metadata);
	}
	
	public void deleteFile(String filename, String missionId) throws Exception {		
		Validate.notNull(filename);
		Validate.notNull(missionId);
		FileMetadata metadata = searchMissionDAO.getFileMetadata(filename, missionId);
		File file = new File(metadata.getFilePath());
		file.delete();
		searchMissionDAO.deleteFileMetadata(filename, missionId);
	}

	public Status getStatusByName(String statusName) throws Exception {
		Validate.notNull(statusName);
		return searchMissionDAO.findMissionStatusByName(statusName);
	}

}
