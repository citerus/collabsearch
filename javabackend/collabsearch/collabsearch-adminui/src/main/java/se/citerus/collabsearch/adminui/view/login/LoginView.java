package se.citerus.collabsearch.adminui.view.login;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class LoginView extends CustomComponent {
	private final LoginListener listener;
	private VerticalLayout mainLayout;
	
	public LoginView(LoginListener listener) {
		this.listener = listener;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}
	
	public Panel getView() {
		return null;
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setWidth(100f, UNITS_PERCENTAGE);
		mainLayout.setHeight(100f, UNITS_PERCENTAGE);
		Panel loginPanel = new Panel("Collaborative Search Admin Login");
		loginPanel.setWidth("250px");
		LoginForm loginForm = new LoginForm();
		loginForm.setUsernameCaption("Användarnamn:");
		loginForm.setPasswordCaption("Lösenord:");
		loginForm.setLoginButtonCaption("Logga in");
		loginForm.addListener(listener);
		//loginForm.setWidth("100%");
		loginForm.setSizeFull();
		loginPanel.addComponent(loginForm);
		mainLayout.addComponent(loginPanel);
		mainLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
	}
	
}
