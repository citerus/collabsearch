package se.citerus.collabsearch.adminui.view.usermgmt;

import java.io.IOException;
import java.util.List;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.UserService;
import se.citerus.collabsearch.model.User;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
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

	private BeanContainer<String, User> beans;
	
	public UserListView(final ViewSwitchController listener) {
		mainLayout = buildMainLayout();
		setCompositionRoot(mainLayout);
		listener.setMainWindowCaption("Collaborative Search - Användare");
		
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
				String selectedUser = table.getValue().toString();
				if (selectedUser == null) {
					listener.displayNotification("Ingen användare markerad", 
							"Markera en användare för redigering");
				} else {
					listener.switchToUserEditView(selectedUser);
				}
			}
		});
		deleteButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				String selectedUser = table.getValue().toString();
				if (selectedUser == null) {
					listener.displayNotification("Ingen användare markerad", 
							"Markera en användare för borttagning");
				} else {
					UserService service = null;
					try {
						service = new UserService();
						service.removeUser(selectedUser);
						table.removeItem(selectedUser);
						listener.displayNotification("Användare borttagen", "Användare " + selectedUser + " borttagen");
					} catch (Exception e) {
						listener.displayError("Fel", "Användare " + selectedUser + " kunde ej tas bort");
					} finally {
						if (service != null) {
							service.cleanUp();
						}
					}
				}
			}
		});
		
		populateTable();
	}

	private void populateTable() {
		List<User> list = null;
		UserService handler = null;
		try {
			handler = new UserService();
			list = handler.getListOfUsers();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			handler.cleanUp();
		}
		
		beans = new BeanContainer<String, User>(User.class);
		beans.setBeanIdProperty("username");
		
		beans.addAll(list);
		table.setContainerDataSource(beans);
		table.setVisibleColumns(new Object[]{"username","role"});
		table.setColumnHeaders(new String[]{"Användarnamn","Roll"});
	}
	
	private Layout buildMainLayout() {
		VerticalLayout mainLayout2 = new VerticalLayout();
		mainLayout2.setSizeFull();
		mainLayout2.setMargin(true, false, false, true);
		
		Panel outerPanel = new Panel();
		outerPanel.setWidth("36%");
		outerPanel.setStyleName("user-panel");
		mainLayout2.addComponent(outerPanel);
		mainLayout2.setComponentAlignment(outerPanel, Alignment.TOP_CENTER);
		
		VerticalLayout outerLayout = new VerticalLayout();
		outerLayout.setWidth("100%");
		outerPanel.addComponent(outerLayout);
		
		HorizontalLayout upperLayout = new HorizontalLayout();
		upperLayout.setSpacing(true);
		
		Embedded embImg = new Embedded("", 
			new ThemeResource("../mytheme/dual_color_extended_trans.png"));
		embImg.setStyleName("small-logo");
		upperLayout.addComponent(embImg);
		upperLayout.setComponentAlignment(embImg, Alignment.MIDDLE_LEFT);
		
		headerLabel = new Label("<h1><b>Användare</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		headerLabel.setStyleName("user-header");
		upperLayout.addComponent(headerLabel);
		upperLayout.setComponentAlignment(headerLabel, Alignment.MIDDLE_LEFT);
		
		outerLayout.addComponent(upperLayout);
		
		table = new Table();
		table.setWidth("100%");
		table.setSelectable(true);
		table.setImmediate(true);
		outerLayout.addComponent(table);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setWidth("100%");
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);

		homeButton = new Button("Tillbaka");
		buttonLayout.addComponent(homeButton);
		
		upperLayout.addComponent(buttonLayout);
		
		HorizontalLayout innerButtonLayout = new HorizontalLayout();
		innerButtonLayout.setSpacing(true);
		
		deleteButton = new Button("Ta bort");
		innerButtonLayout.addComponent(deleteButton);
		
		editButton = new Button("Redigera");
		innerButtonLayout.addComponent(editButton);
		
		addButton = new Button("Lägg till");
		innerButtonLayout.addComponent(addButton);
		
		buttonLayout.addComponent(innerButtonLayout);
		buttonLayout.setComponentAlignment(innerButtonLayout, Alignment.MIDDLE_RIGHT);
		
		outerLayout.addComponent(buttonLayout);
		outerLayout.setComponentAlignment(buttonLayout, Alignment.TOP_RIGHT);
		
		return mainLayout2;
	}

	public void resetView() {
		beans.removeAllItems();
		
		UserService handler = null;
		try {
			handler = new UserService();
			List<User> list = handler.getListOfUsers();
			if (list != null) {
				beans.addAll(list);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			handler.cleanUp();
		}
	}

}
