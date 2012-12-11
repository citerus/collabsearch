package se.citerus.collabsearch.adminui.view.usermgmt;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import se.citerus.collabsearch.adminui.logic.UserService;
import se.citerus.collabsearch.adminui.view.ViewSwitchController;
import se.citerus.collabsearch.model.User;

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
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class UserListView extends CustomComponent {

	private VerticalLayout mainLayout;
	private Button homeButton;
	private Button addButton;
	private Button editButton;
	private Button deleteButton;
	private Button changePasswordButton;
	private Table table;
	private Label headerLabel;
	private Window popupWindow;
	private Button closePopupButton;
	private Button confirmPasswordChangeButton;

	@Autowired
	private UserService service;

	private BeanContainer<String, User> beans;
	private final ViewSwitchController listener;
	private PasswordField passwordField;

	public UserListView(final ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}

	public void init() {
		buildMainLayout();
		buildPopupWindow();
		listener.setMainWindowCaption("Missing People - Användare");

		homeButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToWelcomeView();
			}
		});
		addButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToNewUserView();
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
					try {
						service.removeUser(selectedUser);
						table.removeItem(selectedUser);
						listener.displayNotification("Användare borttagen",
								"Användare " + selectedUser + " borttagen");
					} catch (Exception e) {
						listener.displayError("Fel", "Användare "
								+ selectedUser + " kunde ej tas bort");
					}
				}
			}
		});
		changePasswordButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				String selectedUser = table.getValue().toString();
				if (selectedUser == null) {
					listener.displayNotification("Ingen användare markerad",
							"En användare måste vara markerad för att kunna ändra lösenord");
				} else {
					popupWindow.setCaption("Ändra lösenord för användare: " + selectedUser);
					getWindow().addWindow(popupWindow);
				}
			}
		});

		closePopupButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				(popupWindow.getParent()).removeWindow(popupWindow);
			}
		});
		confirmPasswordChangeButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					service.changePassword(
							table.getValue().toString(), 
							passwordField.getValue().toString());
					(popupWindow.getParent()).removeWindow(popupWindow);
				} catch (IOException e) {
					e.printStackTrace();
					listener.displayError("Fel", "Ett fel uppstod vid ändring av lösenordet");
				}
			}
		});

		populateTable();
	}

	private void populateTable() {
		List<User> list = null;
		try {
			list = service.getListOfUsers();
		} catch (IOException e) {
			e.printStackTrace();
		}

		beans = new BeanContainer<String, User>(User.class);
		beans.setBeanIdProperty("username");

		beans.addAll(list);
		table.setContainerDataSource(beans);
		table.setVisibleColumns(new Object[] { "username", "role" });
		table.setColumnHeaders(new String[] { "Användarnamn", "Roll" });
	}

	private void buildMainLayout() {
		mainLayout.setSizeFull();
		mainLayout.setMargin(true, false, false, true);

		Panel outerPanel = new Panel();
		outerPanel.setWidth("36%");
		outerPanel.setStyleName("user-panel");
		mainLayout.addComponent(outerPanel);
		mainLayout.setComponentAlignment(outerPanel, Alignment.TOP_CENTER);

		VerticalLayout outerLayout = new VerticalLayout();
		outerLayout.setWidth("100%");
		outerPanel.addComponent(outerLayout);

		HorizontalLayout upperLayout = new HorizontalLayout();
		upperLayout.setSpacing(true);

		Embedded embImg = new Embedded("", new ThemeResource(
				"../mytheme/dual_color_extended_trans.png"));
		embImg.setStyleName("small-logo");
		upperLayout.addComponent(embImg);
		upperLayout.setComponentAlignment(embImg, Alignment.MIDDLE_LEFT);

		headerLabel = new Label("<h1><b>Användare</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		headerLabel.setStyleName("logo-header");
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

		changePasswordButton = new Button("Ändra lösenord");
		innerButtonLayout.addComponent(changePasswordButton);

		deleteButton = new Button("Ta bort");
		innerButtonLayout.addComponent(deleteButton);

		editButton = new Button("Redigera");
		innerButtonLayout.addComponent(editButton);

		addButton = new Button("Lägg till");
		innerButtonLayout.addComponent(addButton);

		buttonLayout.addComponent(innerButtonLayout);
		buttonLayout.setComponentAlignment(innerButtonLayout,
				Alignment.MIDDLE_RIGHT);

		outerLayout.addComponent(buttonLayout);
		outerLayout.setComponentAlignment(buttonLayout, Alignment.TOP_RIGHT);
	}

	public void resetView() {
		beans.removeAllItems();

		try {
			List<User> list = service.getListOfUsers();
			if (list != null) {
				beans.addAll(list);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buildPopupWindow() {
		popupWindow = new Window("...");
		popupWindow.setModal(true);
		popupWindow.center();

		VerticalLayout layout = (VerticalLayout) popupWindow.getContent();
		layout.setMargin(true);
		layout.setSpacing(true);

		passwordField = new PasswordField("Nytt lösenord");
		popupWindow.addComponent(passwordField);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		closePopupButton = new Button("Avbryt");
		buttonLayout.addComponent(closePopupButton);
		buttonLayout.setWidth("100%");
		buttonLayout.setComponentAlignment(closePopupButton,
				Alignment.BOTTOM_RIGHT);

		confirmPasswordChangeButton = new Button("OK");
		buttonLayout.addComponent(confirmPasswordChangeButton);
		buttonLayout.setComponentAlignment(confirmPasswordChangeButton,
				Alignment.BOTTOM_RIGHT);
		
		popupWindow.addComponent(buttonLayout);
	}

}
