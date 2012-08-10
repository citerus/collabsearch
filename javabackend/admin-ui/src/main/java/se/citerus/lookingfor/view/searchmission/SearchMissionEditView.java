package se.citerus.lookingfor.view.searchmission;

import java.util.List;

import se.citerus.lookingfor.ViewSwitchController;
import se.citerus.lookingfor.logic.FileMetadata;
import se.citerus.lookingfor.logic.FileUploadHandler;
import se.citerus.lookingfor.logic.SearchMission;
import se.citerus.lookingfor.logic.SearchMissionHandler;
import se.citerus.lookingfor.logic.SearchOperation;
import se.citerus.lookingfor.logic.Status;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SearchMissionEditView extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private final ViewSwitchController listener;
	private Button cancelButton;
	private Button saveButton;
	private Table filesTable;
	private BeanContainer<String,FileMetadata> fileBeanContainer;
	private Upload fileUpload;
	private TextField titleField;
	private TextArea descrField;
	private TextField prioField;
	private ComboBox statusField;
	private BeanContainer<String, Status> statusBeanContainer;
	private BeanItemContainer<SearchOperation> opsBeanContainer;
	private ListSelect opsList;
	private Button deleteButton;
	private Button editButton;
	private Button addButton;
	
	private boolean existingMission;

	public SearchMissionEditView(final ViewSwitchController listener) {
		this.listener = listener;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		listener.setMainWindowCaption("Missing People - Redigera sökuppdrag");
		
		//save and store search mission (including files and ops) 
		saveButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				//dialogue here?
				SearchMissionHandler handler = null;
				try {
					handler = new SearchMissionHandler();
					SearchMission mission = null;
					handler.editMission(mission);
				} catch (Exception e) {
					e.printStackTrace();
					listener.displayError("Fel", "Ett fel uppstod vid sparandet " +
							"av sökuppdraget, datat har ej sparats.");
				} finally {
					if (handler != null) {
						handler.cleanUp();
					}
				}
				listener.switchToSearchMissionListView();
			}
		});
		//cancel actions and return to search mission list
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionListView();
			}
		});
		
		//add new search operation
		addButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				String missionTitle = (String) titleField.getValue();
				if (missionTitle != null) {
					listener.switchToSearchOperationEditView(null, missionTitle);
				} else {
					listener.displayError("Fel: Sökuppdragsnamn saknas", 
							"Sökuppdraget måste namnges innan operationer kan läggas till");
				}
			}
		});
		//edit existing search operation
		editButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				String missionTitle = (String) titleField.getValue();
				if (missionTitle != null) {
					String selectedOp = opsList.getValue().toString();
					listener.switchToSearchOperationEditView(selectedOp, missionTitle);
				} else {
					listener.displayError("Fel: Sökuppdragsnamn saknas", 
							"Sökuppdraget måste namnges innan operationer kan redigeras");
				}
			}
		});
		//delete search operation
		deleteButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				//TODO add delete searchOp logic
				Object itemId = opsList.getValue();
				opsList.removeItem(itemId);
			}
		});
	}

	public void resetView(String selectedSearchMissionName) {
		if (selectedSearchMissionName != null) { //edit mission mode
			populateForms(selectedSearchMissionName);
			existingMission = true;
		} else { //new mission mode
			titleField.setValue(null);
			descrField.setValue(descrField.getNullRepresentation());
			prioField.setValue(prioField.getNullRepresentation());
			statusField.setValue(null);
			
			fileBeanContainer.removeAllItems();
			opsBeanContainer.removeAllItems();
			
			existingMission = true;
		}
	}

	private void populateForms(String missionName) {
		SearchMission mission = null;
		SearchMissionHandler handler = null;
		try {
			handler = new SearchMissionHandler();
			mission = handler.getSearchMissionData(missionName);
			if (mission == null) {
				listener.displayError("Fel", "Uppdraget " + missionName + " kunde ej hittas");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (handler != null) {
				handler.cleanUp();
			}
		}
		
		titleField.setValue(mission.getName());
		descrField.setValue(mission.getDescription());
		prioField.setValue(mission.getPrio());
		statusField.setValue(mission.getStatus().getName());
		
		fileBeanContainer.removeAllItems();
		List<FileMetadata> fileList = mission.getFileList();
		for (FileMetadata fileMetadata : fileList) {
			fileBeanContainer.addBean(fileMetadata);
		}
		
		opsBeanContainer.removeAllItems();
		List<SearchOperation> opsList2 = mission.getOpsList();
		for (SearchOperation searchOp : opsList2) {
			opsBeanContainer.addItem(searchOp);
		}
	}

	private void setupValidators() {
		titleField.addValidator(new StringLengthValidator(
				"Titeln måste vara mellan 1-99 tecken", 1, 99, false));
		titleField.setRequired(true);
//		descrField.addValidator(new StringLengthValidator(
//				"Beskrivningen måste vara mellan 1-140 tecken", 1, 140, false));
		descrField.setRequired(true);
		prioField.addValidator(new IntegerValidator("Prioriteringstalet måste vara ett heltal"));
		prioField.setRequired(true);
		statusField.setRequired(true);
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setMargin(false, false, false, true);
		mainLayout.setSpacing(true);
		
		Label headlineLabel = new Label("<h1><b>Redigera sökuppdrag</b></h1>");
		headlineLabel.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(headlineLabel);
		
		HorizontalLayout outerLayout = new HorizontalLayout();
		outerLayout.setWidth("66%");
		outerLayout.setHeight("75%");
		
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
		
		populateStatusField();
		
		VerticalLayout filesListLayout = new VerticalLayout();
		filesListLayout.setWidth("100%");
		
		fileBeanContainer = new BeanContainer<String, FileMetadata>(FileMetadata.class);
		fileBeanContainer.setBeanIdProperty("filename");
		
		filesTable = new Table("Bifogade filer");
		filesTable.setHeight("150px");
		filesTable.setWidth("100%");
		filesTable.setContainerDataSource(fileBeanContainer);
		filesTable.setVisibleColumns(new Object[]{"filename"});
		filesTable.setColumnHeaders(new String[]{"Filnamn"});
		filesTable.addGeneratedColumn("", new ColumnGenerator() {
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				Button deleteButton = new Button("Ta bort");
				deleteButton.addListener(new ClickListener() {
					public void buttonClick(ClickEvent event) {
						removeFile(source.getContainerDataSource(), itemId);
					}
				});
				return deleteButton;
			}
		});
		filesTable.setColumnExpandRatio("filename", 2f);
		filesTable.setColumnReorderingAllowed(false);
		filesTable.setColumnCollapsingAllowed(false);
		filesTable.setSelectable(true);
		filesListLayout.addComponent(filesTable);
		
		final FileUploadHandler fileUploadHandler = new FileUploadHandler();
		fileUploadHandler.setTableBeanRef(fileBeanContainer);
		fileUploadHandler.setViewRef(listener);
		fileUpload = new Upload("", fileUploadHandler);
		fileUpload.addListener(new StartedListener() {
			public void uploadStarted(StartedEvent event) {
				startFileUpload(fileUploadHandler);
			}
		});
		fileUpload.addListener((Upload.SucceededListener) fileUploadHandler);
		fileUpload.addListener((Upload.FailedListener) fileUploadHandler);
		filesListLayout.addComponent(fileUpload);
		filesListLayout.setComponentAlignment(fileUpload, Alignment.TOP_RIGHT);
		filesListLayout.setExpandRatio(fileUpload, 1f);
		leftFormLayout.addComponent(filesListLayout);
		
		outerLayout.addComponent(leftFormLayout);
		
		VerticalLayout rightFormLayout = new VerticalLayout();
		rightFormLayout.setWidth("75%");
		rightFormLayout.setHeight("100%");
		
		opsBeanContainer = new BeanItemContainer<SearchOperation>(SearchOperation.class);
		opsList = new ListSelect("Operationer", opsBeanContainer);
		opsList.setWidth("100%");
		opsList.setNullSelectionAllowed(false);
		rightFormLayout.addComponent(opsList);
		
		HorizontalLayout opsButtons = new HorizontalLayout();
		opsButtons.setSpacing(true);
		
		deleteButton = new Button("Ta bort");
		opsButtons.addComponent(deleteButton);
		
		editButton = new Button("Redigera");
		opsButtons.addComponent(editButton);
		
		addButton = new Button("Lägg till");
		opsButtons.addComponent(addButton);
		
		rightFormLayout.addComponent(opsButtons);
		rightFormLayout.setComponentAlignment(opsButtons, Alignment.TOP_CENTER);
		outerLayout.addComponent(rightFormLayout);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		cancelButton = new Button("Avbryt");
		buttonLayout.addComponent(cancelButton);
		
		saveButton = new Button("Spara");
		buttonLayout.addComponent(saveButton);
		
		rightFormLayout.addComponent(buttonLayout);
		rightFormLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
		rightFormLayout.setSpacing(true);
		
		mainLayout.addComponent(outerLayout);
		
		setupValidators();
	}
	
	private void populateStatusField() {
		statusBeanContainer.removeAllItems();
		SearchMissionHandler handler = new SearchMissionHandler();
		List<Status> listOfStatuses = null;
		try {
			listOfStatuses = handler.getListOfStatuses();
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
	
	private void removeFile(Container container, Object itemId) {
		String missionName = (String) titleField.getValue();
		System.out.println("Delete clicked for item: " + itemId);
		SearchMissionHandler handler = null;
		try {
			handler = new SearchMissionHandler();
			handler.deleteFile(itemId.toString(), missionName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (handler != null) {
				handler.cleanUp();
			}
		}
		container.removeItem(itemId);
	}
	
	private void startFileUpload(FileUploadHandler handler) {
		String missionTitle = (String) titleField.getValue();
		if (missionTitle != null) {
			if ("".equals(missionTitle) || missionTitle.length() == 0) {
				fileUpload.interruptUpload();
				listener.displayError("Filuppladdning", 
						"Sökuppdraget måste namnges innan filuppladdningar kan genomföras.");
			} else {
				handler.setParentMissionName(missionTitle);
			}
		}
	}

}
