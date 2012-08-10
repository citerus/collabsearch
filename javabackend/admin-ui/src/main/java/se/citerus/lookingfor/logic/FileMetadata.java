package se.citerus.lookingfor.logic;

public class FileMetadata {
	
	String filename;
	String mimeType;
	String filePath;
	
	public FileMetadata(String fileName, String mimeType, String filePath) {
		super();
		this.filename = fileName;
		this.mimeType = mimeType;
		this.filePath = filePath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFileName(String filename) {
		this.filename = filename;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
}
