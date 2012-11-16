package se.citerus.collabsearch.adminui.view.searchoperation;

import static org.apache.commons.collections15.CollectionUtils.collect;
import static org.apache.commons.collections15.CollectionUtils.select;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import se.citerus.collabsearch.adminui.LookingForApp;
import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.SearchOperationService;
import se.citerus.collabsearch.model.GroupNode;
import se.citerus.collabsearch.model.Rank;
import se.citerus.collabsearch.model.Rank.Title;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.exceptions.SearchGroupNotFoundException;
import se.citerus.collabsearch.model.exceptions.SearchGroupValidationException;
import se.citerus.collabsearch.model.exceptions.SearchOperationNotFoundException;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.Action;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class GroupEditView extends CustomComponent {

	/** The real name of the searcher, in the form {firstname lastname}. This is used in the table. */
	private static final String REALNAME_PROPERTY_ID = "realname";
	/** The alphanumeric storage id of the searcher. The same id is used in the tree and the table. */
	private static final String SID_PROPERTY_ID = "id";
	/** The displayed name of the searcher. This is used in the tree. */
	private static final String NAME_PROPERTY_ID = "name";
	/** The rank of the searcher within this group. This is used in the tree. */
	private static final String RANK_PROPERTY_ID = "rank";

	@Autowired
	private SearchOperationService service;
	
	private VerticalLayout mainLayout;
	private final ViewSwitchController listener;
	private Label headerLabel;
	private Tree groupTree;
	private PopupWindow rankChangePopupWindow;
	private String groupId;
	private String opId;
	private Table searcherTable;
	private TextField nameField;
	private Window successDialog;

	public GroupEditView(ViewSwitchController listener) {
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
		this.listener = listener;
	}

	public void init() {
		buildMainLayout();
		setupContextMenu();
		setupPopupWindows();
	}

	private void buildMainLayout() {
		mainLayout.setSizeFull();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		
		Panel mainPanel = new Panel();
		mainPanel.setStyleName("group-panel");
		mainPanel.setWidth("50%");
		
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		
		Embedded logo = new Embedded(null, 
			new ThemeResource("../mytheme/dual_color_extended_trans.png"));
		logo.setStyleName("small-logo");
		headerLayout.addComponent(logo);
		
		headerLabel = new Label("<h1><b>" + "Redigera grupp" + "</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		headerLayout.setStyleName("logo-header");
		headerLayout.addComponent(headerLabel);
		
		mainPanel.addComponent(headerLayout);
		
		HorizontalLayout nameLayout = new HorizontalLayout();
		nameLayout.setWidth("50%");
		
		Label nameLabel = new Label("Sökgruppsnamn:");
		nameLabel.setWidth("100%");
		nameLayout.addComponent(nameLabel);
		nameLayout.setExpandRatio(nameLabel, 1f);
		nameLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_LEFT);
		
		nameField = new TextField();
		nameField.setWidth("100%");
		nameField.addValidator(new StringLengthValidator(
				"Namnet måste vara mellan 3-99 tecken", 3, 99, false));
		nameField.setValidationVisible(true);
		nameLayout.addComponent(nameField);
		nameLayout.setComponentAlignment(nameField, Alignment.MIDDLE_LEFT);
		nameLayout.setExpandRatio(nameField, 2f);
		
		mainPanel.addComponent(nameLayout);
		
		searcherTable = new Table();
		searcherTable.setWidth("100%");
		searcherTable.setHeight("200px");
		searcherTable.setSelectable(true);
		searcherTable.setDragMode(Table.TableDragMode.ROW);
		searcherTable.setStyleName("searcher-table");
		
		groupTree = new Tree("Sökgruppens struktur");
		groupTree.setSizeUndefined();
		groupTree.setSelectable(true);
		groupTree.setStyleName("grouptree");
		
		groupTree.addContainerProperty(NAME_PROPERTY_ID, String.class, "");
		groupTree.addContainerProperty(SID_PROPERTY_ID, String.class, "");
		groupTree.addContainerProperty(RANK_PROPERTY_ID, Rank.Title.class, Rank.Title.SEARCHER);
		groupTree.addContainerProperty(REALNAME_PROPERTY_ID, String.class, "");
		
		groupTree.setDragMode(TreeDragMode.NODE);
		groupTree.setDropHandler(new TreeDragNDropHandler(groupTree, searcherTable));
		searcherTable.setDropHandler(new TableDragNDropHandler(groupTree, searcherTable));
		
		final VerticalLayout treePanelLayout = new VerticalLayout();
		treePanelLayout.addComponent(groupTree);
		final Panel treePanel = new Panel();
		treePanel.setScrollable(true);
		treePanel.setHeight("100%");
		treePanel.setStyleName("group-tree-panel");
		treePanel.setContent(treePanelLayout);
		
		HorizontalLayout outerTreeLayout = new HorizontalLayout();
		outerTreeLayout.setWidth("100%");
		outerTreeLayout.setHeight("200px");
		outerTreeLayout.addComponent(treePanel);
		outerTreeLayout.setMargin(true, false, true, false);
		
		mainPanel.addComponent(outerTreeLayout);
		
		mainPanel.addComponent(searcherTable);
		
		groupTree.addListener(new Tree.ExpandListener() {
			@Override
			public void nodeExpand(ExpandEvent event) {
				treePanelLayout.requestRepaint();
				treePanel.requestRepaint();
			}
		});
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true, false, false, false);
		
		Button cancelButton = new Button("Avbryt");
		cancelButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionListView();
			}
		});
		buttonLayout.addComponent(cancelButton);
		buttonLayout.setComponentAlignment(cancelButton, Alignment.TOP_LEFT);
		
		Button confirmButton = new Button("Spara");
		confirmButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				saveGroup();
			}
		});
		buttonLayout.addComponent(confirmButton);
		buttonLayout.setComponentAlignment(confirmButton, Alignment.TOP_LEFT);
		
		HorizontalLayout btnWrapperLayout = new HorizontalLayout();
		btnWrapperLayout.setWidth("100%");
		mainPanel.addComponent(btnWrapperLayout);
		btnWrapperLayout.addComponent(buttonLayout);
		btnWrapperLayout.setComponentAlignment(buttonLayout, Alignment.TOP_RIGHT);
		
		mainLayout.addComponent(mainPanel);
		mainLayout.setComponentAlignment(mainPanel, Alignment.TOP_CENTER);
	}

	private void setupPopupWindows() {
		buildPopupWindow();
		
		//setup first window
		rankChangePopupWindow = new PopupWindow("Välj ny rang");
		
		final IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(NAME_PROPERTY_ID, String.class, "");
        container.addContainerProperty(RANK_PROPERTY_ID, Rank.Title.class, Rank.Title.SEARCHER);
        Title[] values = Rank.Title.values();
        for (Title title : values) {
			Item item = container.getItem(container.addItem());
			if (item != null) {
				item.getItemProperty(NAME_PROPERTY_ID).setValue(Rank.getNameByRank(title));
				item.getItemProperty(RANK_PROPERTY_ID).setValue(title);
			} else {
				System.err.println("Error: item is null");
			}
		}
        final ComboBox rankBox = new ComboBox(null, container);
        rankBox.setItemCaptionPropertyId(NAME_PROPERTY_ID);
        rankBox.setTextInputAllowed(false);
        rankBox.setNullSelectionAllowed(false);
        
        ClickListener clickListener = new ClickListener() {
        	@Override
        	public void buttonClick(ClickEvent event) {
        		Object rankItemId = rankBox.getValue();
        		Object treeItemId = rankChangePopupWindow.getData();
				Item treeItem = groupTree.getItem(treeItemId);
				Item rankBoxItem = container.getItem(rankItemId);
				if (treeItem != null && rankBoxItem != null) {
					Rank.Title rankValue = (Title) rankBoxItem.getItemProperty(RANK_PROPERTY_ID).getValue();
					treeItem.getItemProperty(RANK_PROPERTY_ID).setValue(rankValue);
					String newName = treeItem.getItemProperty(REALNAME_PROPERTY_ID).getValue().toString();
					newName = newName + " (" + Rank.getNameByRank(rankValue) + ")";
					treeItem.getItemProperty(NAME_PROPERTY_ID).setValue(newName);
				} else {
					System.err.println("Error: " + (treeItem == null ? "treeItem is null" : "") + 
							" " + (rankBoxItem == null ? "rankBoxItem is null" : ""));
				}
        		GroupEditView.this.getWindow().removeWindow(rankChangePopupWindow);
        	}
        };
        
		rankChangePopupWindow.init(rankBox, clickListener);
	}

	private void setupContextMenu() {
		final Action SET_RANK = new Action("Ändra rang på sökare");
		
		final Action[] itemMenu = new Action[]{SET_RANK};
		
		groupTree.addActionHandler(new Action.Handler() {
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action == SET_RANK) {
					changeSearcherRank(target);
				}
			}
			
			@Override
			public Action[] getActions(Object target, Object sender) {
				if (groupTree.getItem(target) != null) {
					return itemMenu;
				}
				return null;
			}
		});
	}

	private void changeSearcherRank(Object itemId) {
		if (itemId != null) {
			rankChangePopupWindow.setData(itemId);
			getWindow().addWindow(rankChangePopupWindow);
		}
	}

	private class TreeDragNDropHandler implements DropHandler {
		private final Tree tree;
		private final Table table;

		public TreeDragNDropHandler(Tree tree, Table table) {
			this.tree = tree;
			this.table = table;
		}

		public void drop(DragAndDropEvent dropEvent) {
            Transferable t = dropEvent.getTransferable();
            
            Component sourceComponent = t.getSourceComponent();
			if ((sourceComponent != tree && sourceComponent != table)
                    || !(t instanceof DataBoundTransferable)) {
                return;
            }
			
			if (sourceComponent == tree) { //drop from tree to tree
	            TargetDetails targetDetails = dropEvent.getTargetDetails();
				TreeTargetDetails dropData = ((TreeTargetDetails) targetDetails);
	            
	            Object sourceItemId = ((DataBoundTransferable) t).getItemId();
	            Object targetItemId = dropData.getItemIdOver();
	            
	            VerticalDropLocation location = dropData.getDropLocation();
	            
	            moveNode(sourceItemId, targetItemId, location);
			} else if (sourceComponent == table) { //drop from table to tree
				DataBoundTransferable t2 = (DataBoundTransferable) dropEvent
                        .getTransferable();
                Container sourceContainer = t2.getSourceContainer();
                
                //check for lone placeholder node, remove if found
                if (tree.getContainerDataSource().getItemIds() != null 
                		&& tree.getContainerDataSource().getItemIds().size() == 1) {
                	Object[] array = tree.getItemIds().toArray();
                	if (array[0].toString().equals("-1")) {
                		//if itemid is -1, the node is the placeholder
                		tree.removeAllItems();
                		//tree.setParent(null, null); //may be unnecessary
                	}
                }
                
                Object sourceItemId = t2.getItemId();
                String name = null;
                String id = null;
                if (sourceContainer != null && sourceItemId != null) {
	                Item sourceItem = sourceContainer.getItem(sourceItemId);
					name = sourceItem.getItemProperty(NAME_PROPERTY_ID).toString();
					id = sourceItem.getItemProperty(SID_PROPERTY_ID).getValue().toString();
                } else {
                	listener.displayError("Sökarförflyttningsfel", 
                			"Ett fel uppstod vid förflyttning av sökaren.");
                }
                
                AbstractSelectTargetDetails dropData = ((AbstractSelectTargetDetails) dropEvent
                        .getTargetDetails());
                VerticalDropLocation location = dropData.getDropLocation();
                Object targetItemId = dropData.getItemIdOver();
                
                //add item to tree, get itemid
                Object newItemId = tree.addItem();
                Item item = tree.getItem(newItemId);
                if (item != null) {
	                item.getItemProperty(SID_PROPERTY_ID).setValue(id);
	                item.getItemProperty(REALNAME_PROPERTY_ID).setValue(name);
	                item.getItemProperty(NAME_PROPERTY_ID).setValue(name + 
	                		" (" + Rank.getNameByRank(Rank.Title.SEARCHER) + ")");
	                item.getItemProperty(RANK_PROPERTY_ID).setValue(Rank.Title.SEARCHER);
                } else {
                	listener.displayError("Fel", "Sökaren kunde ej läggas till i gruppen");
                	return;
                }
                
                //add item to tree, get dropLocation and targetItemId, contact moveNode 
                if (name != null && id != null) {
                	moveNode(newItemId, targetItemId, location);
                }
                
                tree.setChildrenAllowed(newItemId, true);
                tree.expandItem(targetItemId);
                sourceContainer.removeItem(sourceItemId);
                
                //if the last node was removed from the tree, add placeholder
                addPlaceholderNodeToTree();
			}
		}

		private void moveNode(Object sourceItemId, Object targetItemId,
				VerticalDropLocation location) {
			HierarchicalContainer container = (HierarchicalContainer) tree
                    .getContainerDataSource();

            if (location == VerticalDropLocation.MIDDLE) {
                if (container.setParent(sourceItemId, targetItemId)
                        && container.hasChildren(targetItemId)) {
                    container.moveAfterSibling(sourceItemId, null);
                    tree.expandItem(targetItemId);
                }
            } else if (location == VerticalDropLocation.TOP) {
                Object parentId = container.getParent(targetItemId);
                if (container.setParent(sourceItemId, parentId)) {
                    container.moveAfterSibling(sourceItemId, targetItemId);
                    container.moveAfterSibling(targetItemId, sourceItemId);
                }
            } else if (location == VerticalDropLocation.BOTTOM) {
                Object parentId = container.getParent(targetItemId);
                if (container.setParent(sourceItemId, parentId)) {
                    container.moveAfterSibling(sourceItemId, targetItemId);
                    tree.expandItem(parentId);
                }
            } else if (location == null) {
            	container.setParent(sourceItemId, null);
            }
		}

		public AcceptCriterion getAcceptCriterion() {
			return AcceptAll.get();
		}
	}
	
	private class TableDragNDropHandler implements DropHandler {
		private final Tree tree;
		private final Table table;

		public TableDragNDropHandler(Tree tree, Table table) {
			this.tree = tree;
			this.table = table;
		}

		@Override
		public void drop(DragAndDropEvent dropEvent) {
			DataBoundTransferable t = (DataBoundTransferable) dropEvent.getTransferable();

            Component sourceComponent = t.getSourceComponent();
			if (sourceComponent != tree
                    || !(t instanceof Transferable)) {
                return;
            } else if (sourceComponent == table) {
            	//intra-table drops are not important
            	return;
            }
			
			if (sourceComponent == tree) {
				Object sourceItemId = t.getItemId();
				
				Item oldItem = tree.getItem(sourceItemId);
				
				Object targetItemId = table.addItem();
				Item item = table.getItem(targetItemId);
				if (item != null) {
					item.getItemProperty(NAME_PROPERTY_ID).setValue(
							oldItem.getItemProperty(REALNAME_PROPERTY_ID).getValue());
					item.getItemProperty(SID_PROPERTY_ID).setValue(
							oldItem.getItemProperty(SID_PROPERTY_ID).getValue());
					
					tree.removeItem(sourceItemId);
					
					table.refreshRowCache();
					
					addPlaceholderNodeToTree();
				} else {
					listener.displayError("Fel", "Sökaren kunde ej läggas till i tabellen");
				}
			}
		}

		@Override
		public AcceptCriterion getAcceptCriterion() {
			return AcceptAll.get();
		}
	}
	
	private void saveGroup() {
		try {
			if (nameField.isValid() && validateGroupHierarchy()) {
				SearchGroup group = null;
				group = convertTreeToGroup();
				group.setName(nameField.getValue().toString());
				group.setId(groupId);
				service.addOrModifySearchGroup(group, groupId, opId);
				
				getWindow().addWindow(successDialog);
			}
		} catch (SearchGroupValidationException e) {
			listener.displayError("Sökgruppsvalideringsfel", e.getMessage());
			e.printStackTrace();
		} catch (IllegalStateException e) {
			listener.displayError("Fel vid trädkonvertering", e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			listener.displayError("Fel vid sparning", 
				"Ett fel uppstod vid kommunikation med servern.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Validates the structure of the group's hierarchy based on a set of rules.
	 * @return true if the hierarchy is valid, else false.
	 * @throws SearchGroupValidationException 
	 */
	private boolean validateGroupHierarchy() throws SearchGroupValidationException {
		Collection<?> itemIds = groupTree.getItemIds();
		if (itemIds.isEmpty()) {
			return true;
		}
		
		//do these rules need to be configurable?
		boolean rootFound = false;
		int noOfGroupLeaders = 0;
		int noOfPatrolLeaders = 0;
		for (Object itemId : itemIds) {
			Item item = groupTree.getItem(itemId);
			Object parent = groupTree.getParent(itemId);
			if (parent == null) {
				if (rootFound == true) {
					throw new SearchGroupValidationException(
							"Gruppen får endast ha ett högsta befäl.");
				}
				rootFound = true;
			}
			
			Rank.Title rank = (Title) item.getItemProperty("rank").getValue();
			if (rank == Title.SEARCHER) {
				if (groupTree.hasChildren(itemId)) {
					throw new SearchGroupValidationException(
							"Sökare får ej ha underordnade.");
				}
			} else if (rank == Title.ASSISTANT_PATROL_LEADER) {
				if (groupTree.hasChildren(itemId)) {
					Collection<?> children = groupTree.getChildren(itemId);
					if (children.size() > 4) {
						throw new SearchGroupValidationException(
								"En eller flera patrulledarassistener har " +
							"5 eller fler sökare, max är 4.");
					}
				}
			} else if (rank == Title.GROUP_LEADER) {
				noOfGroupLeaders++;
			} else if (rank == Title.PATROL_LEADER) {
				noOfPatrolLeaders++;
			}
		}
		
		if (noOfGroupLeaders > 15) {
			throw new SearchGroupValidationException(
					"Max 15 gruppledare tillåtna");
		}
		if (noOfPatrolLeaders > 15) {
			throw new SearchGroupValidationException(
					"Max 15 patrullledare tillåtna");
		}
		
		//if the group structure is valid, accept the commit
		return true;
	}

	private SearchGroup convertTreeToGroup() {
		//initialize group
		SearchGroup group = new SearchGroup();
		
		//find id of the root
		Collection<?> itemIds = groupTree.getItemIds();
		Object rootItemId = CollectionUtils.find(itemIds, new Predicate<Object>() {
			@Override
			public boolean evaluate(Object itemId) {
				if (groupTree.getParent(itemId) == null) {
					return true;
				}
				return false;
			}
		});
		if (rootItemId == null) {
			//throw new IllegalStateException("Ingen rot funnen i trädet");
			return group;
		}
		
		//traverse tree and convert to GroupNodes
		Item rootItem = groupTree.getItem(rootItemId);
		GroupNode rootNode = new GroupNode(
				rootItem.getItemProperty(SID_PROPERTY_ID).getValue().toString(), 
				(Title) rootItem.getItemProperty(RANK_PROPERTY_ID).getValue(), 
				null);
		group.setTreeRoot(rootNode);
		if (groupTree.hasChildren(rootItemId)) {
			addNodeToGroup(groupTree.getChildren(rootItemId), rootNode);
		}
		
		return group;
	}

	/**
	 * Recursive method to iterate through the searcher tree in the 
	 * UI and convert it to GroupNode's for storing in the database.
	 * @param itemId 
	 * @param childIds 
	 * @param parentNode 
	 */
	private void addNodeToGroup(Collection<?> childIds, GroupNode parentNode) {
		for (Object childItemId : childIds) {
			Item item = groupTree.getItem(childItemId);
			String id = (String) item.getItemProperty(SID_PROPERTY_ID).getValue();
			Rank.Title rank = (Title) item.getItemProperty(RANK_PROPERTY_ID).getValue();
			GroupNode newNode = new GroupNode(id, rank, parentNode);
			parentNode.addChild(newNode);
			if (groupTree.hasChildren(childItemId)) {
				addNodeToGroup(groupTree.getChildren(childItemId), newNode);
			}
		}
	}

	public void resetView(String groupId, String opId) {
		this.groupId = groupId;
		this.opId = opId;
		
		SearchGroup group = null;
		Map<String, String> searcherMap = null;
		
		if (groupId == null) {
			headerLabel.setValue("<h1><b>" + "Ny grupp" + "</b></h1>");
			
			if (nameField.getValue() != null) {
				nameField.setValue("");
			}
		} else {
			headerLabel.setValue("<h1><b>" + "Redigera grupp" + "</b></h1>");
			
			try { //get group data from db
				group = service.getSearchGroup(groupId);
				nameField.setValue(group.getName());
			} catch (SearchGroupNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				listener.displayError("Fel", "Gruppdatat kunde ej hämtas från servern");
				return;
			}
		}
		
		try {
			searcherMap = service.getSearchersByOp(opId);
		} catch (SearchOperationNotFoundException e1) {
			e1.printStackTrace();
			listener.displayError("Fel", "Sökoperationen ej funnen");
			return;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//convert to HierarchicContainer format
		HierarchicalContainer container = new HierarchicalContainer();
		container.removeAllItems();
		
		container.addContainerProperty(NAME_PROPERTY_ID, String.class, "");
		container.addContainerProperty(SID_PROPERTY_ID, String.class, "");
		container.addContainerProperty(RANK_PROPERTY_ID, Rank.Title.class, Rank.Title.SEARCHER);
		container.addContainerProperty(REALNAME_PROPERTY_ID, String.class, "");
		
		//refresh tree data source
		groupTree.setContainerDataSource(container);
		groupTree.setItemCaptionPropertyId(NAME_PROPERTY_ID);
		
		if (group.getTreeRoot() == null) {
			addPlaceholderNodeToTree();
		} else {
			addNodesToTree(null, group.getTreeRoot(), container, searcherMap);
		}
		
		IndexedContainer container2 = new IndexedContainer();
		container2.addContainerProperty(SID_PROPERTY_ID, String.class, "");
		container2.addContainerProperty(NAME_PROPERTY_ID, String.class, "");
		
		Collection<Entry<String, String>> searchersNotAlreadyInTree = select(searcherMap.entrySet(), 
				notIncluded(collect(groupTree.getItemIds(), asUserIds())));
		if (searchersNotAlreadyInTree != null) {
			for (Entry<String,String> entry : searchersNotAlreadyInTree){
				try {
					Item item = container2.getItem(container2.addItem());
					item.getItemProperty(SID_PROPERTY_ID).setValue(entry.getKey());
					item.getItemProperty(NAME_PROPERTY_ID).setValue(entry.getValue());
				} catch (Exception e) {
					e.printStackTrace();
					listener.displayError("Fel", 
						"Ett fel uppstod vid skapandet av sökartabellen");
					break;
				}
			}
		}
		
		searcherTable.setContainerDataSource(container2);
		searcherTable.setVisibleColumns(new Object[]{NAME_PROPERTY_ID});
		searcherTable.setColumnHeaders(new String[]{"Lediga sökare"});
		
//		insertDemoData(searcherMap);
	}

	/**
	 * Inserts dummy data into searcherTable for debug/demo purposes.
	 * @param searcherMap
	 */
	private void insertDemoData(Map<String, String> searcherMap) {
		Container dataSource = searcherTable.getContainerDataSource();
//		for (int i = 0; i < 20; i++) {
//			Item item = dataSource.getItem(dataSource.addItem());
//			String searcherId = "" + r.nextLong();
//			searcherMap.put(searcherId, "Sökare " + r.nextInt());
//			GroupNode node = new GroupNode(searcherId, null, null);
//			setupItemProperties(node, item, searcherMap);
//		}
		
		try {
			Map<String, String> volunteersByOp = service.getVolunteersByOp("");
			Iterator<String> it = volunteersByOp.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = volunteersByOp.get(key);
				Item item = dataSource.getItem(dataSource.addItem());
				searcherMap.put(key, value);
				GroupNode node = new GroupNode(key, null, null);
				setupItemProperties(node, item, searcherMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a list of itemIds from the table that 
	 * does not include the itemIds found in the tree.
	 * @param ids the list to look for the entry in.
	 * @return true if the entry was not found in the list of ids.
	 */
	private Predicate<Entry<String, String>> notIncluded(
			final Collection<String> ids) {
		return new Predicate<Entry<String,String>>() {
			@Override
			public boolean evaluate(Entry<String,String> entry) {
				return !ids.contains(entry.getKey());
			}
		};
	}

	/**
	 * Transforms the tree's Searcher IDs from Objects to Strings.
	 */
	private Transformer<Object, String> asUserIds() {
		return new Transformer<Object, String>(){
			@Override
			public String transform(Object o) {
				return (String) groupTree.getItem(o).getItemProperty(SID_PROPERTY_ID).getValue();
			}
		};
	}
	
	private void addPlaceholderNodeToTree() {
		if (groupTree.getItemIds() == null 
				|| groupTree.getItemIds().size() == 0) {
			Item placeholderItem = groupTree.addItem("-1");
			placeholderItem.getItemProperty(SID_PROPERTY_ID).setValue("-1");
			placeholderItem.getItemProperty(REALNAME_PROPERTY_ID).setValue("PLACEHOLDER");
			placeholderItem.getItemProperty(NAME_PROPERTY_ID).setValue(
				"Drag sökare från tabellen och släpp dem här för att skapa gruppen");
			placeholderItem.getItemProperty(RANK_PROPERTY_ID).setValue(Rank.Title.SEARCHER);
		}
	}

	/**
	 * Traverses the GroupNode and it's children (depth first) in order 
	 * to add them to the tree. The itemId of the first node is returned.
	 * @param itemId the item id of the current node's parent.
	 * @param children the children of the current node, if applicable.
	 * @param container the container the node will be added to.
	 * @param searcherMap the map containing key/value pairs of searchers' ids and names.
	 */
	private void addChildrenToTree(Object parentItemId, List<GroupNode> children, 
			HierarchicalContainer container, Map<String, String> searcherMap) {
		Object itemId = null;
		for (int i = 0; i < children.size(); i++) {
			GroupNode node = children.get(i);
			itemId = container.addItem();
			Item item = container.getItem(itemId);
			if (!node.isLeaf()) {
				addChildrenToTree(itemId, node.getChildren(), 
						container, searcherMap);
			}
			setupItemProperties(node, item, searcherMap);
			container.setParent(itemId, parentItemId);
		}
	}
	
	private void addNodesToTree(Object parentItemId, GroupNode currentNode,
			final HierarchicalContainer container, final Map<String, String> searcherMap) {
		Object currentItemId = container.addItem();
		Item currentItem = container.getItem(currentItemId);
		setupItemProperties(currentNode, currentItem, searcherMap);
		container.setParent(currentItemId, parentItemId);
		for (GroupNode childNode : currentNode.getChildren()) {
			addNodesToTree(currentItemId, childNode, container, searcherMap);
		}
	}
	
	private void setupItemProperties(GroupNode node, Item item, 
			Map<String, String> searcherMap) {
		String searcherId = node.getSearcherId();
		String searcherName = searcherMap.get(searcherId);
		if (searcherName == null && LookingForApp.debugMode) { //
			searcherName = "Sökare " + new Random().nextInt(Integer.MAX_VALUE-1);
		}
		Collection<?> itemPropertyIds = item.getItemPropertyIds();
		for (Object itemPropId : itemPropertyIds) {
			String idString = (String) itemPropId;
			if (NAME_PROPERTY_ID.equals(idString)) {
				String rankString = "";
				if (node.getRank() != null) {
					rankString = " (" + Rank.getNameByRank(node.getRank()) + ")";
				}
				item.getItemProperty(NAME_PROPERTY_ID).setValue(searcherName + rankString);
			} else if (RANK_PROPERTY_ID.equals(idString)) {
				item.getItemProperty(RANK_PROPERTY_ID).setValue(node.getRank());
			} else if (SID_PROPERTY_ID.equals(idString)) {
				item.getItemProperty(SID_PROPERTY_ID).setValue(searcherId);
			} else if (REALNAME_PROPERTY_ID.equals(idString)) {
				item.getItemProperty(REALNAME_PROPERTY_ID).setValue(searcherName);
			}
		}
	}

	private class PopupWindow extends Window {	
		
		protected PopupWindow(String caption) {
			this.setCaption(caption);
		}

		protected void init(Component component, ClickListener listener) {
			setWidth("250px");
			setHeight("100px");
			setModal(true);
			center();
			
			VerticalLayout layout = (VerticalLayout) this.getContent();
	        layout.setMargin(true);
	        layout.setSpacing(true);
	        
	        HorizontalLayout buttonLayout = new HorizontalLayout();
	        Button closePopupButton = new Button("Avbryt");
	        buttonLayout.setWidth("100%");
	        buttonLayout.addComponent(closePopupButton);
	        buttonLayout.setComponentAlignment(closePopupButton, Alignment.BOTTOM_RIGHT);
	        
	        Button okButton = new Button("OK");
	        buttonLayout.addComponent(okButton);
	        buttonLayout.setComponentAlignment(okButton, Alignment.BOTTOM_RIGHT);
	        
	        layout.addComponent(component);
	        
	        okButton.addListener(listener);
	        
	        closePopupButton.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					GroupEditView.this.getWindow().removeWindow(PopupWindow.this);
				}
			});
	        
			addListener(new Window.CloseListener() {
	            public void windowClose(CloseEvent e) {
	            	GroupEditView.this.getWindow().removeWindow(PopupWindow.this);
	            }
	        });
	        
			layout.addComponent(buttonLayout);
		}
	}
	
	private void buildPopupWindow() {
		successDialog = new Window("Meddelande");
		successDialog.setModal(true);
		successDialog.center();
		
		VerticalLayout layout = (VerticalLayout) successDialog.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        
        Label popupMessage = new Label("Sökgruppen sparad");
        successDialog.addComponent(popupMessage);
        
        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button closePopupButton = new Button("Tillbaka");
        buttonLayout.addComponent(closePopupButton);
        buttonLayout.setWidth("100%");
        buttonLayout.setComponentAlignment(closePopupButton, Alignment.BOTTOM_CENTER);
        
        closePopupButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				(successDialog.getParent()).removeWindow(successDialog);
				listener.switchToSearchMissionListView();
			}
		});
        
		successDialog.addListener(new Window.CloseListener() {
            public void windowClose(CloseEvent e) {
            	listener.switchToSearchMissionListView();
            }
        });
        
        successDialog.addComponent(buttonLayout);
	}
}
