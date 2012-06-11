package net.ihe.gazelle.hql.paths;

import net.ihe.gazelle.hql.HQLQueryBuilder;

public abstract class HQLSafePathEntity<T> extends HQLSafePath<T> {

	public HQLSafePathEntity(String path, HQLQueryBuilder<?> queryBuilder) {
		super(path, queryBuilder);
	}

	public void addFetch() {
		queryBuilder.addFetch(path);
	}

	public abstract Class<?> getEntityClass();

}
