package se.citerus.lookingfor.logic;

import java.util.ArrayList;
import java.util.List;

public class SearchMission {
	private String name;
	private String description;
	private int prio;
	private Status status;
	
	//should these be associated with this class?
	private List<FileMetadata> fileList;
	private List<SearchOperation> opsList;
		
	public SearchMission(String name, String description, int prio, Status status) {
		this.name = name;
		this.description = description;
		this.prio = prio;
		this.status = status;
		
		fileList = new ArrayList<FileMetadata>();
		opsList = new ArrayList<SearchOperation>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getPrio() {
		return prio;
	}
	
	public void setPrio(int prio) {
		this.prio = prio;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

//	public String getHumanReadableStatus() {
//		return interpretStatusCode(status);
//	}

//	private String interpretStatusCode(int statusCode) {
//		//refactor into non-hardcoded, modifiable solution
//		String statusMessage = "";
//		if (statusCode == 0) {
//			statusMessage = "Avslutat";
//		} else if (statusCode == 1) {
//			statusMessage = "Sökande pågår";
//		} else {
//			statusMessage = "Okänd status";
//		}
//		return statusMessage;
//	}
	
	@Override
	public String toString() {
		return name + ", " + description + ", " + status;
	}

	public List<SearchOperation> getOpsList() {
		return opsList;
	}

	public void setOpsList(List<SearchOperation> opsList) {
		this.opsList = opsList;
	}

	public List<FileMetadata> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileMetadata> fileList) {
		this.fileList = fileList;
	}
	
}
