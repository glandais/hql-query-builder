package io.github.glandais.hql.restrictions;

import io.github.glandais.hql.HQLQueryBuilder;
import io.github.glandais.hql.HQLRestriction;
import io.github.glandais.hql.beans.HQLRestrictionValues;

abstract class HQLRestrictionLogical implements HQLRestriction {

    private final HQLRestriction[] restrictions;

    HQLRestrictionLogical(HQLRestriction... restrictions) {
        super();
        this.restrictions = restrictions;
    }

    @Override
    public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
        boolean first = true;
        sb.append("(");
        for (HQLRestriction restriction : restrictions) {
            if (!first) {
                sb.append(" ");
                sb.append(getLogicalOperator());
                sb.append(" ");
            }
            first = false;
            sb.append("(");
            restriction.toHQL(queryBuilder, values, sb);
            sb.append(")");
        }
        sb.append(")");
    }

    public abstract String getLogicalOperator();

}
