package se.citerus.collabsearch.publicwebsite;

import se.citerus.collabsearch.model.SearchOperationDTO;

public interface ControllerListener {

	SearchOperationDTO fireReadMoreEvent(String header);

	void submitSearchOpApplication(String selectedOp, String name, String tele, String email);

	/**
	 * 
	 * @return all currently stored search operations.
	 */
	SearchOperationDTO[] getAllSearchOpsIntros();

	void showErrorMessage(String string, String string2);

	SearchOperationDTO[] getSearchOpsByName(String searchString);

}
