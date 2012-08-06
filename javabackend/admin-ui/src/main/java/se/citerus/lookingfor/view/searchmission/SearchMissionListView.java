package se.citerus.lookingfor.view.searchmission;

import java.util.List;

import se.citerus.lookingfor.ViewSwitchController;
import se.citerus.lookingfor.logic.SearchMission;
import se.citerus.lookingfor.logic.SearchMissionHandler;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class SearchMissionListView extends CustomComponent {
	
	private Button endMissionButton;
	private Button editButton;
	private Button addButton;
	private Table table;
	private Button homeButton;
	
	private String selectedRow;
	
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
				listener.switchToSearchMissionEditView(selectedRow);
			}
		});
		endMissionButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				//TODO should this be change status or remove?
			}
		});
		
		table.addListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				selectedRow = (String) event.getProperty().getValue();
			}
		});
		
		populateTable();
	}

	private void populateTable() {
		SearchMissionHandler handler = new SearchMissionHandler();
		final List<SearchMission> list = handler.getListOfSearchMissions();
		handler.cleanUp();
		
		BeanContainer<String, SearchMission> beans = new BeanContainer<String, SearchMission>(SearchMission.class);
		beans.setBeanIdProperty("name");
		
		beans.addAll(list);
		table.setContainerDataSource(beans);
		table.setVisibleColumns(new Object[]{"name","description","humanReadableStatus"});
		table.setColumnHeaders(new String[]{"Namn","Beskrivning","Status"});
	}

	private VerticalLayout buildMainLayout() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);

		homeButton = new Button("Tillbaka");
		mainLayout.addComponent(homeButton);
		
		table = new Table("Sökuppdrag");
		table.setSelectable(true);
		mainLayout.addComponent(table);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		
		addButton = new Button("Lägg till");
		buttonLayout.addComponent(addButton);
		
		editButton = new Button("Redigera");
		buttonLayout.addComponent(editButton);
		
		endMissionButton = new Button("Avsluta");
		buttonLayout.addComponent(endMissionButton);
		
		mainLayout.addComponent(buttonLayout);
		return mainLayout;
	}
	
}
