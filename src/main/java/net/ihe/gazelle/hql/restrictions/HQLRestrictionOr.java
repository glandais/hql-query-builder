package net.ihe.gazelle.hql.restrictions;

import net.ihe.gazelle.hql.HQLRestriction;

public class HQLRestrictionOr extends HQLRestrictionLogical {

	HQLRestrictionOr(HQLRestriction... restrictions) {
		super(restrictions);
	}

	@Override
	public String getLogicalOperator() {
		return "OR";
	}

}
