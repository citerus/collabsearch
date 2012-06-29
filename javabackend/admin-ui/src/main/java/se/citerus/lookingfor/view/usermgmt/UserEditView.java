package se.citerus.lookingfor.view.usermgmt;

import se.citerus.lookingfor.ViewSwitchListener;
import se.citerus.lookingfor.logic.User;
import se.citerus.lookingfor.logic.UserHandler;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class UserEditView extends CustomComponent {
	
	private Layout mainLayout;
	private TextField nameField;
	private PasswordField passwordField;
	private TextField emailField;
	private TextField teleField;
	private TextField roleField;
	private Button cancelButton;
	private Button saveButton;
	private Label saveResultLabel;
	private final ViewSwitchListener listener;

	public UserEditView(final ViewSwitchListener listener, String selectedUser) {
		this.listener = listener;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		
		if (selectedUser != null) {
			populateForms(selectedUser);
		}
		
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToUserListView();
			}
		});
		saveButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				new UserHandler().editUser((String)nameField.getValue(), (String)passwordField.getValue(),
						(String)emailField.getValue(), (String)teleField.getValue(), (String)roleField.getValue());
				listener.displayNotification("Användarredigering", "Sparat!");
				//TODO popup with back-button?
			}
		});
	}

	private void populateForms(String selectedUser) {
		try {
			User userData = new UserHandler().getUserData(selectedUser);
			
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
		
		mainLayout.addComponent(new Label("Lösenord"));
		passwordField = new PasswordField();
		mainLayout.addComponent(passwordField);
		
		mainLayout.addComponent(new Label("Epost"));
		emailField = new TextField();
		mainLayout.addComponent(emailField);
		
		mainLayout.addComponent(new Label("Telefon"));
		teleField = new TextField();
		mainLayout.addComponent(teleField);
		
		mainLayout.addComponent(new Label("Roll"));
		roleField = new TextField();
		mainLayout.addComponent(roleField);
		
		Layout subLayout = new HorizontalLayout();
		
		cancelButton = new Button("Avbryt");
		subLayout.addComponent(cancelButton);
		
		saveButton = new Button("Spara");
		subLayout.addComponent(saveButton);
		
		mainLayout.addComponent(subLayout);
		
		saveResultLabel = new Label("");
		saveResultLabel.setVisible(false);
		mainLayout.addComponent(saveResultLabel);
	}
}
