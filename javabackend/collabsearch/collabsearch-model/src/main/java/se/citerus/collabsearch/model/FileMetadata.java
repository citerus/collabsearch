package se.citerus.collabsearch.model;

public class FileMetadata {
	private String id;
	private String fileName;
	private String mimeType;
	private String filePath;
	
	public FileMetadata(String id, String fileName, String mimeType, String filePath) {
		this.id = id;
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.filePath = filePath;
	}

	public FileMetadata(String fileName, String mimeType, String filePath) {
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String filename) {
		this.fileName = filename;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Returns the complete filepath, e.g /uploads/{missionid}/{filename}.
	 */
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
