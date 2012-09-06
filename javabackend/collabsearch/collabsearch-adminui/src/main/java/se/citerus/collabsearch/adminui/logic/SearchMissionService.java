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

	public String endMission(String missionId) throws Exception {
		return searchMissionDAL.endMission(missionId);
	}

	public List<SearchOperation> getListOfSearchOps(String missionId) {
		try {
			return searchMissionDAL.getAllSearchOpsForMission(missionId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Status> getListOfStatuses() throws Exception {
		return searchMissionDAL.getAllStatuses();
	}

	public SearchMission getSearchMissionData(String missionId) throws Exception {
		return searchMissionDAL.findMission(missionId);
	}

	public SearchOperation getSearchOp(String opName, String missionId) throws Exception {
		return searchMissionDAL.findOperation(opName, missionId);
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
		searchMissionDAL.deleteFileMetadata(filename, missionId);
	}

	public void deleteSearchOperation(String searchOpName, String missionId) throws Exception {
		searchMissionDAL.deleteSearchOperation(searchOpName, missionId);
	}

	public void editSearchOp(SearchOperation operation, String missionId) throws Exception {
		searchMissionDAL.addOrModifySearchOperation(operation, missionId);
	}

	public Status getStatusByName(String statusName) throws Exception {
		return searchMissionDAL.findStatus(statusName);
	}
}
