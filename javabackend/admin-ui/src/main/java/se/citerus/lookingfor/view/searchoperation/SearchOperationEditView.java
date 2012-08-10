package se.citerus.lookingfor.view.searchoperation;

import java.util.Locale;

import se.citerus.lookingfor.ViewSwitchController;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SearchOperationEditView extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private Button zoneButton;
	private Button groupButton;
	private final ViewSwitchController listener;
	private Button cancelButton;
	private Button saveButton;
	private TextField titleField;
	private TextArea descrField;
	private DateField dateField;

	public SearchOperationEditView(final ViewSwitchController listener) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		
		this.listener = listener;
		cancelButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				//TODO make searchmissioneditview save state
				listener.returnToSearchMissionEditView();
			}
		});
	}
	
	public void resetView(String opName, String missionName) {
		if (opName != null && missionName != null) { //existing operation
			//find operation
			//load data from operation into fields
		} else { //new operation
			//empty all fields
		}
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setMargin(false, false, false, true);
		mainLayout.setSpacing(true);
		
		VerticalLayout subLayout = new VerticalLayout();
		subLayout.setWidth("33%");
		subLayout.setSpacing(true);
		
		Label headerLabel = new Label("<h1><b>Redigera sökoperation</h1></b>");
		headerLabel.setContentMode(Label.CONTENT_XHTML);
		subLayout.addComponent(headerLabel);
		
		Label titleLabel = new Label("Titel");
		titleField = new TextField();
		makeFormItem(subLayout, titleLabel, titleField, Alignment.MIDDLE_LEFT);
		
		Label descrLabel = new Label("Beskrivning");
		descrField = new TextArea();
		makeFormItem(subLayout, descrLabel, descrField, Alignment.TOP_LEFT);
		
		Label dateLabel = new Label("Datum");
		dateField = new DateField();
		makeFormItem(subLayout, dateLabel, dateField, Alignment.MIDDLE_LEFT);
		dateField.setLocale(new Locale("sv", "SE"));
		dateField.setResolution(InlineDateField.RESOLUTION_MIN);
		
		HorizontalLayout upperButtonLayout = new HorizontalLayout();
		
		zoneButton = new Button("Hantera zoner");
		zoneButton.setEnabled(false);
		upperButtonLayout.addComponent(zoneButton);
		
		groupButton = new Button("Hantera grupper");
		groupButton.setEnabled(false);
		upperButtonLayout.addComponent(groupButton);
		
		subLayout.addComponent(upperButtonLayout);
		
		HorizontalLayout lowerButtonLayout = new HorizontalLayout();
		
		saveButton = new Button("Spara");
		lowerButtonLayout.addComponent(saveButton);
		
		cancelButton = new Button("Avbryt");
		lowerButtonLayout.addComponent(cancelButton);
		
		subLayout.addComponent(lowerButtonLayout);
		
		mainLayout.addComponent(subLayout);
	}
	
	private void makeFormItem(VerticalLayout formLayout, Label label, AbstractField field, Alignment labelAlignment) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth("100%");
		label.setWidth("100%");
		layout.addComponent(label);
		layout.setExpandRatio(label, 1f);
		layout.setComponentAlignment(label, labelAlignment);
		field.setWidth("100%");
		layout.addComponent(field);
		layout.setComponentAlignment(field, Alignment.TOP_RIGHT);
		layout.setExpandRatio(field, 2f);
		formLayout.addComponent(layout);
	}
}
