package se.citerus.collabsearch.adminui.view.searchoperation;

import java.util.List;

import org.vaadin.hezamu.googlemapwidget.GoogleMap;
import org.vaadin.hezamu.googlemapwidget.overlay.BasicMarkerSource;
import org.vaadin.hezamu.googlemapwidget.overlay.MarkerSource;

import se.citerus.collabsearch.model.SearchGroup;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import static com.vaadin.ui.Alignment.MIDDLE_LEFT;
import static com.vaadin.ui.Alignment.MIDDLE_RIGHT;
import static com.vaadin.ui.Alignment.TOP_CENTER;

/**
 * The ZoneViewFragment is used by both the NewZoneView and EditZoneView, 
 * as the two views share the same layout.
 * @author Ola Rende
 */
@SuppressWarnings("serial")
public class ZoneViewFragment extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private VerticalLayout mapLayout;
	protected TextField nameField;
	protected TextField prioField;
	protected Label headerLabel;
	protected Button saveButton;
	protected Button backButton;
	protected Button clearMapButton;
	protected Button createZoneButton;
	protected Button setMapCenterButton;
	protected ComboBox assignedGroupDropdown;
	
	private MarkerSource markerSource;

	protected ZoneViewFragment() {
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}
	
	protected void init(String headerString) {
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		mainLayout.setWidth("100%");
		
		Panel mainPanel = new Panel();
		mainPanel.setWidth("100%");
		
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		
		Embedded embImg = new Embedded("", 
			new ThemeResource("../mytheme/dual_color_extended_trans.png"));
		embImg.setStyleName("small-logo");
		headerLayout.addComponent(embImg);
		
		headerLabel = new Label("<h1><b>" + headerString + "</b></h1>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		headerLabel.setStyleName("logo-header");
		headerLayout.addComponent(headerLabel);
		
		mainPanel.addComponent(headerLayout);
		
		mapLayout = new VerticalLayout();
		mapLayout.setSizeFull();
		mainPanel.addComponent(mapLayout);
		
		HorizontalLayout mapControlLayout = new HorizontalLayout();
		mapControlLayout.setSpacing(true);
		mapControlLayout.setMargin(true, false, false, false);
		
		clearMapButton = new Button("Rensa kartan");
		mapControlLayout.addComponent(clearMapButton);
		
		createZoneButton = new Button("Bilda zon från markörer");
		mapControlLayout.addComponent(createZoneButton);
		
		mainPanel.addComponent(mapControlLayout);
		
		HorizontalLayout textFieldLayout = new HorizontalLayout();
		textFieldLayout.setWidth("100%");
		textFieldLayout.setStyleName("textfield-layout");
		textFieldLayout.setSpacing(true);
		textFieldLayout.setMargin(true, false, false, false);
		mainPanel.addComponent(textFieldLayout);
		
		HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		bottomLeftLayout.setSpacing(true);
		textFieldLayout.addComponent(bottomLeftLayout);
		textFieldLayout.setComponentAlignment(bottomLeftLayout, Alignment.BOTTOM_LEFT);
		
		nameField = new TextField("Titel");
		bottomLeftLayout.addComponent(nameField);
		bottomLeftLayout.setComponentAlignment(nameField, MIDDLE_LEFT);

		BeanContainer<String,SearchGroup> container = 
			new BeanContainer<String, SearchGroup>(SearchGroup.class);
		container.addNestedContainerProperty("name");
		container.addNestedContainerProperty("id");
		container.setBeanIdProperty("name");
		assignedGroupDropdown = new ComboBox("Tilldela zon till grupp", container);
		assignedGroupDropdown.setImmediate(true);
		assignedGroupDropdown.setNullSelectionAllowed(true);
		bottomLeftLayout.addComponent(assignedGroupDropdown);
		bottomLeftLayout.setComponentAlignment(assignedGroupDropdown, MIDDLE_LEFT);
		
		prioField = new TextField("Prioritet");
		bottomLeftLayout.addComponent(prioField);
		bottomLeftLayout.setComponentAlignment(prioField, MIDDLE_LEFT);
		
		HorizontalLayout bottomRightLayout = new HorizontalLayout();
		bottomRightLayout.setSpacing(true);
		textFieldLayout.addComponent(bottomRightLayout);
		textFieldLayout.setComponentAlignment(bottomRightLayout, MIDDLE_RIGHT);
		
		backButton = new Button("Avbryt");
		bottomRightLayout.addComponent(backButton);
		bottomRightLayout.setComponentAlignment(backButton, Alignment.BOTTOM_RIGHT);
		
		saveButton = new Button("Spara");
		bottomRightLayout.addComponent(saveButton);
		bottomRightLayout.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);
		
		mainLayout.addComponent(mainPanel);
		mainLayout.setComponentAlignment(mainPanel, TOP_CENTER);
		
		markerSource = new BasicMarkerSource();
	}
	
	protected void setMap(GoogleMap map) {
		mapLayout.addComponent(map);
//		map.setMarkerSource(markerSource); //TODO use external marker source
	}

	protected void setupGroupComboBox(List<SearchGroup> groupList) {
		BeanContainer<String, SearchGroup> container = 
			(BeanContainer<String, SearchGroup>) assignedGroupDropdown.getContainerDataSource();
		container.addAll(groupList);
	}
}
