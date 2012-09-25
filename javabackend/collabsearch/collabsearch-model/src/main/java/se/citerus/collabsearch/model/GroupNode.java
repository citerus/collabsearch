package se.citerus.collabsearch.model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

/**
 * A GroupNode represents a Searcher displayed in the group hierarchy tree.
 * @author Ola Rende
 */
public class GroupNode implements TreeNode {
	private int itemId; //mutable tree id
	private Rank.Title rank;
	private GroupNode parent;
	private ArrayList<GroupNode> children;
	private final String searcherId;

	public GroupNode(String searcherId, Rank.Title rank, GroupNode parent) {
		this.searcherId = searcherId;
		this.rank = rank;
		
//		if (rank.isAllowsParent()) {
//			this.parent = parent;
//		}
		children = new ArrayList<GroupNode>(1);
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		if (children != null) {
			for (GroupNode node : children) {
				if (node.getItemId() == childIndex) {
					return node;
				}
			}
		}
		return null;
	}

	@Override
	public int getChildCount() {
		if (children != null) {
			return children.size();
		} else {
			return 0;
		}
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public int getIndex(TreeNode node) {
		return getItemId();
	}

	@Override
	public boolean getAllowsChildren() {
		return rank != Rank.Title.SEARCHER;
	}

	@Override
	public boolean isLeaf() {
		return getChildCount() == 0;
	}

	@Override
	public Enumeration<GroupNode> children() {
		return new ChildNodeEnum();
	}
	
	public void addChild(GroupNode child) {
		children.add(child);
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public Rank.Title getRank() {
		return rank;
	}

	public void setRank(Rank.Title rank) {
		this.rank = rank;
	}
	
	public String getSearcherId() {
		return searcherId;
	}

	private class ChildNodeEnum implements Enumeration<GroupNode> {
		private int currentIndex;
		
		public ChildNodeEnum() {
			currentIndex = 0;
		}
		
		@Override
		public boolean hasMoreElements() {
			return currentIndex < children.size();
		}
		
		@Override
		public GroupNode nextElement() {
			return children.get(currentIndex++);
		}
	}

	public List<GroupNode> getChildren() {
		return children;
	}

}
