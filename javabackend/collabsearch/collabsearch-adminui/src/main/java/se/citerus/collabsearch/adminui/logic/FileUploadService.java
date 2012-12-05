package se.citerus.collabsearch.adminui.logic;

import static java.io.File.separatorChar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import se.citerus.collabsearch.adminui.view.ViewSwitchController;
import se.citerus.collabsearch.model.FileMetadata;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

@SuppressWarnings("serial")
@Configurable(preConstruction=true)
public class FileUploadService implements Upload.SucceededListener,
		Upload.FailedListener, Upload.Receiver {

	private String parentMissionId;
	private FileMetadata metadata;
	private ViewSwitchController listener;
	
	@Autowired
	private SearchMissionService service;

	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null;
		
		File file = getPathToServerStorage(filename);
		
//		String internalFileId = "" + new Random().nextLong(); //do files need id?
		metadata = new FileMetadata(filename, mimeType, file.getPath());
		try {
			fos = new FileOutputStream(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return fos;
	}

	private File getPathToServerStorage(String filename) {
		File uploadDir = null;
		try {
			String catalina_base = System.getProperty("catalina.base", "");
			if (catalina_base.isEmpty()) {
				System.err.println("CATALINA_BASE not set");
				listener.displayError("Filhanteringsfel", "Filuppladdningskatalogen kunde ej skapas, ");
				return null;
			} else if (!catalina_base.endsWith("" + separatorChar)) {
				catalina_base += separatorChar;
			}
			
			File uploadRootDir = new File(catalina_base + "uploads");
			if (!uploadRootDir.exists()) {
				if (!uploadRootDir.mkdir()) {
					System.err.println("Failed to create uploads folder: " + uploadRootDir.getAbsolutePath());
				}
			}
			
			uploadDir = new File(uploadRootDir.getAbsolutePath() + separatorChar + parentMissionId);
			if (!uploadDir.exists()) {
				if (!uploadDir.mkdir()) {
					System.err.println("Failed to create mission folder: " + uploadDir.getAbsolutePath());
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		String pathname = uploadDir.getPath() + separatorChar + filename;
		File file = new File(pathname);
		if (file.exists()) {
			System.err.println("File " + filename
					+ " already exists for this mission");
			return null;
		}
		return file;
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
