package se.citerus.collabsearch.adminui.view.usermgmt;

import java.util.List;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.UserService;
import se.citerus.collabsearch.model.User;
import se.citerus.collabsearch.model.validator.PhoneNumberValidator;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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
import com.vaadin.ui.Window.CloseEvent;

@SuppressWarnings("serial")
public class UserEditView extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private TextField nameField;
	private PasswordField passwordField;
	private TextField emailField;
	private TextField teleField;
	private ComboBox roleField;
	private Button cancelButton;
	private Button saveButton;
	private final ViewSwitchController listener;
	private Window popupWindow;
	private Button closePopupButton;
	private Label popupMessage;
	private Label headerLabel;
	private boolean existingUser;

	public UserEditView(final ViewSwitchController listener) {
		this.listener = listener;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		listener.setMainWindowCaption("Collaborative Search - Användarredigering");
		
		saveButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				saveUserData();
			}
		});
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToUserListView();
			}
		});
	}

	public void resetView(String selectedUsername) {
		if (selectedUsername != null) {
			populateForms(selectedUsername);
			popupMessage.setValue("Användare redigerad.");
			headerLabel.setValue("<h1><b>Redigera användare</b></h1>");
			existingUser = true;
		} else {
			emptyForms();
			popupMessage.setValue("Ny användare skapad.");
			headerLabel.setValue("<h1><b>Ny användare</b></h1>");
			existingUser = false;
		}
	}

	private void emptyForms() {
		nameField.setValue(null);
		passwordField.setValue(null);
		teleField.setValue(null);
		emailField.setValue(null);
	}

	private boolean fieldsValid() {
		AbstractField[] fields = {nameField, passwordField, teleField, emailField, roleField};
		for (AbstractField field : fields) {
			if (field.getValue() != null) {
				if (!field.isValid()) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	private void populateForms(String selectedUser) {
		try {
			UserService userHandler = new UserService();
			User userData = userHandler.getUserData(selectedUser);
			userHandler.cleanUp();
			
			nameField.setValue(userData.getUsername());
			passwordField.setValue(userData.getPassword());
			emailField.setValue(userData.getEmail());
			teleField.setValue(userData.getTele());
			roleField.setValue(userData.getRole());
		} catch (Exception e) {
			listener.displayError("Fel: Användare ej funnen", 
				"Användare " + selectedUser + " ej funnen.");
		}
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setMargin(true);

		Panel mainPanel = new Panel();
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
		
		mainPanel.addComponent(new Label("Lösenord"));
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
		roleField = new ComboBox(null, getRolesDataSource());
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
		
		buildPopupWindow();
	}

	private List<String> getRolesDataSource() {
		UserService handler = new UserService();
		List<String> listOfRoles = handler.getListOfRoles();
		handler.cleanUp();
		return listOfRoles;
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
        
        closePopupButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				(popupWindow.getParent()).removeWindow(popupWindow);
				listener.switchToUserListView();
			}
		});
        
		popupWindow.addListener(new Window.CloseListener() {
            public void windowClose(CloseEvent e) {
            	listener.switchToUserListView();
            }
        });
        
        popupWindow.addComponent(buttonLayout);
	}

	private void saveUserData() {
		if (fieldsValid()) {
			User user = new User(
					(String)nameField.getValue(), 
					(String)passwordField.getValue(), 
					(String)emailField.getValue(), 
					(String)teleField.getValue(), 
					(String)roleField.getValue());
			UserService userHandler = new UserService();
			try {
				if (existingUser) {
					userHandler.editUser(user);
				} else {
					userHandler.addUser(user);
				}
			} catch (Exception e) {
				listener.displayError("Fel", e.getMessage());
			} finally {
				if (userHandler != null) {
					userHandler.cleanUp();
				}
			}
			
			//display popup with button leading back to user list
			if (popupWindow.getParent() != null) {
		        listener.displayNotification("Fel", "Fönstret är redan öppnat");
		    } else {
		        getWindow().addWindow(popupWindow);
		    }
		} else {
			listener.displayNotification("Fel", "Ett eller flera fält innehåller fel");
		}
	}
}
