package se.citerus.lookingfor.view.searchmission;

import se.citerus.lookingfor.ViewSwitchListener;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SearchMissionEditView extends CustomComponent {
	
	public SearchMissionEditView(final ViewSwitchListener listener, String selectedSearchMissionName) {
		Layout mainLayout = new VerticalLayout();
		mainLayout.addComponent(new Label("Work in progress"));
		setCompositionRoot(mainLayout);
	}
}
