package se.citerus.lookingfor;

import se.citerus.lookingfor.logic.User;

public interface ViewSwitchListener {
	
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
	
	public void switchToWelcomeView();
	
	public void switchToUserListView();
	
	public void switchToSearchMissionView();
	
	/**
	 * Switches to the user management view, loading the data of the included user.
	 * @param selectedUser
	 */
	public void switchToUserMgmtView(User selectedUser);

	public void displayError(String caption, String message);
}
