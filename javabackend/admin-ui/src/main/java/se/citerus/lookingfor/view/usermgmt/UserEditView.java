package se.citerus.lookingfor.view.usermgmt;

import se.citerus.lookingfor.ViewSwitchListener;
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
	
	public UserEditView(final ViewSwitchListener listener) {
		Layout layout = new VerticalLayout();
		layout.addComponent(new Label("Användare"));
		
		layout.addComponent(new Label("Namn"));
		final TextField nameField = new TextField();
		layout.addComponent(nameField);
		
		layout.addComponent(new Label("Lösenord"));
		final PasswordField passwordField = new PasswordField();
		layout.addComponent(passwordField);
		
		layout.addComponent(new Label("Epost"));
		final TextField emailField = new TextField();
		layout.addComponent(emailField);
		
		layout.addComponent(new Label("Telefon"));
		final TextField teleField = new TextField();
		layout.addComponent(teleField);
		
		layout.addComponent(new Label("Roll"));
		final TextField roleField = new TextField();
		layout.addComponent(roleField);
		
		Layout subLayout = new HorizontalLayout();
		Button cancelButton = new Button("Avbryt");
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.displayNotification("Användarredigering", "Avbruten");
			}
		});
		subLayout.addComponent(cancelButton);
		Button saveButton = new Button("Spara");
		saveButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				new UserHandler().editUser((String)nameField.getValue(), (String)passwordField.getValue(),
						(String)emailField.getValue(), (String)teleField.getValue(), (String)roleField.getValue());
				listener.displayNotification("Användarredigering", "Sparat!");
			}
		});
		subLayout.addComponent(saveButton);
		layout.addComponent(subLayout);
		
		Label saveResultLabel = new Label("");
		saveResultLabel.setVisible(false);
		layout.addComponent(saveResultLabel);
		
		setCompositionRoot(layout);
	}
}
