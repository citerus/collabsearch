package se.citerus.collabsearch.adminui.view.searchmission;

import java.util.List;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.adminui.logic.SearchOperationService;
import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.Group;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.Zone;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SearchMissionListView extends CustomComponent {
	
	private final ViewSwitchController listener;
	private VerticalLayout mainLayout;
	private Button homeButton;
	private Button endMissionButton;
	private Button editButton;
	private Button addButton;
	private TreeTable treeTable;
	private HierarchicalContainer container;
	
	private Handler contextMenuHandler;
	private static Action ACTION_ADDITEM;
	private static Action ACTION_REMOVEITEM;
	private static Action ACTION_ENDITEM;
	private static Action ACTION_EDITITEM;
	private static Action[] emptyMenu;
	private static Action[] addMenu;
	private static Action[] addEditEndMenu;
	private static Action[] addRemoveMenu;
	private static Action[] addEditRemoveMenu;
	private static Action[] addEditRemoveEndMenu;
	
	private enum NodeType {
		UNDEFINED, 
		MISSION, 
		OPERATION, OPERATIONROOT, 
		FILE, FILEROOT, 
		ZONE, ZONEROOT, 
		GROUP, GROUPROOT
	}
	
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
				try { //add more nodetypes or remove?
					String missionId = null;
					NodeType type = NodeType.UNDEFINED;
					Item item = treeTable.getItem(treeTable.getValue());
					missionId = item.getItemProperty("id").getValue().toString();
					type = (NodeType) item.getItemProperty("type").getValue();
					if (type == NodeType.MISSION) {
						if (missionId != null) {
							listener.switchToSearchMissionEditView(missionId);
						} else {
							listener.displayNotification("Inget sökuppdrag markerat", 
									"Du måste markera ett sökuppdrag för redigering");
						}
					} else {
						listener.displayNotification("Markeringen ej sökuppdrag", 
								"Endast sökuppdrag får markeras för avslutning.");
					}
				} catch (NullPointerException e) {
					listener.displayNotification("Inget sökuppdrag markerat", 
							"Du måste markera ett sökuppdrag för redigering");
				} catch (Exception e) {
					e.printStackTrace();
					listener.displayError("Fel", e.getMessage());
				}
			}
		});
		endMissionButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					SearchMissionService service = null;
					String missionId = null;
					NodeType type = NodeType.UNDEFINED;
					Item item = treeTable.getItem(treeTable.getValue());
					missionId = item.getItemProperty("id").getValue().toString();
					type = (NodeType) item.getItemProperty("type").getValue();
					if (type == NodeType.MISSION) {
						if (missionId != null) {
							try {
								service = new SearchMissionService();
								service.endMission(missionId);
							} catch (Exception e) {
								e.printStackTrace();
								listener.displayError("Fel", e.getMessage());
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
					} else {
						listener.displayNotification("Markeringen ej sökuppdrag", 
								"Endast sökuppdrag får markeras för avslutning.");
					}
				} catch (NullPointerException e) {
					listener.displayNotification("Inget sökuppdrag markerat", 
							"Du måste markera ett sökuppdrag för avslutning");
				} catch (Exception e) {
					e.printStackTrace();
					listener.displayError("Fel", e.getMessage());
				}
			}
		});	
		
		//create menu items
		ACTION_ADDITEM = new Action("Lägg till");
		ACTION_REMOVEITEM = new Action("Ta bort");
		ACTION_EDITITEM = new Action("Redigera");
		ACTION_ENDITEM = new Action("Avsluta");
		
		//create menus
		addMenu = new Action[] { ACTION_ADDITEM };
		addRemoveMenu = new Action[] { ACTION_ADDITEM, ACTION_REMOVEITEM };
		addEditEndMenu = new Action[] { ACTION_ADDITEM, ACTION_EDITITEM, ACTION_ENDITEM };
		addEditRemoveEndMenu = new Action[] { ACTION_ADDITEM, ACTION_EDITITEM, ACTION_REMOVEITEM, ACTION_ENDITEM };
		addEditRemoveMenu = new Action[] { ACTION_ADDITEM, ACTION_EDITITEM, ACTION_REMOVEITEM };
		emptyMenu = new Action[] {};
		
		//setup menu listener
		contextMenuHandler = new Handler() {
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (target == null) {
					handleAddActions(0, NodeType.MISSION);
				} else {
					int itemId = (Integer) target;
					Item item = treeTable.getItem(itemId);
					NodeType type = (NodeType) item.getItemProperty("type").getValue();
					if (action == ACTION_ADDITEM) {
						handleAddActions(itemId, type);
					} else if (action == ACTION_EDITITEM) {
						handleEditActions(itemId, item, type);
					} else if (action == ACTION_REMOVEITEM) {
						handleRemoveActions(itemId, item, type);
					} else if (action == ACTION_ENDITEM) {
						handleEndActions(itemId, item, type);
					}
				}
			}

			@Override
			public Action[] getActions(Object target, Object sender) {
				try {
					NodeType type = (NodeType) treeTable.getItem(target)
							.getItemProperty("type").getValue();
					switch (type) { //setup different menus depending on tree branch
						case MISSION: return addEditEndMenu;
						case OPERATIONROOT: return addMenu;
						case OPERATION: return addEditRemoveEndMenu;
						case FILEROOT: return addMenu;
						case FILE: return addRemoveMenu;
						case ZONEROOT: return addMenu;
						case ZONE: return addEditRemoveMenu;
						case GROUPROOT: return addMenu;
						case GROUP: return addEditRemoveMenu;
						default: return emptyMenu;
					}
				} catch (NullPointerException e) {
					return addMenu;
				}
			}
		};
		treeTable.addActionHandler(contextMenuHandler);
	}

	private String getParentProperty(int depth, int originalItemId, String propertyId) {
		String string = null;
		try {
			Object itemId = originalItemId;
			for (int i = 0; i < depth; i++) {
				itemId = treeTable.getParent(itemId);
			}
			string = treeTable.getItem(itemId).getItemProperty("name").getValue().toString();
		} catch (Exception e) {
			e.printStackTrace();
			listener.displayError("Fel", "Inget sökuppdrag funnet för det markerade objektet.");
		}
		return string;
	}

	private void buildMainLayout() {
		mainLayout.setSizeFull();
		mainLayout.setMargin(false, false, false, true);
		mainLayout.setSpacing(true);
		
		VerticalLayout innerLayout = new VerticalLayout();
		innerLayout.setWidth("50%");
		innerLayout.setHeight("100%");
		innerLayout.setSpacing(true);
		
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		
		homeButton = new Button("Tillbaka");
		headerLayout.addComponent(homeButton);
		headerLayout.setComponentAlignment(homeButton, Alignment.MIDDLE_LEFT);
		
		Label headerLabel = new Label("<h1><b>Sökuppdrag</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		headerLayout.addComponent(headerLabel);
		
		innerLayout.addComponent(headerLayout);
		
		treeTable = new TreeTable();
		treeTable.setSelectable(true);
		treeTable.setSizeFull();
		treeTable.addContainerProperty("id", String.class, "");
		treeTable.addContainerProperty("name", String.class, "");
		treeTable.addContainerProperty("descr", String.class, "");
		treeTable.addContainerProperty("prio", Integer.class, "");
		treeTable.addContainerProperty("status", String.class, "");
		treeTable.addContainerProperty("type", NodeType.class, NodeType.UNDEFINED);
		treeTable.setVisibleColumns(new Object[]{"name", "descr", "prio", "status"});
		treeTable.setColumnHeaders(new String[]{"Namn", "Beskrivning", "Prioritet", "Status"});
		treeTable.setImmediate(true);
		innerLayout.addComponent(treeTable);
		
//		populateTreeTable();
		
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

	/**
	 * Performs a requery and repaint of the mission tree table.
	 */
	public void refreshMissionTree() {
		populateTreeTable();
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
				Item missionItem = treeTable.addItem(missionItemId);
				missionItem.getItemProperty("name").setValue(mission.getName());
				missionItem.getItemProperty("descr").setValue(mission.getDescription());
				missionItem.getItemProperty("prio").setValue(mission.getPrio());
				missionItem.getItemProperty("status").setValue(mission.getStatus().getName());
				missionItem.getItemProperty("type").setValue(NodeType.MISSION);
				missionItem.getItemProperty("id").setValue(mission.getId());
				
				//allow children
				treeTable.setChildrenAllowed(missionItemId, true);
				
				itemId++; //increase itemId for Operations node
				int opsParentId = itemId;
				setupItemProperties(opsParentId, "Operationer", NodeType.OPERATIONROOT, null);
				treeTable.setParent(opsParentId, missionItemId);
				
				//add ops, set parent to above SearchMission itemid
				for (SearchOperation op : mission.getOpsList()) {
					itemId++;
					int opItemId = itemId;
					Item opItem = treeTable.addItem(itemId);
					opItem.getItemProperty("name").setValue(op.getTitle());
					opItem.getItemProperty("descr").setValue(op.getDescr());
					opItem.getItemProperty("status").setValue(op.getStatus());
					opItem.getItemProperty("type").setValue(NodeType.OPERATION);
					opItem.getItemProperty("id").setValue(op.getId());
					treeTable.setParent(itemId, opsParentId);
					
					//add zone parent label
					itemId++;
					int zoneParentId = itemId;
					setupItemProperties(zoneParentId, "Zoner", NodeType.ZONEROOT, null);
					treeTable.setParent(zoneParentId, opItemId);
					//add zones
					for (Zone zone : op.getZones()) {
						itemId++;
						setupItemProperties(itemId, zone.getName(), NodeType.ZONE, zone.getId());
						treeTable.setParent(itemId, zoneParentId);
						treeTable.setChildrenAllowed(itemId, false);
					}
					
					//add group parent label
					itemId++;
					int groupParentId = itemId;
					setupItemProperties(groupParentId, "Grupper", NodeType.GROUPROOT, null);
					treeTable.setParent(itemId, opItemId);
					//add groups
					for (Group group : op.getGroups()) {
						itemId++;
						setupItemProperties(itemId, group.getName(), NodeType.GROUP, group.getId());
						treeTable.setParent(itemId, groupParentId);
						treeTable.setChildrenAllowed(itemId, false);
					}
				}
				
				itemId++; //increase itemId for Files node
				int filesParentId = itemId;
				setupItemProperties(filesParentId, "Filer", NodeType.FILEROOT, null);
				treeTable.setParent(filesParentId, missionItemId);
				
				//add files, set parent to above file itemid
				for (FileMetadata file : mission.getFileList()) {
					itemId++;
					setupItemProperties(itemId, file.getFilename(), NodeType.FILE, file.getId());
					treeTable.setParent(itemId, filesParentId);
					treeTable.setChildrenAllowed(itemId, false);
				}
				
				itemId++; //increment root id (for the next searchmission)
			}
			
//			treeTable.setContainerDataSource(container);
		} catch (Exception e) {
			e.printStackTrace();
			listener.displayError("Fel", 
					"Skapandet av sökuppdragstabellen misslyckades.");
		} finally {
			if (service != null) {
				service.cleanUp();
			}
		}
	}
	
	private void setupItemProperties(int itemId, String name, NodeType type, String id) {
		Item item = treeTable.addItem(itemId);
		item.getItemProperty("name").setValue(name);
		item.getItemProperty("type").setValue(type);
		item.getItemProperty("id").setValue(type);
	}

	public void resetView() {
		populateTreeTable();
	}

	private void handleAddActions(int itemId, NodeType type) {
		switch (type) {
			case MISSION: {
				listener.switchToSearchMissionEditView(null);
				break;
			}
			case OPERATIONROOT: {
				String missionName = getParentProperty(1, itemId, "name");
				listener.switchToSearchOperationEditView(null, missionName); //TODO change missionname to id
				break;
			}
			case OPERATION: {
				String missionName = getParentProperty(2, itemId, "name");
				listener.switchToSearchOperationEditView(null, missionName); //TODO change missionname to id
				break;
			}
			case FILEROOT: {
				String missionId = getParentProperty(1, itemId, "id");
				listener.switchToFileManagementView(missionId, null);
				break;
			}
			case FILE: {
				String missionId = getParentProperty(2, itemId, "id");
				listener.switchToFileManagementView(missionId, null);
				break;
			}
			case ZONEROOT: {
				String opName = getParentProperty(1, itemId, "name");
				listener.switchToZoneEditView(null, opName);
				break;
			}
			case ZONE: {
				String opName = getParentProperty(2, itemId, "name");
				listener.switchToZoneEditView(null, opName);
				break;
			}
			case GROUPROOT: {
				String opName = getParentProperty(1, itemId, "name");
				String missionName = getParentProperty(3, itemId, "name");
				listener.switchToGroupEditView(null, opName, missionName);
				break;
			}
			case GROUP: {
				String opName = getParentProperty(2, itemId, "name");
				String missionName = getParentProperty(4, itemId, "name");
				listener.switchToGroupEditView(null, opName, missionName);
				break;
			}
			default: break;
		}
	}

	private void handleEditActions(int itemId, Item item, NodeType type) {
		String itemName = item.getItemProperty("name").getValue().toString();
		if (type == NodeType.MISSION) {
			listener.switchToSearchMissionEditView(itemName);
		} else if (type == NodeType.OPERATION) {
			String missionName = getParentProperty(2, itemId, "name");
			listener.switchToSearchOperationEditView(itemName, missionName); //TODO change missionname to id
		} else if (type == NodeType.ZONE) {
			String opName = getParentProperty(2, itemId, "name");
			String missionName = getParentProperty(4, itemId, "name");
			String zoneId = null;
			SearchOperationService service = null;
			try {
				service = new SearchOperationService();
				zoneId = service.resolveZoneId(itemName, opName, missionName);
				listener.switchToZoneEditView(zoneId, opName);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (service != null) {
					service.cleanUp();
				}
			}
		} else if (type == NodeType.GROUP) {
			String missionName = getParentProperty(4, itemId, "name");
			String opName = getParentProperty(2, itemId, "name");
			String groupId = null;
			SearchOperationService service = null;
			try {
				service = new SearchOperationService();
				groupId = service.resolveZoneId(itemName, opName, missionName);
				listener.switchToGroupEditView(groupId, opName, missionName);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (service != null) {
					service.cleanUp();
				}
			}
		}
	}
	
	private void handleRemoveActions(int itemId, Item item, NodeType type) {
		String itemName = item.getItemProperty("name").getValue().toString();
		String missionName = null;
		if (type == NodeType.FILE) {
			String missionId = getParentProperty(2, itemId, "id");
			SearchMissionService service = null;
			try {
				service = new SearchMissionService();
				service.deleteFile(itemName, missionId);
				treeTable.removeItem(itemId);
			} catch (Exception e) {
				e.printStackTrace();
				listener.displayError("Filraderingen misslyckades", e.getMessage());
			} finally {
				if (service != null) {
					service.cleanUp();
				}
			}
		} else if (type == NodeType.OPERATION) {
			missionName = getParentProperty(2, itemId, "name");
			SearchMissionService service = null;
			try {
				service = new SearchMissionService();
				service.deleteSearchOperation(itemName, missionName);
				treeTable.removeItem(itemId);
			} catch (Exception e) {
				e.printStackTrace();
				listener.displayError("Operationsborttagningen misslyckades", e.getMessage());
			} finally {
				if (service != null) {
					service.cleanUp();
				}
			}
		} else if (type == NodeType.ZONE) {
			missionName = getParentProperty(4, itemId, "name");
			SearchOperationService service = null;
			try {
				service = new SearchOperationService();
				String opName = getParentProperty(2, itemId, "name");
				String zoneId = service.resolveZoneId(itemName, opName, missionName);
				service.deleteZone(zoneId);
				treeTable.removeItem(itemId);
			} catch (Exception e) {
				e.printStackTrace();
				listener.displayError("Zonborttagningen misslyckades", e.getMessage());
			} finally {
				if (service != null) {
					service.cleanUp();
				}
			}
		} else if (type == NodeType.GROUP) {
			missionName = getParentProperty(4, itemId, "name");
			SearchOperationService service = null;
			try {
				service = new SearchOperationService();
				String opName = getParentProperty(2, itemId, "name");
				String groupId = service.resolveGroupId(itemName, opName, missionName);
				service.deleteGroup(groupId);
			} catch (Exception e) {
				e.printStackTrace();
				listener.displayError("Gruppborttagningen misslyckades", e.getMessage());
			} finally {
				if (service != null) {
					service.cleanUp();
				}
			}
		}
	}

	private void handleEndActions(int itemId, Item item, NodeType type) {
		String itemName = item.getItemProperty("name").getValue().toString();
		if (type == NodeType.MISSION) {
			SearchMissionService service = null;
			try {
				service = new SearchMissionService();
				String statusName = service.endMission(itemName);
				item.getItemProperty("status").setValue(statusName);
				treeTable.requestRepaintAll();
			} catch (Exception e) {
				e.printStackTrace();
				listener.displayError("Fel", e.getMessage());
			} finally {
				if (service != null) {
					service.cleanUp();
				}
			}
		} else if (type == NodeType.OPERATION) {
			SearchOperationService service = null;
			try {
				service = new SearchOperationService();
				String missionName = getParentProperty(2, itemId, "name");
				String statusName = service.endOperation(itemName, missionName);
				item.getItemProperty("status").setValue(statusName);
			} catch (Exception e) {
				e.printStackTrace();
				listener.displayError("Fel", e.getMessage());
			} finally {
				if (service != null) {
					service.cleanUp();
				}
			}
		} else {
			listener.displayNotification("Ogiltig markering", 
					"Endast sökuppdrag eller sökoperationer kan avslutas.");
		}
	}
	
}
