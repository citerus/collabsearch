package se.citerus.collabsearch.adminui.logic;

import java.util.List;

import org.apache.commons.lang.Validate;

import se.citerus.collabsearch.model.SearchZone;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;
import se.citerus.collabsearch.store.inmemory.SearchMissionDAOInMemory;

public class SearchOperationService { // TODO refactor into spring service

	private SearchOperationDAO searchOperationDAO;

	public SearchOperationService() {
		// TODO choose type of DAO by config file
		// searchOperationDAO = new SearchOperationDAOMongoDB();
		searchOperationDAO = new SearchMissionDAOInMemory();
	}

	public List<Status> getAllSearchOpStatuses() throws Exception {
		return searchOperationDAO.getAllSearchOpStatuses();
	}

	public Status getSearchOpStatusByName(String opName) throws Exception {
		return searchOperationDAO.getSearchOpStatusByName(opName);
	}

	public void cleanUp() {
		// try {
		// searchOperationDAO.disconnect();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	public String endOperation(String opId) throws Exception {
		if (opId != null && !opId.equals("")) {
			String statusName = searchOperationDAO.endOperation(opId);
			return statusName;
		}
		throw new Exception("Felaktigt operations- eller uppdragsnamn");
	}

	public void deleteZone(String zoneId) throws Exception {
		if (zoneId != null) {
			searchOperationDAO.deleteZone(zoneId);
		}
	}

	public void deleteGroup(String groupId) throws Exception {
		if (groupId != null) {
			searchOperationDAO.deleteGroup(groupId);
		}
	}

	public SearchZone getZone(String zoneId) {
		Validate.notNull(zoneId);
		return searchOperationDAO.getZoneById(zoneId);
	}

	public void editZone(String zoneId, SearchZone zone) {
		Validate.notNull(zoneId);
		Validate.notNull(zone);
		searchOperationDAO.editZone(zoneId, zone);
	}

	public void createZone(String opId, SearchZone zone) {
		Validate.notNull(opId);
		Validate.notNull(zone);
		String zoneId = searchOperationDAO.createZone(opId, zone);
	}
}
