package se.citerus.collabsearch.model;

public class Rank {
	public enum Title {
		OPERATIONAL_MANAGER,	//OPERATIV_CHEF,
		ASSISTANT_OM,			//OC_ASSISTENT,
		ADMIN_MANAGER,			//ADMIN_CHEF,
		ASSISTANT_AM,			//AC_ASSISTENT,
		GROUP_MANAGER,			//GRUPPCHEF,
		ASSISTANT_GM,			//GC_ASSISTENT,
		GROUP_LEADER,			//GRUPPLEDARE,
		PATROL_LEADER,			//PATRULLEDARE,
		ASSISTANT_PATROL_LEADER,//PATRULLEDARASSISTENT,
		SEARCHER				//SÖKARE
	}
	
	// The internal value of the rank, used for comparisons. Lower value means
	// closer to the tree root.
//	private final int rankId;
//	private String name;
//	private final int[] subordinates;
//	private boolean allowsChildren;
//	private boolean allowsParent;
//	
//	public static final int ALLOW_CHILDREN = 1;
//	public static final int NO_CHILDREN = 0;
//	public static final int ALLOW_PARENT = 1;
//	public static final int NO_PARENT = 0;
//	
//	/**
//	 * Creates a new Rank for use with the GroupEditView.
//	 * @param rankId the internal id and value of the rank, used in comparisons.
//	 * @param name the displayed name of the rank.
//	 * @param subordinates the possible subordinate ranks of this rank.
//	 */
//	public Rank(int rankId, String name, int[] subordinates, int allowsChildren, int allowsParent) {
//		this.rankId = rankId;
//		this.name = name;
//		this.subordinates = subordinates;
//		this.allowsChildren = allowsChildren == 1 ? true : false;
//		this.allowsParent = allowsParent == 1 ? true : false;
//	}
//
//	public int getRankId() {
//		return rankId;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public final int[] getSubordinates() {
//		return subordinates;
//	}
//
//	public boolean isAllowsChildren() {
//		return allowsChildren;
//	}
//
//	public void setAllowsChildren(boolean allowsChildren) {
//		this.allowsChildren = allowsChildren;
//	}
//
//	public boolean isAllowsParent() {
//		return allowsParent;
//	}
//
//	public void setAllowsParent(boolean allowsParent) {
//		this.allowsParent = allowsParent;
//	}
	
	public static Rank.Title getRankByName(String name) {
		Title[] values = Title.values();
		for (Title title : values) {
			if (title.name().equals(name)) {
				return title;
			}
		}
		return null;
	}
	
	/**
	 * Returns the localized rank name for the given rank.
	 * @param rankTitle The internal enum representation of the rank.
	 * @return A string containing the localized rank name.
	 */
	public static String getNameByRank(Title rankTitle) { //TODO replace with i18n
		if (rankTitle == Title.OPERATIONAL_MANAGER) {	
			return "Operativ chef";
		} else if (rankTitle == Title.ASSISTANT_OM) {		
			return "O.C. assistent";
		} else if (rankTitle == Title.ADMIN_MANAGER) {			
			return "Admin. chef";
		} else if (rankTitle == Title.ASSISTANT_AM) {			
			return "A.C. assistent";
		} else if (rankTitle == Title.GROUP_MANAGER) {			
			return "Gruppchef";
		} else if (rankTitle == Title.ASSISTANT_GM) {			
			return "G.C. assistent";
		} else if (rankTitle == Title.GROUP_LEADER) {			
			return "Gruppledare";
		} else if (rankTitle == Title.PATROL_LEADER) {			
			return "Patrulledare";
		} else if (rankTitle == Title.ASSISTANT_PATROL_LEADER) {
			return "Patrulledarassistent";
		} else if (rankTitle == Title.SEARCHER) {
			return "Sökare";
		}
		return "";
	}
}
