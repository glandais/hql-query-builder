package net.ihe.gazelle.common.filter.hql;

import java.util.List;

public interface HQLQueryBuilderInterface<T> {

	int getCount();

	List<T> getList();

	T getUniqueResult();

	List<HQLStatistic<T>> getListWithStatistics(List<HQLStatisticItem> items);

	List<Object> getListWithStatisticsItems(List<HQLStatisticItem> items, HQLStatistic<T> item, int statisticItemIndex);

	void setFirstResult(int firstResult);

	void setMaxResults(int maxResults);

	boolean isCachable();

	void setCachable(boolean cachable);

}