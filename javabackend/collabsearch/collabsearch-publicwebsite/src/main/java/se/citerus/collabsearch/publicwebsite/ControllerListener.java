package se.citerus.collabsearch.publicwebsite;

public interface ControllerListener {

	SearchOperationDTO fireReadMoreEvent(String header);

	void submitSearchOpApplication(String selectedOp, String name, String tele, String email);

	SearchOperationDTO[] getAllSearchOps();

	void showErrorMessage(String string, String string2);

	SearchOperationDTO[] getSearchOpsByName(String searchString);

}
