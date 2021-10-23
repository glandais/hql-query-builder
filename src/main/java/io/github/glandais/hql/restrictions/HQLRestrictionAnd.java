package io.github.glandais.hql.restrictions;

import io.github.glandais.hql.HQLRestriction;

public class HQLRestrictionAnd extends HQLRestrictionLogical {

    public HQLRestrictionAnd(HQLRestriction... restrictions) {
        super(restrictions);
    }

    @Override
    public String getLogicalOperator() {
        return "AND";
    }

}
