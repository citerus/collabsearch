package se.citerus.collabsearch.adminui.view.usermgmt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import se.citerus.collabsearch.adminui.view.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.UserService;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;

@SuppressWarnings("serial")
@Configurable(preConstruction=true)
public class NewUserView extends CustomComponent {

	private UserViewFragment fragment;
	private VerticalLayout mainLayout;
	private final ViewSwitchController listener;
	
	@Autowired
	private UserService service;

	public NewUserView(final ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}
	
	public void init() {
		fragment = new UserViewFragment();
		fragment.init("Ny användare");
		fragment.roleField.setContainerDataSource(getRolesDataSource());
		mainLayout.addComponent(fragment);
		
		listener.setMainWindowCaption("Collaborative Search - Användarhantering");
		
		fragment.saveButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				saveUserData();
			}
		});
		fragment.cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToUserListView();
			}
		});
		
		fragment.closePopupButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				(fragment.popupWindow.getParent()).removeWindow(fragment.popupWindow);
				listener.switchToUserListView();
			}
		});
        
		fragment.popupWindow.addListener(new Window.CloseListener() {
            public void windowClose(CloseEvent e) {
            	listener.switchToUserListView();
            }
        });
	}
	
	private IndexedContainer getRolesDataSource() {
		List<String> listOfRoles = service.getListOfRoles();
		IndexedContainer container = new IndexedContainer();
		for (String role : listOfRoles) {
			container.addItem(role);
		}
		return container;
	}
	
	private void saveUserData() {
		if (fieldsValid()) {
			try {
				String name = (String)fragment.nameField.getValue();
				String pass = (String)fragment.passwordField.getValue(); 
				String email = (String)fragment.emailField.getValue(); 
				String tele = (String)fragment.teleField.getValue(); 
				String role = (String)fragment.roleField.getValue();
				service.addUser(name, pass, email, tele, role);
			} catch (Exception e) {
				listener.displayError("Fel", e.getMessage());
			}
			
			//display popup with button leading back to user list
			if (fragment.popupWindow.getParent() != null) {
		        listener.displayNotification("Fel", "Fönstret är redan öppnat");
		    } else {
		        getWindow().addWindow(fragment.popupWindow);
		    }
		} else {
			listener.displayNotification("Fel", "Ett eller flera fält innehåller fel");
		}
	}
	
	private boolean fieldsValid() {
		AbstractField[] fields = {
				fragment.nameField, 
				fragment.passwordField, 
				fragment.teleField, 
				fragment.emailField, 
				fragment.roleField};
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
	
	public void resetView() {
		fragment.nameField.setValue(null);
		fragment.passwordField.setValue(null);
		fragment.teleField.setValue(null);
		fragment.emailField.setValue(null);
		
		fragment.popupMessage.setValue("Användare skapad.");
		fragment.headerLabel.setValue("<h1><b>Skapa användare</b></h1>");
	}
}
