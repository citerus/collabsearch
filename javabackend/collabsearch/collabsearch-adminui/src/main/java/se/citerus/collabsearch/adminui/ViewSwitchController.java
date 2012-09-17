package se.citerus.collabsearch.adminui;

import com.vaadin.data.util.BeanItemContainer;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.User;

public interface ViewSwitchController {
	
	/**
	 * Displays a toast-like notification to the user.
	 * @param caption The caption to be displayed in the notification.
	 * @param message The message to be displayed in the notification.
	 */
	public void displayNotification(String caption, String message);
	
	/**
	 * Logs out the user and reloads the application, switching view to the login screen.
	 */
	public void logoutAndReload();
	
	/**
	 * Displays and error popup with the included caption and message.
	 * @param caption
	 * @param message
	 */
	public void displayError(String caption, String message);
	
	/**
	 * Sets the browser window caption to the included string.
	 */
	public void setMainWindowCaption(String caption);
	
	public void switchToLoginView();
	
	public void switchToWelcomeView();
	
	public void switchToUserListView();
	
	public void switchToSearchMissionListView();
	
	/**
	 * Switches to the user management view, loading the data of the included user.
	 * @param selectedUserName the username with which to query the database.
	 */
	public void switchToUserEditView(String selectedUserName);

	/**
	 * Switches to the search mission management view, loading the data of the included search mission.
	 * @param missionId the Search Mission name with which to query the database.
	 */
	public void switchToSearchMissionEditView(String missionId);

	public void switchToSearchOperationEditView(String opId, String missionId);
	
	/**
	 * Switches to the file mgmt view, with a reference to the mission whose files will be managed.
	 * @param missionName the name of the Search Mission whose files will be managed.
	 * @param fileName 
	 */
	public void switchToFileUploadView(String missionName);
	
	public void switchToZoneEditView(String zoneId, String opId);
	
	public void switchToGroupEditView(String groupId, String opId);
		
	public void refreshMissionTree();

}
