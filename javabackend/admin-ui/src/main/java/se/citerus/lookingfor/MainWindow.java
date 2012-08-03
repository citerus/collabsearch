package se.citerus.lookingfor;

import se.citerus.lookingfor.logic.Authenticator;
import se.citerus.lookingfor.logic.User;
import se.citerus.lookingfor.view.login.LoginView;
import se.citerus.lookingfor.view.login.WelcomeView;
import se.citerus.lookingfor.view.searchmission.SearchMissionEditView;
import se.citerus.lookingfor.view.searchmission.SearchMissionListView;
import se.citerus.lookingfor.view.usermgmt.UserEditView;
import se.citerus.lookingfor.view.usermgmt.UserListView;

import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

public class MainWindow extends Window implements LoginListener, ViewSwitchListener {

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
			if (new Authenticator().login(event.getLoginParameter("username"), 
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

	public void switchToWelcomeView() {
		setContent(new WelcomeView(this));
	}

	public void switchToUserListView() {
		setContent(new UserListView(this));
	}
	
	public void switchToUserEditView(String selectedUsername) {
		setContent(new UserEditView(this, selectedUsername));
	}

	public void switchToSearchMissionListView() {
		setContent(new SearchMissionListView(this));
	}

	public void switchToSearchMissionEditView(String selectedSearchMissionName) {
		setContent(new SearchMissionEditView(this, selectedSearchMissionName));
	}
	
	public void switchToLoginView() {
		setContent(new LoginView(this));
	}

}
