package se.citerus.collabsearch.adminui.view.searchmission;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import se.citerus.collabsearch.adminui.logic.SMSService;
import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.adminui.logic.SearchOperationService;
import se.citerus.collabsearch.adminui.view.ViewSwitchController;
import se.citerus.collabsearch.model.FileMetadata;
import se.citerus.collabsearch.model.SMSMessage;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearchMission;
import se.citerus.collabsearch.model.SearchOperation;
import se.citerus.collabsearch.model.SearchZone;
import se.citerus.collabsearch.model.SearcherInfo;
import se.citerus.collabsearch.model.exceptions.SearchMissionNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchOperationNotFoundException;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class SearchMissionListView extends CustomComponent {
	
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String DESCR = "descr";
	private static final String PRIO = "prio";
	private static final String STATUS = "status";
	private static final String TYPE = "type";
	
	@Autowired
	private SearchMissionService service;
	
	@Autowired
	private SearchOperationService opService;
	
	@Autowired
	private SMSService smsService;
	
	private final ViewSwitchController listener;
	private VerticalLayout mainLayout;
	private Button homeButton;
	private Button endButton;
	private Button editButton;
	private Button addButton;
	private Button removeButton;
	private Button composeSMSButton;
	private TreeTable treeTable;
	private Window popupWindow;
	
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
	private String uploadFolderPath;
	private DateField smsDateField;
	private TextField smsLocField;
	private TextField smsOpTitleField;
	private TextField smsContactField;
	private TextField smsBodyField;
	
	private static enum NodeType {
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
		listener.setMainWindowCaption("Missing People - Sökuppdrag");
		
		homeButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToWelcomeView();
			}
		});
		addButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				handleAddButtonClick();
			}
		});
		editButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				handleEditButtonClick();
			}
		});
		endButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				handleEndButtonClick();
			}
		});
		removeButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				handleRemoveButtonClick();
			}
		});
		composeSMSButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				handleComposeSMSButtonClick();
			}
		});
		
		uploadFolderPath = System.getProperty("catalina.base");
		if (uploadFolderPath == null) {
			System.err.println("Enviro var catalina.base not found, " +
							"falling back to local disk upload folder");
			uploadFolderPath = "/tmp";
		}
		uploadFolderPath += uploadFolderPath.endsWith("/") ? "" : "/";
		uploadFolderPath += "uploads/";
		
		treeTable.addListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				Object itemId = treeTable.getValue();
				if (itemId == null) {
					return;
				}
				
				endButton.setEnabled(false);
				addButton.setEnabled(false);
				editButton.setEnabled(false);
				removeButton.setEnabled(false);
				composeSMSButton.setEnabled(false);
				
				Item item = treeTable.getItem(itemId);
				NodeType type = (NodeType) item.getItemProperty(TYPE).getValue();
				switch (type) {
					case MISSION: 
						endButton.setEnabled(true);
						addButton.setEnabled(true);
						editButton.setEnabled(true);
						break;
					case OPERATIONROOT: 
						addButton.setEnabled(true);
						break;
					case OPERATION: 
						endButton.setEnabled(true);
						addButton.setEnabled(true);
						editButton.setEnabled(true);
						removeButton.setEnabled(true);
						composeSMSButton.setEnabled(true);
						break;
					case FILEROOT: 
						addButton.setEnabled(true);
						break;
					case FILE: 
						addButton.setEnabled(true);
						removeButton.setEnabled(true);
						break;
					case ZONEROOT: 
						addButton.setEnabled(true);
						break;
					case ZONE: 
						addButton.setEnabled(true);
						editButton.setEnabled(true);
						removeButton.setEnabled(true);
						break;
					case GROUPROOT: 
						addButton.setEnabled(true);
						break;
					case GROUP: 
						addButton.setEnabled(true);
						editButton.setEnabled(true);
						removeButton.setEnabled(true);
						break;
					default: return;
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
					NodeType type = (NodeType) item.getItemProperty(TYPE).getValue();
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
							.getItemProperty(TYPE).getValue();
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
				//NPE is thrown when menu is requested from the blank treetable
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
			string = treeTable.getItem(itemId).getItemProperty(propertyId).getValue().toString();
		} catch (Exception e) {
			e.printStackTrace();
			listener.displayError("Fel", "Inget sökuppdrag funnet för det markerade objektet.");
		}
		return string;
	}

	private void buildMainLayout() {
		mainLayout.setSizeFull();
		mainLayout.setMargin(true, false, false, true);
		mainLayout.setSpacing(true);
		
		Panel mainPanel = new Panel();
		mainPanel.setWidth("50%");
		
		VerticalLayout innerLayout = new VerticalLayout();
		innerLayout.setWidth("100%");
		innerLayout.setHeight("100%");
		innerLayout.setSpacing(true);
		
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		
		Embedded embImg = new Embedded("", 
			new ThemeResource("../mytheme/dual_color_extended_trans.png"));
		embImg.setStyleName("small-logo");
		headerLayout.addComponent(embImg);
		
		Label headerLabel = new Label("<h1><b>Sökuppdrag</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		headerLabel.setStyleName("logo-header");
		headerLayout.addComponent(headerLabel);
		
		innerLayout.addComponent(headerLayout);
		
		treeTable = new TreeTable();
		treeTable.setSelectable(true);
		treeTable.setSizeFull();
		treeTable.addContainerProperty(ID, String.class, "");
		treeTable.addContainerProperty(NAME, String.class, "");
		treeTable.addContainerProperty(DESCR, String.class, "");
		treeTable.addContainerProperty(PRIO, Integer.class, "");
		treeTable.addContainerProperty(STATUS, String.class, "");
		treeTable.addContainerProperty(TYPE, NodeType.class, NodeType.UNDEFINED);
		treeTable.setVisibleColumns(new Object[]{NAME, DESCR, PRIO, STATUS});
		treeTable.setColumnHeaders(new String[]{"Namn", "Beskrivning", "Prioritet", "Status"});
		treeTable.setImmediate(true);
		treeTable.setColumnWidth(DESCR, 145);
		innerLayout.addComponent(treeTable);
		
		treeTable.addGeneratedColumn(DESCR, new ColumnGenerator() {
			@Override
			public Object generateCell(Table table, Object itemId, Object columnId) {
				final Item item = table.getItem(itemId);
				NodeType type = (NodeType) item.getItemProperty(TYPE).getValue();
				if (type == NodeType.FILE) {
					final String missionId = getParentProperty(2, (Integer)itemId, ID);
					final String fileName = item.getItemProperty(NAME).getValue().toString();
					return generateFileLink(missionId, fileName);
				} else {
					return item.getItemProperty(DESCR).getValue();
				}
			}

			private Object generateFileLink(final String missionId,
					final String fileName) {
				Button linkButton = new Button("Öppna filen");
				linkButton.setStyleName(BaseTheme.BUTTON_LINK);
				linkButton.addListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						Validate.notNull(uploadFolderPath);
						File file = new File(uploadFolderPath  + missionId + "/" + fileName);
						getWindow().open(new FileResource(file, getApplication()));
					}
				});
				return linkButton;
			}
		});
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth("100%");
		
		homeButton = new Button("Tillbaka");
		buttonLayout.addComponent(homeButton);
		buttonLayout.setComponentAlignment(homeButton, Alignment.MIDDLE_LEFT);
		
		HorizontalLayout rightButtonLayout = new HorizontalLayout();
		rightButtonLayout.setSpacing(true);
//		rightButtonLayout.setWidth("100%");
		
		composeSMSButton = new Button("Skapa SMS-utskick");
		composeSMSButton.setEnabled(false);
		rightButtonLayout.addComponent(composeSMSButton);
		
		endButton = new Button("Avsluta");
		endButton.setEnabled(false);
		rightButtonLayout.addComponent(endButton);
		
		removeButton = new Button("Ta bort");
		removeButton.setEnabled(false);
		rightButtonLayout.addComponent(removeButton);
		
		editButton = new Button("Redigera");
		editButton.setEnabled(false);
		rightButtonLayout.addComponent(editButton);
		
		addButton = new Button("Lägg till");
		addButton.setEnabled(false);
		rightButtonLayout.addComponent(addButton);
		
		buttonLayout.addComponent(rightButtonLayout);
		buttonLayout.setComponentAlignment(rightButtonLayout, Alignment.MIDDLE_RIGHT);
		
		innerLayout.addComponent(buttonLayout);
		innerLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_LEFT);
		
		mainPanel.addComponent(innerLayout);
		mainLayout.addComponent(mainPanel);
		mainLayout.setComponentAlignment(mainPanel, Alignment.TOP_CENTER);
		
		buildPopupWindow();
	}

	/**
	 * Performs a requery and repaint of the mission tree table.
	 */
	public void refreshMissionTree() {
		populateTreeTable();
		
		if (treeTable.getItemIds().isEmpty()) {
			addButton.setEnabled(true);
		}
	}
	
	private void populateTreeTable() {
		treeTable.removeAllItems();
		
		List<SearchMission> list = null;
		try {
			list = service.getListOfSearchMissions();
			if (list == null) {
//				listener.displayError("Fel", "Inga sökuppdrag hittade");
				return;
			}
			
			int itemId = 0;
			for (SearchMission mission : list) {
				//add SearchMission to highest level
				int missionItemId = itemId;
				Item missionItem = setupItemProperties(missionItemId, mission.getName(), NodeType.MISSION, mission.getId());
				missionItem.getItemProperty(DESCR).setValue(mission.getDescription());
				missionItem.getItemProperty(PRIO).setValue(mission.getPrio());
				missionItem.getItemProperty(STATUS).setValue(mission.getStatus().getName());
				
				//allow children
				treeTable.setChildrenAllowed(missionItemId, true);
				
				itemId++; //increase itemId for Operations node
				int opsParentId = itemId;
				setupItemProperties(opsParentId, "Operationer", NodeType.OPERATIONROOT, null);
				treeTable.setParent(opsParentId, missionItemId);
				
				//add ops, set parent to above SearchMission itemid
				final List<SearchOperation> opsList = mission.getOpsList();
				if (opsList != null) {
					for (SearchOperation op : opsList) {
						itemId++;
						int opItemId = itemId;
						Item opItem = setupItemProperties(opItemId, op.getTitle(), NodeType.OPERATION, op.getId());
						opItem.getItemProperty(DESCR).setValue(op.getDescr());
						opItem.getItemProperty(STATUS).setValue(op.getStatus());
						treeTable.setParent(itemId, opsParentId);
						
						//add zone parent label
						itemId++;
						int zoneParentId = itemId;
						setupItemProperties(zoneParentId, "Zoner", NodeType.ZONEROOT, null);
						treeTable.setParent(zoneParentId, opItemId);
						//add zones
						final List<SearchZone> zones = op.getZones();
						if (zones != null) {
							for (SearchZone zone : zones) {
								itemId++;
								Item item = setupItemProperties(itemId, zone.getTitle(), NodeType.ZONE, zone.getId());
								item.getItemProperty(PRIO).setValue(zone.getPriority());
								treeTable.setParent(itemId, zoneParentId);
								treeTable.setChildrenAllowed(itemId, false);
							}
						}
						
						//add group parent label
						itemId++;
						int groupParentId = itemId;
						setupItemProperties(groupParentId, "Grupper", NodeType.GROUPROOT, null);
						treeTable.setParent(itemId, opItemId);
						//add groups
						final List<SearchGroup> groups = op.getGroups();
						if (groups != null) {
							for (SearchGroup group : groups) {
								itemId++;
								setupItemProperties(itemId, group.getName(), NodeType.GROUP, group.getId());
								treeTable.setParent(itemId, groupParentId);
								treeTable.setChildrenAllowed(itemId, false);
							}
						}
					}
				}
				
				itemId++; //increase itemId for Files node
				int filesParentId = itemId;
				setupItemProperties(filesParentId, "Filer", NodeType.FILEROOT, null);
				treeTable.setParent(filesParentId, missionItemId);
				
				//add files, set parent to above file itemid
				List<FileMetadata> fileList = mission.getFileList();
				if (fileList != null) {
					for (FileMetadata file : fileList) {
						itemId++;
						setupItemProperties(itemId, file.getFileName(), NodeType.FILE, file.getId());
						treeTable.setParent(itemId, filesParentId);
						treeTable.setChildrenAllowed(itemId, false);
					}
				}
				
				itemId++; //increment root id (for the next searchmission)
			}
		} catch (Exception e) {
			e.printStackTrace();
			listener.displayError("Fel", 
					"Skapandet av sökuppdragstabellen misslyckades.");
		}
	}
	
	private Item setupItemProperties(int itemId, String name, NodeType type, String id) {
		Item item = treeTable.addItem(itemId);
		item.getItemProperty(NAME).setValue(name);
		item.getItemProperty(TYPE).setValue(type);
		item.getItemProperty(ID).setValue(id);
		return item;
	}

	public void resetView() {
		populateTreeTable();
	}

	private void handleAddActions(int itemId, NodeType type) {
		String id;
		switch (type) {
			case MISSION: 
				listener.switchToSearchMissionEditView(null);
				break;
			case OPERATIONROOT: 
				id = getParentProperty(1, itemId, ID);
				listener.switchToSearchOperationEditView(null, id);
				break;
			case OPERATION: 
				id = getParentProperty(2, itemId, ID);
				listener.switchToSearchOperationEditView(null, id);
				break;
			case FILEROOT: 
				id = getParentProperty(1, itemId, ID);
				listener.switchToFileUploadView(id);
				break;
			case FILE: 
				id = getParentProperty(2, itemId, ID);
				listener.switchToFileUploadView(id);
				break;
			case ZONEROOT: 
				id = getParentProperty(1, itemId, ID);
				listener.switchToNewZoneView(id);
				break;
			case ZONE: 
				id = getParentProperty(2, itemId, ID);
				listener.switchToNewZoneView(id);
				break;
			case GROUPROOT: 
				id = getParentProperty(1, itemId, ID);
				listener.switchToGroupEditView(null, id);
				break;
			case GROUP: 
				id = getParentProperty(2, itemId, ID);
				listener.switchToGroupEditView(null, id);
				break;
			default: break;
		}
	}

	private void handleEditActions(int itemId, Item item, NodeType type) {
		String id = item.getItemProperty(ID).getValue().toString();
		if (type == NodeType.MISSION) {
			listener.switchToSearchMissionEditView(id);
		} else if (type == NodeType.OPERATION) {
			String missionId = getParentProperty(2, itemId, ID);
			listener.switchToSearchOperationEditView(id, missionId);
		} else if (type == NodeType.ZONE) {
			listener.switchToEditZoneView(id);
		} else if (type == NodeType.GROUP) {
			String opId = getParentProperty(2, itemId, ID);
			listener.switchToGroupEditView(id, opId);
		} else {
			listener.displayNotification("Kan ej redigeras", 
					"Det valda objektet kan ej redigeras.");
		}
	}
	
	private void handleRemoveActions(int itemId, Item item, NodeType type) {
		if (type == NodeType.FILE) {
			String fileName = item.getItemProperty(NAME).getValue().toString();
			String missionId = getParentProperty(2, itemId, ID);
			try {
				service.deleteFile(fileName, missionId);
				treeTable.removeItem(itemId);
			} catch (SearchMissionNotFoundException e) {
				listener.displayError("Fel", "Sökuppdraget ej funnet");
			} catch (Exception e) {
				e.printStackTrace();
				listener.displayError("Filraderingen misslyckades", e.getMessage());
			}
		} else if (type == NodeType.OPERATION) {
			String opId = item.getItemProperty(ID).getValue().toString();
			try {
				opService.deleteSearchOp(opId);
				removeItemsRecursively(itemId);
			} catch (Exception e) {
				e.printStackTrace();
				listener.displayError("Operationsborttagningen misslyckades", e.getMessage());
			} finally {
				if (service != null) {
					service.cleanUp();
				}
			}
		} else if (type == NodeType.ZONE) {
			String zoneId = item.getItemProperty(ID).getValue().toString();
			try {
				opService.deleteZone(zoneId);
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
			String groupId = item.getItemProperty(ID).getValue().toString();
			try {
				opService.deleteGroup(groupId);
				treeTable.removeItem(itemId);
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
		String itemName = item.getItemProperty(NAME).getValue().toString();
		if (type == NodeType.MISSION) {
			try {
				service.endMission(itemName);
				item.getItemProperty(STATUS).setValue("Avslutat");
				treeTable.requestRepaintAll();
			} catch (SearchMissionNotFoundException e) {
				listener.displayError("Fel", "Sökuppgradet ej funnet");
			} catch (Exception e) {
				e.printStackTrace();
				listener.displayError("Fel", e.getMessage());
			}
		} else if (type == NodeType.OPERATION) {
			try {
				String opId = item.getItemProperty(ID).getValue().toString();
				String statusName = opService.endOperation(opId);
				item.getItemProperty(STATUS).setValue(statusName);
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
	
	/**
	 * Removes a node from the tree and recursively removes all it's children.
	 * @param rootItemId the itemid of the outermost node to remove.
	 */
	private void removeItemsRecursively(Object rootItemId) {
		ArrayList<Object> removeList = new ArrayList<Object>();
		
		Collection<?> children = treeTable.getChildren(rootItemId);
		for (Object object : children) {
			if (treeTable.hasChildren(object)) {
				Collection<?> children2 = treeTable.getChildren(object);
				for (Object object2 : children2) {
					removeList.add(object2); //individual group/zone
				}
			}
			removeList.add(object); //parent group/zone
		}
		removeList.add(rootItemId); //root searchop
		
		for (Object object : removeList) {
			treeTable.removeItem(object);
		}
	}
	
	private void handleAddButtonClick() {
		Object itemId = treeTable.getValue();
		if (itemId != null) {
			Item item = treeTable.getItem(itemId);
			NodeType type = (NodeType) item.getItemProperty(TYPE).getValue();
			handleAddActions((Integer)itemId, type);
		} else if (treeTable.getItemIds().isEmpty()) {
			handleAddActions(-1, NodeType.MISSION);
		}
	}
	
	private void handleEditButtonClick() {
		Object itemId = treeTable.getValue();
		if (itemId != null) {
			Item item = treeTable.getItem(itemId);
			NodeType type = (NodeType) item.getItemProperty(TYPE).getValue();
			handleEditActions((Integer)itemId, item, type);
		}
	}
	
	private void handleEndButtonClick() {
		Object itemId = treeTable.getValue();
		if (itemId != null) {
			Item item = treeTable.getItem(itemId);
			NodeType type = (NodeType) item.getItemProperty(TYPE).getValue();
			handleEndActions((Integer)itemId, item, type);
		}
	}
	
	private void handleRemoveButtonClick() {
		Object itemId = treeTable.getValue();
		if (itemId != null) {
			Item item = treeTable.getItem(itemId);
			NodeType type = (NodeType) item.getItemProperty(TYPE).getValue();
			handleRemoveActions((Integer)itemId, item, type);
		}
	}
	
	private void handleComposeSMSButtonClick() {
		try {
			Object itemId = treeTable.getValue();
			Item item = treeTable.getItem(itemId);
			String opId = item.getItemProperty(ID).getValue().toString();
			
			SearchOperation op = opService.getSearchOp(opId);
			
			smsDateField.setValue(op.getDate());
			smsLocField.setValue(op.getLocation());
			smsOpTitleField.setValue(op.getTitle());
			smsContactField.setValue(opService.getOpOrganizerContactInfo());
			smsBodyField.setValue(opService.getDefaultSMSBody());
			getWindow().addWindow(popupWindow);
		} catch (SearchOperationNotFoundException e) {
			listener.displayError("Sökoperationsfel", "Sökuppdraget ej funnet");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleSendSMSButtonClick() {
		SMSMessage message = new SMSMessage(
			((Date)smsDateField.getValue()).getTime(), 
			smsLocField.getValue().toString(), 
			smsBodyField.getValue().toString(), 
			smsOpTitleField.getValue().toString(), 
			smsContactField.getValue().toString());
		
		String messageString = message.toString();
		if (messageString.length() > 140) {
			listener.displayError("Fel vid SMS-utskick", 
					"Meddelandet är för långt: " + messageString.length() + "/140");
			return;
		}
		
		Object itemId = treeTable.getValue();
		if (itemId != null) {
			Item item = treeTable.getItem(itemId);
			NodeType type = (NodeType) item.getItemProperty(TYPE).getValue();
			if (type != NodeType.OPERATION) {
				return;
			}
			
			//get selected op id
			String opId = item.getItemProperty(ID).getValue().toString();
			
			//get all searchers for mission
			List<SearcherInfo> searcherList = null;
			try {
				searcherList = opService.getSearchersInfoByOp(opId);
			} catch (SearchOperationNotFoundException e) {
				e.printStackTrace();
				listener.displayError("Fel vid SMS-utskick", 
						"Operationens id kunde ej hittas");
				return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			try {
				List<String> list = smsService.sendSMSToSearchers(searcherList, message);
				if (!list.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					for (String numStr : list) {
						sb.append(numStr + "\n");
					}
					listener.displayError("Fel vid SMS-utskick", 
							"Följande nummer gick ej att nå: " + sb.toString());
				}
			} catch (Exception e) {
				listener.displayError("Fel vid SMS-utskick", e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private void buildPopupWindow() {
		popupWindow = new Window("Skapa SMS-utskick");
		popupWindow.setWidth("250px");
		popupWindow.setModal(true);
		popupWindow.center();

		VerticalLayout layout = (VerticalLayout) popupWindow.getContent();
		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout formLayout = new VerticalLayout();
		formLayout.setWidth("100%");
		formLayout.setSpacing(true);

		smsDateField = new DateField("Datum för sökoperation");
		smsDateField.setWidth("100%");
		smsDateField.setImmediate(true);
		smsDateField.setLocale(new Locale("sv", "SE"));
		smsDateField.setResolution(InlineDateField.RESOLUTION_MIN);
		formLayout.addComponent(smsDateField);

		smsLocField = new TextField("Ort för sökoperation");
		smsLocField.setWidth("100%");
		formLayout.addComponent(smsLocField);

		smsOpTitleField = new TextField("Operationsnamn");
		smsOpTitleField.setWidth("100%");
		formLayout.addComponent(smsOpTitleField);

		smsContactField = new TextField("Kontaktinfo för översta sökbefäl");
		smsContactField.setWidth("100%");
		formLayout.addComponent(smsContactField);

		smsBodyField = new TextField("Meddelandekropp");
		smsBodyField.setWidth("100%");
		smsBodyField.setHeight("75px");
		formLayout.addComponent(smsBodyField);

		layout.addComponent(formLayout);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);

		Button closePopupButton = new Button("Avbryt");
		closePopupButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				(popupWindow.getParent()).removeWindow(popupWindow);
			}
		});
		buttonLayout.addComponent(closePopupButton);

		Button sendSMSButton = new Button("Skicka");
		sendSMSButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					handleSendSMSButtonClick();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					(popupWindow.getParent()).removeWindow(popupWindow);
				}
			}
		});
		buttonLayout.addComponent(sendSMSButton);

		layout.addComponent(buttonLayout);
		layout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
	}
	
}
