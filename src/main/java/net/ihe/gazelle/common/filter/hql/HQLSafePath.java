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
		queryBuilder.addRestriction(new HQLRestrictionEq(path, value));
	}

	public void neq(T value) {
		queryBuilder.addRestriction(new HQLRestrictionNeq(path, value));
	}

	public void in(Collection<? extends T> elements) {
		queryBuilder.addRestriction(new HQLRestrictionIn(path, elements));
	}

	public void nin(Collection<? extends T> elements) {
		queryBuilder.addRestriction(new HQLRestrictionNotIn(path, elements));
	}

	public void isNotNull() {
		queryBuilder.addRestriction(new HQLRestrictionIsNotNull(path));
	}

	public void isNull() {
		queryBuilder.addRestriction(new HQLRestrictionIsNull(path));
	}

	public void isNotEmpty() {
		queryBuilder.addRestriction(new HQLRestrictionIsNotEmpty(path));
	}

	public void isEmpty() {
		queryBuilder.addRestriction(new HQLRestrictionIsEmpty(path));
	}

	@SuppressWarnings("unchecked")
	public List<T> getListDistinct() {
		return (List<T>) queryBuilder.getListDistinct(path);
	}

	public List<Object[]> getStatistics() {
		return queryBuilder.getStatistics(path);
	}

}
