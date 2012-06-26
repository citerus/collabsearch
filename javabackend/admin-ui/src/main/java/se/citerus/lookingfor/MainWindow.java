package se.citerus.lookingfor;

import se.citerus.lookingfor.logic.Authenticator;

import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Window;

public class MainWindow extends Window implements LoginListener, ViewSwitchListener {

	public MainWindow(String caption) {
		setCaption(caption);
		if (LookingForApp.get().getUser() == null) {
			setContent(new LoginView(this).getView());
		} else {
			showMainUI();
		}
	}

	private void showMainUI() {
		setContent(new WelcomeView(this));
	}

	public void onLogin(LoginEvent event) {
		System.out.println("Logging in as: " + event.getLoginParameter("username") + "@" + event.getLoginParameter("password"));
		if (new Authenticator().login(event.getLoginParameter("username"), 
        		event.getLoginParameter("password").toCharArray())) {
			LookingForApp.get().setUser(true); //TODO: store session id here
        	showNotification("New Login", "Username: " + event.getLoginParameter("username"));
        	showMainUI();
        } else {
        	showNotification("Error");
        }
	}

	public void returnToWelcomeView() {
		showMainUI();
	}
}
