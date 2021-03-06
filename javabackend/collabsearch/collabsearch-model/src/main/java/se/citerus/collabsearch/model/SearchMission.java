package se.citerus.collabsearch.model;

import java.util.ArrayList;
import java.util.List;

public class SearchMission {
	private String id;
	private String name;
	private String description;
	private int prio;
	private Status status;
	
	private List<FileMetadata> fileList = new ArrayList<FileMetadata>();
	private List<SearchOperation> opsList = new ArrayList<SearchOperation>();
	
	/** Constructor for new search mission state saving. */
	public SearchMission() {
		//this ctor intentionally left empty
	}
	
	public SearchMission(String id, String name, String description, int prio, Status status) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.prio = prio;
		this.status = status;
		
		fileList = new ArrayList<FileMetadata>(0);
		opsList = new ArrayList<SearchOperation>(0);
	}
	
	public SearchMission(String name, String description, int prio, Status status) {
		this(null, name, description, prio, status);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	
	@Override
	public String toString() {
		return id + ", " + name + ", " + description + ", " + status;
	}
}
