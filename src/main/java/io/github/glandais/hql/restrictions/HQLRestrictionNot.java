package io.github.glandais.hql.restrictions;

import io.github.glandais.hql.HQLQueryBuilder;
import io.github.glandais.hql.HQLRestriction;
import io.github.glandais.hql.beans.HQLRestrictionValues;

public class HQLRestrictionNot implements HQLRestriction {

    private final HQLRestriction toNegate;

    public HQLRestrictionNot(HQLRestriction toNegate) {
        super();
        this.toNegate = toNegate;
    }

    @Override
    public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
        sb.append("not(");
        toNegate.toHQL(queryBuilder, values, sb);
        sb.append(")");
    }

}
