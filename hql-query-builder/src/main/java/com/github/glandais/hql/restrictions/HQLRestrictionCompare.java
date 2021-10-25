package com.github.glandais.hql.restrictions;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.HQLRestriction;
import com.github.glandais.hql.NullValue;
import com.github.glandais.hql.beans.HQLRestrictionValues;

public class HQLRestrictionCompare implements HQLRestriction {

    private final String path;
    private final Object value;
    private final String operator;

    public HQLRestrictionCompare(String path, Object value, String operator) {
        super();
        this.path = path;
        this.value = value;
        this.operator = operator;
    }

    @Override
    public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
        if (value != null && !NullValue.NULL_VALUE.equals(value)) {
            sb.append(queryBuilder.getShortProperty(path));
            sb.append(" ").append(operator).append(" :").append(values.addValue(path, value));
        }
    }
}
