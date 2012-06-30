package se.citerus.lookingfor.view.usermgmt;

import se.citerus.lookingfor.ViewSwitchListener;
import se.citerus.lookingfor.logic.User;
import se.citerus.lookingfor.logic.UserHandler;

import com.vaadin.ui.Alignment;
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
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class UserEditView extends CustomComponent {
	
	private Layout mainLayout;
	private TextField nameField;
	private PasswordField passwordField;
	private TextField emailField;
	private TextField teleField;
	private TextField roleField;
	private Button cancelButton;
	private Button saveButton;
	private final ViewSwitchListener listener;
	private Window popupWindow;
	private Button closePopupButton;

	public UserEditView(final ViewSwitchListener listener, String selectedUser) {
		this.listener = listener;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		listener.setMainWindowCaption("Missing People - Användarredigering");
		
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

				//display popup with button leading back to user list
				if (popupWindow.getParent() != null) {
                    getWindow().showNotification("Window is already open");
                } else {
                    getWindow().addWindow(popupWindow);
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
		
		HorizontalLayout subLayout = new HorizontalLayout();
		subLayout.setSpacing(true);
		
		cancelButton = new Button("Avbryt");
		subLayout.addComponent(cancelButton);
		
		saveButton = new Button("Spara");
		subLayout.addComponent(saveButton);
		
		mainLayout.addComponent(subLayout);
		
		buildPopupWindow();
	}

	private void buildPopupWindow() {
		popupWindow = new Window("Meddelande");
		popupWindow.setModal(true);
		popupWindow.center();
		
		VerticalLayout layout = (VerticalLayout) popupWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        
        Label message = new Label("Ny användare skapad.");
        popupWindow.addComponent(message);
        
        HorizontalLayout buttonLayout = new HorizontalLayout();
        closePopupButton = new Button("Tillbaka");
        buttonLayout.addComponent(closePopupButton);
        buttonLayout.setWidth("100%");
        buttonLayout.setComponentAlignment(closePopupButton, Alignment.BOTTOM_CENTER);
        
        popupWindow.addComponent(buttonLayout);
	}
}
