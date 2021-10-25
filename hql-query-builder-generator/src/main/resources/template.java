import java.util.*;

import com.github.glandais.hql.*;
import com.github.glandais.hql.beans.*;
import com.github.glandais.hql.paths.*;
import com.github.glandais.hql.restrictions.*;

@SuppressWarnings("all")
public class CLASSNAME<T extends TYPE> extends SUPER<T> implements HQLQueryBuilderInterface<T> {

    public CLASSNAME(EntityManager entityManager) {
        super("this", new HQLQueryBuilder(entityManager, TYPE.class));
    }

    public CLASSNAME(HQLQueryBuilder<?> queryBuilder) {
        super("this", queryBuilder);
    }

    public CLASSNAME(String path, HQLQueryBuilder<?> queryBuilder) {
        super(path, queryBuilder);
    }

    public Class<?> getEntityClass() {
        return TYPE.class;
    }

    public void isNotEmpty() {
        queryBuilder.addRestriction(isNotEmptyRestriction());
    }

    public void isEmpty() {
        queryBuilder.addRestriction(isEmptyRestriction());
    }

    public HQLRestriction isNotEmptyRestriction() {
        return HQLRestrictions.isNotEmpty(path);
    }

    public HQLRestriction isEmptyRestriction() {
        return HQLRestrictions.isEmpty(path);
    }

    @SuppressWarnings("unchecked")
    public List<T> getListDistinct() {
        return (List<T>) queryBuilder.getListDistinct(path);
    }

    public int getCountOnPath() {
        return queryBuilder.getCountOnPath(path);
    }

    public int getCountDistinctOnPath() {
        return queryBuilder.getCountDistinctOnPath(path);
    }

    public void eq(T value) {
        queryBuilder.addRestriction(eqRestriction(value));
    }

    public void eqIfValueNotNull(T value) {
        if (value != null) {
            eq(value);
        }
    }

    public void neq(T value) {
        queryBuilder.addRestriction(neqRestriction(value));
    }

    public void in(Collection<? extends T> elements) {
        queryBuilder.addRestriction(inRestriction(elements));
    }

    public void nin(Collection<? extends T> elements) {
        queryBuilder.addRestriction(ninRestriction(elements));
    }

    public void isNotNull() {
        queryBuilder.addRestriction(isNotNullRestriction());
    }

    public void isNull() {
        queryBuilder.addRestriction(isNullRestriction());
    }

    public HQLRestriction eqRestriction(T value) {
        return HQLRestrictions.eq(path, value);
    }

    public HQLRestriction neqRestriction(T value) {
        return HQLRestrictions.neq(path, value);
    }

    public HQLRestriction inRestriction(Collection<? extends T> elements) {
        return HQLRestrictions.in(path, elements);
    }

    public HQLRestriction ninRestriction(Collection<? extends T> elements) {
        return HQLRestrictions.nin(path, elements);
    }

    public HQLRestriction isNotNullRestriction() {
        return HQLRestrictions.isNotNull(path);
    }

    public HQLRestriction isNullRestriction() {
        return HQLRestrictions.isNull(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return queryBuilder.getCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getList() {
        return (List<T>) queryBuilder.getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getListNullIfEmpty() {
        return (List<T>) queryBuilder.getListNullIfEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getUniqueResult() {
        return (T) queryBuilder.getUniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HQLStatistic<T>> getListWithStatistics(List<HQLStatisticItem> items) {
        List<?> result = queryBuilder.getListWithStatistics(items);
        return (List<HQLStatistic<T>>) result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getListWithStatisticsItems(List<HQLStatisticItem> items, HQLStatistic<T> item,
                                                   int statisticItemIndex) {
        HQLStatistic safeItem = item;
        List<Object> result = queryBuilder.getListWithStatisticsItems(items, safeItem, statisticItemIndex);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstResult(int firstResult) {
        queryBuilder.setFirstResult(firstResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxResults(int maxResults) {
        queryBuilder.setMaxResults(maxResults);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCachable() {
        return queryBuilder.isCachable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCachable(boolean cachable) {
        queryBuilder.setCachable(cachable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRestriction(HQLRestriction restriction) {
        queryBuilder.addRestriction(restriction);
    }