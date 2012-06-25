package net.ihe.gazelle.hql.paths;

import java.util.List;

import net.ihe.gazelle.hql.HQLQueryBuilder;
import net.ihe.gazelle.hql.HQLRestriction;
import net.ihe.gazelle.hql.beans.HQLStatisticItem;

public abstract class HQLSafePath<T> implements Comparable<HQLSafePath<T>> {

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

	public String getPath() {
		return path;
	}

	public HQLQueryBuilder<?> getQueryBuilder() {
		return queryBuilder;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HQLSafePath other = (HQLSafePath) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	public int compareTo(HQLSafePath<T> o) {
		return getPath().compareTo(o.getPath());
	}

}
