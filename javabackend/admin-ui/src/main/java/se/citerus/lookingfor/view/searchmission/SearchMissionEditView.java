package se.citerus.lookingfor.view.searchmission;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import se.citerus.lookingfor.ViewSwitchController;
import se.citerus.lookingfor.logic.FileWrapper;
import se.citerus.lookingfor.logic.SearchOperation;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout.MarginInfo;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SearchMissionEditView extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private final ViewSwitchController listener;
	private Button cancelButton;
	private Button saveButton;
	private Table filesTable;
	private BeanContainer<String,FileWrapper> fileBeanContainer;
	private Upload fileUpload;
	private TextField titleField;
	private TextArea descrField;
	private TextField prioField;
	private ComboBox statusField;
	private BeanContainer<String, String> statusBeanContainer;
	private Receiver uploadReceiver;
	private BeanItemContainer<SearchOperation> opsBeanContainer;
	private ListSelect opsList;
	private Button deleteButton;
	private Button editButton;
	private Button addButton;

	public SearchMissionEditView(final ViewSwitchController listener, String selectedSearchMissionName) {
		this.listener = listener;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		listener.setMainWindowCaption("Missing People - Redigera sökuppdrag");
		
		populateMissionTable();
		populateOpsTable();
	}

	public void resetView() {
		fileBeanContainer.removeAllItems();
		//get and add files for current mission
		
		opsBeanContainer.removeAllItems();
		//get and add ops for current mission
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		
		Label headlineLabel = new Label("<h1><b>Redigera sökuppdrag</b></h1>");
		headlineLabel.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(headlineLabel);
		
		HorizontalLayout outerLayout = new HorizontalLayout();
		outerLayout.setWidth("66%");
		
		VerticalLayout formLayout = new VerticalLayout();
		formLayout.setWidth("100%");
		formLayout.setMargin(false, true, false, false);
		
		Label titleLabel = new Label("Titel");
		titleField = new TextField();
		makeFormItem(formLayout, titleLabel, titleField, Alignment.MIDDLE_LEFT);
		
		Label descrLabel = new Label("Beskrivning");
		descrField = new TextArea();
		makeFormItem(formLayout, descrLabel, descrField, Alignment.TOP_LEFT);
		
		Label prioLabel = new Label("Prio");
		prioField = new TextField();
		makeFormItem(formLayout, prioLabel, prioField, Alignment.MIDDLE_LEFT);
		
		Label statusLabel = new Label("Status");
		statusBeanContainer = new BeanContainer<String, String>(String.class);
		statusField = new ComboBox(null, statusBeanContainer);
		makeFormItem(formLayout, statusLabel, statusField, Alignment.MIDDLE_LEFT);
		
		VerticalLayout filesListLayout = new VerticalLayout();
		filesListLayout.setWidth("100%");
		filesTable = new Table("Bifogade filer");
		filesListLayout.addComponent(filesTable);
		uploadReceiver = createUploadReceiver();
		fileUpload = new Upload("", uploadReceiver);
//		fileUpload.setWidth("100%");
		filesListLayout.addComponent(fileUpload);
		filesListLayout.setComponentAlignment(fileUpload, Alignment.TOP_RIGHT);
		filesListLayout.setExpandRatio(fileUpload, 1f);
		formLayout.addComponent(filesListLayout);
		
		setupUploadListeners();
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
//		buttonLayout.setWidth("100%");
		buttonLayout.setSpacing(true);
		cancelButton = new Button("Avbryt");
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionListView();
			}
		});
		buttonLayout.addComponent(cancelButton);
		saveButton = new Button("Spara");
		buttonLayout.addComponent(saveButton);
		
		formLayout.addComponent(buttonLayout);
		formLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		formLayout.setSpacing(true);
		
		outerLayout.addComponent(formLayout);
		
		VerticalLayout opsLayout = new VerticalLayout();
		opsLayout.setWidth("75%");
		
		opsBeanContainer = new BeanItemContainer<SearchOperation>(SearchOperation.class);
		opsList = new ListSelect("Operationer", opsBeanContainer);
		opsList.setWidth("100%");
		opsList.setNullSelectionAllowed(false);
		opsLayout.addComponent(opsList);
		
		HorizontalLayout opsButtons = new HorizontalLayout();
		opsButtons.setSpacing(true);
		//opsButtons.setMargin(true, false, false, false);
		
		deleteButton = new Button("Ta bort");
		opsButtons.addComponent(deleteButton);
		
		editButton = new Button("Redigera");
		opsButtons.addComponent(editButton);
		
		addButton = new Button("Lägg till");
		opsButtons.addComponent(addButton);
		
		opsLayout.addComponent(opsButtons);
		opsLayout.setComponentAlignment(opsButtons, Alignment.TOP_CENTER);
		outerLayout.addComponent(opsLayout);
		
		mainLayout.addComponent(outerLayout);
		
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
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

	private Receiver createUploadReceiver() {
		return new Receiver() {
			public OutputStream receiveUpload(String filename, String mimeType) {
				try {
					return new FileOutputStream(filename);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				}
			}
		};
	}
	
	private void setupUploadListeners() {
		fileUpload.addListener(new StartedListener() {
			public void uploadStarted(StartedEvent event) {
			}
		});
		fileUpload.addListener(new Upload.ProgressListener() {
			public void updateProgress(long readBytes, long contentLength) {
			}
		});
		fileUpload.addListener(new Upload.SucceededListener() {
			public void uploadSucceeded(SucceededEvent event) {
			}
		});
		fileUpload.addListener(new Upload.FinishedListener() {
			public void uploadFinished(FinishedEvent event) {
			}
		});
		fileUpload.addListener(new Upload.FailedListener() {
			public void uploadFailed(FailedEvent event) {
			}
		});
	}

	private void populateMissionTable() {
		fileBeanContainer = new BeanContainer<String, FileWrapper>(FileWrapper.class);
		
		fileBeanContainer.setBeanIdProperty("fileName");
		fileBeanContainer.addBean(new FileWrapper("fil1.pdf","",""));
		fileBeanContainer.addBean(new FileWrapper("fil2.png","",""));
		
		filesTable.setHeight("150px");
		filesTable.setWidth("100%");
		filesTable.setContainerDataSource(fileBeanContainer);
		filesTable.setVisibleColumns(new Object[]{"fileName"});
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
		filesTable.setColumnExpandRatio("fileName", 2f);
	}
	
	private void removeFile(Container container, Object itemId) {
		System.out.println("Delete clicked for item: " + itemId);
		//container.removeItem(itemId);
	}
	
	private void populateOpsTable() {
		Date date = Calendar.getInstance().getTime();
		opsBeanContainer.addItem(new SearchOperation("Operation 1", "beskrivning här", date));
		opsBeanContainer.addItem(new SearchOperation("Operation 2", "beskrivning här", date));
		opsBeanContainer.addItem(new SearchOperation("Operation 3", "beskrivning här", date));
	}
}
