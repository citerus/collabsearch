package se.citerus.lookingfor;

import se.citerus.lookingfor.logic.Authenticator;
import se.citerus.lookingfor.logic.User;
import se.citerus.lookingfor.view.login.LoginView;
import se.citerus.lookingfor.view.searchmission.SearchMissionListView;
import se.citerus.lookingfor.view.usermgmt.UserEditView;
import se.citerus.lookingfor.view.usermgmt.UserListView;
import se.citerus.lookingfor.view.welcome.WelcomeView;

import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Window;

public class MainWindow extends Window implements LoginListener, ViewSwitchListener {

	public MainWindow(String caption) {
		setCaption(caption);
		if (LookingForApp.get().getUser() == null) {
			setContent(new LoginView(this).getView());
		} else {
			switchToWelcomeView();
		}
	}

	public void onLogin(LoginEvent event) {
		System.out.println("Logging in as: " + event.getLoginParameter("username") + "@" + event.getLoginParameter("password"));
		if (new Authenticator().login(event.getLoginParameter("username"), 
        		event.getLoginParameter("password").toCharArray())) {
			//LookingForApp.get().setUser(true); //TODO: store session id here (move to logic?)
			//getApplication().setUser(sessionKey);
        	switchToWelcomeView();
        } else {
        	showNotification("Error");
        }
	}

	public void displayNotification(String caption, String message) {
		showNotification(caption, message);
	}

	public void switchToWelcomeView() {
		setContent(new WelcomeView(this));
	}

	public void switchToUserListView() {
		setContent(new UserListView(this));
	}

	public void logoutAndReload() {
		getApplication().close();
	}

	public void switchToSearchMissionView() {
		setContent(new SearchMissionListView(this));
	}

	public void switchToUserMgmtView(String selectedUsername) {
		setContent(new UserEditView(this, selectedUsername));
	}

	public void displayError(String caption, String message) {
		showNotification(caption, message, Notification.TYPE_ERROR_MESSAGE);
	}

}
