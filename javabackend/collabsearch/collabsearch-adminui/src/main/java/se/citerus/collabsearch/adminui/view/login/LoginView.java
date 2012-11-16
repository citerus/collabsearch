package se.citerus.collabsearch.adminui.view.login;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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
	
	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setWidth(100f, UNITS_PERCENTAGE);
		mainLayout.setHeight(100f, UNITS_PERCENTAGE);
		mainLayout.setStyleName("mainlayout-login");
		mainLayout.setSpacing(true);
		
		Panel outerPanel = new Panel();
		outerPanel.setWidth("300px");
		outerPanel.setStyleName("login-outer-panel");
		
		Label headerLabel = new Label(
				"<h1><b>Collaborative Search Admin Login</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		headerLabel.setStyleName("login-label");
		outerPanel.addComponent(headerLabel);
		
		LoginForm loginForm = new LoginForm();
		loginForm.setStyleName("login-form");
		loginForm.setUsernameCaption("Användarnamn:");
		loginForm.setPasswordCaption("Lösenord:");
		loginForm.setLoginButtonCaption("Logga in");
		loginForm.addListener(listener);
		loginForm.setSizeFull();
		outerPanel.addComponent(loginForm);
		
		mainLayout.addComponent(outerPanel);
		mainLayout.setComponentAlignment(outerPanel, Alignment.MIDDLE_CENTER);
	}
	
}
