package se.citerus.lookingfor;

public interface ViewSwitchListener {
	
	public void displayNotification(String caption, String message);
	
	public void switchToWelcomeView();
	
	public void switchToUserMgmtView();
	
	/**
	 * Logs out the user and reloads the application, switching view to the login screen.
	 */
	public void logoutAndReload();
	
	public void switchToSearchMissionView();
}
