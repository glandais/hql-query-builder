package com.github.glandais.hql.paths;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.HQLRestriction;
import com.github.glandais.hql.restrictions.HQLRestrictionCompare;

public class HQLSafePathBasicNumber<T> extends HQLSafePathBasic<T> {

    public HQLSafePathBasicNumber(HQLSafePathEntity parent, String path,
                                  HQLQueryBuilder<?> queryBuilder, Class<?> type) {
        super(parent, path, queryBuilder, type);
    }

    public void ge(T value) {
        queryBuilder.addRestriction(geRestriction(value));
    }

    public HQLRestriction geRestriction(T value) {
        return new HQLRestrictionCompare(path, value, ">=");
    }

    public void le(T value) {
        queryBuilder.addRestriction(leRestriction(value));
    }

    public HQLRestriction leRestriction(T value) {
        return new HQLRestrictionCompare(path, value, "<=");
    }

    public void gt(T value) {
        queryBuilder.addRestriction(gtRestriction(value));
    }

    public HQLRestriction gtRestriction(T value) {
        return new HQLRestrictionCompare(path, value, ">");
    }

    public void lt(T value) {
        queryBuilder.addRestriction(ltRestriction(value));
    }

    public HQLRestriction ltRestriction(T value) {
        return new HQLRestrictionCompare(path, value, "<");
    }

}
