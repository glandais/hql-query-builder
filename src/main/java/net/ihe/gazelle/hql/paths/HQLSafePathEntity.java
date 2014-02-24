package net.ihe.gazelle.hql.paths;

import net.ihe.gazelle.hql.HQLQueryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HQLSafePathEntity<T> extends HQLSafePath<T> {

	private static Logger log = LoggerFactory.getLogger(HQLSafePathEntity.class);

	public HQLSafePathEntity(String path, HQLQueryBuilder<?> queryBuilder) {
		super(path, queryBuilder);
	}

	public void addFetch() {
		queryBuilder.addFetch(path);
	}

	public abstract Class<?> getEntityClass();

	/*
	private boolean single;

	public HQLSafePathEntity(boolean single, String path, HQLQueryBuilder<?> queryBuilder) {
		super(path, queryBuilder);
		this.single = single;
	}

	public boolean isSingle() {
		return single;
	}
	 */
}
