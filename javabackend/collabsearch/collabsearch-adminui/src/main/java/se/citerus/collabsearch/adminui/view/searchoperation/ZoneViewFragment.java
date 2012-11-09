package se.citerus.collabsearch.adminui.view.searchoperation;

import org.vaadin.hezamu.googlemapwidget.GoogleMap;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

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
	protected Button saveButton;
	protected Button backButton;
	protected Label headerLabel;
	protected Button clearMapButton;
	protected Button createZoneButton;
	protected Button setMapCenterButton;

	protected ZoneViewFragment() {
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}
	
	protected void init(String headerString) {
//		mainLayout.setSizeFull();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		mainLayout.setWidth("100%");
		
		Panel mainPanel = new Panel();
		mainPanel.setWidth("100%");
		
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		
		backButton = new Button("Tillbaka");
		headerLayout.addComponent(backButton);
		headerLayout.setComponentAlignment(backButton, Alignment.MIDDLE_LEFT);
		
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
		
		setMapCenterButton = new Button("Nuvarande vy som kartcentrum");
		mapControlLayout.addComponent(setMapCenterButton);
		
		mainPanel.addComponent(mapControlLayout);
		
		HorizontalLayout textFieldLayout = new HorizontalLayout();
		textFieldLayout.setWidth("100%");
		textFieldLayout.setSpacing(true);
		
		nameField = new TextField("Titel");
		textFieldLayout.addComponent(nameField);
		textFieldLayout.setComponentAlignment(nameField, Alignment.MIDDLE_LEFT);
		
		prioField = new TextField("Prioritet");
		textFieldLayout.addComponent(prioField);
		textFieldLayout.setComponentAlignment(prioField, Alignment.MIDDLE_LEFT);
		
		saveButton = new Button("Spara");
		textFieldLayout.addComponent(saveButton);
		textFieldLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);
		
		mainPanel.addComponent(textFieldLayout);
		
		mainLayout.addComponent(mainPanel);
		mainLayout.setComponentAlignment(mainPanel, Alignment.TOP_CENTER);
	}
	
	protected void setMap(GoogleMap map) {
		mapLayout.addComponent(map);
	}
}
