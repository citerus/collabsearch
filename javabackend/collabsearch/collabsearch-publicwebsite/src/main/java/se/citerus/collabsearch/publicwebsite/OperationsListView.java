package se.citerus.collabsearch.publicwebsite;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class OperationsListView extends CustomComponent {

	private VerticalLayout mainLayout;
	private VerticalLayout listLayout;
	private final ControllerListener listener;
	private List<Component> componentsList;
	private Window subWindow;
	private TextField nameField;
	private TextField teleField;
	private TextField emailField;
	private Button applyButton;
	private Button cancelButton;
	private String selectedOp;
	private Button searchButton;
	private TextField searchField;
	private Button advSearchButton;

	public OperationsListView(final ControllerListener listener) {
		this.listener = listener;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		
		componentsList = new ArrayList<Component>();
		
		applyButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if (allFieldsValid()) {
					String name = (String) nameField.getValue();
					String tele = (String) teleField.getValue();
					String email = (String) emailField.getValue();
					listener.submitSearchOpApplication(selectedOp, name, tele, email);
					resetAndClosePopup();
				}
			}
		});
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				resetAndClosePopup();
			}
		});
		
		searchButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				String searchString = (String) searchField.getValue();
				listener.getSearchOpsByName(searchString);
			}
		});
		advSearchButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				
			}
		});
		
		resetView(); //TODO debug only, remove later
	}

	protected boolean allFieldsValid() {
		if (nameField.isValid() && teleField.isValid() && emailField.isValid()) {
			return true;
		}
		return false;
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setMargin(false, false, false, true);
		
		buildTopLayout();
		
		final Panel listPanel = new Panel();
		listPanel.setHeight("600px");
		listPanel.setWidth("50%");
		listLayout = new VerticalLayout();
		listLayout.setMargin(false, true, false, true);
		listLayout.setSpacing(true);
		listPanel.setContent(listLayout);
		mainLayout.addComponent(listPanel);
		
		//build "ApplyMe" popup window
		buildPopupWindow();
	}

	private void buildTopLayout() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setWidth("50%");
		
		HorizontalLayout topLeftLayout = new HorizontalLayout();
		
		Label headerLabel = new Label("<h1><b>" + "Sökoperationer" + "</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		topLeftLayout.addComponent(headerLabel);
		
		HorizontalLayout topRightLayout = new HorizontalLayout();
		topRightLayout.setSpacing(true);
		
		searchField = new TextField();
		searchField.setInputPrompt("Sök efter sökuppdrag");
		topRightLayout.addComponent(searchField);
		topRightLayout.setComponentAlignment(searchField, Alignment.MIDDLE_RIGHT);
		
		searchButton = new Button("Sök");
		topRightLayout.addComponent(searchButton);
		
		advSearchButton = new Button("Avancerad sökning");
		topRightLayout.addComponent(advSearchButton);
		
		topLayout.addComponent(topLeftLayout);
		topLayout.addComponent(topRightLayout);
		topLayout.setComponentAlignment(topLeftLayout, Alignment.TOP_LEFT);
		topLayout.setComponentAlignment(topRightLayout, Alignment.MIDDLE_RIGHT);
		mainLayout.addComponent(topLayout);
	}

	private void buildPopupWindow() {
		subWindow = new Window("Anmälning");
		subWindow.setModal(true);
		
		VerticalLayout layout = (VerticalLayout) subWindow.getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		
		nameField = new TextField("Namn");
		nameField.addValidator(new StringLengthValidator(
				"Namn måste vara mellan 1 till 99 tecken", 1, 99, false));
		nameField.setRequired(true);
		nameField.setImmediate(true);
		
		teleField = new TextField("Tele");
		//TODO add dependency to collabsearch-model
//		teleField.addValidator(new PhoneNumberValidator("Telefonnummer " +
//				"får ej innehålla andra tecken än siffror och mellanslag"));
		teleField.setRequired(true);
		teleField.setImmediate(true);
		
		emailField = new TextField("Email");
		emailField.addValidator(new EmailValidator("Ogiltig epostadress"));
		emailField.setRequired(true);
		emailField.setImmediate(true);
		
		applyButton = new Button("Anmäl");
		cancelButton = new Button("Avbryt");
		
		layout.addComponent(nameField);
		layout.addComponent(teleField);
		layout.addComponent(emailField);
		layout.addComponent(applyButton);
		layout.addComponent(cancelButton);
	}

	public void resetView() {
		//empty list and table
		clearRowComponents();
		
		//requery db for searchops list
		SearchOperationDTO[] opsArray = listener.getAllSearchOps(); //TODO fetch only titles/short descrs
		for (int i = 0; i < opsArray.length; i++) {
			SearchOperationDTO dto = opsArray[i];
			addRowComponent(dto.getTitle(), dto.getDescr(), "Läs mer");
		}
	}

	private Component buildListRowComponent(String header, String descr, 
			String buttonText, ClickListener clickListener) {
		Panel panel = new Panel();
		panel.setWidth("100%");
		panel.setData(header);
		
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth("100%");
		
		Label headerLabel = new Label("<h2><b>" + header + "</b></h2>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		layout.addComponent(headerLabel);
		
		Label descrLabel = new Label(descr);
		layout.addComponent(descrLabel);
		
		Button readMoreButton = new Button(buttonText, clickListener);
		layout.addComponent(readMoreButton);
		layout.setComponentAlignment(readMoreButton, Alignment.BOTTOM_RIGHT);
		
		panel.setContent(layout);
		return panel;
	}

	/**
	 * Adds a new row component to the UI and to the underlying component list.
	 * @param header the title (a search operation name) of the row
	 * @param descr the body (a short description) of the row
	 * @param buttonText the text of the "Show more" button
	 */
	private void addRowComponent(final String header, String descr, String buttonText) {
		Component listRowComponent = buildListRowComponent(
			header, descr, buttonText, 
			new ClickListener() {
				public void buttonClick(ClickEvent event) {
					SearchOperationDTO dto = listener.fireReadMoreEvent(header);
					expandRowComponent(header, dto);
				}
			}
		);
		componentsList.add(listRowComponent);
		listLayout.addComponent(listRowComponent);
	}

	private void expandRowComponent(final String header, SearchOperationDTO dto) {
		Component oldComponent = null;
		for (Component comp : componentsList) {
			Panel panel = (Panel) comp;
			String name = (String) panel.getData();
			if (name.equals(header)) {
				oldComponent = comp;
				break;
			}
		}
		Component newComponent = buildListRowComponent(
			dto.getTitle(), dto.getDescr(), "Anmäl mig", 
			new ClickListener() {
				public void buttonClick(ClickEvent event) {
					selectedOp = header;
					getWindow().addWindow(subWindow);
				}
			}
		);
		listLayout.replaceComponent(oldComponent, newComponent);
		componentsList.set(componentsList.indexOf(oldComponent), newComponent);
	}

	/**
	 * Clears (removes) all components from the UI and empties the underlying component list.
	 */
	private void clearRowComponents() {
		listLayout.removeAllComponents();
		componentsList.clear();
	}

	private void resetAndClosePopup() {
		selectedOp = null;
		nameField.setValue("");
		teleField.setValue("");
		emailField.setValue("");
		subWindow.getParent().removeWindow(subWindow);
	}

	private interface ApplyMeClickListener extends ClickListener {
		public void buttonClick(ClickEvent event);
	}

	private interface ReadMoreClickListener extends ClickListener {
		public void buttonClick(ClickEvent event);
	}
}
