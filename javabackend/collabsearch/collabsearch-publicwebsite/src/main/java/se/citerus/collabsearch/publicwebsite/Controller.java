package se.citerus.collabsearch.publicwebsite;

import com.vaadin.ui.Window;

public class Controller implements ControllerListener {

	private final Model model;
	private final View view;

	public Controller(Window mainWindow) {
		model = new Model(this);
		view = new View(this, mainWindow);
	}

	public SearchOperationDTO fireReadMoreEvent(String header) {
		return model.getSearchOpByName(header);
	}

	public void submitSearchOpApplication(String selectedOp, String name, String tele,
			String email) {
		model.submitSearchOpApplication(selectedOp, name, tele, email);
	}

	public SearchOperationDTO[] getAllSearchOps() {
		return model.getAllSearchOps();
	}

	public void showErrorMessage(String errorHeader, String errorMessage) {
		view.showErrorMessage(errorHeader, errorMessage);
	}

	public SearchOperationDTO[] getSearchOpsByName(String searchString) {
		return model.getSearchOpsByName(searchString);
	}

}
