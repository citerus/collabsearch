package se.citerus.collabsearch.adminui.logic;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import se.citerus.collabsearch.adminui.DAL.SearchMissionDAO;
import se.citerus.collabsearch.adminui.DAL.SearchMissionDAOInMemory;
import se.citerus.collabsearch.adminui.DAL.SearchMissionDAOMongoDB;
import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Status;

public class SearchMissionService { //TODO refactor into spring service
	
	private SearchMissionDAO searchMissionDAL;
	
	public SearchMissionService() {
		//TODO choose type of DAL by config file
		//searchMissionDAL = new SearchMissionDALMongoDB();
		searchMissionDAL = new SearchMissionDAOInMemory();
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

	public void deleteSearchOperation(String searchOpName, String missionName) throws Exception {
		searchMissionDAL.deleteSearchOperation(searchOpName, missionName);
	}

	public void clearSavedState() {
		if (searchMissionDAL instanceof SearchMissionDAOInMemory) {
			((SearchMissionDAOInMemory)searchMissionDAL).clearNewMissionContainer();
		}
	}

	public void setupSavedState() {
		if (searchMissionDAL instanceof SearchMissionDAOInMemory) {
			((SearchMissionDAOInMemory)searchMissionDAL).initNewMissionContainer();
		}
	}

	public void editSearchOp(SearchOperation operation, String missionName) throws Exception {
		searchMissionDAL.addOrModifySearchOperation(operation, missionName);
	}

	public Status getStatusByName(String statusName) throws Exception {
		return searchMissionDAL.findStatus(statusName);
	}
}
