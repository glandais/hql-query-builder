package com.github.glandais.hql.paths;

import com.github.glandais.hql.HQLQueryBuilder;

public class HQLSafePathBasicDate<T> extends HQLSafePathBasicNumber<T> {

    public HQLSafePathBasicDate(HQLSafePathEntity parent, String path,
                                HQLQueryBuilder<?> queryBuilder, Class<?> type) {
        super(parent, path, queryBuilder, type);
    }

}
