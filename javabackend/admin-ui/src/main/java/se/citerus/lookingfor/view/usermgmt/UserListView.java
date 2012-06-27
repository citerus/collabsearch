package se.citerus.lookingfor.view.usermgmt;

import se.citerus.lookingfor.ViewSwitchListener;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

public class UserListView extends CustomComponent {
	public UserListView(final ViewSwitchListener listener) {
		setCaption("Användare");
		Layout layout = new VerticalLayout();
		
		setCompositionRoot(layout);
	}
}
