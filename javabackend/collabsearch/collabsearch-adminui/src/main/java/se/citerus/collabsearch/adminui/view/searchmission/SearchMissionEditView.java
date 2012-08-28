package se.citerus.collabsearch.adminui.view.searchmission;

import java.io.File;
import java.util.List;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.FileUploadHandler;
import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Status;

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
	private BeanContainer<String,FileMetadata> fileBeanContainer;
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
				saveSearchMissionData(listener);
			}
		});
		//cancel actions and return to search mission list
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				clearSavedState();
				listener.switchToSearchMissionListView();
			}
		});
		
		
		
		listener.setMainWindowCaption("Collaborative Search - Redigera sökuppdrag");
	}

	public void resetView(String selectedSearchMissionName) {
		if (selectedSearchMissionName != null) { //edit mission mode
			populateForms(selectedSearchMissionName);
		} else { //new mission mode
			titleField.setValue(null);
			descrField.setValue(descrField.getNullRepresentation());
			prioField.setValue(prioField.getNullRepresentation());
			statusField.setValue(null);
			
//			fileBeanContainer.removeAllItems();
//			opsBeanContainer.removeAllItems();
			
			clearSavedState();
			setupSavedState();
		}
	}

	private void populateForms(String missionName) {
		SearchMission mission = null;
		SearchMissionService handler = null;
		try {
			handler = new SearchMissionService();
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

	private void buildMainLayout() {
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
		
		//TODO break out file list into separate view
//		buildFileListLayout(leftFormLayout);
		
		outerLayout.addComponent(leftFormLayout);
		
		VerticalLayout rightFormLayout = new VerticalLayout();
		rightFormLayout.setWidth("75%");
		rightFormLayout.setHeight("100%");

		//TODO break out ops list into separate view
//		buildOpsListLayout(outerLayout, rightFormLayout);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		cancelButton = new Button("Avbryt");
		buttonLayout.addComponent(cancelButton);
		
		saveButton = new Button("Spara");
		buttonLayout.addComponent(saveButton);
		
		rightFormLayout.addComponent(buttonLayout);
		rightFormLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
		rightFormLayout.setSpacing(true);
		
		outerLayout.addComponent(rightFormLayout);
		
		mainLayout.addComponent(outerLayout);
		
		setupValidators();
	}

	private void buildOpsListLayout(HorizontalLayout outerLayout,
			VerticalLayout rightFormLayout) {
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
		
		//add new search operation
		addButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				String missionTitle = (String) titleField.getValue();
				if (missionTitle != null) {
					commitMission(missionTitle);
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
				String selectedOp = opsList.getValue().toString();
				if (missionTitle != null && selectedOp != null) {
					commitMission(missionTitle);
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
				Object itemId = opsList.getValue();
				opsList.removeItem(itemId);
				SearchMissionService handler = new SearchMissionService();
				try {
					handler.deleteSearchOperation(itemId.toString(), 
							titleField.getValue().toString());
				} catch (Exception e) {
					listener.displayError("Fel", e.getMessage());
				} finally {
					handler.cleanUp();
				}
			}
		});
	}

	private void buildFileListLayout(VerticalLayout leftFormLayout) {
		VerticalLayout fileListLayout = new VerticalLayout();
		fileListLayout.setWidth("100%");
		
		fileBeanContainer = new BeanContainer<String, FileMetadata>(FileMetadata.class);
		fileBeanContainer.setBeanIdProperty("filename");
		
		Table filesTable = new Table("Bifogade filer");
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
		fileListLayout.addComponent(filesTable);
		
		HorizontalLayout fileButtonsLayout = new HorizontalLayout();
		
		final FileUploadHandler fileUploadHandler = new FileUploadHandler();
		fileUploadHandler.setTableBeanRef(fileBeanContainer);
		fileUploadHandler.setViewRef(listener);
		final Upload fileUpload = new Upload(null, fileUploadHandler);
		fileUpload.setButtonCaption(null);
		fileUpload.addListener((Upload.SucceededListener) fileUploadHandler);
		fileUpload.addListener((Upload.FailedListener) fileUploadHandler);
		fileButtonsLayout.addComponent(fileUpload);
		
		Button uploadButton = new Button("Ladda upp fil");
		uploadButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				startFileUpload(fileUploadHandler, fileUpload);
			}
		});
		fileButtonsLayout.addComponent(uploadButton);
		fileListLayout.addComponent(fileButtonsLayout);
		
		fileListLayout.setComponentAlignment(fileButtonsLayout, Alignment.TOP_RIGHT);
		fileListLayout.setExpandRatio(fileButtonsLayout, 1f);
		leftFormLayout.addComponent(fileListLayout);
	}
	
	private void populateStatusField() {
		statusBeanContainer.removeAllItems();
		SearchMissionService handler = new SearchMissionService();
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
		if (missionName != null) {
			SearchMissionService handler = null;
			try {
				handler = new SearchMissionService();
				String filename = itemId.toString();
				handler.deleteFile(filename, missionName);
				
				File file = new File("tmp/uploads" + itemId.toString());
				boolean fileDeletionStatus = file.delete();
				System.out.println("File " + filename + " was " + 
						(fileDeletionStatus == true ? "deleted" : "not deleted"));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (handler != null) {
					handler.cleanUp();
				}
			}
			container.removeItem(itemId);
		} else {
			listener.displayError("Filuppladdning", 
					"Sökuppdraget måste namnges innan filer kan tas bort.");
		}
	}
	
	private void startFileUpload(FileUploadHandler handler, Upload fileUpload) {
		String missionTitle = (String) titleField.getValue();
		if (missionTitle != null) {
			if ("".equals(missionTitle) || missionTitle.length() == 0) {
				listener.displayError("Filuppladdning", 
						"Sökuppdraget måste namnges innan filuppladdningar kan genomföras.");
			} else {
				handler.setParentMissionName(missionTitle);
				fileUpload.submitUpload();
			}
		}
	}
	
	private boolean fieldsValid() {
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
//		descrField.addValidator(new StringLengthValidator(
//				"Beskrivningen måste vara mellan 1-140 tecken", 1, 140, false));
		descrField.setRequired(true);
		prioField.addValidator(new IntegerValidator("Prioriteringstalet måste vara ett heltal"));
		prioField.setRequired(true);
		statusField.setRequired(true);
	}
	
	private void clearSavedState() {
		SearchMissionService handler = new SearchMissionService();
		try {
			handler.clearSavedState();
		} catch (Exception e) {
			listener.displayError("Fel", e.getMessage());
		} finally {
			handler.cleanUp();
		}
	}

	private void setupSavedState() {
		SearchMissionService handler = new SearchMissionService();
		try {
			handler.setupSavedState();
		} catch (Exception e) {
			listener.displayError("Fel", e.getMessage());
		} finally {
			handler.cleanUp();
		}
	}

	public void refreshOpsTable() {
		opsBeanContainer.removeAllItems();
		SearchMissionService service = new SearchMissionService();
		List<SearchOperation> list = service.getListOfSearchOps(titleField.getValue().toString());
		if (list != null) {
			opsBeanContainer.addAll(list);
		}
	}
	
	private void saveSearchMissionData(final ViewSwitchController listener) {
		//popup dialogue here?
		if (fieldsValid()) {
			SearchMissionService handler = null;
			try {
				handler = new SearchMissionService();
				int prio = Integer.parseInt(prioField.getValue().toString());
				Status status = handler.getStatusByName(statusField.getValue().toString());
				SearchMission mission = new SearchMission(
						(String) titleField.getValue(), 
						(String) descrField.getValue(), 
						prio, 
						status
				);
				handler.editMission(mission);
				clearSavedState();
				listener.switchToSearchMissionListView();
			} catch (Exception e) {
				listener.displayError("Fel", "Ett fel uppstod vid sparandet " +
						"av sökuppdraget, datat har ej sparats.");
			} finally {
				if (handler != null) {
					handler.cleanUp();
				}
			}
		} else {
			listener.displayError("Fel", "Ett eller flera fält är inte korrekt ifyllda");
		}
	}

	private void commitMission(String missionTitle) {
		SearchMission mission = new SearchMission();
		mission.setName(missionTitle);
		SearchMissionService service = new SearchMissionService();
		try {
			service.editMission(mission);
		} catch (Exception e) {
			listener.displayError("Fel", e.getMessage());
		} finally {
			service.cleanUp();
		}
	}
}
