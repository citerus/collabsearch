package se.citerus.collabsearch.publicwebsite;

import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class View {

	private OperationsListView opsListView;
	private ControllerListener listener;
	private final Window mainWindow;

	public View(ControllerListener listener, Window mainWindow) {
		this.listener = listener;
		this.mainWindow = mainWindow;
		
		opsListView = new OperationsListView(listener);
		
		//set starting view
		mainWindow.setContent(opsListView);
	}
	
	public void showErrorMessage(String header, String message) {
		mainWindow.showNotification(header, message, Notification.TYPE_ERROR_MESSAGE);
	}

}
