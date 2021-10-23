package io.github.glandais.hql.restrictions;

import io.github.glandais.hql.HQLRestriction;

public class HQLRestrictionOr extends HQLRestrictionLogical {

    HQLRestrictionOr(HQLRestriction... restrictions) {
        super(restrictions);
    }

    @Override
    public String getLogicalOperator() {
        return "OR";
    }

}
