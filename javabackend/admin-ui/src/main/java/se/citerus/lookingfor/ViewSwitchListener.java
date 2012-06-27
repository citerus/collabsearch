package se.citerus.lookingfor;

public interface ViewSwitchListener {
	
	public void displayNotification(String caption, String message);
	
	public void switchToWelcomeView();
	
	public void switchToUserMgmtView();
	
	public void switchToLoginView();
	
	public void switchToSearchMissionView();
}
