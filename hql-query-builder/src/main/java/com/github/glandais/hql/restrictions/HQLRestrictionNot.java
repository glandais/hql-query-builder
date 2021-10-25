package com.github.glandais.hql.restrictions;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.HQLRestriction;
import com.github.glandais.hql.beans.HQLRestrictionValues;

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
