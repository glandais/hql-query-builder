package net.ihe.gazelle.common.filter.hql;

import java.util.Collection;
import java.util.List;

public abstract class HQLSafePath<T> {

	protected String path;

	protected HQLQueryBuilder<?> queryBuilder;

	protected HQLSafePath(String path) {
		this.path = path;
	}

	public HQLSafePath(String path, HQLQueryBuilder<?> queryBuilder) {
		this.path = path;
		this.queryBuilder = queryBuilder;
	}

	protected void setQueryBuilder(HQLQueryBuilder<?> queryBuilder) {
		this.queryBuilder = queryBuilder;
	}

	@Override
	public String toString() {
		return this.path;
	}

	public HQLStatisticItem statisticItem(HQLRestriction restriction) {
		return new HQLStatisticItem(path, restriction);
	}

	public HQLStatisticItem statisticItem() {
		return new HQLStatisticItem(path);
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

	public void isNotEmpty() {
		queryBuilder.addRestriction(isNotEmptyRestriction());
	}

	public void isEmpty() {
		queryBuilder.addRestriction(isEmptyRestriction());
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

	public HQLRestriction isNotEmptyRestriction() {
		return new HQLRestrictionIsNotEmpty(path);
	}

	public HQLRestriction isEmptyRestriction() {
		return new HQLRestrictionIsEmpty(path);
	}

	@SuppressWarnings("unchecked")
	public List<T> getListDistinct() {
		return (List<T>) queryBuilder.getListDistinct(path);
	}

	public List<Object[]> getStatistics() {
		return queryBuilder.getStatistics(path);
	}

}
