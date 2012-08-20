package se.citerus.collabsearch.publicwebsite;

import com.vaadin.ui.Window;

public class View {

	private OperationsListView opsListView;
	private ControllerListener listener;
	private final Window mainWindow;

	public View(ControllerListener listener, Window mainWindow) {
		this.listener = listener;
		this.mainWindow = mainWindow;
		
		opsListView = new OperationsListView();
		
		//set starting view
		mainWindow.setContent(opsListView);
	}

}
