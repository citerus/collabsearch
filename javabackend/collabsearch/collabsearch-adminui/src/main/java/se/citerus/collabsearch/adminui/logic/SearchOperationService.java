package se.citerus.collabsearch.adminui.logic;

import java.util.List;

import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;
import se.citerus.collabsearch.store.inmemory.SearchOperationDAOInMemory;

public class SearchOperationService { //TODO refactor into spring service
	
	private SearchOperationDAO searchOperationDAO;
	
	public SearchOperationService() {
		//TODO choose type of DAO by config file
		//searchOperationDAO = new SearchOperationDAOMongoDB();
		searchOperationDAO = new SearchOperationDAOInMemory();
	}
	
	public List<Status> getAllSearchOpStatuses() throws Exception {
		return searchOperationDAO.getAllSearchOpStatuses();
	}

	public Status getSearchOpStatusByName(String opName) throws Exception {
		return searchOperationDAO.getSearchOpStatusByName(opName);
	}

	public void cleanUp() {
		searchOperationDAO.disconnect();
	}

	public String endOperation(String opName, String missionName) throws Exception {
		if ((opName != null && !opName.equals("")) 
				&& (missionName != null && !missionName.equals(""))) {
			String statusName = searchOperationDAO.endOperation(opName, missionName);
			return statusName;
		}
		throw new Exception("Felaktigt operations- eller uppdragsnamn");
	}

	public String resolveZoneId(String zoneName, String opName, String missionName) throws Exception {
		if ((zoneName != null) && !zoneName.equals("") 
				&& (opName != null) && !opName.equals("")
				&& (missionName != null) && !missionName.equals("")) {
			return searchOperationDAO.getZoneIdByName(zoneName, opName);
		}
		throw new Exception("Felaktigt zon-, operations- eller uppdragsnamn");
	}

	public void deleteZone(String zoneId) throws Exception {
		if (zoneId != null) {
			searchOperationDAO.deleteZone(zoneId);
		}
	}

	public String resolveGroupId(String groupName, String opName,
			String missionName) throws Exception {
		if ((groupName != null) && !groupName.equals("") 
				&& (opName != null) && !opName.equals("")
				&& (missionName != null) && !missionName.equals("")) {
			return searchOperationDAO.getGroupIdByName(groupName, opName);
		}
		throw new Exception("Felaktigt zon-, operations- eller uppdragsnamn");
	}

	public void deleteGroup(String groupId) throws Exception {
		if (groupId != null) {
			searchOperationDAO.deleteGroup(groupId);
		}
	}
}
