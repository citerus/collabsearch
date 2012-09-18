package se.citerus.collabsearch.adminui.view.searchoperation;

import java.util.ArrayList;
import java.util.Collection;
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
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
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
	private PopupWindow chooseNewSearcherPopupWindow;
	private PopupWindow rankChangePopupWindow;

	private String groupId;
	private String opId;

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
				SearcherInfo searcher = new SearcherInfo(
						"" + r.nextLong(), "Person " + r.nextInt(), "wad@dwa.awd", "123213");
				GroupNode node = new GroupNode(searcher, Rank.Title.SEARCHER, null);
				setupItemProperties(node, item);
			}
		});
		mainLayout.addComponent(newSearcherButton);
		//end of debug button
		
		Button confirmButton = new Button("Spara");
		confirmButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				//TODO implement service/DAO methods
			}
		});
		mainLayout.addComponent(confirmButton);
		
		setupContextMenu();
		setupPopupWindows();
	}

	private void setupPopupWindows() {
		rankChangePopupWindow = new PopupWindow("Välj ny rang");
		rankChangePopupWindow.init(null, null);
		
		chooseNewSearcherPopupWindow = new PopupWindow("Lägg till ny sökare");
		chooseNewSearcherPopupWindow.init(null, null);
//		chooseNewSearcherPopupWindow.setLayoutContents();
//		chooseNewSearcherPopupWindow.setConfirmButtonBehaviour();
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
		if (itemId != null) {
//			rankChangePopupWindow.setItemId(itemId);
			rankChangePopupWindow.setData(itemId);
			getWindow().addWindow(rankChangePopupWindow);
		}
	}

	private void removeSearcherFromTable(Object itemId) {
		//TODO what should be done with child nodes? Let them be moved to root level (default) or remove from tree?
		groupTree.removeItem(itemId);
	}

	private void addSearcherToTable(Object itemId) {
		SearchMissionService service = null;
		List<SearcherInfo> list = null;
		try {
			service = new SearchMissionService();
			list = service.getListOfSearchers(opId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (service != null) {
				service.cleanUp();
			}
		}
		
		//TODO choose one of the searchers to add to the table
		chooseNewSearcherPopupWindow.setData(list);
		getWindow().addWindow(chooseNewSearcherPopupWindow);
	}

	private class TreeDragNDropHandler implements DropHandler {
		private final Tree tree;

		public TreeDragNDropHandler(Tree tree, HierarchicalContainer container) {
			this.tree = tree;
		}

		public void drop(DragAndDropEvent dropEvent) {
            Transferable t = dropEvent.getTransferable();

            if (t.getSourceComponent() != tree
                    || !(t instanceof DataBoundTransferable)) {
                return;
            }

            TreeTargetDetails dropData = ((TreeTargetDetails) dropEvent
                    .getTargetDetails());

            Object sourceItemId = ((DataBoundTransferable) t).getItemId();
            Object targetItemId = dropData.getItemIdOver();

            VerticalDropLocation location = dropData.getDropLocation();

            moveNode(sourceItemId, targetItemId, location);
		}

		private void moveNode(Object sourceItemId, Object targetItemId,
				VerticalDropLocation location) {
			HierarchicalContainer container = (HierarchicalContainer) tree
                    .getContainerDataSource();

            if (location == VerticalDropLocation.MIDDLE) {
                if (container.setParent(sourceItemId, targetItemId)
                        && container.hasChildren(targetItemId)) {
                    container.moveAfterSibling(sourceItemId, null);
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
                }
            }
		}

		public AcceptCriterion getAcceptCriterion() {
			return AcceptAll.get();
		}
	}

	public void resetView(String groupId, String opId) {
		this.groupId = groupId;
		this.opId = opId;
		
		SearchGroup group = null;
		
		if (groupId == null) {
			headerLabel.setValue("<h1><b>" + "Ny grupp" + "</b></h1>");
		} else {
			headerLabel.setValue("<h1><b>" + "Redigera grupp" + "</b></h1>");
			//get group data from db
			SearchMissionService service = null;
			try {
				service = new SearchMissionService();
				group = service.getSearchGroup(groupId);
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

	private class PopupWindow extends Window {		
		protected PopupWindow(String caption) {
			this.setCaption(caption);
		}

		protected void init(IndexedContainer container2, ClickListener listener) {
			this.setModal(true);
			this.center();
			
			VerticalLayout layout = (VerticalLayout) this.getContent();
	        layout.setMargin(true);
	        layout.setSpacing(true);
	        
	        Label popupMessage = new Label("...");
	        layout.addComponent(popupMessage);
	        
	        HorizontalLayout buttonLayout = new HorizontalLayout();
	        Button closePopupButton = new Button("Avbryt");
	        buttonLayout.setWidth("100%");
	        buttonLayout.addComponent(closePopupButton);
	        buttonLayout.setComponentAlignment(closePopupButton, Alignment.BOTTOM_RIGHT);
	        
	        Button okButton = new Button("OK");
	        buttonLayout.addComponent(okButton);
	        buttonLayout.setComponentAlignment(okButton, Alignment.BOTTOM_RIGHT);
	        
	        //specific part #1 (edit rank)
	        IndexedContainer container = new IndexedContainer();
	        container.addContainerProperty("name", String.class, "");
	        container.addContainerProperty("rank", Rank.Title.class, Rank.Title.SEARCHER);
	        Title[] values = Rank.Title.values();
	        for (Title title : values) {
				Item item = container.getItem(container.addItem());
				item.getItemProperty("name").setValue(Rank.getRankName(title));
				item.getItemProperty("rank").setValue(title);
			}
	        final ComboBox rankBox = new ComboBox(null, container);
	        rankBox.setItemCaptionPropertyId("name");
	        rankBox.setTextInputAllowed(false);
	        layout.addComponent(rankBox);
	        
	        okButton.addListener(new ClickListener() {
	        	@Override
	        	public void buttonClick(ClickEvent event) {
	        		//specific listener
	        		String chosenRank = rankBox.getValue().toString();
	        		Object itemId2 = PopupWindow.this.getData();
					Item item = groupTree.getItem(itemId2);
	        		item.getItemProperty("rank").setValue(Rank.Title.valueOf(chosenRank));
	        		(getParent()).removeWindow(PopupWindow.this);
	        	}
	        });
	        //end of specific part
	        
	        //specific part #2 (add new searcher)
	        IndexedContainer container3 = new IndexedContainer();
	        container3.addContainerProperty("id", String.class, "");
	        container3.addContainerProperty("name", String.class, "");
	        List<SearcherInfo> list = (List<SearcherInfo>) PopupWindow.this.getData();
	        for (SearcherInfo searcherInfo : list) {
	        	Item item = container3.getItem(container3.addItem());
	        	item.getItemProperty("id").setValue(searcherInfo.getId());
	        	item.getItemProperty("name").setValue(searcherInfo.getName());
			}
	        final ComboBox userBox = new ComboBox(null, container3);
	        userBox.setItemCaptionPropertyId("name");
	        userBox.setTextInputAllowed(false);
	        layout.addComponent(userBox);
	        
	        okButton.addListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					Object itemId = userBox.getValue();
					Item item = groupTree.getItem(groupTree.addItem());
					item.getItemProperty("id").setValue("");
					item.getItemProperty("name").setValue("");
				}
			});
	        //end of specific part #2
	        
	        closePopupButton.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					(getParent()).removeWindow(PopupWindow.this);
				}
			});
	        
			this.addListener(new Window.CloseListener() {
	            public void windowClose(CloseEvent e) {
	            	(getParent()).removeWindow(PopupWindow.this);
	            }
	        });
	        
	        this.addComponent(buttonLayout);
		}
	}
	
}
