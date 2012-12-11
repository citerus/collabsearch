package se.citerus.collabsearch.adminui.view.searchmission;

import se.citerus.collabsearch.adminui.logic.FileUploadService;
import se.citerus.collabsearch.adminui.view.ViewSwitchController;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class FileUploadView extends CustomComponent {
	private final ViewSwitchController listener;
	private VerticalLayout mainLayout;
	private Button uploadButton;
	private Button cancelButton;
	private FileUploadService fileUploadHandler;

	public FileUploadView(final ViewSwitchController listener) {
		this.listener = listener;
		mainLayout = new VerticalLayout();
		setCompositionRoot(mainLayout);
	}

	public void init() {
		listener.setMainWindowCaption("Missing People - Filhantering");

		mainLayout.setSizeFull();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		VerticalLayout outerLayout = new VerticalLayout();

		HorizontalLayout fileButtonsLayout = new HorizontalLayout();
		fileButtonsLayout.setSpacing(true);

		fileUploadHandler = new FileUploadService(listener);

		final Upload fileUpload = new Upload(null, fileUploadHandler);
		fileUpload.setButtonCaption(null);
		fileUpload.addListener((Upload.SucceededListener) fileUploadHandler);
		fileUpload.addListener((Upload.FailedListener) fileUploadHandler);
		fileButtonsLayout.addComponent(fileUpload);

		uploadButton = new Button("Ladda upp fil");
		uploadButton.addListener(new ClickListener() {
			public void buttonClick(ClickEvent event) {
				fileUpload.submitUpload();
			}
		});
		fileButtonsLayout.addComponent(uploadButton);

		cancelButton = new Button("Avbryt");
		cancelButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				listener.switchToSearchMissionListView();
			}
		});
		fileButtonsLayout.addComponent(cancelButton);

		outerLayout.addComponent(fileButtonsLayout);
		outerLayout.setComponentAlignment(fileButtonsLayout,
				Alignment.MIDDLE_CENTER);

		mainLayout.addComponent(outerLayout);
	}

	public void resetView(String missionId) {
		fileUploadHandler.setParentMissionId(missionId);
	}
}
