package se.citerus;

import se.citerus.logic.Authenticator;

import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Window;

public class MainWindow extends Window implements LoginListener {

	public MainWindow(String caption) {
		setCaption(caption);
		if (LookingForApp.get().getUser() == null) {
			setContent(new LoginView(this).getView());
		} else {
			showMainUI();
		}
	}

	private void showMainUI() {
		setContent(new WelcomeView());
	}

	public void onLogin(LoginEvent event) {
		System.out.println("Logging in as: " + event.getLoginParameter("username") + "@" + event.getLoginParameter("password"));
		if (new Authenticator().login(event.getLoginParameter("username"), 
        		event.getLoginParameter("password").toCharArray())) {
        	showNotification("New Login", "Username: " + event.getLoginParameter("username"));
        	showMainUI();
        } else {
        	showNotification("Error");
        }
	}
}
