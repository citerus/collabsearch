package se.citerus;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class WelcomeView extends CustomComponent {
	public WelcomeView() {
		setCaption("Välkommen!");
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Label("Inloggningen lyckades"));
		setCompositionRoot(layout);
	}
}
