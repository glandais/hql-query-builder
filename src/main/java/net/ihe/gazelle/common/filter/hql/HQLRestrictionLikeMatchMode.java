package net.ihe.gazelle.common.filter.hql;

public enum HQLRestrictionLikeMatchMode {

	EXACT("", ""),

	START("", "%"),

	END("%", ""),

	ANYWHERE("%", "%");

	private String prefix;
	private String suffix;

	HQLRestrictionLikeMatchMode(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public String toHQL(String value) {
		return prefix + value + suffix;
	}

}
