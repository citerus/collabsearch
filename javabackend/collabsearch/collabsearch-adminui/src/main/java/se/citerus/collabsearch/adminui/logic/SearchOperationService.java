package se.citerus.collabsearch.adminui.logic;

import java.awt.geom.Point2D.Double;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;

import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchOperation;
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
		List<Status> statuses = searchOperationDAO.getAllSearchOpStatuses();
		Validate.notNull(statuses);
		return statuses;
	}

	public Status getSearchOpStatusByName(String opName) throws Exception {
		Validate.notNull(opName);
		Status status = searchOperationDAO.getSearchOpStatus(opName);
		Validate.notNull(status);
		return status;
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
		Validate.notNull(zoneId);
		searchOperationDAO.deleteZone(zoneId);
	}

	public void deleteGroup(String groupId) throws Exception {
		Validate.notNull(groupId);
		searchOperationDAO.deleteGroup(groupId);
	}

	public SearchZone getZone(String zoneId) throws Exception {
		Validate.notNull(zoneId);
		SearchZone zone = searchOperationDAO.getZoneById(zoneId);
		Validate.notNull(zone);
		return zone;
	}

	public String createZone(String opId, SearchZone zone) throws Exception {
		Validate.notNull(opId);
		Validate.notNull(zone);
		String zoneId = searchOperationDAO.createZone(opId, zone);
		Validate.notNull(zoneId);
		return zoneId;
	}

	public void editZone(String zoneId, String title, String prioStr, 
			Double[] points, int zoomLevel) throws Exception {
		validateZoneInput(zoneId, title, prioStr, points, zoomLevel);
		
		int priority = Integer.parseInt(prioStr);
		
		SearchZone zone = new SearchZone(title, priority, points, zoomLevel);
		searchOperationDAO.editZone(zoneId, zone);
	}

	public void createZone(String opId, String title, String prioStr,
			Double[] points, int zoomLevel) throws Exception {
		validateZoneInput(opId, title, prioStr, points, zoomLevel);
		
		int priority = Integer.parseInt(prioStr);
		
		SearchZone zone = new SearchZone(title, priority, points, zoomLevel);
		String createdZoneId = searchOperationDAO.createZone(opId, zone);
		Validate.notNull(createdZoneId);
	}
	
	private void validateZoneInput(String id, String title, String prioStr,
			Double[] points, int zoomLevel) {
		Validate.notNull(id);
		Validate.notNull(title);
		Validate.notEmpty(title);
		Validate.notNull(prioStr);
		Validate.notEmpty(prioStr);
		Validate.notNull(points); //XXX should empty zones be allowed?
		Validate.notEmpty(points);
		if (zoomLevel <= 0) {
			throw new IllegalArgumentException("Zoom level must be higher than zero.");
		}
	}
	
	public SearchOperation getSearchOp(String searchOpId) throws Exception {
		Validate.notNull(searchOpId);
		SearchOperation searchOperation = searchOperationDAO.findOperation(searchOpId);
		Validate.notNull(searchOperation);
		return searchOperation;
	}

	/**
	 * Deletes a search operation and all it's zones and groups.
	 * @param searchOpId
	 * @throws Exception
	 */
	public void deleteSearchOperation(String searchOpId) throws Exception {
		Validate.notNull(searchOpId);
		searchOperationDAO.deleteSearchOperation(searchOpId);
	}

	public void editSearchOp(SearchOperation operation, String opId, String missionId) throws Exception {
		//TODO break into two methods 
		if (opId == null && missionId != null) {
			//TODO create operation object here
			searchOperationDAO.addSearchOperation(operation, missionId);
		} else if (opId != null && missionId == null) {
			//TODO create operation object here
			searchOperationDAO.editSearchOperation(operation, opId);
		}
	}

	public SearchGroup getSearchGroup(String groupId) throws Exception {
		Validate.notNull(groupId);
		SearchGroup group = searchOperationDAO.getSearchGroup(groupId);
		Validate.notNull(group);
		return group;
	}

	/**
	 * Get a list of Searchers who have volunteered for the specified SearchOperation.
	 * @param opId the id of the SearchOperation.
	 * @return a list of SearcherInfo objects representing the searchers applied to the operation.
	 * @throws Exception
	 */
	public Map<String, String> getSearchersByOp(String opId) throws Exception {
		Validate.notNull(opId);
		Map<String, String> map = searchOperationDAO.getUsersForSearchOp(opId);
		Validate.notNull(map);
		return map;
	}

	public void addorModifySearchGroup(SearchGroup group, String groupId, String opId) throws Exception {
		if (opId == null || group == null) {
			throw new Exception("Ingen sökgrupp eller sökoperationsid specifierat");
		} else if (group.getId() == null || group.getName() == null) {
			throw new Exception("Gruppen har ett ogiltigt namn eller id");
		}
		if (groupId == null) {
			searchOperationDAO.addSearchGroup(group, opId);
		} else {
			searchOperationDAO.editSearchGroup(group, opId);
		}
	}
}
