package se.citerus.collabsearch.adminui.view.usermgmt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import se.citerus.collabsearch.adminui.view.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.UserService;
import se.citerus.collabsearch.model.User;
import se.citerus.collabsearch.model.exceptions.UserNotFoundException;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

@SuppressWarnings("serial")
@Configurable(preConstruction=true)
public class EditUserView extends CustomComponent {
	private final ViewSwitchController listener;
	private VerticalLayout mainLayout;
	private UserViewFragment fragment;
	
	@Autowired
	private UserService service;
	private String userId;

	public EditUserView(final ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}
	
	public void init() {
		mainLayout.setSizeFull();
		mainLayout.setMargin(false, true, false, true);
		
		fragment = new UserViewFragment();
		fragment.init("Redigera användare");
		fragment.roleField.setContainerDataSource(getRolesDataSource());
		mainLayout.addComponent(fragment);
		
		fragment.removePasswordField();
//		fragment.nameField.setReadOnly(true); //TODO keep or remove?
		
		listener.setMainWindowCaption("Missing People - Användarhantering");
		
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

	public void resetView(String selectedUsername) {
		if (selectedUsername != null) {
			populateForms(selectedUsername);
			fragment.popupMessage.setValue("Användare redigerad.");
			fragment.headerLabel.setValue("<h1><b>Redigera användare</b></h1>");
		}
	}

	private void populateForms(String selectedUser) {
		try {
			User userData = service.findUserByName(selectedUser);
			userId = userData.getId();
			if (userId == null) {
				throw new NullPointerException("Användaren " + selectedUser + " har inget id");
			}
			fragment.nameField.setValue(userData.getUsername());
			fragment.emailField.setValue(userData.getEmail());
			fragment.teleField.setValue(userData.getTele());
			fragment.roleField.setValue(userData.getRole());
		} catch (NullPointerException e) {
			e.printStackTrace();
			listener.displayError("Användarhanteringsfel", e.getMessage());
		} catch (UserNotFoundException e) {
			e.printStackTrace();
			listener.displayError("Fel: Användare ej funnen", 
					"Användare " + selectedUser + " ej funnen.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private IndexedContainer getRolesDataSource() {
		List<String> listOfRoles = service.getListOfRoles();
		IndexedContainer container = new IndexedContainer();
		for (String role : listOfRoles) {
			container.addItem(role);
		}
		return container;
	}
	
	private boolean fieldsValid() {
		AbstractField[] fields = {
				fragment.nameField, 
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

	private void saveUserData() {
		if (fieldsValid()) {
			try {
				String name = (String)fragment.nameField.getValue();
				String email = (String)fragment.emailField.getValue(); 
				String tele = (String)fragment.teleField.getValue(); 
				String role = (String)fragment.roleField.getValue();
				service.editUser(userId, name, email, tele, role);
			} catch (UserNotFoundException e) {
				listener.displayError("Fel", "Användaren ej funnen");
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
}
