package net.ihe.gazelle.hql.paths;

import net.ihe.gazelle.hql.HQLQueryBuilder;
import net.ihe.gazelle.hql.HQLRestriction;
import net.ihe.gazelle.hql.restrictions.HQLRestrictionCompare;

public class HQLSafePathBasicNumber<T> extends HQLSafePathBasic<T> {

	private Integer minValue = null;
	private Integer maxValue = null;

	public HQLSafePathBasicNumber(HQLSafePathEntity parent, String path,
			HQLQueryBuilder<?> queryBuilder, Class<?> type) {
		super(parent, path, queryBuilder, type);
	}

	public void ge(T value) {
		queryBuilder.addRestriction(geRestriction(value));
	}

	public HQLRestriction geRestriction(T value) {
		return new HQLRestrictionCompare(path, value, ">=");
	}

	public void le(T value) {
		queryBuilder.addRestriction(leRestriction(value));
	}

	public HQLRestriction leRestriction(T value) {
		return new HQLRestrictionCompare(path, value, "<=");
	}

	public void gt(T value) {
		queryBuilder.addRestriction(gtRestriction(value));
	}

	public HQLRestriction gtRestriction(T value) {
		return new HQLRestrictionCompare(path, value, ">");
	}

	public void lt(T value) {
		queryBuilder.addRestriction(ltRestriction(value));
	}

	public HQLRestriction ltRestriction(T value) {
		return new HQLRestrictionCompare(path, value, "<");
	}

	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

}
