package net.ihe.gazelle.common.filter.hql;

public class HQLStatisticItem {

	private String path;

	private HQLRestriction restriction;

	public HQLStatisticItem(Object path, HQLRestriction restriction) {
		super();
		this.path = path.toString();
		this.restriction = restriction;
	}

	public HQLStatisticItem(Object path) {
		super();
		this.path = path.toString();
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
