package net.ihe.gazelle.common.filter;

import net.ihe.gazelle.common.filter.hql.HQLQueryBuilder;

public interface EntityFiltrable<T> {

	Class<T> getEntityClass();

	void appendDefaultFilter(HQLQueryBuilder<T> queryBuilder);

}
