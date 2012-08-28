package se.citerus.collabsearch.adminui.view.searchoperation;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.adminui.logic.SearchOperationService;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Status;
import se.citerus.collabsearch.model.validator.DateValidator;

import com.vaadin.data.Container;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
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
	private String missionName;
	private Label headerLabel;
	private TextField locationField;
	private ComboBox statusField;
	private BeanContainer<String, Status> statusBeanContainer;

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
					SearchMissionService missionService = null;
					SearchOperationService opService = null;
					try {
						missionService = new SearchMissionService();
						opService = new SearchOperationService();
						//TODO replace "" with (String) locationField.getValue()
						Status status = opService.getSearchOpStatusByName((String)statusField.getValue());
						SearchOperation op = new SearchOperation(
								(String) titleField.getValue(), 
								(String) descrField.getValue(), 
								(Date) dateField.getValue(), 
								(String) locationField.getValue(),
								status);
						missionService.editSearchOp(op, missionName);
						listener.refreshOpsTable();
						listener.returnToSearchMissionEditView();
					} catch (Exception e) {
						listener.displayError("Fel", e.getMessage());
					} finally {
						if (missionService != null) {
							missionService.cleanUp();
						}
						if (opService != null) {
							opService.cleanUp();
						}
					}
				} else {
					listener.displayError("Fel", "Ett eller flera fält är inte korrekt ifyllda");
				}
			}
		});
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.returnToSearchMissionEditView();
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

	public void resetView(String opName, String missionName) {
		if (opName != null) { //existing operation
			//find operation
			SearchMissionService service = null;
			try {
				service = new SearchMissionService();
				SearchOperation searchOp = service.getSearchOp(opName, missionName);
				if (searchOp != null) {
					//load data from operation into fields
					titleField.setValue(searchOp.getTitle());
					descrField.setValue(searchOp.getDescr());
					dateField.setValue(searchOp.getDate());
				}
			} catch (Exception e) {
				listener.displayError("Fel", e.getMessage());
			} finally {
				if (service != null) {
					service.cleanUp();
				}
			}
			
			headerLabel.setValue("<h1><b>" + "Redigera sökoperation" + "</b></h1>");
		} else { //new operation
			this.missionName = missionName;
			
			//empty all fields
			titleField.setValue(null);
			descrField.setValue(null);
			dateField.setValue(null);
			
			headerLabel.setValue("<h1><b>" + "Ny sökoperation" + "</b></h1>");
		}
	}

	private void buildMainLayout() {
		mainLayout.setSizeFull();
		mainLayout.setMargin(false, false, false, true);
		mainLayout.setSpacing(true);
		
		VerticalLayout subLayout = new VerticalLayout();
		subLayout.setWidth("33%");
		subLayout.setSpacing(true);
		
		headerLabel = new Label();
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		subLayout.addComponent(headerLabel);
		
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
		
		HorizontalLayout upperButtonLayout = new HorizontalLayout();
		upperButtonLayout.setSpacing(true);
		
		zoneButton = new Button("Hantera zoner");
		zoneButton.setEnabled(false);
		upperButtonLayout.addComponent(zoneButton);
		
		groupButton = new Button("Hantera grupper");
		groupButton.setEnabled(false);
		upperButtonLayout.addComponent(groupButton);
		
		subLayout.addComponent(upperButtonLayout);
		
		HorizontalLayout lowerButtonLayout = new HorizontalLayout();
		lowerButtonLayout.setSpacing(true);
		
		saveButton = new Button("Spara");
		lowerButtonLayout.addComponent(saveButton);
		
		cancelButton = new Button("Avbryt");
		lowerButtonLayout.addComponent(cancelButton);
		
		subLayout.addComponent(lowerButtonLayout);
		
		mainLayout.addComponent(subLayout);
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
		SearchOperationService handler = new SearchOperationService();
		List<Status> listOfStatuses = null;
		try {
			listOfStatuses = handler.getAllSearchOpStatuses();
			statusBeanContainer.addAll(listOfStatuses);
		} catch (Exception e) {
			e.printStackTrace();
			listener.displayError("Fel", "Inga statusar funna!");
		}
	}
}