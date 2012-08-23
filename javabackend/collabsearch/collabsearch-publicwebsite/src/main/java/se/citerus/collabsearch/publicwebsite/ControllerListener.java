package se.citerus.collabsearch.publicwebsite;

import java.util.Date;

import se.citerus.collabsearch.model.SearchOperationDTO;
import se.citerus.collabsearch.model.SearchOperationIntro;

public interface ControllerListener {

	SearchOperationDTO fireReadMoreEvent(String header);

	void submitSearchOpApplication(String selectedOp, String name, String tele, String email);

	/**
	 * Queries for a list of short introductions to all available Search Operations.
	 * @return introductory texts for all currently stored search operations.
	 */
	SearchOperationIntro[] getAllSearchOpsIntros();

	void showErrorMessage(String errorHeader, String errorMessage);

	SearchOperationIntro[] getSearchOpsByName(String searchString);

	SearchOperationIntro[] getSearchOpsByFilter(String name, String location,
			long date);

	void showTrayNotification(String caption, String message);

}
