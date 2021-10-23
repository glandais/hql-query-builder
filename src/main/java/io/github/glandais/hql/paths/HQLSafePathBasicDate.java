package io.github.glandais.hql.paths;

import io.github.glandais.hql.HQLQueryBuilder;

public class HQLSafePathBasicDate<T> extends HQLSafePathBasicNumber<T> {

    public HQLSafePathBasicDate(HQLSafePathEntity parent, String path,
                                HQLQueryBuilder<?> queryBuilder, Class<?> type) {
        super(parent, path, queryBuilder, type);
    }

}
