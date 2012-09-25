package se.citerus.collabsearch.publicwebsite;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationWrapper;

public interface ControllerListener {
	
	void showErrorMessage(String errorHeader, String errorMessage);
	
	void showTrayNotification(String caption, String message);

	SearchOperation fireReadMoreEvent(String header);

	void submitSearchOpApplication(String selectedOp, String name, String tele, String email);

	/**
	 * Queries for a list of short introductions to all available Search Operations.
	 * @return introductory texts for all currently stored search operations.
	 */
	SearchOperationWrapper[] getAllSearchOpsIntros();

	SearchOperationWrapper[] getSearchOpsByName(String searchString);

	SearchOperationWrapper[] getSearchOpsByFilter(String name, String location,
			long date, long endDate);

	String[] getAllTitles();

	String[] getAllLocations();

}
