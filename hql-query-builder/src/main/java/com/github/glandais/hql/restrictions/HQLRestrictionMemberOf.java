package com.github.glandais.hql.restrictions;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.HQLRestriction;
import com.github.glandais.hql.NullValue;
import com.github.glandais.hql.beans.HQLRestrictionValues;

public class HQLRestrictionMemberOf implements HQLRestriction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String path;
    private final Object value;

    public HQLRestrictionMemberOf(Object value, String path) {
        super();
        this.path = path;
        this.value = value;
    }

    @Override
    public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
        if (value != null && !NullValue.NULL_VALUE.equals(value)) {
            if (!(value instanceof String)) {
                sb.append(" :").append(values.addValue(path, value)).append(" MEMBER OF ").append(queryBuilder.getShortProperty(path));
            } else {
                sb.append(" :").append(values.addValue(path, value)).append(" IN ").append("elements(" + queryBuilder.getShortProperty(path) + ")");
            }
        }
    }

}
