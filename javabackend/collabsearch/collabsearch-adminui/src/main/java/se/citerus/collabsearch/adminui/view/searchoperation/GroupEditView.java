package se.citerus.collabsearch.adminui.view.searchoperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.table.TableStringConverter;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.model.GroupNode;
import se.citerus.collabsearch.model.Rank;
import se.citerus.collabsearch.model.Rank.Title;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearcherInfo;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDropCriterion;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class GroupEditView extends CustomComponent {

	private VerticalLayout mainLayout;
	private final ViewSwitchController listener;
	private Label headerLabel;
	private Tree groupTree;
	private PopupWindow rankChangePopupWindow;

	private String groupId;
	private String opId;
	private Table searcherTable;

	public GroupEditView(ViewSwitchController listener) {
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
		this.listener = listener;
	}

	public void init() {
		buildMainLayout();
	}

	private void buildMainLayout() {
		mainLayout.setWidth("50%");
		mainLayout.setMargin(false, false, false, true);
		mainLayout.setSpacing(true);
		
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		
		Button backButton = new Button("Tillbaka");
		backButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionListView();
			}
		});
		topLayout.addComponent(backButton);
		topLayout.setComponentAlignment(backButton, Alignment.MIDDLE_LEFT);
		
		headerLabel = new Label("<h1><b>" + "Redigera grupp" + "</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		topLayout.addComponent(headerLabel);
		
		mainLayout.addComponent(topLayout);
		
		Panel treePanel = new Panel();
		
		searcherTable = new Table();
		searcherTable.setWidth("100%");
		searcherTable.setSelectable(true);
		searcherTable.setDragMode(Table.TableDragMode.ROW);
		searcherTable.setDropHandler(new TableDragNDropHandler(groupTree, searcherTable));
		
		groupTree = new Tree();
		groupTree.setWidth("100%");
		groupTree.setSelectable(true);
		groupTree.setMultiSelect(false);
		
		groupTree.addContainerProperty("name", String.class, "");
		groupTree.addContainerProperty("id", String.class, "");
		groupTree.addContainerProperty("rank", Rank.Title.class, Rank.Title.SEARCHER);
		
		// Allow all nodes to have child nodes
		for (Object itemId : groupTree.getItemIds()) {
			groupTree.setChildrenAllowed(itemId, true);
		}
		
		// Expand all nodes
		for (Iterator<?> it = groupTree.rootItemIds().iterator(); it.hasNext();) {
			groupTree.expandItemsRecursively(it.next());
		}
		groupTree.setDragMode(TreeDragMode.NODE);
		groupTree.setDropHandler(new TreeDragNDropHandler(groupTree, searcherTable));
				
		treePanel.addComponent(groupTree);
		mainLayout.addComponent(treePanel);
		
		mainLayout.addComponent(searcherTable);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		mainLayout.addComponent(buttonLayout);
		mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
		
		Button cancelButton = new Button("Avbryta");
		cancelButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionListView();
			}
		});
		buttonLayout.addComponent(cancelButton);
		
		Button confirmButton = new Button("Spara");
		confirmButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				//TODO implement service/DAO methods
			}
		});
		buttonLayout.addComponent(confirmButton);
		
		//debug button, remove later
		Button newSearcherButton = new Button("(debug) Ny sökare");
		newSearcherButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Container container = searcherTable.getContainerDataSource();
				Item item = container.getItem(container.addItem());
				Random r = new Random();
				SearcherInfo searcher = new SearcherInfo(
						"" + r.nextLong(), "Person " + r.nextInt(), "wad@dwa.awd", "123213");
				GroupNode node = new GroupNode(searcher, null, null);
				setupItemProperties(node, item);
			}
		});
		mainLayout.addComponent(newSearcherButton);
		//end of debug button
		
		setupContextMenu();
		setupPopupWindows();
	}

	private void setupPopupWindows() {
		//setup first window
		rankChangePopupWindow = new PopupWindow("Välj ny rang");
		
		final IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("name", String.class, "");
        container.addContainerProperty("rank", Rank.Title.class, Rank.Title.SEARCHER);
        Title[] values = Rank.Title.values();
        for (Title title : values) {
			Item item = container.getItem(container.addItem());
			if (item != null) {
				item.getItemProperty("name").setValue(Rank.getRankName(title));
				item.getItemProperty("rank").setValue(title);
			} else {
				System.err.println("Error: item is null");
			}
		}
        final ComboBox rankBox = new ComboBox(null, container);
        rankBox.setItemCaptionPropertyId("name");
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
					Rank.Title rankValue = (Title) rankBoxItem.getItemProperty("rank").getValue();
					treeItem.getItemProperty("rank").setValue(rankValue);
					String newName = treeItem.getItemProperty("realname").getValue().toString();
					newName = newName + " (" + Rank.getRankName(rankValue) + ")";
					treeItem.getItemProperty("name").setValue(newName);
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
		final Action REMOVE_SEARCHER = new Action("Ta bort sökare");
		final Action SET_RANK = new Action("Ändra rang på sökare");
		
		final Action[] itemMenu = new Action[]{SET_RANK, REMOVE_SEARCHER};
		
		groupTree.addActionHandler(new Action.Handler() {
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action == REMOVE_SEARCHER) {
					removeSearcherFromTable(target);
				} else if (action == SET_RANK) {
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

	private void removeSearcherFromTable(Object itemId) {
		//TODO what should be done with child nodes? Let them be moved to root level (default) or remove from tree?
		groupTree.getContainerDataSource().removeItem(itemId);
	}

	private void addSearcherToTable(Object itemId) {
		SearchMissionService service = null;
		List<SearcherInfo> list = null;
		try {
//			service = new SearchMissionService();
//			list = service.getListOfSearchers(opId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (service != null) {
				service.cleanUp();
			}
		}
		
		//TODO choose one of the searchers to add to the table
//		chooseNewSearcherPopupWindow.setData(list);
//		getWindow().addWindow(chooseNewSearcherPopupWindow);
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
	            //check target type (tree or table)
				TreeTargetDetails dropData = ((TreeTargetDetails) targetDetails);
	            
	            Object sourceItemId = ((DataBoundTransferable) t).getItemId();
	            Object targetItemId = dropData.getItemIdOver();
	            
	            VerticalDropLocation location = dropData.getDropLocation();
	            
	            moveNode(sourceItemId, targetItemId, location);
			} else if (sourceComponent == table) { //drop from table to tree
				DataBoundTransferable t2 = (DataBoundTransferable) dropEvent
                        .getTransferable();
                Container sourceContainer = t2.getSourceContainer();
                Object sourceItemId = t2.getItemId();
                Item sourceItem = sourceContainer.getItem(sourceItemId);
                String name = sourceItem.getItemProperty("name").toString();
                String id = sourceItem.getItemProperty("id").getValue().toString();

                AbstractSelectTargetDetails dropData = ((AbstractSelectTargetDetails) dropEvent
                        .getTargetDetails());
                Object targetItemId = dropData.getItemIdOver();

                if (targetItemId != null && name != null && id != null) {
                	Object newItemId = tree.addItem();
                    Item item = tree.getItem(newItemId);
                    item.getItemProperty("id").setValue(id);
					item.getItemProperty("realname").setValue(name);
					item.getItemProperty("name").setValue(name + 
							" (" + Rank.getRankName(Rank.Title.SEARCHER) + ")");
					item.getItemProperty("rank").setValue(Rank.Title.SEARCHER);
                    tree.setParent(newItemId, targetItemId);
                    tree.setChildrenAllowed(newItemId, true);
                    
                    tree.expandItem(targetItemId);

                    sourceContainer.removeItem(sourceItemId);
                }
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
		public void drop(DragAndDropEvent event) {
		}

		@Override
		public AcceptCriterion getAcceptCriterion() {
			return AcceptAll.get();
		}
		
	}

	public void resetView(String groupId, String opId) {
		this.groupId = groupId;
		this.opId = opId;
		
		SearchGroup group = null;
		List<SearcherInfo> searcherList = null;
		
		if (groupId == null) {
			headerLabel.setValue("<h1><b>" + "Ny grupp" + "</b></h1>");
		} else {
			headerLabel.setValue("<h1><b>" + "Redigera grupp" + "</b></h1>");
			//get group data from db
			SearchMissionService service = null;
			try {
				service = new SearchMissionService();
				group = service.getSearchGroup(groupId);
				searcherList = service.getListOfSearchers(opId);
			} catch (Exception e) {
				e.printStackTrace();
				listener.displayError("Fel", "Gruppens data kunde ej hämtas från servern");
				return;
			} finally {
				if (service != null) {
					service.cleanUp();
				}
			}
		}
		
		//convert to HierarchicContainer format
		HierarchicalContainer container = new HierarchicalContainer();
		
		container.addContainerProperty("name", String.class, "");
		container.addContainerProperty("id", String.class, "");
		container.addContainerProperty("rank", Rank.Title.class, Rank.Title.SEARCHER);
		container.addContainerProperty("realname", String.class, "");
		
		if (group != null) {
			int itemId = 0;
			Item rootItem = container.addItem(itemId);
			GroupNode rootNode = (GroupNode) group.getTreeRoot();
			setupItemProperties(rootNode, rootItem);
			
			if (!rootNode.isLeaf()) {
				itemId = addChildrenToTree(itemId, rootNode.getChildren(), container);
			}
		}
		
		//refresh tree data source
		groupTree.setContainerDataSource(container);
		groupTree.setItemCaptionPropertyId("name");
		
		IndexedContainer container2 = new IndexedContainer();
		container2.addContainerProperty("id", String.class, "");
		container2.addContainerProperty("name", String.class, "");
		
		for (SearcherInfo searcherInfo : searcherList) {
			Item item = container2.getItem(container2.addItem());
			item.getItemProperty("name").setValue(searcherInfo.getName());
			item.getItemProperty("id").setValue(searcherInfo.getId());
		}
		
		searcherTable.setContainerDataSource(container2);
		searcherTable.setVisibleColumns(new Object[]{"name"});
		searcherTable.setColumnHeaders(new String[]{"Namn"});
	}

	private void setupItemProperties(GroupNode node, Item item) {
		SearcherInfo searcher = node.getSearcher();
		Collection<?> itemPropertyIds = item.getItemPropertyIds();
		for (Object itemPropId : itemPropertyIds) {
			Property property = item.getItemProperty(itemPropId);
			String idString = (String) itemPropId;
			if ("name".equals(idString)) {
				String rankString = "";
				if (node.getRank() != null) {
					rankString = " (" + Rank.getRankName(node.getRank()) + ")";
				}
				item.getItemProperty("name").setValue(searcher.getName() + rankString);
			} else if ("rank".equals(idString)) {
				item.getItemProperty("rank").setValue(node.getRank());
			} else if ("id".equals(idString)) {
				item.getItemProperty("id").setValue(searcher.getId());
			} else if ("realname".equals(idString)) {
				item.getItemProperty("realname").setValue(searcher.getName());
			}
		}
	}

	private int addChildrenToTree(int itemId, List<GroupNode> children, HierarchicalContainer container) {
		int rootItemId = itemId;
		for (int i = 0; i < children.size(); i++) {
			itemId++;
			GroupNode node = children.get(i);
			if (!node.isLeaf()) {
				itemId = addChildrenToTree(itemId, node.getChildren(), container);
			}
			Item item = container.addItem(itemId);
			setupItemProperties(node, item);
			container.setParent(itemId, rootItemId);
		}
		return itemId;
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
	
}
