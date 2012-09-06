package se.citerus.collabsearch.store.inmemory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.store.facades.SearchOperationDAO;

public class SearchOperationDAOInMemory implements SearchOperationDAO {

	private ArrayList<Status> statusList;

	public SearchOperationDAOInMemory() {
		if (statusList == null) {
			statusList = new ArrayList<Status>();
			statusList.add(new Status(0, "Ej påbörjad", "beskrivning här"));
			statusList.add(new Status(1, "Sökning inledd", "beskrivning här"));
			statusList.add(new Status(2, "Sökning avslutad", "beskrivning här"));
		}
	}
	
	public List<Status> getAllSearchOpStatuses() throws IOException {
		if (statusList == null) {
			throw new IOException("No statuses found");
		}
		return statusList;
	}

	public Status getSearchOpStatusByName(String opName) throws IOException {
		for (Status status : statusList) {
			if (status.getName().equals(opName)) {
				return status;
			}
		}
		throw new IOException("Status " + opName + " not found");
	}

	public void disconnect() {
		//not applicable
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
