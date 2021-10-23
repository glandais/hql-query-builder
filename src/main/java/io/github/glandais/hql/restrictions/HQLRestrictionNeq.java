package io.github.glandais.hql.restrictions;

import io.github.glandais.hql.HQLQueryBuilder;
import io.github.glandais.hql.HQLRestriction;
import io.github.glandais.hql.NullValue;
import io.github.glandais.hql.beans.HQLRestrictionValues;

public class HQLRestrictionNeq implements HQLRestriction {

    private final String path;
    private final Object value;

    public HQLRestrictionNeq(String path, Object value) {
        super();
        this.path = path;
        this.value = value;
    }

    @Override
    public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
        if (value != null && !NullValue.NULL_VALUE.equals(value)) {
            sb.append(queryBuilder.getShortProperty(path));
            sb.append(" != :").append(values.addValue(path, value));
        } else {
            sb.append(queryBuilder.getShortProperty(path)).append(" is not null");
        }
    }

}
