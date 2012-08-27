package se.citerus.collabsearch.publicwebsite.view;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchOperationIntro;
import se.citerus.collabsearch.model.validator.PhoneNumberValidator;
import se.citerus.collabsearch.publicwebsite.ControllerListener;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

@SuppressWarnings("serial")
public class OperationsListView extends CustomComponent {

	private VerticalLayout mainLayout;
	private VerticalLayout listLayout;
	private final ControllerListener listener;
	private List<Component> componentsList;
	private List<Component> expandedComponents;
	private Window applyWindow;
	private Window advSearchWindow;
	private TextField nameField;
	private TextField teleField;
	private TextField emailField;
	private Button applyButton;
	private Button cancelButton;
	private String selectedOp;
	private Button searchButton;
	private TextField searchField;
	private Button advSearchPopupButton;
	private TextField titleQueryField;
	private TextField locationQueryField;
	private DateField dateQueryField;
	private Button advSearchCommitButton;

	public OperationsListView(final ControllerListener listener) {
		this.listener = listener;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		
		componentsList = new ArrayList<Component>();
		expandedComponents = new ArrayList<Component>();
		
		applyButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if (allFieldsValid()) {
					String name = (String) nameField.getValue();
					String tele = (String) teleField.getValue();
					String email = (String) emailField.getValue();
					listener.submitSearchOpApplication(selectedOp, name, tele, email);
					resetAndCloseApplyMePopup();
				} else {
					listener.showTrayNotification("Valideringsfel", 
							"Ett eller flera av fälten innehåller felaktiga värden.");
				}
			}
		});
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				resetAndCloseApplyMePopup();
			}
		});
		
		searchButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				titleSearch();
			}
		});
		advSearchPopupButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				OperationsListView.this.getWindow().addWindow(advSearchWindow);
			}
		});
		advSearchCommitButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				advancedSearch();
				closeAdvSearchPopup();
			}
		});
		
		resetView(); //TODO debug only, remove later
	}

	public void resetView() {
		//empty list and table
		clearRowComponents();
		
		//requery db for searchops list
		SearchOperationIntro[] opsArray = listener.getAllSearchOpsIntros();
		for (int i = 0; i < opsArray.length; i++) {
			SearchOperationIntro dto = opsArray[i];
			addRowComponent(dto.getTitle(), dto.getDescr(), "Läs mer");
		}
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
		buildApplyMePopupWindow();
		
		//build Advanced Search popup window
		buildAdvSearchWindow();
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
		
		advSearchPopupButton = new Button("Avancerad sökning");
		topRightLayout.addComponent(advSearchPopupButton);
		
		topLayout.addComponent(topLeftLayout);
		topLayout.addComponent(topRightLayout);
		topLayout.setComponentAlignment(topLeftLayout, Alignment.TOP_LEFT);
		topLayout.setComponentAlignment(topRightLayout, Alignment.MIDDLE_RIGHT);
		mainLayout.addComponent(topLayout);
	}

	private void buildApplyMePopupWindow() {
		applyWindow = new Window("Anmälning");
		applyWindow.setModal(true);
		
		applyWindow.addListener(new CloseListener() {
			public void windowClose(CloseEvent e) {
				selectedOp = null;
				nameField.setValue("");
				teleField.setValue("");
				emailField.setValue("");
			}
		});
		
		VerticalLayout layout = (VerticalLayout) applyWindow.getContent();
		layout.setSizeUndefined();
		layout.setMargin(true);
		layout.setSpacing(true);
		
		nameField = new TextField("Namn");
		nameField.addValidator(new StringLengthValidator(
				"Namn måste vara mellan 1 till 99 tecken", 1, 99, false));
		nameField.setRequired(true);
		nameField.setImmediate(true);
		layout.addComponent(nameField);
		
		teleField = new TextField("Tele");
		teleField.addValidator(new PhoneNumberValidator("Telefonnummer " +
				"får ej innehålla andra tecken än siffror och mellanslag"));
		teleField.setRequired(true);
		teleField.setImmediate(true);
		layout.addComponent(teleField);
		
		emailField = new TextField("Email");
		emailField.addValidator(new EmailValidator("Ogiltig epostadress"));
		emailField.setRequired(true);
		emailField.setImmediate(true);
		layout.addComponent(emailField);
		
		HorizontalLayout bottomButtonsLayout = new HorizontalLayout();
		bottomButtonsLayout.setSpacing(true);
		
		cancelButton = new Button("Avbryt");
		bottomButtonsLayout.addComponent(cancelButton);
		
		applyButton = new Button("Anmäl");
		bottomButtonsLayout.addComponent(applyButton);
		
		layout.addComponent(bottomButtonsLayout);
		layout.setComponentAlignment(bottomButtonsLayout, Alignment.MIDDLE_CENTER);
	}
	
	private void buildAdvSearchWindow() {
		advSearchWindow = new Window("Avancerad sökning");
		advSearchWindow.setModal(true);
		
		VerticalLayout layout = (VerticalLayout) advSearchWindow.getContent();
		layout.setSizeUndefined();
		layout.setMargin(true);
		layout.setSpacing(true);
		
		titleQueryField = new TextField();
		titleQueryField.setInputPrompt("Namn på uppdrag");
		titleQueryField.setNullRepresentation("");
		layout.addComponent(titleQueryField);
		
		locationQueryField = new TextField();
		locationQueryField.setInputPrompt("Ort för uppdrag");
		locationQueryField.setNullRepresentation("");
		layout.addComponent(locationQueryField);
		
		dateQueryField = new DateField();
		dateQueryField.setWidth("100%");
		dateQueryField.setResolution(DateField.RESOLUTION_DAY);
		layout.addComponent(dateQueryField);
		
		advSearchCommitButton = new Button("Sök");
		layout.addComponent(advSearchCommitButton);
		layout.setComponentAlignment(advSearchCommitButton, Alignment.MIDDLE_CENTER);
	}

	private Component buildContractedRowComponent(String opTitle, String opDescr, 
			String buttonText, ClickListener lowerRightClickListener) {
		Panel panel = new Panel();
		panel.setWidth("100%");
		panel.setData(opTitle);
		
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth("100%");
		
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setWidth("100%");
		
		Label headerLabel = new Label("<h2><b>" + opTitle + "</b></h2>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		topLayout.addComponent(headerLabel);
		
		layout.addComponent(topLayout);
		
		Label descrLabel = new Label(opDescr);
		layout.addComponent(descrLabel);
		
		Button lowerRightButton = new Button(buttonText, lowerRightClickListener);
		layout.addComponent(lowerRightButton);
		layout.setComponentAlignment(lowerRightButton, Alignment.BOTTOM_RIGHT);
		
		panel.setContent(layout);
		return panel;
	}
	
	private Component buildExpandedRowComponent(SearchOperation dto, 
			ClickListener lowerRightClickListener, ClickListener contractClickListener) {
		Panel panel = new Panel();
		panel.setWidth("100%");
		panel.setData(dto.getTitle());
		
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth("100%");
		
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setWidth("100%");
		
		Label headerLabel = new Label("<h2><b>" + dto.getTitle() + "</b></h2>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		topLayout.addComponent(headerLabel);
		
		Button contractButton = new Button("Minimera", contractClickListener);
		topLayout.addComponent(contractButton);
		topLayout.setComponentAlignment(contractButton, Alignment.TOP_RIGHT);
				
		layout.addComponent(topLayout);
		
		Label descrLabel = new Label(dto.getDescr());
		layout.addComponent(descrLabel);
		
		Label lineBreaker = new Label("<br>");
		lineBreaker.setContentMode(Label.CONTENT_XHTML);
		layout.addComponent(lineBreaker);
		
		makeFormItem("<b>Ort:</b>", dto.getLocation(), layout);
		makeFormItem("<b>Datum:</b>", getFormattedDate(dto.getDate()), layout);
		makeFormItem("<b>Status:</b>", dto.getStatus().getName(), layout);
		
		Button lowerRightButton = new Button("Anmäl mig", lowerRightClickListener);
		layout.addComponent(lowerRightButton);
		layout.setComponentAlignment(lowerRightButton, Alignment.BOTTOM_RIGHT);
		
		panel.setContent(layout);
		return panel;
	}

	private void makeFormItem(String keyString, String valueString, VerticalLayout layout) {
		HorizontalLayout subLayout = new HorizontalLayout();
		subLayout.setSpacing(true);
		Label formFieldKeyLabel = new Label(keyString);
		formFieldKeyLabel.setContentMode(Label.CONTENT_XHTML);
		subLayout.addComponent(formFieldKeyLabel);
		Label formFieldValueLabel = new Label(valueString);
		subLayout.addComponent(formFieldValueLabel);
		layout.addComponent(subLayout);
	}

	private String getFormattedDate(Date date) {
		String dateString = DateFormat.getInstance().format(date);
		return dateString;
	}

	/**
	 * Adds a new row component to the UI and to the underlying component list.
	 * @param opTitle the title (a search operation name) of the row
	 * @param descr the body (a short description) of the row
	 * @param buttonText the text of the "Show more" button
	 */
	private void addRowComponent(final String opTitle, String descr, String buttonText) {
		Component listRowComponent = buildContractedRowComponent(
			opTitle, descr, buttonText, 
			new ReadMoreClickListener(opTitle)
		);
		componentsList.add(listRowComponent);
		listLayout.addComponent(listRowComponent);
	}

	private void expandRowComponent(final String opTitle, SearchOperation dto) {
		Component oldComponent = getCurrentRowComponent(opTitle, componentsList);
		Component newComponent = buildExpandedRowComponent(dto, 
			new ApplyMeClickListener(opTitle), new ContractClickListener(opTitle)
		);
		listLayout.replaceComponent(oldComponent, newComponent);
		componentsList.set(componentsList.indexOf(oldComponent), newComponent);
		expandedComponents.add(oldComponent);
	}

	private void contractRowComponent(String opTitle) {
		Component oldComponent = getCurrentRowComponent(opTitle, componentsList);
		Component newComponent = getExpandedComponent(opTitle, expandedComponents);
		listLayout.replaceComponent(oldComponent, newComponent);
		componentsList.set(componentsList.indexOf(oldComponent), newComponent);
	}

	/**
	 * Retrieves a row component currently shown in the UI.
	 * @param opTitle The title of the component contents
	 * @param list The list holding the component
	 * @return the currently visible component or null if none is found.
	 */
	private Component getCurrentRowComponent(final String opTitle, List<Component> list) {
		Component oldComponent = null;
		for (Component comp : list) {
			Panel panel = (Panel) comp;
			String name = (String) panel.getData();
			if (name.equals(opTitle)) {
				oldComponent = comp;
				break;
			}
		}
		return oldComponent;
	}

	private Component getExpandedComponent(String opTitle, List<Component> list) {
		Component oldComponent = null;
		for (int i = 0; i < list.size(); i++) {
			Panel panel = (Panel) list.get(i);
			String name = (String) panel.getData();
			if (name.equals(opTitle)) {
				oldComponent = list.remove(i);
				break;
			}
		}
		return oldComponent;
	}

	/**
	 * Clears (removes) all components from the UI and empties the underlying component list.
	 */
	private void clearRowComponents() {
		listLayout.removeAllComponents();
		componentsList.clear();
		expandedComponents.clear();
	}

	private void resetAndCloseApplyMePopup() {
		selectedOp = null;
		nameField.setValue("");
		teleField.setValue("");
		emailField.setValue("");
		applyWindow.getParent().removeWindow(applyWindow);
	}

	private void titleSearch() {
		clearRowComponents();
		
		String searchString = (String) searchField.getValue();
		SearchOperationIntro[] searchOpsArray = null;
		boolean searchStringOk = false;
		if (searchString != null) {
			if (!searchString.equals("")) {
				searchStringOk = true;
			}
		}
		if (searchStringOk == true) {
			searchOpsArray = listener.getSearchOpsByName(searchString);
		} else {
			searchOpsArray = listener.getAllSearchOpsIntros();
		}
		if (searchOpsArray != null) {
			for (int i = 0; i < searchOpsArray.length; i++) {
				SearchOperationIntro opIntro = searchOpsArray[i];
				addRowComponent(opIntro.getTitle(), opIntro.getDescr(), "Läs mer");
			}
		} else {
			listener.showTrayNotification("Sökning", "Inga sökuppdrag med namnet " + searchString + " funna");
		}
	}

	private void advancedSearch() {
		clearRowComponents();
		
		String name = null;
		String location = null;
		long date = 0;
		try {
			name = (String) titleQueryField.getValue();
			if (name != null && name.equals("")) {
				name = null;
			}
			location = (String) locationQueryField.getValue();
			if (location != null && location.equals("")) {
				location = null;
			}
			Date realDate = (Date) dateQueryField.getValue();
			if (realDate != null) {
				date = realDate.getTime();
			}
		} catch (Exception e) {
			listener.showErrorMessage("Sökfel", "En eller flera av söktermerna är felaktiga");
			closeAdvSearchPopup();
			return;
		}
		SearchOperationIntro[] searchOpsArray = null;
		
		if (allSearchFieldsValid(name, location, date)) {
			searchOpsArray = listener.getSearchOpsByFilter(name, location, date);
		}
		if (searchOpsArray != null) {
			for (int i = 0; i < searchOpsArray.length; i++) {
				SearchOperationIntro opIntro = searchOpsArray[i];
				addRowComponent(opIntro.getTitle(), opIntro.getDescr(), "Läs mer");
			}
		} else {
			listener.showTrayNotification("Sökning", "Inga sökuppdrag funna med de valda söktermerna");
		}
	}

	private boolean allFieldsValid() {
		if (nameField.isValid() && teleField.isValid() && emailField.isValid()) {
			return true;
		}
		return false;
	}

	private boolean allSearchFieldsValid(String name, String location, long date) {
		if (name == null && location == null && date == 0) {
			return false;
		}
		return true;
	}

	private void closeAdvSearchPopup() {
		advSearchWindow.getParent().removeWindow(advSearchWindow);
	}

	private class ApplyMeClickListener implements ClickListener {
		private final String opTitle;
		public ApplyMeClickListener(String opTitle) {
			this.opTitle = opTitle;
		}
		public void buttonClick(ClickEvent event) {
			selectedOp = opTitle;
			OperationsListView.this.getWindow().addWindow(applyWindow);
		}
	}

	private class ReadMoreClickListener implements ClickListener {
		private final String opTitle;
		public ReadMoreClickListener(String opTitle) {
			this.opTitle = opTitle;
		}
		public void buttonClick(ClickEvent event) {
			SearchOperation dto = listener.fireReadMoreEvent(opTitle);
			expandRowComponent(opTitle, dto);
		}
	}

	private class ContractClickListener implements ClickListener {
		private final String opTitle;
		public ContractClickListener(String opTitle) {
			this.opTitle = opTitle;
		}
		public void buttonClick(ClickEvent event) {
			contractRowComponent(opTitle);
		}
	}
}
