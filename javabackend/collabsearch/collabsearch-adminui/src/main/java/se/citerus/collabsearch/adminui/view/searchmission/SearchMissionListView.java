package se.citerus.collabsearch.adminui.view.searchmission;

import java.util.List;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.adminui.logic.SearchMission;
import se.citerus.collabsearch.adminui.logic.SearchMissionService;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SearchMissionListView extends CustomComponent {
	
	private Button endMissionButton;
	private Button editButton;
	private Button addButton;
	private Table table;
	private Button homeButton;
	private BeanContainer<String, SearchMission> beans;
	
	public SearchMissionListView(final ViewSwitchController listener) {
		VerticalLayout mainLayout = buildMainLayout();
		setCompositionRoot(mainLayout);
		listener.setMainWindowCaption("Missing People - Sökuppdrag");
		
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
				String missionTitle = (String) table.getValue();
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
				//TODO should this be change status or remove?
				SearchMissionService handler = null;
				String itemId = (String) table.getValue();
				if (itemId != null) {
					try {
						handler = new SearchMissionService();
						handler.endMission(itemId);
						//table.removeItem(itemId);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (handler != null) {
							handler.cleanUp();
						}
					}
				} else {
					listener.displayNotification("Inget sökuppdrag markerat", 
							"Du måste markera ett sökuppdrag för avslutning");
				}
			}
		});
		
		populateTable();
	}

	private void populateTable() {
		beans = new BeanContainer<String, SearchMission>(SearchMission.class);
		beans.setBeanIdProperty("name");
		
		SearchMissionService handler = new SearchMissionService();
		try {
			List<SearchMission> list = handler.getListOfSearchMissions();
			if (list != null) {
				beans.addAll(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			handler.cleanUp();
		}
		
		table.setContainerDataSource(beans);
		table.setVisibleColumns(new Object[]{"name","description","status"});
		table.setColumnHeaders(new String[]{"Namn","Beskrivning","Status"});
	}

	private VerticalLayout buildMainLayout() {
		VerticalLayout mainLayout = new VerticalLayout();
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
		
		table = new Table();
		table.setSelectable(true);
		table.setWidth("100%");
		innerLayout.addComponent(table);
		
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
		
		return mainLayout;
	}

	public void resetView() {
		beans.removeAllItems();
		
		SearchMissionService handler = new SearchMissionService();
		try {
			List<SearchMission> list = handler.getListOfSearchMissions();
			if (list != null) {
				beans.addAll(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			handler.cleanUp();
		}
	}
	
}
