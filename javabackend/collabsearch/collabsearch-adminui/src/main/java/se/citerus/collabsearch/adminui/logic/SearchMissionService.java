package se.citerus.collabsearch.adminui.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

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

	public SearchOperation getSearchOp(String searchOpId) throws Exception {
		return searchMissionDAL.findOperation(searchOpId);
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

	/**
	 * Deletes a search operation and all it's zones and groups.
	 * @param searchOpId
	 * @throws Exception
	 */
	public void deleteSearchOperation(String searchOpId) throws Exception {
		searchMissionDAL.deleteSearchOperation(searchOpId);
	}

	public void editSearchOp(SearchOperation operation, String opId, String missionId) throws Exception {
		if (opId == null && missionId != null) {
			searchMissionDAL.addSearchOperation(operation, missionId);
		} else if (opId != null && missionId == null) {
			searchMissionDAL.editSearchOperation(operation, opId);
		}
	}

	public Status getStatusByName(String statusName) throws Exception {
		return searchMissionDAL.findStatus(statusName);
	}

	public SearchGroup getSearchGroup(String groupId) throws Exception {
		if (groupId == null) {
			throw new Exception("Inget gruppid specifierat");
		}
		SearchGroup group = searchMissionDAL.getGroupById(groupId);
		if (group == null) {
			throw new Exception("Ingen gruppdata funnen");
		}
		return group;
	}

	/**
	 * Get a list of Searchers who have volunteered for the specified SearchOperation.
	 * @param opId the id of the SearchOperation.
	 * @return a list of SearcherInfo objects representing the searchers applied to the operation.
	 * @throws Exception
	 */
	public List<SearcherInfo> getListOfSearchers(String opId) throws Exception {
		if (opId == null) {
			throw new Exception("Inget sökoperationsid specifierat");
		}
		List<SearcherInfo> list = searchMissionDAL.getUsersForSearchOp(opId); 
		return list;
	}

	public void addGroupToOperation(SearchGroup group) {
	}

}
