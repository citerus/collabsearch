package se.citerus.lookingfor;

import se.citerus.lookingfor.logic.Authenticator;
import se.citerus.lookingfor.view.login.LoginView;
import se.citerus.lookingfor.view.usermgmt.UserEditView;
import se.citerus.lookingfor.view.usermgmt.UserListView;
import se.citerus.lookingfor.view.welcome.WelcomeView;

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
			LookingForApp.get().setUser(true); //TODO: store session id here (move to logic?)
        	showNotification("New Login", "Username: " + event.getLoginParameter("username"));
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

	public void switchToUserMgmtView() {
		setContent(new UserListView(this));
	}

	public void logoutAndReload() {
		//setContent(new LoginView(this).getView());
		LookingForApp lookingForApp = LookingForApp.get();
		//lookingForApp.setUser(null);
		lookingForApp.close();
	}

	public void switchToSearchMissionView() {
		//setContent(new SearchMissionView(this));
	}

}
