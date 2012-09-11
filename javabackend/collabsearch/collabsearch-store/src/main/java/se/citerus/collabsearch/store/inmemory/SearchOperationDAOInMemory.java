package se.citerus.collabsearch.store.inmemory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;
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
		//not applicable to the in-memory impl 
	}

	@Override
	public String endOperation(String opName, String missionName) {
		return "Sökning avslutad";
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

	@Override
	public SearchOperationIntro[] getAllSearchOps() throws IOException {
		Random r = new Random();
		SearchOperationIntro[] array = new SearchOperationIntro[3];
		array[0] = new SearchOperationIntro("" + r.nextLong(), "Sökoperation 1", "text...");
		array[1] = new SearchOperationIntro("" + r.nextLong(), "Sökoperation 2", "text...");
		array[2] = new SearchOperationIntro("" + r.nextLong(), "Sökoperation 3", "text...");
		return array;
	}

	@Override
	public SearchOperation getSearchOpById(String name) throws IOException {
		return null;
	}

	@Override
	public void assignUserToSearchOp(String opName, String name, String email,
			String tele) throws IOException {
	}

	@Override
	public String[] getAllOpLocations() {
		return null;
	}

	@Override
	public String[] getAllOpTitles() {
		return null;
	}

	@Override
	public SearchOperationIntro[] getSearchOpsByFilter(String title,
			String location, String startDate, String endDate)
			throws IOException {
		return null;
	}
	
}
