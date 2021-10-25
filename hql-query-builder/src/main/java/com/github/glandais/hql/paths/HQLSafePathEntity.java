package com.github.glandais.hql.paths;

import com.github.glandais.hql.HQLQueryBuilder;

public abstract class HQLSafePathEntity<T> extends HQLSafePath<T> {

    public HQLSafePathEntity(String path, HQLQueryBuilder<?> queryBuilder) {
        super(path, queryBuilder);
    }

    public void addFetch() {
        queryBuilder.addFetch(path);
    }

    public abstract Class<?> getEntityClass();

}
