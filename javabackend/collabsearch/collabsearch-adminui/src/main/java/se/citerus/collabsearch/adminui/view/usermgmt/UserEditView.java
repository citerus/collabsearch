package se.citerus.collabsearch.adminui.view.usermgmt;

import java.util.List;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.PhoneNumberValidator;
import se.citerus.collabsearch.adminui.logic.User;
import se.citerus.collabsearch.adminui.logic.UserService;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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
		listener.setMainWindowCaption("Missing People - Användarredigering");
		
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
			headerLabel.setValue("Redigera användare");
			existingUser = true;
		} else {
			emptyForms();
			popupMessage.setValue("Ny användare skapad.");
			headerLabel.setValue("Ny användare");
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
		mainLayout.setMargin(false, false, false, true);

		headerLabel = new Label("<h1><b>Användare</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(headerLabel);
		
		mainLayout.addComponent(new Label("Namn"));
		nameField = new TextField();
		mainLayout.addComponent(nameField);
		nameField.addValidator(new StringLengthValidator(
				"Invalid username, must be between 1-99 characters", 1, 99, false));
		nameField.setRequired(true);
		nameField.setImmediate(true);
		nameField.setNullRepresentation("");
		
		mainLayout.addComponent(new Label("Lösenord"));
		passwordField = new PasswordField();
		mainLayout.addComponent(passwordField);
		passwordField.addValidator(new StringLengthValidator(
				"Ogiltigt lösenord, måste vara mellan 1-99 tecken", 1, 99, false));
		passwordField.setRequired(true);
		passwordField.setImmediate(true);
		passwordField.setNullRepresentation("");
		
		mainLayout.addComponent(new Label("Epost"));
		emailField = new TextField();
		mainLayout.addComponent(emailField);
		emailField.addValidator(new EmailValidator("Ogiltig mailadress"));
		emailField.setRequired(true);
		emailField.setImmediate(true);
		emailField.setNullRepresentation("");
		
		mainLayout.addComponent(new Label("Telefon"));
		teleField = new TextField();
		mainLayout.addComponent(teleField);
		teleField.addValidator(new PhoneNumberValidator(
				"Ogiltigt telefonnummer, får bara innehålla siffror"));
		teleField.setRequired(true);
		teleField.setImmediate(true);
		teleField.setNullRepresentation("");
		
		mainLayout.addComponent(new Label("Roll"));
		roleField = new ComboBox(null, getRolesDataSource());
		mainLayout.addComponent(roleField);
		roleField.setNullSelectionAllowed(false);
		roleField.setRequired(true);
		roleField.setImmediate(true);
		
		HorizontalLayout subLayout = new HorizontalLayout();
		subLayout.setSpacing(true);
		
		cancelButton = new Button("Avbryt");
		subLayout.addComponent(cancelButton);
		
		saveButton = new Button("Spara");
		subLayout.addComponent(saveButton);
		
		mainLayout.addComponent(subLayout);
//		mainLayout.setComponentAlignment(subLayout, Alignment.TOP_RIGHT);
		
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
