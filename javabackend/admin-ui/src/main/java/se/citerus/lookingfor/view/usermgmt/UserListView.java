package se.citerus.lookingfor.view.usermgmt;

import java.io.IOException;
import java.util.List;

import se.citerus.lookingfor.ViewSwitchController;
import se.citerus.lookingfor.logic.User;
import se.citerus.lookingfor.logic.UserHandler;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class UserListView extends CustomComponent {
	
	private Layout mainLayout;
	private Button homeButton;
	private Button addButton;
	private Button editButton;
	private Button deleteButton;
	private Table table;
	private Label headerLabel;

	private String selectedUsername;
	
	public UserListView(final ViewSwitchController listener) {
		//buildMainLayout();
		mainLayout = buildMainLayout2();
		setCompositionRoot(mainLayout);
		listener.setMainWindowCaption("Missing People - Användare");
		
		selectedUsername = null;
		
		homeButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToWelcomeView();
			}
		});
		addButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToUserEditView(null);
			}
		});
		editButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				String selectedUser = getSelectedUser();
				if (selectedUser == null) {
					listener.displayNotification("Ingen användare markerad", "Markera en användare för redigering");
				} else {
					listener.switchToUserEditView(selectedUser);
				}
			}
		});
		deleteButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				String selectedUser = getSelectedUser();
				if (selectedUser == null) {
					listener.displayNotification("Ingen användare markerad", "Markera en användare för borttagning");
				} else {
					Boolean removeStatus = new UserHandler().removeUser(selectedUser);
					if (removeStatus) {
						table.removeItem(selectedUser);
						listener.displayNotification("Användare borttagen", "Användare " + selectedUser + " borttagen");
					} else {
						listener.displayError("Fel", "Användare " + selectedUser + " kunde ej tas bort");
					}
				}
			}
		});
		
		table.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				selectedUsername = (String) event.getProperty().getValue();
			}
		});
		
		populateTable();
	}

	private void populateTable() {
		List<User> list = null;
		UserHandler handler = null;
		try {
			handler = new UserHandler();
			list = handler.getListOfUsers();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			handler.cleanUp();
		}
		
		BeanContainer<String, User> beans = new BeanContainer<String, User>(User.class);
		beans.setBeanIdProperty("username");
		
		beans.addAll(list);
		table.setContainerDataSource(beans);
		table.setVisibleColumns(new Object[]{"username","role"});
		table.setColumnHeaders(new String[]{"Användarnamn","Roll"});
	}
	
	protected String getSelectedUser() {
		return selectedUsername;
	}
	
	private Layout buildMainLayout2() {
		VerticalLayout mainLayout2 = new VerticalLayout();
		mainLayout2.setSizeFull();
		mainLayout2.setMargin(false, false, false, true);
		
		VerticalLayout outerLayout = new VerticalLayout();
		outerLayout.setWidth("33%");
		
		HorizontalLayout upperLayout = new HorizontalLayout();
		upperLayout.setSpacing(true);
		
		homeButton = new Button("Tillbaka");
		upperLayout.addComponent(homeButton);
		upperLayout.setComponentAlignment(homeButton, Alignment.MIDDLE_LEFT);
		
		headerLabel = new Label("<h1><b>Användare</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		upperLayout.addComponent(headerLabel);
		
		outerLayout.addComponent(upperLayout);
		
		table = new Table("Användare");
		table.setWidth("100%");
		table.setSelectable(true);
		table.setImmediate(true);
		outerLayout.addComponent(table);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		
		deleteButton = new Button("Ta bort");
		buttonLayout.addComponent(deleteButton);
		
		editButton = new Button("Redigera");
		buttonLayout.addComponent(editButton);
		
		addButton = new Button("Lägg till");
		buttonLayout.addComponent(addButton);
		
		outerLayout.addComponent(buttonLayout);
		outerLayout.setComponentAlignment(buttonLayout, Alignment.TOP_RIGHT);
		
		mainLayout2.addComponent(outerLayout);
		
		return mainLayout2;
	}

}
