package io.github.glandais.hql.restrictions;

import io.github.glandais.hql.HQLQueryBuilder;
import io.github.glandais.hql.HQLRestriction;
import io.github.glandais.hql.beans.HQLRestrictionValues;

public class HQLRestrictionIsNull implements HQLRestriction {

    private final String propertyName;

    public HQLRestrictionIsNull(String propertyName) {
        super();
        this.propertyName = propertyName;
    }

    @Override
    public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
        sb.append(queryBuilder.getShortProperty(propertyName)).append(" is null");
    }

}
