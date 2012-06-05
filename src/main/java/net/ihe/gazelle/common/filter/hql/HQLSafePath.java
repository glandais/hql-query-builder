package net.ihe.gazelle.common.filter.hql;

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

	@SuppressWarnings("unchecked")
	public List<T> getListDistinct() {
		return (List<T>) queryBuilder.getListDistinct(path);
	}

	public List<Object[]> getStatistics() {
		return queryBuilder.getStatistics(path);
	}

}
