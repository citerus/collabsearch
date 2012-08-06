package se.citerus.lookingfor.logic;

public class FileWrapper {
	
	String fileName;
	String mimeType;
	String filePath;
	
	public FileWrapper(String fileName, String mimeType, String filePath) {
		super();
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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
