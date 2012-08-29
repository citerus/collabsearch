package se.citerus.collabsearch.adminui.logic;

import java.io.IOException;
import java.util.List;

import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
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

	public void addOrModifyMission(SearchMission mission, String missionName) throws Exception {
		if (missionName == null) {
			searchMissionDAL.addNewSearchMission(mission);
		} else {
			searchMissionDAL.editExistingMission(mission, missionName);
		}
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

	public void editSearchOp(SearchOperation operation, String missionName) throws Exception {
		searchMissionDAL.addOrModifySearchOperation(operation, missionName);
	}

	public Status getStatusByName(String statusName) throws Exception {
		return searchMissionDAL.findStatus(statusName);
	}
}
