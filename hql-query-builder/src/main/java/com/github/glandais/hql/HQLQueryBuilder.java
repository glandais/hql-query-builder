package com.github.glandais.hql;

import com.github.glandais.hql.beans.*;
import com.github.glandais.hql.paths.HQLSafePath;
import com.github.glandais.hql.restrictions.HQLRestrictions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Slf4j
public class HQLQueryBuilder<T> implements HQLQueryBuilderInterface<T> {

    private static final String LINE_FEED = "\r\n";
    private static final Map<String, HQLQueryBuilderCache> cache = Collections
            .synchronizedMap(new HashMap<>());
    private final Map<String, HQLAlias> aliases = new LinkedHashMap<String, HQLAlias>();
    private final Set<String> aliasValues = new HashSet<String>();
    private final List<HQLRestriction> restrictions = new ArrayList<HQLRestriction>();
    private final List<HQLOrder> orders = new ArrayList<HQLOrder>();
    private final List<String> fetches = new ArrayList<String>();
    private HQLQueryBuilderCache hqlQueryBuilderCache;
    private int firstResult = -1;
    private int maxResults = -1;

    private boolean leftJoinDone = false;

    private Class<T> entityClass;
    private boolean cachable = true;

    private EntityManager entityManager = null;

    private boolean computeListUsingIdSubQuery;

    public HQLQueryBuilder(EntityManager entityManager, Class<T> entityClass) {
        super();
        this.entityManager = entityManager;
        init(entityClass);
    }

    private void init(Class<T> entityClass) {
        this.entityClass = entityClass;
        String canonicalName = entityClass.getCanonicalName();
        HQLQueryBuilderCache hqlQueryBuilderCacheTmp = cache.get(canonicalName);
        if (hqlQueryBuilderCacheTmp == null) {
            synchronized (HQLQueryBuilder.class) {
                hqlQueryBuilderCacheTmp = cache.get(canonicalName);
                if (hqlQueryBuilderCacheTmp == null) {
                    hqlQueryBuilderCacheTmp = new HQLQueryBuilderCache(entityManager, entityClass);
                    cache.put(canonicalName, hqlQueryBuilderCacheTmp);
                }
            }
        }
        this.hqlQueryBuilderCache = hqlQueryBuilderCacheTmp;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public boolean isComputeListUsingIdSubQuery() {
        return computeListUsingIdSubQuery;
    }

    public void setComputeListUsingIdSubQuery(boolean computeListUsingIdSubQuery) {
        this.computeListUsingIdSubQuery = computeListUsingIdSubQuery;
    }

    public void addEq(String propertyName, Object value) {
        restrictions.add(HQLRestrictions.eq(propertyName, value));
    }

    public void addIn(String propertyName, Collection<?> elements) {
        restrictions.add(HQLRestrictions.in(propertyName, elements));
    }

    public void addLike(String propertyName, String value) {
        restrictions.add(HQLRestrictions.like(propertyName, value));
    }

    public void addOrder(String propertyName, boolean ascending) {
        String shortProperty = getShortProperty(propertyName);
        orders.add(new HQLOrder(shortProperty, ascending));
    }

    public void addOrder(String propertyName, boolean ascending, boolean lowerCase) {
        String shortProperty = getShortProperty(propertyName);
        orders.add(new HQLOrder(shortProperty, ascending, lowerCase));
    }

    public List<HQLOrder> getOrders() {
        return orders;
    }

    public void addRestriction(HQLRestriction restriction) {
        restrictions.add(restriction);
    }

    public List<HQLRestriction> getRestrictions() {
        return restrictions;
    }

    protected StringBuilder buildQueryFrom() {
        StringBuilder sb = new StringBuilder();

        sb.append(LINE_FEED);
        sb.append(" from ");
        sb.append(entityClass.getCanonicalName());
        sb.append(" as this_ ");

        for (HQLAlias alias : aliases.values()) {
            sb.append(LINE_FEED);
            if (alias.getJoinType() == HQLAlias.LEFT_JOIN) {
                sb.append(" left join ");
            } else {
                // Criteria.INNER_JOIN
                sb.append(" join ");
            }

            String path = alias.getPath();
            if (fetches.contains(path)) {
                sb.append(" fetch ");
            }

            String parentPath = path.substring(0, path.lastIndexOf('.'));
            HQLAlias parentAlias = aliases.get(parentPath);
            if (parentAlias != null) {
                path = StringUtils.replaceOnce(path, parentPath, parentAlias.getAlias());
            }

            sb.append(path);

            sb.append(" as ");
            sb.append(alias.getAlias());
        }

        return sb;
    }

    protected HQLRestrictionValues buildQueryWhere(StringBuilder sb) {
        return buildQueryWhere(sb, restrictions);
    }

    private HQLRestrictionValues buildQueryWhere(StringBuilder sb, List<HQLRestriction> restrictions_) {
        HQLRestrictionValues values = new HQLRestrictionValues();
        if (restrictions_.size() > 0) {
            sb.append(LINE_FEED);
            sb.append(" where ");
            boolean first = true;
            for (HQLRestriction restriction : restrictions_) {
                if (first) {
                    first = false;
                } else {
                    sb.append(" and ");
                }
                sb.append(LINE_FEED);

                restriction.toHQL(this, values, sb);
            }
        }
        return values;
    }

    protected void buildQueryGroupBy(StringBuilder sb, String path) {
        sb.append(LINE_FEED);
        sb.append(" group by ");
        sb.append(path);
    }

    protected void buildQueryOrder(StringBuilder sb) {
        if (orders.size() > 0) {
            sb.append(LINE_FEED);
            sb.append(" order by ");
            boolean first = true;
            for (HQLOrder order : orders) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(LINE_FEED);
                sb.append(order.getPath());
                if (order.isAscending()) {
                    sb.append(" asc");
                } else {
                    sb.append(" desc");
                }
            }
        }
    }

    protected void buildQueryWhereParameters(Query query, HQLRestrictionValues values) {
        Set<Entry<String, Object>> entrySet = values.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }

    protected Query createRealQuery(StringBuilder sb) {
        log.debug("*******************");
        log.debug(sb.toString());
        log.debug("*******************");
        Query query = entityManager.createQuery(sb.toString());
        query.setHint("org.hibernate.cacheable", cachable);
        return query;
    }

    /**
     * @param path A path to an object mapped by Hibernate, starting with this_.
     * @return the alias
     */
    protected HQLAlias getAlias(String path) {
        HQLAlias hqlAlias = aliases.get(path);
        if (hqlAlias == null) {
            int joinType = HQLAlias.LEFT_JOIN;

            boolean isBagType = hqlQueryBuilderCache.isBagType(path);
            if (isBagType) {
                if (!leftJoinDone) {
                    leftJoinDone = true;
                } else {
                    joinType = HQLAlias.INNER_JOIN;
                }
            }

            String alias = hqlQueryBuilderCache.getAlias(path);

            hqlAlias = new HQLAlias(path, alias, joinType);
            aliases.put(path, hqlAlias);
            aliasValues.add(alias);
        }
        return hqlAlias;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return getCountGeneric("distinct this_");
    }

    public List<?> getListDistinct(String path) {
        StringBuilder sb = new StringBuilder("select distinct ").append(getShortProperty(path));
        return getListWithProvidedFrom(sb, false);
    }

    public int getCountOnPath(String path) {
        String propertyPath = getShortProperty(path);
        return getCountGeneric(propertyPath);
    }

    public int getCountDistinctOnPath(String path) {
        String propertyPath = "distinct " + getShortProperty(path);
        return getCountGeneric(propertyPath);
    }

    private int getCountGeneric(String propertyPath) {
        StringBuilder sb = new StringBuilder("select ");
        sb.append("count(").append(propertyPath).append(")");

        // First build where, to get all paths in from
        StringBuilder sbWhere = new StringBuilder();
        HQLRestrictionValues values = buildQueryWhere(sbWhere);

        sb.append(buildQueryFrom());
        sb.append(sbWhere);

        Query query = createRealQuery(sb);
        buildQueryWhereParameters(query, values);

        List<Object> resultList = query.getResultList();
        return resultList.stream().map(n -> (Number) n).map(Number::intValue)
                .reduce(0, Integer::sum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getList() {
        if (computeListUsingIdSubQuery) {
            return getListUsingIdSubQuery();
        } else {
            return getListClassic();
        }
    }

    private List<T> getListClassic() {
        StringBuilder sb = new StringBuilder("select distinct this_");
        return getListWithProvidedFrom(sb);
    }

    private List<T> getListUsingIdSubQuery() {
        StringBuilder sb = new StringBuilder("select distinct this_.id");
        List<T> listIds = getListWithProvidedFrom(sb);

        sb = new StringBuilder("select distinct this_");
        // First build where, to get all paths in from
        StringBuilder sbWhere = new StringBuilder();

        List<HQLRestriction> idRestrictions = new ArrayList<HQLRestriction>(1);
        idRestrictions.add(HQLRestrictions.in("id", listIds));
        HQLRestrictionValues values = buildQueryWhere(sbWhere, idRestrictions);

        // Build order, to get all paths in from
        buildQueryOrder(sbWhere);

        // add order columns if needed, cannot sort without it!
        // will be removed at the end (result list will be Object[] then)
        for (HQLOrder order : orders) {
            sb.append(", ").append(order.getPath());
        }
        sb.append(buildQueryFrom());
        sb.append(sbWhere);

        Query query = createRealQuery(sb);

        buildQueryWhereParameters(query, values);

        List resultList = query.getResultList();

        List finalResult = resultList;
        if (orders.size() > 0) {
            finalResult = new ArrayList(resultList.size());
            for (Object object : resultList) {
                Object[] row = (Object[]) object;
                finalResult.add(row[0]);
            }
        }
        return finalResult;
    }

    @Override
    public List<T> getListNullIfEmpty() {
        List<T> result = getList();
        if (result.size() == 0) {
            return null;
        } else {
            return result;
        }
    }

    public List<T> getListWithProvidedFrom(StringBuilder sb) {
        return getListWithProvidedFrom(sb, true);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<T> getListWithProvidedFrom(StringBuilder sb, boolean doSort) {
        // First build where, to get all paths in from
        StringBuilder sbWhere = new StringBuilder();
        HQLRestrictionValues values = buildQueryWhere(sbWhere);

        if (doSort) {
            // Build order, to get all paths in from
            buildQueryOrder(sbWhere);
            // add order columns if needed, cannot sort without it!
            // will be removed at the end (result list will be Object[] then)
            for (HQLOrder order : orders) {
                sb.append(", ").append(order.getPath());
            }
        }

        sb.append(buildQueryFrom());
        sb.append(sbWhere);

        Query query = createRealQuery(sb);

        buildQueryWhereParameters(query, values);

        if (firstResult >= 0) {
            query.setFirstResult(firstResult);
        }
        if (maxResults >= 0) {
            query.setMaxResults(maxResults);
        }

        List resultList = query.getResultList();

        List finalResult = resultList;
        if (orders.size() > 0 && doSort) {
            finalResult = new ArrayList(resultList.size());
            for (Object object : resultList) {
                Object[] row = (Object[]) object;
                finalResult.add(row[0]);
            }
        }
        return finalResult;
    }

    public List<?> getMultiSelect(HQLSafePath<?>... paths) {
        String[] pathsString = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
            pathsString[i] = paths[i].toString();
        }
        return getMultiSelect(pathsString);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<?> getMultiSelect(String... paths) {
        StringBuilder sb = new StringBuilder("select ");
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(getShortProperty(path));
        }

        // First build where, to get all paths in from
        StringBuilder sbWhere = new StringBuilder();
        HQLRestrictionValues values = buildQueryWhere(sbWhere);
        // Build order, to get all paths in from
        buildQueryOrder(sbWhere);

        // add order columns if needed, cannot sort without it!
        // will be removed at the end (result list will be Object[] then)
        for (HQLOrder order : orders) {
            sb.append(", ").append(order.getPath());
        }
        sb.append(buildQueryFrom());
        sb.append(sbWhere);

        Query query = createRealQuery(sb);

        buildQueryWhereParameters(query, values);

        if (firstResult >= 0) {
            query.setFirstResult(firstResult);
        }
        if (maxResults >= 0) {
            query.setMaxResults(maxResults);
        }

        List resultList = query.getResultList();
        List finalResult = resultList;
        if (paths.length != 1) {
            finalResult = new ArrayList(resultList.size());
            for (Object object : resultList) {
                Object[] row = (Object[]) object;
                List realRow = new ArrayList(paths.length);
                for (int i = 0; i < paths.length; i++) {
                    realRow.add(row[i]);
                }
                finalResult.add(realRow);
            }
        }
        return finalResult;
    }

    public String getShortProperty(String propertyName) {
        if ((propertyName == null) || (propertyName.equals(""))) {
            return "this_";
        }

        if (propertyName.equals("this") || propertyName.equals("this_")) {
            return "this_";
        }

        propertyName = patchPropertyName(propertyName);

        String[] parts = StringUtils.split(propertyName, '.');
        StringBuilder currentPath = new StringBuilder();
        HQLAlias hqlAlias = null;
        for (int i = 0; i < parts.length - 1; i++) {
            if (i != 0) {
                currentPath.append(".");
            }
            currentPath.append(parts[i]);

            String string = currentPath.toString();
            if (!string.equals("this_")) {
                hqlAlias = getAlias(string);
            }
        }
        String alias = null;
        if (hqlAlias == null) {
            alias = "this_";
        } else {
            alias = hqlAlias.getAlias();
        }
        return alias + "." + parts[parts.length - 1];
    }

    /**
     * Gets some statistics of a path relative to the entity class. Restrictions are applied.<br />
     * A first query retrieve the id of the statistic entity and count occurences. A second retrieves instances from ids
     *
     * @param path the path (like testInstance.lastStatus)
     * @return a list of object array. First element is the instance matching the path (can be null). Second one is the number of entities.
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getStatistics(String path) {

        path = patchPropertyName(path);

        String pathGroupedBy = null;

        EntityType<?> classMetadata = hqlQueryBuilderCache.getClassMetadata(path);

        Class<?> mappedClass;
        if (classMetadata == null) {
            pathGroupedBy = path;
            mappedClass = null;
        } else {
            pathGroupedBy = path + ".id";
            mappedClass = hqlQueryBuilderCache.getPathToClass(path);
        }

        pathGroupedBy = getShortProperty(pathGroupedBy);
        StringBuilder sb = new StringBuilder("select " + pathGroupedBy + ", count(distinct this_)");

        StringBuilder sbWhere = new StringBuilder();
        HQLRestrictionValues restrictionValues = buildQueryWhere(sbWhere);
        buildQueryGroupBy(sbWhere, pathGroupedBy);

        sb.append(buildQueryFrom());
        sb.append(sbWhere);

        Query query = createRealQuery(sb);
        buildQueryWhereParameters(query, restrictionValues);

        List<Object[]> resultList = query.getResultList();

        if (mappedClass != null) {

            Map<Integer, Object> values = new HashMap<Integer, Object>();
            for (Object[] objects : resultList) {
                if (objects[0] != null && objects[0] instanceof Number) {
                    int id = ((Number) objects[0]).intValue();
                    values.put(id, null);
                }
            }

            Set<Integer> keySet = values.keySet();
            if (keySet.size() > 0) {
                StringBuilder subQuerySb = new StringBuilder("select this_");
                subQuerySb.append(LINE_FEED);
                subQuerySb.append(" from ");
                subQuerySb.append(mappedClass.getCanonicalName());
                subQuerySb.append(" as this_");
                subQuerySb.append(LINE_FEED);
                subQuerySb.append(" where this_.id in (:keySet) ");
                Query subQuery = createRealQuery(subQuerySb);
                subQuery.setParameter("keySet", keySet);

                List<Object> instances = subQuery.getResultList();
                for (Object object : instances) {
                    Object id = null;
                    try {
                        id = getId(object);
                    } catch (GetIdException e) {
                        log.error("Failed to get id on " + object);
                    }
                    if (id != null && id instanceof Number) {
                        values.put(((Number) id).intValue(), object);
                    }
                }

                for (Object[] objects : resultList) {
                    if (objects[0] != null && objects[0] instanceof Number) {
                        Object newValue = values.get(((Number) objects[0]).intValue());
                        objects[0] = newValue;
                    }
                }
            }
        }

        return resultList;
    }

    private Object getId(Object object) throws GetIdException {
        try {
            Method method = object.getClass()
                    .getMethod("getId");
            return method.invoke(object);
        } catch (Exception e) {
            throw new GetIdException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HQLStatistic<T>> getListWithStatistics(List<HQLStatisticItem> items) {

        Map<Integer, HQLStatistic<T>> resultBuilder = new HashMap<Integer, HQLStatistic<T>>();

        int oldFirstResults = firstResult;
        int oldMaxResults = maxResults;
        firstResult = -1;
        maxResults = -1;
        List<T> list = getList();
        firstResult = oldFirstResults;
        maxResults = oldMaxResults;

        for (T t : list) {
            Object ido = null;
            try {
                ido = getId(t);
            } catch (GetIdException e) {
                log.error("Failed to get id on " + t);
            }
            if (ido != null && ido instanceof Number) {
                Integer id = ((Number) ido).intValue();
                HQLStatistic<T> statistic = new HQLStatistic<T>(id, t, items.size());
                resultBuilder.put(id, statistic);
            }
        }

        // First process all counts without restriction
        getListWithStatisticsWithoutRestriction(items, resultBuilder);

        // First process all counts without restriction
        getListWithStatisticsWithRestriction(items, resultBuilder);

        return new ArrayList<HQLStatistic<T>>(resultBuilder.values());
    }

    @SuppressWarnings("unchecked")
    private void getListWithStatisticsWithRestriction(List<HQLStatisticItem> items,
                                                      Map<Integer, HQLStatistic<T>> resultBuilder) {
        for (int i = 0; i < items.size(); i++) {
            HQLRestriction restriction = items.get(i).getRestriction();
            if (restriction != null) {
                String path = items.get(i).getPath();
                // testQuery("select t.id, s.id, count(distinct ti) from Test as t left join t.instances as ti left join ti.lastStatus as s group by t.id, s.id");
                StringBuilder sb = new StringBuilder("select this_.id");
                sb.append(", count(distinct ").append(getShortProperty(path)).append(")");

                StringBuilder sbWhere = new StringBuilder();
                List<HQLRestriction> restrictions_ = new ArrayList<HQLRestriction>(restrictions);
                restrictions_.add(restriction);
                HQLRestrictionValues restrictionValues = buildQueryWhere(sbWhere, restrictions_);
                buildQueryGroupBy(sbWhere, "this_.id");

                sb.append(buildQueryFrom());
                sb.append(sbWhere);

                Query query = createRealQuery(sb);
                buildQueryWhereParameters(query, restrictionValues);

                List<Object[]> resultList = query.getResultList();
                for (Object[] objects : resultList) {
                    Integer id = ((Number) objects[0]).intValue();
                    HQLStatistic<T> statistic = resultBuilder.get(id);

                    statistic.getCounts()[i] = ((Number) objects[1]).intValue();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void getListWithStatisticsWithoutRestriction(List<HQLStatisticItem> items,
                                                         Map<Integer, HQLStatistic<T>> resultBuilder) {
        List<Integer> itemIndexes = new ArrayList<Integer>();
        List<String> paths = new ArrayList<String>();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getRestriction() == null) {
                itemIndexes.add(i);
                paths.add(items.get(i).getPath());
            }
        }

        if (itemIndexes.size() > 0) {
            // testQuery("select t.id, count(distinct ti), count(distinct u) from Test as t left join t.instances as ti left join ti.monitorInSession as mis left join mis.user as u group by t.id");
            StringBuilder sb = new StringBuilder("select this_.id");
            for (String path : paths) {
                sb.append(", count(distinct ").append(getShortProperty(path)).append(")");
            }
            StringBuilder sbWhere = new StringBuilder();
            HQLRestrictionValues restrictionValues = buildQueryWhere(sbWhere);
            buildQueryGroupBy(sbWhere, "this_.id");

            sb.append(buildQueryFrom());
            sb.append(sbWhere);

            Query query = createRealQuery(sb);
            buildQueryWhereParameters(query, restrictionValues);

            List<Object[]> resultList = query.getResultList();
            for (Object[] objects : resultList) {

                Integer id = ((Number) objects[0]).intValue();
                HQLStatistic<T> statistic = resultBuilder.get(id);

                for (int i = 1; i < objects.length; i++) {
                    Integer itemIndex = itemIndexes.get(i - 1);
                    statistic.getCounts()[itemIndex] = ((Number) objects[i]).intValue();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getListWithStatisticsItems(List<HQLStatisticItem> items, HQLStatistic<T> item,
                                                   int statisticItemIndex) {

        List<Object> resultList = null;

        String path = items.get(statisticItemIndex).getPath();
        StringBuilder sb = new StringBuilder("select distinct ").append(getShortProperty(path));

        HQLRestriction restriction = items.get(statisticItemIndex).getRestriction();

        StringBuilder sbWhere = new StringBuilder();
        HQLRestrictionValues restrictionValues = null;
        List<HQLRestriction> restrictions_ = new ArrayList<HQLRestriction>(restrictions);

        // with restriction
        if (restriction != null) {
            restrictions_.add(restriction);
        }

        Object id = null;
        try {
            id = getId(item.getItem());
        } catch (GetIdException e) {
            log.error("Failed to get id on " + item.getItem());
        }
        restrictions_.add(HQLRestrictions.eq("id", id));
        restrictionValues = buildQueryWhere(sbWhere, restrictions_);
        sb.append(buildQueryFrom());
        sb.append(sbWhere);

        Query query = createRealQuery(sb);
        buildQueryWhereParameters(query, restrictionValues);

        resultList = query.getResultList();

        return resultList;
    }

    /**
     * Patch property name, adding this_. at the begining.
     *
     * @param propertyName the property name
     * @return the string
     */
    protected String patchPropertyName(String propertyName) {
        if (!propertyName.startsWith("this_.")) {
            if (!propertyName.startsWith("this.")) {
                propertyName = "this." + propertyName;
            }

            propertyName = "this_." + propertyName.substring(5);
        }
        return propertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCachable() {
        return cachable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCachable(boolean cachable) {
        this.cachable = cachable;
    }

    public void addFetch(String path) {
        getShortProperty(path + ".id");
        fetches.add(patchPropertyName(path));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getUniqueResult() {
        T result = null;

        int oldMaxResults = maxResults;
        setMaxResults(1);
        maxResults = oldMaxResults;

        List<T> list = getList();
        if (list.size() > 0) {
            result = list.get(0);
        } else {
            result = null;
        }
        return result;
    }

}
