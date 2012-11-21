package se.citerus.collabsearch.adminui.view.searchoperation;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import se.citerus.collabsearch.adminui.view.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.SearchOperationService;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.validator.DateValidator;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Configurable(preConstruction=true)
public class SearchOperationEditView extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private Button zoneButton;
	private Button groupButton;
	private final ViewSwitchController listener;
	private Button cancelButton;
	private Button saveButton;
	private TextField titleField;
	private TextArea descrField;
	private DateField dateField;
	private Label headerLabel;
	private TextField locationField;
	private ComboBox statusField;
	private BeanContainer<String, Status> statusBeanContainer;
	
	@Autowired
	private SearchOperationService service;

	private String missionId;
	private String opId;

	public SearchOperationEditView(final ViewSwitchController listener) {
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);

		this.listener = listener;
	}

	public void init() {
		buildMainLayout();
		
		saveButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				if (allFieldsValid()) {
					try {
						Status status = service.getSearchOpStatusByName((String)statusField.getValue());
						SearchOperation op = new SearchOperation(
								null, 
								(String) titleField.getValue(), 
								(String) descrField.getValue(), 
								(Date) dateField.getValue(), 
								(String) locationField.getValue(),
								status);
						service.editSearchOp(op, opId, missionId);
						listener.switchToSearchMissionListView();
					} catch (Exception e) {
						listener.displayError("Sparningsfel", e.getMessage());
					}
				} else {
					listener.displayError("Valideringsfel", "Ett eller flera fält är inte korrekt ifyllda");
				}
			}
		});
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
//				listener.returnToSearchMissionEditView();
				listener.switchToSearchMissionListView();
			}
		});
	}

	protected boolean allFieldsValid() {
		if (titleField.isValid() && dateField.isValid() 
				&& locationField.isValid() && statusField.isValid()) {
			return true;
		}
		return false;
	}

	public void resetView(String opId, String missionId) {
		this.opId = opId;
		this.missionId = missionId;
		
		if (opId != null) { //existing operation	
			this.missionId = null;
			
			try { //find operation
				SearchOperation searchOp = service.getSearchOp(opId);
				if (searchOp != null) {
					//load data from operation into fields
					titleField.setValue(searchOp.getTitle());
					descrField.setValue(searchOp.getDescr());
					dateField.setValue(searchOp.getDate());
					locationField.setValue(searchOp.getLocation());
					statusField.setValue(searchOp.getStatus().getName());
				}
			} catch (Exception e) {
				listener.displayError("Fel vid skapandet av fönstret", e.getMessage());
			}
			
			headerLabel.setValue("<h1><b>" + "Redigera sökoperation" + "</b></h1>");
		} else { //new operation
			this.opId = null;
			
			//empty all fields
			titleField.setValue(null);
			descrField.setValue(null);
			dateField.setValue(null);
			locationField.setValue(null);
			statusField.setValue(null);
			
			headerLabel.setValue("<h1><b>" + "Ny sökoperation" + "</b></h1>");
		}
	}

	private void buildMainLayout() {
		mainLayout.setSizeFull();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		
		Panel mainPanel = new Panel();
		mainPanel.setWidth("33%");
		
		VerticalLayout subLayout = new VerticalLayout();
		subLayout.setWidth("100%");
		subLayout.setSpacing(true);
		
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		
		Embedded logo = new Embedded(null, 
			new ThemeResource("../mytheme/dual_color_extended_trans.png"));
		logo.setStyleName("small-logo");
		headerLayout.addComponent(logo);
		
		headerLabel = new Label();
		headerLabel.setStyleName("logo-header");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		headerLayout.addComponent(headerLabel);
		
		mainPanel.addComponent(headerLayout);
		
		Label titleLabel = new Label("Titel");
		titleField = new TextField();
		makeFormItem(subLayout, titleLabel, titleField, Alignment.MIDDLE_LEFT);
		titleField.setImmediate(true);
		titleField.setNullRepresentation("");
		titleField.setRequired(true);
		titleField.setValidationVisible(true);
		titleField.addValidator(new StringLengthValidator(
				"Namnet måste vara mellan 1-99 tecken", 1, 99, false));
		
		Label descrLabel = new Label("Beskrivning");
		descrField = new TextArea();
		makeFormItem(subLayout, descrLabel, descrField, Alignment.TOP_LEFT);
		descrField.setNullRepresentation("");
		
		Label dateLabel = new Label("Datum");
		dateField = new DateField();
		makeFormItem(subLayout, dateLabel, dateField, Alignment.MIDDLE_LEFT);
		dateField.setImmediate(true);
		dateField.setLocale(new Locale("sv", "SE"));
		dateField.setResolution(InlineDateField.RESOLUTION_MIN);
		dateField.setRequired(true);
		dateField.setValidationVisible(true);
		dateField.addValidator(new DateValidator());
		
		Label locationLabel = new Label("Ort");
		locationField = new TextField();
		makeFormItem(subLayout, locationLabel, locationField, Alignment.MIDDLE_LEFT);
		locationField.setImmediate(true);
		locationField.setNullRepresentation("");
		locationField.setRequired(true);
		locationField.setValidationVisible(true);
		locationField.addValidator(new StringLengthValidator(
				"Orten måste vara mellan 1-99 tecken", 1, 99, false));
		
		Label statusLabel = new Label("Status");
		statusField = new ComboBox();
		makeFormItem(subLayout, statusLabel, statusField, Alignment.MIDDLE_LEFT);
		statusField.setRequired(true);
		statusBeanContainer = new BeanContainer<String, Status>(Status.class);
		statusBeanContainer.setBeanIdProperty("name");
		statusField.setContainerDataSource(statusBeanContainer);
		
		populateStatusField();
		
		HorizontalLayout lowerButtonLayout = new HorizontalLayout();
		lowerButtonLayout.setSpacing(true);
		
		cancelButton = new Button("Avbryt");
		lowerButtonLayout.addComponent(cancelButton);
		lowerButtonLayout.setComponentAlignment(cancelButton, Alignment.TOP_RIGHT);
		
		saveButton = new Button("Spara");
		lowerButtonLayout.addComponent(saveButton);
		lowerButtonLayout.setComponentAlignment(saveButton, Alignment.TOP_RIGHT);
		
		subLayout.addComponent(lowerButtonLayout);
		subLayout.setComponentAlignment(lowerButtonLayout, Alignment.TOP_RIGHT);
		
		mainPanel.addComponent(subLayout);
		
		mainLayout.addComponent(mainPanel);
		mainLayout.setComponentAlignment(mainPanel, Alignment.TOP_CENTER);
	}
	
	private void makeFormItem(VerticalLayout formLayout, Label label, AbstractField field, Alignment labelAlignment) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth("100%");
		label.setWidth("100%");
		layout.addComponent(label);
		layout.setExpandRatio(label, 1f);
		layout.setComponentAlignment(label, labelAlignment);
		field.setWidth("100%");
		layout.addComponent(field);
		layout.setComponentAlignment(field, Alignment.TOP_RIGHT);
		layout.setExpandRatio(field, 2f);
		formLayout.addComponent(layout);
	}
	
	private void populateStatusField() {
		statusBeanContainer.removeAllItems();
		List<Status> listOfStatuses = null;
		try {
			listOfStatuses = service.getAllSearchOpStatuses();
			statusBeanContainer.addAll(listOfStatuses);
		} catch (Exception e) {
			e.printStackTrace();
			listener.displayError("Fel vid statushämtning", "Inga statusar funna!");
		}
	}
}
