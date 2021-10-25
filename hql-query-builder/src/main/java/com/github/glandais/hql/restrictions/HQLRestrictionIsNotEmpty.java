package com.github.glandais.hql.restrictions;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.HQLRestriction;
import com.github.glandais.hql.beans.HQLRestrictionValues;

public class HQLRestrictionIsNotEmpty implements HQLRestriction {

    private final String propertyName;

    HQLRestrictionIsNotEmpty(String propertyName) {
        super();
        this.propertyName = propertyName;
    }

    @Override
    public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
        sb.append(queryBuilder.getShortProperty(propertyName)).append(" is not empty");
    }

}
