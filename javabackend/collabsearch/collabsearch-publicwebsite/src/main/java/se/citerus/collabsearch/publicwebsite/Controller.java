package se.citerus.collabsearch.publicwebsite;

import com.vaadin.ui.Window;

public class Controller implements ControllerListener {

	private final Model model;
	private final View view;

	public Controller(Window mainWindow) {
		model = new Model(this);
		view = new View(this, mainWindow);
	}

}
