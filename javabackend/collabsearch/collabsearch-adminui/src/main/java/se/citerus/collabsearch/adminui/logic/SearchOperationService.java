package se.citerus.collabsearch.adminui.logic;

import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.awt.geom.Point2D.Double;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
		notEmpty(statuses);
		return statuses;
	}

	public Status getSearchOpStatusByName(String opName) throws Exception {
		notEmpty(opName);
		Status status = searchOperationDAO.getSearchOpStatus(opName);
		notNull(status);
		return status;
	}

	public void cleanUp() {
	}

	public String endOperation(String opId) throws Exception {
		notEmpty(opId);
		String statusName = searchOperationDAO.endOperation(opId);
		notEmpty(statusName);
		return statusName;
	}

	public void deleteZone(String zoneId) throws Exception {
		notEmpty(zoneId);
		searchOperationDAO.deleteZone(zoneId);
	}

	public void deleteGroup(String groupId) throws Exception {
		notEmpty(groupId);
		searchOperationDAO.deleteGroup(groupId);
	}

	public SearchZone getZone(String zoneId) throws Exception {
		notEmpty(zoneId);
		SearchZone zone = searchOperationDAO.getZoneById(zoneId);
		notNull(zone);
		return zone;
	}

	public String createZone(String opId, SearchZone zone) throws Exception {
		notEmpty(opId);
		notNull(zone);
		String zoneId = searchOperationDAO.createZone(opId, zone);
		notEmpty(zoneId);
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
		notEmpty(createdZoneId);
	}
	
	private void validateZoneInput(String id, String title, String prioStr,
			Double[] points, int zoomLevel) {
		notEmpty(id);
		notEmpty(title);
		notEmpty(prioStr);
		notNull(points); //XXX should empty zones be allowed?
		notEmpty(points);
		if (zoomLevel <= 0) {
			throw new IllegalArgumentException("Zoom level must be higher than zero.");
		}
	}
	
	public SearchOperation getSearchOp(String searchOpId) throws Exception {
		notEmpty(searchOpId);
		SearchOperation searchOperation = searchOperationDAO.findOperation(searchOpId);
		notNull(searchOperation);
		return searchOperation;
	}

	/**
	 * Deletes a search operation and all it's zones and groups.
	 * @param searchOpId
	 * @throws Exception
	 */
	public void deleteSearchOperation(String searchOpId) throws Exception {
		notEmpty(searchOpId);
		searchOperationDAO.deleteSearchOperation(searchOpId);
	}

	public String editSearchOp(SearchOperation operation, String opId, String missionId) throws Exception {
		//TODO break into two methods 
		if (opId == null && missionId != null) {
			//TODO create operation object here
			String id = searchOperationDAO.createSearchOperation(operation, missionId);
			notEmpty(id);
			return id;
		} else if (opId != null && missionId == null) {
			//TODO create operation object here
			searchOperationDAO.editSearchOperation(operation, opId);
		}
		return null;
	}

	public SearchGroup getSearchGroup(String groupId) throws Exception {
		notEmpty(groupId);
		SearchGroup group = searchOperationDAO.getSearchGroup(groupId);
		notNull(group);
		return group;
	}

	/**
	 * Get a list of Searchers who have volunteered for the specified SearchOperation.
	 * @param opId the id of the SearchOperation.
	 * @return a list of SearcherInfo objects representing the searchers applied to the operation.
	 * @throws Exception
	 */
	public Map<String, String> getSearchersByOp(String opId) throws Exception {
		notEmpty(opId);
		Map<String, String> map = searchOperationDAO.getUsersForSearchOp(opId);
		notNull(map);
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

	//TODO rewrite after demo
	public Map<String, String> getVolunteersByOp(String opId) {
		Map<String, String> map = new HashMap<String, String>();
		Random r = new Random();
		for (int i = 0; i < 20; i++) {
			String name = generateName();
			map.put("" + r.nextLong(), name );
		}
		return map;
	}

	//TODO remove after demo
	private String[] lastNames = new String[] { "Johansson", "Andersson",
			"Karlsson", "Nilsson", "Eriksson", "Larsson", "Olsson",
			"Persson", "Svensson", "Gustafsson" };
	private String[] femaleFirstNames = new String[] { "Maria", "Anna",
			"Margareta", "Elisabeth", "Eva", "Birgitta", "Kristina",
			"Karin", "Elisabet", "Marie" };
	private String[] maleFirstNames = new String[] { "Erik", "Lars", "Karl",
			"Anders", "Per", "Johan", "Nils", "Lennart", "Jan", "Hans" };
	private String generateName() {
		Random r = new Random();
		if (r.nextBoolean()) {
			return maleFirstNames[r.nextInt(maleFirstNames.length)] 
					+ " " + lastNames[r.nextInt(lastNames.length)];
		} else {
			return femaleFirstNames[r.nextInt(femaleFirstNames.length)] 
					+ " " + lastNames[r.nextInt(lastNames.length)];
		}
	}
}
