package io.github.glandais.hql;

import io.github.glandais.hql.beans.HQLStatistic;
import io.github.glandais.hql.beans.HQLStatisticItem;

import java.util.List;


/**
 * Classes implementing this interface select items from a database.<br>
 * Instances have a status, like restrictions, orders, defined fetches, ...<br>
 * Method results are linked to this status
 *
 * @param <T> type of selected items
 */
public interface HQLQueryBuilderInterface<T> {

    /**
     * Number of elements without considering limits (first result, max results)
     *
     * @return The number of elements matching this query
     */
    int getCount();

    /**
     * Elements matching the restrictions
     *
     * @return Return the list of elements matching this query, max size is max
     * results from first item index is first result<br>
     * If no element, list is empty, not null
     */
    List<T> getList();

    /**
     * Elements matching the restrictions
     *
     * @return Return the list of elements matching this query, max size is max
     * results from first item index is first result<br>
     * If no element, list is null, not empty
     */
    List<T> getListNullIfEmpty();

    /**
     * Returns the first element
     *
     * @return Return the first element matching this query<br>
     * If no element, result is null
     */
    T getUniqueResult();

    /**
     * Returns a table of items for statistics purposes
     *
     * @param items List of columns for which we want data. Each column is a path
     *              relative to T, parameter type of this query.<br>
     *              a HQLStatisticItem may or may not have an additional
     *              restriction<br>
     *              Ex : if T is a truck, we may want to count number of tires
     *              having less than one year
     * @return List of items matching this criteria, and for each line, the
     * counter of distinct items on each path (matching restriction if
     * any). For each value, list of items can be retrieved using
     * getListWithStatisticsItems
     */
    List<HQLStatistic<T>> getListWithStatistics(List<HQLStatisticItem> items);

    /**
     * Returns list of distinct items of a path, size of the list matches the
     * value return in a HQLStatistic of getListWithStatistics
     *
     * @param items              same items as in getListWithStatistics()
     * @param item               an element of the result of getListWithStatistics()
     * @param statisticItemIndex the index of the item for which we want to get the values
     * @return List of distinct objects on the path of
     * items[statisticItemIndex], for the element T in HQLStatistic<T>
     */
    List<Object> getListWithStatisticsItems(List<HQLStatisticItem> items, HQLStatistic<T> item, int statisticItemIndex);

    /**
     * Set the first result index for getList() / getListWithStatistics()
     *
     * @param firstResult First element index, starting from 0
     */
    void setFirstResult(int firstResult);

    /**
     * Number of results to return in getList() / getListWithStatistics()
     *
     * @param maxResults Number of results, 10 means 10 items
     */
    void setMaxResults(int maxResults);

    /**
     * Is the query will be cached using internal mechanism (like ehcache for
     * Hibernate)
     *
     * @return Cached or not
     */
    boolean isCachable();

    /**
     * Set the query as cachable using internal mechanism (like ehcache for
     * Hibernate)
     *
     * @param cachable Cached or not
     */
    void setCachable(boolean cachable);

    /**
     * Add a restriction to this query builder
     *
     * @param restriction Restriction to add
     */
    void addRestriction(HQLRestriction restriction);

}