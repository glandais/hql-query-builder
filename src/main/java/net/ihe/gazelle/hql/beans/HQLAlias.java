package net.ihe.gazelle.hql.beans;

public class HQLAlias {

	private String path;
	private String alias;
	// Criteria.LEFT_JOIN or Criteria.INNER_JOIN
	private int joinType;

	public HQLAlias(String path, String alias, int joinType) {
		super();
		this.path = path;
		this.alias = alias;
		this.joinType = joinType;
	}

	public String getPath() {
		return path;
	}

	public String getAlias() {
		return alias;
	}

	public int getJoinType() {
		return joinType;
	}

}
