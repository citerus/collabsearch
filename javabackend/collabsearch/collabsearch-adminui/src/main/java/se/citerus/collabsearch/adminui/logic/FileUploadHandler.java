package se.citerus.collabsearch.adminui.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.Random;

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

	private static final String UPLOADDIR = "/tmp/uploads/"; //TODO change filepath to search mission specific path
	private String parentMissionId;
	private File file;
	private FileMetadata metadata;
	private ViewSwitchController listener;

	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null;
		
		File uploadDir = new File(UPLOADDIR);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		
		String filepath = UPLOADDIR + parentMissionId + "/" + filename;
		file = new File(filepath);
		if (file.exists()) {
			System.err.println("File " + filename + " already exists for this mission");
			return null;
		}
		//check if file exists here?
		String debugId = "" + new Random().nextLong(); //TODO to be replaced with better solution
		metadata = new FileMetadata(debugId, filename, mimeType, UPLOADDIR);
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
			handler.addFileToMission(parentMissionId, metadata);
		} catch (RuntimeException e) {
			listener.displayError("Filöverföring", "Ett uppdragsnamn måste bestämmas innan filer kan laddas upp");
		} catch (Exception e) {
			listener.displayError("Fel", e.getMessage());
		} finally {
			if (handler != null) {
				handler.cleanUp();
			}
			parentMissionId = null;
			metadata = null;
			file = null;
		}
		
		listener.displayNotification("Filuppladdning", "Filöverföringen lyckades.");
	}
	
	public void uploadFailed(FailedEvent event) {
		listener.displayError("Filuppladdning", "Filöverföringen misslyckades.");
		parentMissionId = null;
		metadata = null;
		file = null;
	}

	public String getParentMissionName() {
		return parentMissionId;
	}

	public void setParentMissionId(String parentMissionName) {
		this.parentMissionId = parentMissionName;
	}

	public void setViewRef(ViewSwitchController listener) {
		this.listener = listener;
	}

}
