package se.citerus.collabsearch.adminui.view.usermgmt;

import java.util.Collections;

import se.citerus.collabsearch.model.validator.PhoneNumberValidator;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class UserViewFragment extends CustomComponent {
	private VerticalLayout mainLayout;
	protected TextField nameField;
	protected PasswordField passwordField;
	protected TextField emailField;
	protected TextField teleField;
	protected ComboBox roleField;
	protected Button cancelButton;
	protected Button saveButton;
	protected Window popupWindow;
	protected Button closePopupButton;
	protected Label popupMessage;
	protected Label headerLabel;
	private Label passwordLabel;
	private Panel mainPanel;
	
	public UserViewFragment() {
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}
	
	public void init(String headerMsg) {
		buildMainLayout();
		buildPopupWindow();
	}

	private void buildMainLayout() {
		mainLayout.setSizeFull();
		mainLayout.setMargin(true);

		mainPanel = new Panel();
		mainPanel.setWidth("25%");
		
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		
		Embedded logo = new Embedded("", 
			new ThemeResource("../mytheme/dual_color_extended_trans.png"));
		logo.setStyleName("small-logo");
		headerLayout.addComponent(logo);
		headerLayout.setComponentAlignment(logo, Alignment.TOP_LEFT);
		
		headerLabel = new Label("<h1><b>Användare</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		headerLabel.setStyleName("logo-header");
		headerLayout.addComponent(headerLabel);
		headerLayout.setComponentAlignment(headerLabel, Alignment.TOP_LEFT);
		
		mainPanel.addComponent(headerLayout);
		
		mainPanel.addComponent(new Label("Namn"));
		nameField = new TextField();
		mainPanel.addComponent(nameField);
		nameField.addValidator(new StringLengthValidator(
				"Invalid username, must be between 1-99 characters", 1, 99, false));
		nameField.setRequired(true);
		nameField.setImmediate(true);
		nameField.setNullRepresentation("");
		
		passwordLabel = new Label("Lösenord");
		mainPanel.addComponent(passwordLabel);
		passwordField = new PasswordField();
		mainPanel.addComponent(passwordField);
		passwordField.addValidator(new StringLengthValidator(
				"Ogiltigt lösenord, måste vara mellan 1-99 tecken", 1, 99, false));
		passwordField.setRequired(true);
		passwordField.setImmediate(true);
		passwordField.setNullRepresentation("");
		
		mainPanel.addComponent(new Label("Epost"));
		emailField = new TextField();
		mainPanel.addComponent(emailField);
		emailField.addValidator(new EmailValidator("Ogiltig mailadress"));
		emailField.setRequired(true);
		emailField.setImmediate(true);
		emailField.setNullRepresentation("");
		
		mainPanel.addComponent(new Label("Telefon"));
		teleField = new TextField();
		mainPanel.addComponent(teleField);
		teleField.addValidator(new PhoneNumberValidator(
				"Ogiltigt telefonnummer, får bara innehålla siffror"));
		teleField.setRequired(true);
		teleField.setImmediate(true);
		teleField.setNullRepresentation("");
		
		mainPanel.addComponent(new Label("Roll"));
		roleField = new ComboBox(null, Collections.EMPTY_LIST);
		mainPanel.addComponent(roleField);
		roleField.setNullSelectionAllowed(false);
		roleField.setRequired(true);
		roleField.setImmediate(true);
		
		HorizontalLayout subLayout = new HorizontalLayout();
		subLayout.setSpacing(true);
		subLayout.setMargin(true, false, false, false);
		
		cancelButton = new Button("Avbryt");
		subLayout.addComponent(cancelButton);
		
		saveButton = new Button("Spara");
		subLayout.addComponent(saveButton);
		
		mainPanel.addComponent(subLayout);
		mainLayout.addComponent(mainPanel);
		mainLayout.setComponentAlignment(mainPanel, Alignment.TOP_CENTER);
	}

	private void buildPopupWindow() {
		popupWindow = new Window("Meddelande");
		popupWindow.setModal(true);
		popupWindow.center();
		
		VerticalLayout layout = (VerticalLayout) popupWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        
        popupMessage = new Label("...");
        popupWindow.addComponent(popupMessage);
        
        HorizontalLayout buttonLayout = new HorizontalLayout();
        closePopupButton = new Button("Tillbaka");
        buttonLayout.addComponent(closePopupButton);
        buttonLayout.setWidth("100%");
        buttonLayout.setComponentAlignment(closePopupButton, Alignment.BOTTOM_CENTER);
        
        popupWindow.addComponent(buttonLayout);
	}

	public void removePasswordField() {
		mainPanel.removeComponent(passwordLabel);
		mainPanel.removeComponent(passwordField);
	}
}
