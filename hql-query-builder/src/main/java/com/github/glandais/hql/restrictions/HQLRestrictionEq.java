package com.github.glandais.hql.restrictions;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.HQLRestriction;
import com.github.glandais.hql.NullValue;
import com.github.glandais.hql.beans.HQLRestrictionValues;

public class HQLRestrictionEq implements HQLRestriction {

    private final String path;
    private final Object value;

    public HQLRestrictionEq(String path, Object value) {
        super();
        this.path = path;
        this.value = value;
    }

    @Override
    public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
        if (value != null && !NullValue.NULL_VALUE.equals(value)) {
            sb.append(queryBuilder.getShortProperty(path));
            sb.append(" = :").append(values.addValue(path, value));
        } else {
            sb.append(queryBuilder.getShortProperty(path)).append(" is null");
        }
    }

}
