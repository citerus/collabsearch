package se.citerus.lookingfor.view.login;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Panel;

public class LoginView extends CustomComponent {
	private final LoginListener listener;
	
	public LoginView(LoginListener listener) {
		this.listener = listener;
	}
	
	public Panel getView() {
		Panel loginPanel = new Panel("Login");
		loginPanel.setWidth("250px");
		LoginForm login = new LoginForm();
		login.setUsernameCaption("Användarnamn:");
		login.setPasswordCaption("Lösenord:");
		login.setLoginButtonCaption("Logga in");
		login.addListener(listener);
		loginPanel.addComponent(login);
		return loginPanel;
	}
	
}
