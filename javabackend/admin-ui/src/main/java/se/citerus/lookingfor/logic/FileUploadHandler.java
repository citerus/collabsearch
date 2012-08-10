package se.citerus.lookingfor.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import se.citerus.lookingfor.ViewSwitchController;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

public class FileUploadHandler implements Upload.SucceededListener,
		Upload.FailedListener, Upload.Receiver {

	private static final String FILEPATH = "/tmp/uploads/"; //TODO remember to change this before production
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
		SearchMissionHandler handler = null;
		try {
			handler = new SearchMissionHandler();
			handler.addFileToMission(parentMissionName, metadata);
		} catch (Exception e) {
			e.printStackTrace();
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
