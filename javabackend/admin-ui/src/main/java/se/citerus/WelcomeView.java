package se.citerus;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class WelcomeView extends CustomComponent {
	public WelcomeView() {
		setCaption("Välkommen!");
		VerticalLayout layout = new VerticalLayout();
		Label loginSuccessLabel = new Label("Inloggningen lyckades");
		layout.addComponent(loginSuccessLabel);
		layout.setComponentAlignment(loginSuccessLabel, Alignment.MIDDLE_CENTER);
		HorizontalLayout subLayout = new HorizontalLayout();
		Button userButton = new Button("Användare");
		Button logoutButton = new Button("Logga ut");
		Button searchMissionButton = new Button("Sökuppdrag");
		subLayout.addComponent(userButton);
		subLayout.addComponent(logoutButton);
		subLayout.addComponent(searchMissionButton);
		layout.addComponent(subLayout);
		setCompositionRoot(layout);
	}
}
