package se.citerus.lookingfor.logic;

public class SearchMission {
	private String name;
	private String description;
	private int prio;
	private int status;
	
	private String humanReadableStatus;
	
	public SearchMission(String name, String description, int status) {
		this.name = name;
		this.description = description;
		this.status = status;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getHumanReadableStatus() {
		return interpretStatusCode(status);
	}

	private String interpretStatusCode(int statusCode) {
		String statusMessage = "";
		if (statusCode == 1) {
			statusMessage = "Sökande pågår";
		} else {
			statusMessage = "Okänd status";
		}
		return statusMessage;
	}
	
	@Override
	public String toString() {
		return name + ", " + description + ", " + status;
	}
	
}
