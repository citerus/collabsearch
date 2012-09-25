package se.citerus.collabsearch.model;

/**
 * A lighter version of SearcherInfo to be used when viewing and editing groups,
 * where only names and ID's are important.
 * 
 * @author Ola Rende
 * 
 */
public class SearcherInfoWrapper {
	private final String id;
	private final String name;

	public SearcherInfoWrapper(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
