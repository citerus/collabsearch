package se.citerus.collabsearch.publicwebsite.view;

import se.citerus.collabsearch.publicwebsite.ControllerListener;

import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class View {
	private ControllerListener listener;
	private final Window mainWindow;
	
	private OperationsListView opsListView;

	public View(ControllerListener listener, Window mainWindow) {
		this.listener = listener;
		this.mainWindow = mainWindow;
	}
	
	public void showErrorMessage(String header, String message) {
		mainWindow.showNotification(header, message, Notification.TYPE_ERROR_MESSAGE);
	}

	public void showTrayNotification(String caption, String message) {
		mainWindow.showNotification(caption, message, Notification.TYPE_TRAY_NOTIFICATION);
	}
	
	public void switchToOpsListView() {
		if (opsListView == null) {
			opsListView = new OperationsListView(listener);
			opsListView.init();
		}
		opsListView.resetView();
		mainWindow.setContent(opsListView);
	}

}
