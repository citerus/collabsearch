package se.citerus.collabsearch.adminui.view.searchoperation;

import java.util.Iterator;

import se.citerus.collabsearch.adminui.ViewSwitchController;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class GroupEditView extends CustomComponent {

	private VerticalLayout mainLayout;
	private final ViewSwitchController listener;
	private Button homeButton;
	private Label headerLabel;
	private static final String[][] exampleData = {
			{ "Desktops", "Dell OptiPlex GX240", "Dell OptiPlex GX260", "Dell OptiPlex GX280" },
			{ "Monitors", "Benq T190HD", "Benq T220HD", "Benq T240HD" },
			{ "Laptops", "IBM ThinkPad T40", "IBM ThinkPad T43", "IBM ThinkPad T60" } };
	private Tree groupTree;

	public GroupEditView(ViewSwitchController listener) {
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
		this.listener = listener;
	}

	public void init() {
		buildMainLayout();
		
		homeButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				//XXX return to SearchOp view
				listener.switchToWelcomeView();
			}
		});
	}

	private void buildMainLayout() {
		mainLayout.setWidth("50%");
		mainLayout.setMargin(false, false, false, true);
		
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		
		homeButton = new Button("Tillbaka");
		topLayout.addComponent(homeButton);
		topLayout.setComponentAlignment(homeButton, Alignment.MIDDLE_LEFT);
		
		headerLabel = new Label("<h1><b>" + "Grupper i sökuppdraget" + "</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		topLayout.addComponent(headerLabel);
		
		mainLayout.addComponent(topLayout);
		
		Panel treePanel = new Panel();
		
		groupTree = new Tree();
		groupTree.setSelectable(true);
		groupTree.setMultiSelect(true);
		
		HierarchicalContainer container = getHierarchicalContainer();
		groupTree.setContainerDataSource(container);
		groupTree.setItemCaptionPropertyId("name");
		
		// Allow all nodes to have child nodes
		for (Object itemId : groupTree.getItemIds()) {
			groupTree.setChildrenAllowed(itemId, true);
		}
		
		// Expand all nodes
		for (Iterator<?> it = groupTree.rootItemIds().iterator(); it.hasNext();) {
			groupTree.expandItemsRecursively(it.next());
		}
		groupTree.setDragMode(TreeDragMode.NODE);
		groupTree.setDropHandler(new TreeDragNDropHandler(groupTree, container));
		
		treePanel.addComponent(groupTree);
		mainLayout.addComponent(treePanel);
	}

	private HierarchicalContainer getHierarchicalContainer() {
		Item item = null;
		int itemId = 0; // Increasing numbering for itemId:s
		
		// Create new container
		HierarchicalContainer container = new HierarchicalContainer();
		// Create containerproperty for name
		container.addContainerProperty("name", String.class, null);
		
		for (int i = 0; i < exampleData.length; i++) {
			// Add new item
			item = container.addItem(itemId);
			// Add name property for item
			item.getItemProperty("name").setValue(exampleData[i][0]);
			// Allow children
			container.setChildrenAllowed(itemId, true);
			itemId++;
			for (int j = 1; j < exampleData[i].length; j++) {
				// Add child items
				item = container.addItem(itemId);
				item.getItemProperty("name").setValue(exampleData[i][j]);
				container.setParent(itemId, itemId - j);
				container.setChildrenAllowed(itemId, false);
				
				itemId++;
			}
		}
		return container;
	}

	public class TreeDragNDropHandler implements DropHandler {

		public TreeDragNDropHandler(Tree tree, HierarchicalContainer container) {
		}

		public void drop(DragAndDropEvent event) {
		}

		public AcceptCriterion getAcceptCriterion() {
			return AcceptAll.get();
		}

	}

	public void resetView(String groupId, String opName, String missionName) {
		//get group data from db
		
		//convert to HierarchicContainer format
		
		//refresh tree container
		//orgTree.setContainerDataSource(newDataSource);
	}

}
