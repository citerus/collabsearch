package se.citerus.lookingfor.view.usermgmt;

import java.util.List;

import se.citerus.lookingfor.ViewSwitchListener;
import se.citerus.lookingfor.logic.PhoneNumberValidator;
import se.citerus.lookingfor.logic.User;
import se.citerus.lookingfor.logic.UserHandler;

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
	private final ViewSwitchListener listener;
	private Window popupWindow;
	private Button closePopupButton;
	private Label popupMessage;

	public UserEditView(final ViewSwitchListener listener, String selectedUser) {
		this.listener = listener;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		listener.setMainWindowCaption("Missing People - Användarredigering");
		
		if (selectedUser != null) {
			populateForms(selectedUser);
			popupMessage.setValue("Ny användare skapad.");
		} else {
			popupMessage.setValue("Användare redigerad.");
		}
		
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToUserListView();
			}
		});
		saveButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if (areAllFieldsValid()) {
					User user = new User(
							(String)nameField.getValue(), 
							(String)passwordField.getValue(), 
							(String)emailField.getValue(), 
							(String)teleField.getValue(), 
							(String)roleField.getValue());
					UserHandler userHandler = new UserHandler();
					userHandler.editUser(user);
					userHandler.cleanUp();
					
					//display popup with button leading back to user list
					if (popupWindow.getParent() != null) {
	                    listener.displayNotification("Fel", "Fönstret är redan öppnat");
	                } else {
	                    getWindow().addWindow(popupWindow);
	                }
				} else {
					listener.displayNotification("Fel", "Vissa fält innehåller fel");
				}
			}
		});
		
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
	}
	
	private boolean areAllFieldsValid() {
		AbstractField[] fields = {nameField, passwordField, teleField, emailField, roleField};
		for (AbstractField field : fields) {
			if (!field.isValid()) {
				return false;
			}
		}
		return true;
	}

	private void populateForms(String selectedUser) {
		try {
			UserHandler userHandler = new UserHandler();
			User userData = userHandler.getUserData(selectedUser);
			userHandler.cleanUp();
			
			nameField.setValue(userData.getUsername());
			passwordField.setValue(userData.getPassword());
			emailField.setValue(userData.getEmail());
			teleField.setValue(userData.getTele());
			roleField.setValue(userData.getRole());
		} catch (Exception e) {
			listener.displayError("Fel: Användare ej funnen", "Användare " + selectedUser + " ej funnen.");
		}
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.addComponent(new Label("Användare"));
		
		mainLayout.addComponent(new Label("Namn"));
		nameField = new TextField();
		mainLayout.addComponent(nameField);
		nameField.addValidator(new StringLengthValidator(
				"Invalid username, must be between 1-99 characters", 1, 99, false));
		nameField.setRequired(true);
		nameField.setImmediate(true);
		
		mainLayout.addComponent(new Label("Lösenord"));
		passwordField = new PasswordField();
		mainLayout.addComponent(passwordField);
		passwordField.addValidator(new StringLengthValidator(
				"Invalid password, must be between 1-99 characters", 1, 99, false));
		passwordField.setRequired(true);
		passwordField.setImmediate(true);
		
		mainLayout.addComponent(new Label("Epost"));
		emailField = new TextField();
		mainLayout.addComponent(emailField);
		emailField.addValidator(new EmailValidator("Invalid email address"));
		emailField.setRequired(true);
		emailField.setImmediate(true);
		
		mainLayout.addComponent(new Label("Telefon"));
		teleField = new TextField();
		mainLayout.addComponent(teleField);
		teleField.addValidator(new PhoneNumberValidator("Invalid phone number, may only contain digits"));
		teleField.setRequired(true);
		teleField.setImmediate(true);
		
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
		UserHandler handler = new UserHandler();
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
        
        popupWindow.addComponent(buttonLayout);
	}
}
