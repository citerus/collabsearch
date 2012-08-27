package se.citerus.collabsearch.adminui.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.model.FileMetadata;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

@SuppressWarnings("serial")
public class FileUploadHandler implements Upload.SucceededListener,
		Upload.FailedListener, Upload.Receiver {

	private static final String FILEPATH = "/tmp/uploads/"; //TODO change filepath to search mission specific path
	private String parentMissionName;
	private BeanContainer<String, FileMetadata> fileBeanContainer;
	private File file;
	private FileMetadata metadata;
	private ViewSwitchController listener;

	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null;
		
		File uploadDir = new File(FILEPATH);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		
		file = new File(FILEPATH + filename);
		//check if file exists here?
		metadata = new FileMetadata(filename, mimeType, FILEPATH);
		try {
			fos = new FileOutputStream(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return fos;
	}

	public void uploadStarted(StartedEvent event) {
		listener.displayNotification("Filuppladdning", "Filöverföring påbörjad...");
	}

	public void uploadSucceeded(SucceededEvent event) {
		//add filemetadata to db
		SearchMissionService handler = null;
		try {
			handler = new SearchMissionService();
			handler.addFileToMission(parentMissionName, metadata);
		} catch (RuntimeException e) {
			listener.displayError("Filöverföring", "Ett uppdragsnamn måste bestämmas innan filer kan laddas upp");
		} catch (Exception e) {
			listener.displayError("Fel", e.getMessage());
		} finally {
			handler.cleanUp();
		}
		
		//add file to list
		fileBeanContainer.addBean(metadata);
		
		listener.displayNotification("Filuppladdning", "Filöverföringen lyckades.");
	}
	
	public void uploadFailed(FailedEvent event) {
		listener.displayError("Filuppladdning", "Filöverföringen misslyckades.");
	}

	public String getParentMissionName() {
		return parentMissionName;
	}

	public void setParentMissionName(String parentMissionName) {
		this.parentMissionName = parentMissionName;
	}

	public void setTableBeanRef(BeanContainer<String, FileMetadata> fileBeanContainer) {
		this.fileBeanContainer = fileBeanContainer;
	}

	public void setViewRef(ViewSwitchController listener) {
		this.listener = listener;
	}

}
