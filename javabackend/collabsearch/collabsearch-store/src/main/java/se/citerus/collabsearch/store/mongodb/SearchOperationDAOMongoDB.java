package se.citerus.collabsearch.store.mongodb;

import java.io.IOException;
import java.util.List;

import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;

public class SearchOperationDAOMongoDB implements SearchOperationDAO {

	@Override
	public List<Status> getAllSearchOpStatuses() throws IOException {
		return null;
	}

	@Override
	public Status getSearchOpStatusByName(String statusName) throws IOException {
		return null;
	}

	@Override
	public void disconnect() {
	}

	@Override
	public String endOperation(String opName, String missionName) {
		return null;
	}

	@Override
	public String getZoneIdByName(String zoneName, String opName) {
		return null;
	}

	@Override
	public void deleteZone(String zoneId) {
	}

	@Override
	public String getGroupIdByName(String groupName, String opName) {
		return null;
	}

	@Override
	public void deleteGroup(String groupId) {
	}

}
