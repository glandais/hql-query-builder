package com.github.glandais.hql.paths;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.HQLRestriction;
import com.github.glandais.hql.beans.HQLStatisticItem;

import java.io.Serializable;
import java.util.List;

public abstract class HQLSafePath<T> implements Comparable<HQLSafePath<T>>, Serializable {

    protected String path;

    protected HQLQueryBuilder<?> queryBuilder;

    protected HQLSafePath(String path) {
        this.path = path;
    }

    public HQLSafePath(String path, HQLQueryBuilder<?> queryBuilder) {
        this.path = path;
        this.queryBuilder = queryBuilder;
    }

    @Override
    public String toString() {
        return this.path;
    }

    public String getPath() {
        return path;
    }

    public HQLQueryBuilder<?> getQueryBuilder() {
        return queryBuilder;
    }

    protected void setQueryBuilder(HQLQueryBuilder<?> queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public HQLStatisticItem statisticItem(HQLRestriction restriction) {
        return new HQLStatisticItem(path, restriction);
    }

    public HQLStatisticItem statisticItem() {
        return new HQLStatisticItem(path);
    }

    public List<Object[]> getStatistics() {
        return queryBuilder.getStatistics(path);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HQLSafePath other = (HQLSafePath) obj;
        if (path == null) {
            return other.path == null;
        } else return path.equals(other.path);
    }

    public int compareTo(HQLSafePath<T> o) {
        return getPath().compareTo(o.getPath());
    }

}
