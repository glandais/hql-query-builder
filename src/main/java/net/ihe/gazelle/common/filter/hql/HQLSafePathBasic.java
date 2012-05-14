package net.ihe.gazelle.common.filter.hql;

public class HQLSafePathBasic<T> extends HQLSafePath<T> {

	public HQLSafePathBasic(String path, HQLQueryBuilder<?> queryBuilder) {
		super(path, queryBuilder);
	}

	public void order(boolean ascending) {
		queryBuilder.addOrder(path, ascending);
	}

	public void like(String value, HQLRestrictionLikeMatchMode matchMode) {
		queryBuilder.addRestriction(likeRestriction(value, matchMode));
	}

	public HQLRestriction likeRestriction(String value, HQLRestrictionLikeMatchMode matchMode) {
		return new HQLRestrictionLike(path, value, matchMode);
	}

	public void like(String value) {
		queryBuilder.addRestriction(likeRestriction(value));
	}

	public HQLRestriction likeRestriction(String value) {
		return new HQLRestrictionLike(path, value);
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

}
