package se.citerus.lookingfor.view.searchmission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import se.citerus.lookingfor.ViewSwitchController;
import se.citerus.lookingfor.logic.FileWrapper;
import se.citerus.lookingfor.logic.SearchMission;
import se.citerus.lookingfor.logic.SearchOperation;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SearchMissionEditView extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private final ViewSwitchController listener;
	private Button cancelButton;
	private Button saveButton;
	private Table filesTable;
	private BeanContainer<String,FileWrapper> beans;
	private Upload fileUpload;
	private TextField titleField;
	private TextArea descrField;
	private TextField prioField;
	private ComboBox statusField;
	private BeanContainer<String, String> prioBeanContainer;
	private BeanContainer<String, String> statusBeanContainer;
	private Receiver uploadReceiver;
	private BeanContainer<String, SearchOperation> opsBeanContainer;

	public SearchMissionEditView(final ViewSwitchController listener, String selectedSearchMissionName) {
		this.listener = listener;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		
		populateTable();
	}
	
	public void resetView() {
		
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		
		Button backButton = new Button("Tillbaka");
		backButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionListView();
			}
		});		
		mainLayout.addComponent(backButton);
		
		HorizontalLayout outerLayout = new HorizontalLayout();
		outerLayout.setWidth("66%");
		
		VerticalLayout formLayout = new VerticalLayout();
		formLayout.setWidth("50%");
		
		Label titleLabel = new Label("Titel");
		titleField = new TextField();
		makeFormItem(formLayout, titleLabel, titleField);
		
		Label descrLabel = new Label("Beskrivning");
		descrField = new TextArea();
		makeFormItem(formLayout, descrLabel, descrField);
		
		Label prioLabel = new Label("Prio");
		prioField = new TextField();
		makeFormItem(formLayout, prioLabel, prioField);
		
		Label statusLabel = new Label("Status");
		statusBeanContainer = new BeanContainer<String, String>(String.class);
		statusField = new ComboBox(null, statusBeanContainer);
		makeFormItem(formLayout, statusLabel, statusField);
		
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
		
		//spara, avbryt
		HorizontalLayout buttonLayout = new HorizontalLayout();
//		buttonLayout.setWidth("100%");
		buttonLayout.setSpacing(true);
		cancelButton = new Button("Avbryt");
		buttonLayout.addComponent(cancelButton);
		saveButton = new Button("Spara");
		buttonLayout.addComponent(saveButton);
		
		formLayout.addComponent(buttonLayout);
		formLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		formLayout.setSpacing(true);
		
		outerLayout.addComponent(formLayout);
		
		VerticalLayout opsLayout = new VerticalLayout();
		
		opsBeanContainer = new BeanContainer<String, SearchOperation>(SearchOperation.class);
		ListSelect opsList = new ListSelect("Operationer", opsBeanContainer);
		opsLayout.addComponent(opsList);
		
		HorizontalLayout opsButtons = new HorizontalLayout(); //TODO lägg till i ui't
		Button deleteButton = new Button("Ta bort");
		Button editButton = new Button("Redigera");
		Button addButton = new Button("Lägg till");

		mainLayout.addComponent(outerLayout);
		
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
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

	private void makeFormItem(VerticalLayout formLayout, Label label, AbstractField field) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth("100%");
		label.setWidth("100%");
		layout.addComponent(label);
		layout.setExpandRatio(label, 1f);
		layout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
		field.setWidth("100%");
		layout.addComponent(field);
		layout.setComponentAlignment(field, Alignment.TOP_RIGHT);
		layout.setExpandRatio(field, 2f);
		formLayout.addComponent(layout);
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

	private void populateTable() {
		beans = new BeanContainer<String, FileWrapper>(FileWrapper.class);
		
		beans.setBeanIdProperty("fileName");
		beans.addBean(new FileWrapper("fil1.pdf","",""));
		beans.addBean(new FileWrapper("fil2.png","",""));
		
		filesTable.setHeight("150px");
		filesTable.setWidth("100%");
		filesTable.setContainerDataSource(beans);
		filesTable.setVisibleColumns(new Object[]{"fileName"});
		filesTable.setColumnHeaders(new String[]{"Filnamn"});
		filesTable.addGeneratedColumn("", new ColumnGenerator() {
			public Object generateCell(final Table source, final Object itemId, Object columnId) {
				Button deleteButton = new Button("Ta bort");
				deleteButton.addListener(new ClickListener() {
					public void buttonClick(ClickEvent event) {
						System.out.println("Delete clicked for item: " + itemId);
						//source.getContainerDataSource().removeItem(itemId);
					}
				});
				return deleteButton;
			}
		});
		filesTable.setColumnExpandRatio("fileName", 2f);
	}
}
