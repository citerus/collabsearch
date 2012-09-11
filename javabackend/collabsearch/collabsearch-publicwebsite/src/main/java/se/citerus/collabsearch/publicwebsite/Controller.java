package se.citerus.collabsearch.publicwebsite;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;
import se.citerus.collabsearch.publicwebsite.logic.Model;
import se.citerus.collabsearch.publicwebsite.view.View;

import com.vaadin.ui.Window;

public class Controller implements ControllerListener {

	private final Model model;
	private final View view;

	public Controller(Window mainWindow) {
		model = new Model(this);
		view = new View(this, mainWindow);
	}

	public SearchOperation fireReadMoreEvent(String id) {
		return model.getSearchOpById(id);
	}

	public void submitSearchOpApplication(String selectedOp, String name, String tele,
			String email) {
		model.submitSearchOpApplication(selectedOp, name, tele, email);
	}

	public SearchOperationIntro[] getAllSearchOpsIntros() {
		return model.getAllSearchOps();
	}

	public void showErrorMessage(String errorHeader, String errorMessage) {
		view.showErrorMessage(errorHeader, errorMessage);
	}

	public SearchOperationIntro[] getSearchOpsByName(String searchString) {
		return model.getSearchOpsByName(searchString);
	}

	public SearchOperationIntro[] getSearchOpsByFilter(String name,
			String location, long startDate, long endDate) {
		return model.getSearchOpsByFilter(name, location, startDate, endDate);
	}

	public void showTrayNotification(String caption, String message) {
		view.showTrayNotification(caption, message);
	}

	@Override
	public String[] getAllTitles() {
		return model.getAllSeachOpsTitles();
	}

	@Override
	public String[] getAllLocations() {
		return model.getAllSeachOpsLocations();
	}

	public void startup() {
		view.switchToOpsListView();
	}

}
