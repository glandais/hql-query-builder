package com.github.glandais.hql.paths;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.HQLRestriction;
import com.github.glandais.hql.restrictions.*;

import java.util.Collection;
import java.util.List;

public class HQLSafePathBasic<T> extends HQLSafePath<T> {

    private final Class<?> type;
    private final HQLSafePathEntity parent;

    public HQLSafePathBasic(HQLSafePathEntity parent, String path, HQLQueryBuilder<?> queryBuilder, Class<?> type) {
        super(path, queryBuilder);
        this.type = type;
        this.parent = parent;
    }

    public Class<?> getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public List<T> getListDistinct() {
        return (List<T>) queryBuilder.getListDistinct(path);
    }

    public void order(boolean ascending) {
        queryBuilder.addOrder(path, ascending);
    }

    public void order(boolean ascending, boolean lowerCase) {
        queryBuilder.addOrder(path, ascending, lowerCase);
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

    public int getCountOnPath() {
        return queryBuilder.getCountOnPath(path);
    }

    public int getCountDistinctOnPath() {
        return queryBuilder.getCountDistinctOnPath(path);
    }

    public HQLRestriction eqRestriction(T value) {
        return new HQLRestrictionEq(path, value);
    }

    public HQLRestriction neqRestriction(T value) {
        return new HQLRestrictionNeq(path, value);
    }

    public HQLRestriction inRestriction(Collection<? extends T> elements) {
        return new HQLRestrictionIn(path, elements);
    }

    public HQLRestriction ninRestriction(Collection<? extends T> elements) {
        return new HQLRestrictionNotIn(path, elements);
    }

    public HQLRestriction isNotNullRestriction() {
        return new HQLRestrictionIsNotNull(path);
    }

    public HQLRestriction isNullRestriction() {
        return new HQLRestrictionIsNull(path);
    }

}
