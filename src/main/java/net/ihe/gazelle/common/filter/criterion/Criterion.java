package net.ihe.gazelle.common.filter.criterion;

import net.ihe.gazelle.common.filter.ValueProvider;
import net.ihe.gazelle.common.filter.hql.HQLQueryBuilder;

public interface Criterion<E, F> {

	String getKeyword();

	Class<F> getSelectableClass();

	ValueProvider getValueFixer();

	String getSelectableLabelNotNull(F instance);

	ValueProvider getValueInitiator();

	String getPath();

	void appendDefaultFilter(HQLQueryBuilder<E> queryBuilder);

}
