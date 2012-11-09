package se.citerus.collabsearch.adminui.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import se.citerus.collabsearch.adminui.ViewSwitchController;
import se.citerus.collabsearch.model.FileMetadata;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

@SuppressWarnings("serial")
@Configurable(preConstruction=true)
public class FileUploadHandler implements Upload.SucceededListener,
		Upload.FailedListener, Upload.Receiver {

	//TODO upload filepath should be set in config file
	private static final String UPLOADDIR = "/tmp/uploads/";
	private String parentMissionId;
	private FileMetadata metadata;
	private ViewSwitchController listener;
	
	@Autowired
	private SearchMissionService service;

	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null;
		
		File uploadDir = new File(UPLOADDIR + parentMissionId);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		
		File file = new File(uploadDir.getPath() + "/" + filename);
		if (file.exists()) {
			System.err.println("File " + filename + " already exists for this mission");
			return null;
		}
		//check if file exists here?
		String debugFileId = "" + new Random().nextLong(); //TODO to be replaced with better solution
		metadata = new FileMetadata(debugFileId, filename, mimeType, file.getPath());
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
		try {
			service.addFileToMission(parentMissionId, metadata);
		} catch (RuntimeException e) {
			e.printStackTrace();
			listener.displayError("Fel", "Ett fel uppstod vid filöverföringen");
		} catch (Exception e) {
			e.printStackTrace();
			listener.displayError("Fel", e.getMessage());
		} finally {
			parentMissionId = null;
			metadata = null;
		}
		
		listener.displayNotification("Filuppladdning", "Filöverföringen lyckades.");
	}
	
	public void uploadFailed(FailedEvent event) {
		listener.displayError("Filuppladdning", "Filöverföringen misslyckades.");
		parentMissionId = null;
		metadata = null;
	}

	public String getParentMissionId() {
		return parentMissionId;
	}

	public void setParentMissionId(String parentMissionId) {
		this.parentMissionId = parentMissionId;
	}

	public void setViewRef(ViewSwitchController listener) {
		this.listener = listener;
	}

}
