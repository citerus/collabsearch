package se.citerus.collabsearch.publicwebsite.logic;

import java.util.Date;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.publicwebsite.ControllerListener;

public class Model {
	private ControllerListener listener;
	private RestClient restClient;

	public Model(ControllerListener listener) {
		this.listener = listener;
	}

	public SearchOperation getSearchOpByName(String searchOpName){
		return new SearchOperation(
				null, 
				searchOpName,
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
				"Plats XYZ", new Status(0, "Sökande pågår", "beskrivning här..."));
	}

	public void submitSearchOpApplication(String selectedOp, String name,
			String tele, String email){
		System.out.println("Received application for searchop " + selectedOp
				+ " from " + name + " with tele " + tele + " and email " + email);
	}

	public SearchOperationIntro[] getAllSearchOps(){
		SearchOperationIntro[] array = new SearchOperationIntro[6];
		for (int i = 0; i < array.length; i++) {
			array[i] = new SearchOperationIntro(
				"Sökoperation " + (i+1), "kort beskrivning");
		}
		return array;
	}

	public SearchOperationIntro[] getSearchOpsByName(String searchString){
		SearchOperationIntro[] array = new SearchOperationIntro[1];
		array[0] = new SearchOperationIntro("Sökoperation X", "kort beskrivning av Sökoperation X här...");
		return array;
	}

	public SearchOperationIntro[] getSearchOpsByFilter(String name,
			String location, long date){
		SearchOperationIntro[] array = new SearchOperationIntro[1];
		array[0] = new SearchOperationIntro("Sökoperation Y", "kort beskrivning av Sökoperation Y här...");
		return array;
	}
}
