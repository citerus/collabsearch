package se.citerus.collabsearch.adminui.view.searchmission;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.adminui.view.ViewSwitchController;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.Status;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Configurable(preConstruction=true)
public class SearchMissionEditView extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private final ViewSwitchController listener;
	private Button cancelButton;
	private Button saveButton;
	private TextField titleField;
	private TextArea descrField;
	private TextField prioField;
	private ComboBox statusField;
	private BeanContainer<String, Status> statusBeanContainer;
	
	@Autowired
	private SearchMissionService service;
	
	private String missionId;
	private Label headerLabel;

	public SearchMissionEditView(final ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}

	public void init() {
		buildMainLayout();
		
		//save and store search mission (including files and ops) 
		saveButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				saveSearchMissionData();
			}
		});
		//cancel actions and return to search mission list
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				missionId = null;
				listener.switchToSearchMissionListView();
			}
		});
	}

	public void resetView(String missionId) {
		if (missionId != null) { //edit mission mode
			this.missionId = missionId;
			populateForms(missionId);
			
			listener.setMainWindowCaption("Missing People - Sökuppdrag");
			headerLabel = new Label("<h1><b>Redigera sökuppdrag</b></h1>");
		} else { //new mission mode
			titleField.setValue(null);
			descrField.setValue(descrField.getNullRepresentation());
			prioField.setValue(prioField.getNullRepresentation());
			statusField.setValue(null);
			listener.setMainWindowCaption("Missing People - Sökuppdrag");
			headerLabel = new Label("<h1><b>Nytt sökuppdrag</b></h1>");
		}
	}

	private void populateForms(String missionId) {
		SearchMission mission = null;
		try {
			mission = service.getSearchMissionById(missionId);
			if (mission == null) {
				listener.displayError("Fel", "Uppdraget " + missionId + " kunde ej hittas");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		titleField.setValue(mission.getName());
		descrField.setValue(mission.getDescription());
		prioField.setValue(mission.getPrio());
		statusField.setValue(mission.getStatus().getName());
	}

	private void buildMainLayout() {
		mainLayout.setSizeFull();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		
		Panel mainPanel = new Panel();
		mainPanel.setWidth("40%");
		mainLayout.addComponent(mainPanel);
		mainLayout.setComponentAlignment(mainPanel, Alignment.TOP_CENTER);
		
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		
		Embedded logo = new Embedded(null, 
			new ThemeResource("../mytheme/dual_color_extended_trans.png"));
		logo.setStyleName("small-logo");
		headerLayout.addComponent(logo);
		headerLayout.setComponentAlignment(logo, Alignment.TOP_LEFT);
		
		headerLabel = new Label("<h1><b>Redigera sökuppdrag</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		headerLabel.setStyleName("logo-header");
		headerLayout.addComponent(headerLabel);
		headerLayout.setComponentAlignment(headerLabel, Alignment.TOP_LEFT);
		
		mainPanel.addComponent(headerLayout);
		
		HorizontalLayout outerLayout = new HorizontalLayout();
		outerLayout.setWidth("100%");
		
		VerticalLayout leftFormLayout = new VerticalLayout();
		leftFormLayout.setWidth("100%");
		leftFormLayout.setMargin(false, true, false, false);
		leftFormLayout.setSpacing(true);
		
		Label titleLabel = new Label("Titel");
		titleField = new TextField();
		makeFormItem(leftFormLayout, titleLabel, titleField, Alignment.MIDDLE_LEFT);
		titleField.setNullRepresentation("");
		titleField.setImmediate(true);
		
		Label descrLabel = new Label("Beskrivning");
		descrField = new TextArea();
		makeFormItem(leftFormLayout, descrLabel, descrField, Alignment.TOP_LEFT);
		descrField.setNullRepresentation("");
		descrField.setImmediate(true);
		
		Label prioLabel = new Label("Prio");
		prioField = new TextField();
		makeFormItem(leftFormLayout, prioLabel, prioField, Alignment.MIDDLE_LEFT);
		prioField.setNullRepresentation("");
		prioField.setImmediate(true);
		
		Label statusLabel = new Label("Process/Status");
		statusBeanContainer = new BeanContainer<String, Status>(Status.class);
		statusBeanContainer.setBeanIdProperty("name");
		statusField = new ComboBox(null, statusBeanContainer);
		makeFormItem(leftFormLayout, statusLabel, statusField, Alignment.MIDDLE_LEFT);
		statusField.setNullSelectionAllowed(false);
		
		populateStatusComboBox();
		
		outerLayout.addComponent(leftFormLayout);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		cancelButton = new Button("Avbryt");
		cancelButton.setDescription("Förkastar ändringarna och återgår till sökuppdragslistan");
		buttonLayout.addComponent(cancelButton);
		
		saveButton = new Button("Spara");
		saveButton.setDescription("Sparar ändringarna permanent och återgår till sökuppdragslistan");
		buttonLayout.addComponent(saveButton);
		
		leftFormLayout.addComponent(buttonLayout);
		leftFormLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
				
		mainPanel.addComponent(outerLayout);
		
		setupValidators();
	}

	private void populateStatusComboBox() {
		statusBeanContainer.removeAllItems();
		List<Status> listOfStatuses = null;
		try {
			listOfStatuses = service.getListOfStatuses();
			statusBeanContainer.addAll(listOfStatuses);
		} catch (Exception e) {
			e.printStackTrace();
			listener.displayError("Fel", "Inga statusar funna!");
		}
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
	
	private boolean allFieldsValid() {
		AbstractField[] fields = {titleField, descrField, prioField, statusField};
		for (AbstractField field : fields) {
			if (!field.isValid()) {
				return false;
			}
		}
		return true;
	}

	private void setupValidators() {
		titleField.addValidator(new StringLengthValidator(
				"Titeln måste vara mellan 1-99 tecken", 1, 99, false));
		titleField.setRequired(true);
		descrField.setRequired(true);
		prioField.addValidator(new IntegerValidator("Prioriteringstalet måste vara ett heltal"));
		prioField.setRequired(true);
		statusField.setRequired(true);
	}
	
	private void saveSearchMissionData() {
		if (allFieldsValid()) {
			try {
				int prio = Integer.parseInt(prioField.getValue().toString());
				Status status = service.getStatusByName(statusField.getValue().toString());
				String title = (String) titleField.getValue();
				String description = (String) descrField.getValue();
				service.addOrModifyMission(title, description, prio, status, missionId);
				missionId = null;
				listener.switchToSearchMissionListView();
			} catch (Exception e) {
				listener.displayError("Fel", "Ett fel uppstod vid sparandet " +
						"av sökuppdraget, datat har ej sparats.");
			}
		} else {
			listener.displayError("Fel", "Ett eller flera fält är inte korrekt ifyllda");
		}
	}
}
