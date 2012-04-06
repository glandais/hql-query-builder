package net.ihe.gazelle.common.filter.hql;

public class HQLSafePathBasic<T> extends HQLSafePath<T> {

	public HQLSafePathBasic(String path, HQLQueryBuilder<?> queryBuilder) {
		super(path, queryBuilder);
	}

	public void order(boolean ascending) {
		queryBuilder.addOrder(path, ascending);
	}

	public void like(String value, HQLRestrictionLikeMatchMode matchMode) {
		queryBuilder.addRestriction(new HQLRestrictionLike(path, value, matchMode));
	}

	public void like(String value) {
		queryBuilder.addRestriction(new HQLRestrictionLike(path, value));
	}

	public void ge(T value) {
		queryBuilder.addRestriction(new HQLRestrictionCompare(path, value, ">="));
	}

	public void le(T value) {
		queryBuilder.addRestriction(new HQLRestrictionCompare(path, value, "<="));
	}

	public void gt(T value) {
		queryBuilder.addRestriction(new HQLRestrictionCompare(path, value, ">"));
	}

	public void lt(T value) {
		queryBuilder.addRestriction(new HQLRestrictionCompare(path, value, "<"));
	}

}
