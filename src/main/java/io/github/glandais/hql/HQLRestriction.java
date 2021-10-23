package io.github.glandais.hql;

import io.github.glandais.hql.beans.HQLRestrictionValues;

import java.io.Serializable;

public interface HQLRestriction extends Serializable {

    void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb);

}
