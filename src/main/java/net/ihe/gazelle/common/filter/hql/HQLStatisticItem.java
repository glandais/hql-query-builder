package net.ihe.gazelle.common.filter.hql;

public class HQLStatisticItem {

	private String path;

	private HQLRestriction restriction;

	public HQLStatisticItem(String path, HQLRestriction restriction) {
		super();
		this.path = path;
		this.restriction = restriction;
	}

	public HQLStatisticItem(String path) {
		super();
		this.path = path;
		this.restriction = null;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public HQLRestriction getRestriction() {
		return restriction;
	}

	public void setRestriction(HQLRestriction restriction) {
		this.restriction = restriction;
	}

}
