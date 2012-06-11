package net.ihe.gazelle.common.filter.hql;

public abstract class HQLSafePathEntity<T> extends HQLSafePath<T> {

	public HQLSafePathEntity(String path, HQLQueryBuilder<?> queryBuilder) {
		super(path, queryBuilder);
	}

	public void addFetch() {
		queryBuilder.addFetch(path);
	}

	public abstract Class<?> getEntityClass();

}
