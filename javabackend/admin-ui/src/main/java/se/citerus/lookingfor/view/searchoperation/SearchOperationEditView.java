package se.citerus.lookingfor.view.searchoperation;

import se.citerus.lookingfor.ViewSwitchController;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SearchOperationEditView extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private Button zoneButton;
	private Button groupButton;
	private final ViewSwitchController listener;
	private Button cancelButton;

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
		
		mainLayout.addComponent(new Label("Work in progress"));
		
		zoneButton = new Button("Hantera zoner");
		zoneButton.setEnabled(false);
		mainLayout.addComponent(zoneButton);
		
		groupButton = new Button("Hantera grupper");
		groupButton.setEnabled(false);
		mainLayout.addComponent(groupButton);
		
		cancelButton = new Button("Avbryt");
		mainLayout.addComponent(cancelButton);
	}
}
