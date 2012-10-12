package se.citerus.collabsearch.adminui.logic;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
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

	public SearchZone getZone(String zoneId) throws Exception {
		Validate.notNull(zoneId);
		return searchOperationDAO.getZoneById(zoneId);
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
}
