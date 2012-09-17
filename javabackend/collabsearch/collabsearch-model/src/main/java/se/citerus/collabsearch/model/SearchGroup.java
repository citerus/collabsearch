package se.citerus.collabsearch.model;

public class SearchGroup {
	private String id;
	private String name;
	private GroupNode treeRoot;

	/** Serialization ctor */
	public SearchGroup() {
	}

	public SearchGroup(String id, String name, GroupNode treeRoot) {

		this.id = id;
		this.name = name;
		this.treeRoot = treeRoot;
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
	
	@Override
	public String toString() {
		return name;
	}

	public GroupNode getTreeRoot() {
		return treeRoot;
	}

	public void setTreeRoot(GroupNode treeRoot) {
		this.treeRoot = treeRoot;
	}
}
