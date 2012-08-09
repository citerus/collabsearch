package se.citerus.lookingfor;

import com.vaadin.data.util.BeanItemContainer;

import se.citerus.lookingfor.logic.SearchOperation;
import se.citerus.lookingfor.logic.User;

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
	 * @param selectedSearchMissionName the Search Mission name with which to query the database.
	 */
	public void switchToSearchMissionEditView(String selectedSearchMissionName);

	public void switchToSearchOperationEditView(String selectedSearchOperationName, String missionTitle);

	public void returnToSearchMissionEditView();

}
