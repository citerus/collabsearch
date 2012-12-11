package se.citerus.collabsearch.adminui.view;

import se.citerus.collabsearch.adminui.view.login.WelcomeView;
import se.citerus.collabsearch.adminui.view.searchmission.FileUploadView;
import se.citerus.collabsearch.adminui.view.searchmission.SearchMissionEditView;
import se.citerus.collabsearch.adminui.view.searchmission.SearchMissionListView;
import se.citerus.collabsearch.adminui.view.searchoperation.EditZoneView;
import se.citerus.collabsearch.adminui.view.searchoperation.GroupEditView;
import se.citerus.collabsearch.adminui.view.searchoperation.NewZoneView;
import se.citerus.collabsearch.adminui.view.searchoperation.SearchOperationEditView;
import se.citerus.collabsearch.adminui.view.usermgmt.EditUserView;
import se.citerus.collabsearch.adminui.view.usermgmt.NewUserView;
import se.citerus.collabsearch.adminui.view.usermgmt.UserListView;

import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class MainWindow extends Window implements ViewSwitchController {

	private WelcomeView welcomeView;
	private UserListView userListView;
	private EditUserView userEditView;
	private SearchMissionListView searchMissionListView;
	private SearchMissionEditView searchMissionEditView;
	private SearchOperationEditView searchOperationEditView;
	private FileUploadView fileMgmtView;
	private GroupEditView groupEditView;
	private EditZoneView editZoneView;
	private NewZoneView newZoneView;
	private NewUserView newUserView;

	public MainWindow() {
		setCaption("Missing People - Inloggning");
		
		addListener(new ResizeListener() {
			@Override
			public void windowResized(ResizeEvent e) {
				System.out.println(
					"New height: " + e.getWindow().getBrowserWindowHeight() + " " + 
					"New width: " + e.getWindow().getBrowserWindowWidth());
			}
		});
	}

	public void initWindow() {
		switchToWelcomeView();
	}
	
	@Override
	public void logoutAndReload() {
		getApplication().close();
	}

	@Override
	public void displayNotification(String caption, String message) {
		showNotification(caption, message);
	}

	@Override
	public void displayError(String caption, String message) {
		showNotification(caption, message, Notification.TYPE_ERROR_MESSAGE);
	}

	@Override
	public void setMainWindowCaption(String caption) {
		setCaption(caption);
	}

	@Override
	public void switchToWelcomeView() {
		if (welcomeView == null) {
			welcomeView = new WelcomeView(this);
			welcomeView.init();
		}
		welcomeView.resetView();
		setContent(welcomeView);
	}

	@Override
	public void switchToUserListView() {
		if (userListView == null) {
			userListView = new UserListView(this);
			userListView.init();
		}
		userListView.resetView();
		setContent(userListView);
	}
	
	@Override
	public void switchToUserEditView(String userId) {
		if (userEditView == null) {
			userEditView = new EditUserView(this);
			userEditView.init();
		}
		userEditView.resetView(userId);
		setContent(userEditView);
	}

	@Override
	public void switchToNewUserView() {
		if (newUserView == null) {
			newUserView = new NewUserView(this);
			newUserView.init();
		}
		newUserView.resetView();
		setContent(newUserView);
	}
	
	@Override
	public void switchToSearchMissionListView() {
		if (searchMissionListView == null) {
			searchMissionListView = new SearchMissionListView(this);
			searchMissionListView.init();
		}
		searchMissionListView.resetView();
		setContent(searchMissionListView);
	}

	@Override
	public void switchToSearchMissionEditView(String missionId) {
		if (searchMissionEditView == null) {
			searchMissionEditView = new SearchMissionEditView(this);
			searchMissionEditView.init();
		}
		searchMissionEditView.resetView(missionId);
		setContent(searchMissionEditView);
	}

	@Override
	public void switchToSearchOperationEditView(String opId, String missionId) {
		if (searchOperationEditView == null) {
			searchOperationEditView = new SearchOperationEditView(this);
			searchOperationEditView.init();
		}
		searchOperationEditView.resetView(opId, missionId);
		setContent(searchOperationEditView);
	}

	@Override
	public void switchToGroupEditView(String groupId, String opId) {
		if (groupEditView == null) {
			groupEditView = new GroupEditView(this);
			groupEditView.init();
		}
		groupEditView.resetView(groupId, opId);
		setContent(groupEditView);
	}

	@Override
	public void switchToFileUploadView(String missionId) {
		if (fileMgmtView == null) {
			fileMgmtView = new FileUploadView(this);
			fileMgmtView.init();
		}
		fileMgmtView.resetView(missionId);
		setContent(fileMgmtView);
	}

	@Override
	public void switchToEditZoneView(String zoneId) {
		if (editZoneView == null) {
			editZoneView = new EditZoneView(this);
			editZoneView.init();
		}
		setContent(editZoneView); //view must be attached to window before google maps init
		editZoneView.resetView(zoneId); //map window initialized here
	}

	@Override
	public void switchToNewZoneView(String opId) {
		if (newZoneView == null) {
			newZoneView = new NewZoneView(this);
			newZoneView.init();
		}
		setContent(newZoneView);
		newZoneView.resetView(opId);
	}

}
