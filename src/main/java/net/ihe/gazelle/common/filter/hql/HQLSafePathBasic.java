package net.ihe.gazelle.common.filter.hql;

import java.util.Collection;

public class HQLSafePathBasic<T> extends HQLSafePath<T> {

	private Class<?> type;

	public HQLSafePathBasic(String path, HQLQueryBuilder<?> queryBuilder, Class<?> type) {
		super(path, queryBuilder);
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}

	public void order(boolean ascending) {
		queryBuilder.addOrder(path, ascending);
	}

	public void eq(T value) {
		queryBuilder.addRestriction(eqRestriction(value));
	}

	public void neq(T value) {
		queryBuilder.addRestriction(neqRestriction(value));
	}

	public void in(Collection<? extends T> elements) {
		queryBuilder.addRestriction(inRestriction(elements));
	}

	public void nin(Collection<? extends T> elements) {
		queryBuilder.addRestriction(ninRestriction(elements));
	}

	public void isNotNull() {
		queryBuilder.addRestriction(isNotNullRestriction());
	}

	public void isNull() {
		queryBuilder.addRestriction(isNullRestriction());
	}

	public HQLRestriction eqRestriction(T value) {
		return new HQLRestrictionEq(path, value);
	}

	public HQLRestriction neqRestriction(T value) {
		return new HQLRestrictionNeq(path, value);
	}

	public HQLRestriction inRestriction(Collection<? extends T> elements) {
		return new HQLRestrictionIn(path, elements);
	}

	public HQLRestriction ninRestriction(Collection<? extends T> elements) {
		return new HQLRestrictionNotIn(path, elements);
	}

	public HQLRestriction isNotNullRestriction() {
		return new HQLRestrictionIsNotNull(path);
	}

	public HQLRestriction isNullRestriction() {
		return new HQLRestrictionIsNull(path);
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
