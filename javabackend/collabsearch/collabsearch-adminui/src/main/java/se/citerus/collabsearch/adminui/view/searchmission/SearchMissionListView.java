package se.citerus.collabsearch.adminui.view.searchmission;

import java.io.FileWriter;
import java.util.List;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Status;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.sqlcontainer.ColumnProperty;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SearchMissionListView extends CustomComponent {
	
	private Button endMissionButton;
	private Button editButton;
	private Button addButton;
	private TreeTable treeTable;
	private Table table;
	private Button homeButton;
	private BeanContainer<String, SearchMission> beans;
	private final ViewSwitchController listener;
	private VerticalLayout mainLayout;
	private HierarchicalContainer container;
	
	public SearchMissionListView(final ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}
	
	public void init() {
		buildMainLayout();
		listener.setMainWindowCaption("Collaborative Search - Sökuppdrag");
		
		homeButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToWelcomeView();
			}
		});
		addButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionEditView(null);
			}
		});
		editButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				String missionTitle = (String) treeTable.getValue(); //TODO might need fix
				if (missionTitle != null) {
					listener.switchToSearchMissionEditView(missionTitle);
				} else {
					listener.displayNotification("Inget sökuppdrag markerat", 
							"Du måste markera ett sökuppdrag för redigering");
				}
			}
		});
		endMissionButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				SearchMissionService service = null;
				String itemId = (String) treeTable.getValue(); //TODO might need fix
				if (itemId != null) {
					try {
						service = new SearchMissionService();
						service.endMission(itemId);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (service != null) {
							service.cleanUp();
						}
					}
					refreshMissionTree();
				} else {
					listener.displayNotification("Inget sökuppdrag markerat", 
							"Du måste markera ett sökuppdrag för avslutning");
				}
			}
		});	
	}

	private void buildMainLayout() {
		mainLayout.setSizeFull();
		mainLayout.setMargin(false, false, false, true);
		mainLayout.setSpacing(true);
		
		VerticalLayout innerLayout = new VerticalLayout();
		innerLayout.setWidth("33%");
		
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		
		homeButton = new Button("Tillbaka");
		headerLayout.addComponent(homeButton);
		headerLayout.setComponentAlignment(homeButton, Alignment.MIDDLE_LEFT);
		
		Label headerLabel = new Label("<h1><b>Sökuppdrag</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		headerLayout.addComponent(headerLabel);
		
		innerLayout.addComponent(headerLayout);
		
//		table = new Table();
//		table.setSelectable(true);
//		table.setWidth("100%");		
//		beans = new BeanContainer<String, SearchMission>(SearchMission.class);
//		beans.setBeanIdProperty("name");
//		beans.addBean(new SearchMission("Placeholder", "Placeholder", 1, new Status(0, "Placeholder", "Placeholder")));
//		table.setContainerDataSource(beans);
//		table.setVisibleColumns(new Object[]{"name","description","status"});
//		table.setColumnHeaders(new String[]{"Namn","Beskrivning","Status"});
//		innerLayout.addComponent(table);
		
		treeTable = new TreeTable();
		treeTable.setSelectable(true);
		treeTable.setWidth("100%");
		treeTable.addContainerProperty("name", String.class, "");
		treeTable.addContainerProperty("descr", String.class, "");
		treeTable.addContainerProperty("prio", Integer.class, "");
		treeTable.addContainerProperty("status", String.class, "");
		treeTable.setVisibleColumns(new Object[]{"name", "descr", "prio", "status"});
		treeTable.setColumnHeaders(new String[]{"Namn","beskrivning","Prio","Status"});
		innerLayout.addComponent(treeTable);
		
		populateTreeTable();
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		
		endMissionButton = new Button("Avsluta");
		buttonLayout.addComponent(endMissionButton);
		
		editButton = new Button("Redigera");
		buttonLayout.addComponent(editButton);
		
		addButton = new Button("Lägg till");
		buttonLayout.addComponent(addButton);
		
		innerLayout.addComponent(buttonLayout);
		innerLayout.setComponentAlignment(buttonLayout, Alignment.TOP_RIGHT);
		
		mainLayout.addComponent(innerLayout);
	}

	public void refreshMissionTree() {
//		beans.removeAllItems();
		
		SearchMissionService service = new SearchMissionService();
		List<SearchMission> list = null;
		try {
			list = service.getListOfSearchMissions();
		} catch (Exception e) {
			e.printStackTrace();
			listener.displayError("Fel", "Hämtningen av sökuppdrag från databasen misslyckades.");
		} finally {
			if (service != null) {
				service.cleanUp();
			}
		}
		if (list != null) {
//			beans.addAll(list);
		}
	}
	
	private void populateTreeTable() {
		treeTable.removeAllItems();
		
		SearchMissionService service = null;
		List<SearchMission> list = null;
		try {
//			container = new HierarchicalContainer();
			service = new SearchMissionService();
			list = service.getListOfSearchMissions();
			if (list == null) {
				listener.displayError("Fel", "Inga sökuppdrag hittade");
				return;
			}
			
			int itemId = 0;
			for (SearchMission mission : list) {
				//add SearchMission to highest level
				int missionItemId = itemId;
				Object missionRowId = treeTable.addItem(mission.toObjectArray(), missionItemId);
				
				//allow children
				treeTable.setChildrenAllowed(missionItemId, true);
				
				itemId++; //increase itemId for Operations node
				int opsParentId = itemId;
				Item opsParentItem = treeTable.addItem(opsParentId);
				opsParentItem.getItemProperty("name").setValue("Operationer");
				treeTable.setParent(opsParentId, missionItemId);
				
				//add ops, set parent to above SearchMission itemid
				for (SearchOperation op : mission.getOpsList()) {
					itemId++;
					int opItemId = itemId;
					Item searchOpItem = treeTable.addItem(itemId);
					searchOpItem.getItemProperty("name").setValue(op.getTitle());
					treeTable.setParent(itemId, opsParentId);
					
					//add zones
					op.getZones();
					itemId++;
					Item zoneItem = treeTable.addItem(itemId);
					zoneItem.getItemProperty("name").setValue("Zon " + itemId);
					treeTable.setParent(itemId, opItemId);
					
					//add groups
					op.getGroups();
					itemId++;
					treeTable.addItem(itemId);
					treeTable.setParent(itemId, opItemId);
				}
				
				itemId++; //increase itemId for Files node
				int filesParentId = itemId;
				Item filesParentItem = treeTable.addItem(filesParentId);
				filesParentItem.getItemProperty("name").setValue("Filer");
				treeTable.setParent(filesParentId, missionItemId);
				
				//add files, set parent to above file itemid
				for (FileMetadata file : mission.getFileList()) {
					itemId++;
					Item fileItem = treeTable.addItem(itemId);
					fileItem.getItemProperty("name").setValue(file.getFilename());
					treeTable.setParent(itemId, filesParentId);
					treeTable.setChildrenAllowed(itemId, false);
				}
				
				itemId++; //increment root id (for the next searchmission)
			}
			
//			treeTable.setContainerDataSource(container);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (service != null) {
				service.cleanUp();
			}
		}
	}

	public void resetView() {
//		refreshMissionTree();
		populateTreeTable();
	}
	
}
