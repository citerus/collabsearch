package se.citerus.collabsearch.publicwebsite;

import java.util.Date;

import se.citerus.collabsearch.model.SearchOperationDTO;

public class Model {
	private ControllerListener listener;

	public Model(ControllerListener listener) {
		this.listener = listener;
	}

	public SearchOperationDTO getSearchOpByName(String header) {
		return new SearchOperationDTO(
				header,
				"Lorem ipsum dolor sit amet, consectetur adipisicing elit, "
						+ "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
						+ "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris "
						+ "nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in "
						+ "reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. "
						+ "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui "
						+ "officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit " 
						+ "amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt "
						+ "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud "
						+ "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. "
						+ "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum "
						+ "dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non "
						+ "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
				new Date(System.currentTimeMillis()), 
				"Plats XYZ");
	}

	public void submitSearchOpApplication(String selectedOp, String name,
			String tele, String email) {
		int responseCode = 200; //XXX DEBUGVALUE
		System.out.println("Received application for searchop " + selectedOp
				+ " from " + name + " with tele " + tele + " and email " + email);
		if (responseCode != 200) {
			listener.showErrorMessage("Fel","Ett fel uppstod vid kommunikationen med servern, försök igen");
		}
	}

	public SearchOperationDTO[] getAllSearchOps() {
		SearchOperationDTO[] array = new SearchOperationDTO[6];
		for (int i = 0; i < array.length; i++) {
			array[i] = new SearchOperationDTO(
					"Sökoperation " + (i+1),
					"kort beskrivning", 
					new Date(System.currentTimeMillis()),
					"Plats XYZ");
		}
		return array;
	}

	public SearchOperationDTO[] getSearchOpsByName(String searchString) {
		SearchOperationDTO[] array = null;
		return array;
	}

}
