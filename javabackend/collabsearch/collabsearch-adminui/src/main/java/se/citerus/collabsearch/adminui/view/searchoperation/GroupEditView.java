package se.citerus.collabsearch.adminui.view.searchoperation;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.SearchMissionService;
import se.citerus.collabsearch.model.GroupNode;
import se.citerus.collabsearch.model.Rank;
import se.citerus.collabsearch.model.Rank.Title;
import se.citerus.collabsearch.model.SearchGroup;
import se.citerus.collabsearch.model.SearcherInfo;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class GroupEditView extends CustomComponent {

	private VerticalLayout mainLayout;
	private final ViewSwitchController listener;
	private Button backButton;
	private Label headerLabel;
	private Tree groupTree;
	private String groupId;

	public GroupEditView(ViewSwitchController listener) {
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
		this.listener = listener;
	}

	public void init() {
		buildMainLayout();
		
		backButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionListView();
			}
		});
	}

	private void buildMainLayout() {
		mainLayout.setWidth("50%");
		mainLayout.setMargin(false, false, false, true);
		
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		
		backButton = new Button("Tillbaka");
		topLayout.addComponent(backButton);
		topLayout.setComponentAlignment(backButton, Alignment.MIDDLE_LEFT);
		
		headerLabel = new Label("<h1><b>" + "Redigera grupp" + "</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		topLayout.addComponent(headerLabel);
		
		mainLayout.addComponent(topLayout);
		
		Panel treePanel = new Panel();
		
		groupTree = new Tree();
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
		groupTree.setDropHandler(new TreeDragNDropHandler(groupTree, new HierarchicalContainer()));
				
		treePanel.addComponent(groupTree);
		mainLayout.addComponent(treePanel);
		
		//XXX debug button, remove later
		Button newSearcherButton = new Button("(debug) Ny sökare");
		newSearcherButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Object itemId = groupTree.addItem();
				Item item = groupTree.getItem(itemId);
				Random r = new Random();
				SearcherInfo searcher = new SearcherInfo("" + r.nextLong(), "Person " + r.nextInt(), "wad@dwa.awd", "123213");
				GroupNode node = new GroupNode(searcher, Rank.Title.SEARCHER, null);
				setupItemProperties(node, item);
			}
		});
		mainLayout.addComponent(newSearcherButton);
		
		Button confirmButton = new Button("Spara");
		confirmButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				//TODO implement service/DAO methods
			}
		});
		mainLayout.addComponent(confirmButton);
		
		setupContextMenu();
	}

	private void setupContextMenu() {
		final Action ADD_SEARCHER = new Action("Lägg till sökare");
		final Action REMOVE_SEARCHER = new Action("Ta bort sökare");
		final Action SET_RANK = new Action("Ändra rang på sökare");
		
		final Action[] itemMenu = new Action[]{ADD_SEARCHER, REMOVE_SEARCHER, SET_RANK}; 
		final Action[] tableMenu = new Action[]{ADD_SEARCHER};
		
		groupTree.addActionHandler(new Action.Handler() {
			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action == ADD_SEARCHER) {
					addSearcherToTable(target);
				} else if (action == REMOVE_SEARCHER) {
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
				return tableMenu;
			}
		});
	}

	private void changeSearcherRank(Object itemId) {
		//TODO show popup window with ranks, return user choice
		Item item = groupTree.getItem(itemId);
		Rank.Title newRank = Rank.Title.SEARCHER;
		item.getItemProperty("rank").setValue(newRank);
	}

	private void removeSearcherFromTable(Object itemId) {
		groupTree.removeItem(itemId);
	}

	private void addSearcherToTable(Object itemId) {
		SearchMissionService service = null;
		try {
			service = new SearchMissionService();
			//List<SearcherInfo> list = service.getListOfSearchers();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (service != null) {
				service.cleanUp();
			}
		}
	}

	public class TreeDragNDropHandler implements DropHandler {
		private final Tree tree;

		public TreeDragNDropHandler(Tree tree, HierarchicalContainer container) {
			this.tree = tree;
		}

		public void drop(DragAndDropEvent dropEvent) {
            // Make sure the drag source is the same tree
            Transferable t = dropEvent.getTransferable();

            if (t.getSourceComponent() != tree
                    || !(t instanceof DataBoundTransferable)) {
                return;
            }

            TreeTargetDetails dropData = ((TreeTargetDetails) dropEvent
                    .getTargetDetails());

            Object sourceItemId = ((DataBoundTransferable) t).getItemId();
            Object targetItemId = dropData.getItemIdOver();

            // Location describes on which part of the node the drop took place
            VerticalDropLocation location = dropData.getDropLocation();

            moveNode(sourceItemId, targetItemId, location);
		}

		private void moveNode(Object sourceItemId, Object targetItemId,
				VerticalDropLocation location) {
			HierarchicalContainer container = (HierarchicalContainer) tree
                    .getContainerDataSource();

            // Sorting goes as
            // - If dropped ON a node, we append it as a child
            // - If dropped on the TOP part of a node, we move/add it before
            // the node
            // - If dropped on the BOTTOM part of a node, we move/add it
            // after the node

            if (location == VerticalDropLocation.MIDDLE) {
                if (container.setParent(sourceItemId, targetItemId)
                        && container.hasChildren(targetItemId)) {
                    // move first in the container
                    container.moveAfterSibling(sourceItemId, null);
                }
            } else if (location == VerticalDropLocation.TOP) {
                Object parentId = container.getParent(targetItemId);
                if (container.setParent(sourceItemId, parentId)) {
                    // reorder only the two items, moving source above target
                    container.moveAfterSibling(sourceItemId, targetItemId);
                    container.moveAfterSibling(targetItemId, sourceItemId);
                }
            } else if (location == VerticalDropLocation.BOTTOM) {
                Object parentId = container.getParent(targetItemId);
                if (container.setParent(sourceItemId, parentId)) {
                    container.moveAfterSibling(sourceItemId, targetItemId);
                }
            }
		}

		public AcceptCriterion getAcceptCriterion() {
			return AcceptAll.get();
		}
	}

	public void resetView(String groupId, String opId) {
		this.groupId = groupId;
		
		//get group data from db
		SearchMissionService service = null;
		HierarchicalContainer container;
		try {
			service = new SearchMissionService();
			SearchGroup group = service.getSearchGroup(groupId);
			
			//convert to HierarchicContainer format
			container = new HierarchicalContainer();
			
			container.addContainerProperty("name", String.class, "");
			container.addContainerProperty("id", String.class, "");
			container.addContainerProperty("rank", Rank.Title.class, Rank.Title.SEARCHER);
			
			int itemId = 0;
			Item rootItem = container.addItem(itemId);
			GroupNode rootNode = (GroupNode) group.getTreeRoot();
			setupItemProperties(rootNode, rootItem);
			
			if (!rootNode.isLeaf()) {
				itemId = addChildrenToTree(itemId, rootNode.getChildren(), container);
			}
		} catch (Exception e) {
			e.printStackTrace();
			listener.displayError("Fel", "Gruppens data kunde ej hämtas från servern");
			return;
		} finally {
			if (service != null) {
				service.cleanUp();
			}
		}
		
		//refresh tree data source
		groupTree.setContainerDataSource(container);
		groupTree.setItemCaptionPropertyId("name");
	}

	private void setupItemProperties(GroupNode node, Item item) {
		SearcherInfo searcher = node.getSearcher();
		item.getItemProperty("name").setValue(searcher.getName() + " (" + Rank.getRankName(node.getRank()) + ")");
		item.getItemProperty("rank").setValue(node.getRank());
		item.getItemProperty("id").setValue(searcher.getId());
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

}
