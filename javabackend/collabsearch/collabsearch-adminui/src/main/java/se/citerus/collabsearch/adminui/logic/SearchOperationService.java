package se.citerus.collabsearch.adminui.logic;

import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.awt.geom.Point2D.Double;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;
import se.citerus.collabsearch.model.SearchZone;
import se.citerus.collabsearch.model.SearcherInfo;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.exceptions.SearchGroupNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchOperationNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchZoneNotFoundException;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;
import se.citerus.collabsearch.store.mongodb.SearchMissionDAOMongoDB;

@Service
public class SearchOperationService {

	@Autowired
	private SearchOperationDAO searchOperationDAO;
	
	private static String ORGANIZER_TELE;
	
	public SearchOperationService() {
	}

	@PostConstruct
	public void init() {
		try {
			Properties prop = new Properties();
			InputStream stream = SearchMissionDAOMongoDB.class.getResourceAsStream(
					"/sms-config.properties");
			if (stream != null) {
				prop.load(stream);
				ORGANIZER_TELE = prop.getProperty("ORGANIZER_TELE");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Status> getAllSearchOpStatuses() throws Exception {
		List<Status> statuses = searchOperationDAO.getAllSearchOpStatuses();
		notEmpty(statuses);
		return statuses;
	}

	public Status getSearchOpStatusByName(String statusName) throws Exception {
		notEmpty(statusName);
		Status status = searchOperationDAO.getSearchOpStatus(statusName);
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

	public SearchZone getZone(String zoneId) throws SearchZoneNotFoundException, Exception {
		notEmpty(zoneId);
		SearchZone zone = searchOperationDAO.getZoneById(zoneId);
		notNull(zone);
		return zone;
	}

	public String createZone(String opId, String title, String prioStr,
			Double[] points, int zoomLevel, Double center, String groupId) throws Exception {
		validateZoneInput(opId, title, prioStr, points, zoomLevel);
		
		int priority = Integer.parseInt(prioStr);
		
		SearchZone zone = new SearchZone(title, priority, points, zoomLevel, center, groupId); 
		String createdZoneId = searchOperationDAO.createZone(opId, zone);
		notEmpty(createdZoneId);
		return createdZoneId;
	}

	public void editZone(String zoneId, String title, String prioStr, 
			Double[] points, int zoomLevel, Double center, String groupId) throws Exception {
		validateZoneInput(zoneId, title, prioStr, points, zoomLevel);
		
		int priority = Integer.parseInt(prioStr);
		
		SearchZone zone = new SearchZone(title, priority, points, zoomLevel, center, groupId);
		searchOperationDAO.editZone(zoneId, zone);
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
	
	public SearchOperation getSearchOp(String searchOpId) throws SearchOperationNotFoundException, Exception {
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
	public void deleteSearchOp(String searchOpId) throws Exception {
		notEmpty(searchOpId);
		searchOperationDAO.deleteSearchOperation(searchOpId);
	}

	/**
	 * Creates (if opId is null) or edits a SearchOperation.
	 * @param operation
	 * @param opId
	 * @param missionId
	 * @return the operation id of the created op or null if op was edited.
	 * @throws Exception
	 */
	public String editSearchOp(SearchOperation operation, String opId, String missionId) throws Exception {
		//TODO break into two methods 
		if (opId == null && missionId != null) {
			String id = searchOperationDAO.createSearchOperation(operation, missionId);
			notEmpty(id);
			return id;
		} else if (opId != null && missionId == null) {
			searchOperationDAO.editSearchOperation(operation, opId);
		}
		return null;
	}

	public SearchGroup getSearchGroup(String groupId) throws SearchGroupNotFoundException, Exception {
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
	public Map<String, String> getSearchersByOp(String opId) throws Exception, SearchOperationNotFoundException {
		notEmpty(opId);
		Map<String, String> map = searchOperationDAO.getSearcherNamesByOp(opId);
		notNull(map);
		return map;
	}

	public String addOrModifySearchGroup(SearchGroup group, String groupId, String opId) throws Exception {
		notNull(group);
		if (groupId == null) {
			return searchOperationDAO.createSearchGroup(group, opId);
		} else {
			searchOperationDAO.editSearchGroup(group, groupId);
		}
		return null;
	}

	public List<SearchOperation> getAllSearchOps() throws Exception {
		List<SearchOperation> list = searchOperationDAO.getAllSearchOps();
		notNull(list);
		return list;
	}

	public List<SearchGroup> getSearchGroupsByOp(String opId) throws IOException, SearchGroupNotFoundException {
		notEmpty(opId);
		List<SearchGroup> list = searchOperationDAO.getSearchGroupsByOp(opId);
		notNull(list);
		return list;
	}

	public String getZoneParent(String zoneId) throws IOException, SearchOperationNotFoundException {
		notEmpty(zoneId);
		String opId = searchOperationDAO.getOpIdByZone(zoneId);
		notNull(opId);
		return opId;
	}

	public void setDebugMode() {
		searchOperationDAO.setDebugDB("test");
	}

	public List<SearcherInfo> getSearchersInfoByOp(String opId) throws IOException, SearchOperationNotFoundException {
		notEmpty(opId);
		List<SearcherInfo> list = searchOperationDAO.getSearchersInfoByOp(opId);
		notNull(list);
		return list;
	}

	public String getDefaultSMSBody() { //TODO replace with user-defined/external string
		String bodyMsg = "Du är kallad till skallgång.";
		return bodyMsg;
	}

	public String getOpOrganizerContactInfo() {
		return ORGANIZER_TELE;
	}

	public SearchOperationWrapper[] getAllSearchOpsInShortForm() throws IOException {
		return searchOperationDAO.getAllSearchOpsInShortForm();
	}

	public void assignSearcherToSearchOp(String opId, String name,
			String email, String tele) throws IOException, SearchOperationNotFoundException {
		notEmpty(opId);
		notEmpty(name);
		notEmpty(email);
		notEmpty(tele);
		searchOperationDAO.assignUserToSearchOp(opId, name, email, tele);
	}

	public SearchOperationWrapper[] getFilteredSearchOpsInShortForm(
			String title, String location, String startDate, String endDate) throws IOException {
		Validate.isTrue(!(title == null && location == null && startDate == null && endDate == null));
		return searchOperationDAO.getSearchOpsByFilter(title, location, startDate, endDate);
	}
}
