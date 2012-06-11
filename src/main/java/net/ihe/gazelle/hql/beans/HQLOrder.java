package net.ihe.gazelle.hql.beans;

public class HQLOrder {

	private String path;
	private boolean ascending;

	public HQLOrder(String path, boolean ascending) {
		super();
		this.path = path;
		this.ascending = ascending;
	}

	public String getPath() {
		return path;
	}

	public boolean isAscending() {
		return ascending;
	}

}
