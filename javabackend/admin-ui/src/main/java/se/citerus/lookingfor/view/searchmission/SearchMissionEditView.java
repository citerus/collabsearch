package se.citerus.lookingfor.view.searchmission;

import java.awt.TextField;

import se.citerus.lookingfor.ViewSwitchListener;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SearchMissionEditView extends CustomComponent {
	
	private VerticalLayout mainLayout;
	private final ViewSwitchListener listener;

	public SearchMissionEditView(final ViewSwitchListener listener, String selectedSearchMissionName) {
		this.listener = listener;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		//mainLayout.addComponent(new Label("Work in progress"));
		
		VerticalLayout subLayout = new VerticalLayout();
		
//		Form form = new Form();
//		form.addField("title", (Field) new TextField());
//		form.addField("descr", (Field) new TextField());
//		form.addField("prio", new ComboBox());
//		form.addField("status", new ComboBox());
//		mainLayout.addComponent(form);
		
		Button backButton = new Button("Tillbaka");
		backButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionListView();
			}
		});
		mainLayout.addComponent(backButton);
		
		mainLayout.addComponent(subLayout);
		mainLayout.setComponentAlignment(subLayout, Alignment.MIDDLE_CENTER);
	}
}
