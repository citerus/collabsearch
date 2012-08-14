package se.citerus.lookingfor;

import se.citerus.lookingfor.logic.Authenticator;
import se.citerus.lookingfor.view.login.LoginView;
import se.citerus.lookingfor.view.login.WelcomeView;
import se.citerus.lookingfor.view.searchmission.SearchMissionEditView;
import se.citerus.lookingfor.view.searchmission.SearchMissionListView;
import se.citerus.lookingfor.view.searchoperation.SearchOperationEditView;
import se.citerus.lookingfor.view.usermgmt.UserEditView;
import se.citerus.lookingfor.view.usermgmt.UserListView;

import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class MainWindow extends Window implements LoginListener, ViewSwitchController {

	private LoginView loginView;
	private WelcomeView welcomeView;
	private UserListView userListView;
	private UserEditView userEditView;
	private SearchMissionListView searchMissionListView;
	private SearchMissionEditView searchMissionEditView;
	private SearchOperationEditView searchOperationEditView;

	public MainWindow() {
		setCaption("Missing People - Login");
		if (LookingForApp.getInstance().getUser() == null) {
			switchToLoginView();
		} else {
			switchToWelcomeView();
		}
	}

	public void onLogin(LoginEvent event) {
		try {
			Authenticator authenticator = new Authenticator();
			if (authenticator.login(event.getLoginParameter("username"), 
	        		event.getLoginParameter("password").toCharArray())) {
				LookingForApp.getInstance().setUser(event.getLoginParameter("username"));
	        	switchToWelcomeView();
	        } else {
	        	displayNotification("Login failed", "Wrong username or password");
	        }
		} catch (Exception e) {
			displayError("Exception", e.getMessage());
		}
	}

	public void displayNotification(String caption, String message) {
		showNotification(caption, message);
	}

	public void logoutAndReload() {
		LookingForApp.getInstance().setUser(null);
		getApplication().close();
	}

	public void displayError(String caption, String message) {
		showNotification(caption, message, Notification.TYPE_ERROR_MESSAGE);
	}

	public void setMainWindowCaption(String caption) {
		setCaption(caption);
	}

	public void switchToLoginView() {
		if (loginView == null) {
			loginView = new LoginView(this);
		}
		setContent(loginView);
	}

	public void switchToWelcomeView() {
		if (welcomeView == null) {
			welcomeView = new WelcomeView(this);
		}
		setContent(welcomeView);
	}

	public void switchToUserListView() {
		if (userListView == null) {
			userListView = new UserListView(this);
		}
		userListView.resetView();
		setContent(userListView);
	}
	
	public void switchToUserEditView(String selectedUsername) {
		if (userEditView == null) {
			userEditView = new UserEditView(this);
		}
		userEditView.resetView(selectedUsername);
		setContent(userEditView);
	}

	public void switchToSearchMissionListView() {
		if (searchMissionListView == null) {
			searchMissionListView = new SearchMissionListView(this);
		}
		searchMissionListView.resetView();
		setContent(searchMissionListView);
	}

	public void switchToSearchMissionEditView(String selectedSearchMissionName) {
		if (searchMissionEditView == null) {
			searchMissionEditView = new SearchMissionEditView(this);
		}
		searchMissionEditView.resetView(selectedSearchMissionName);
		setContent(searchMissionEditView);
	}

	public void switchToSearchOperationEditView(String opName, String missionName) {
		if (searchOperationEditView == null) {
			searchOperationEditView = new SearchOperationEditView(this);
		}
		searchOperationEditView.resetView(opName, missionName);
		setContent(searchOperationEditView);
	}

	public void returnToSearchMissionEditView() {
		//return to searchMissionEditView without clearing state
		setContent(searchMissionEditView);
	}

	public void refreshOpsTable() {
		searchMissionEditView.refreshOpsTable();
	}

}
