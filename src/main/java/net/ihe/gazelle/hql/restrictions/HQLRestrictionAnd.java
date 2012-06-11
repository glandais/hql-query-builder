package net.ihe.gazelle.hql.restrictions;

import net.ihe.gazelle.hql.HQLRestriction;

public class HQLRestrictionAnd extends HQLRestrictionLogical {

	public HQLRestrictionAnd(HQLRestriction... restrictions) {
		super(restrictions);
	}

	@Override
	public String getLogicalOperator() {
		return "AND";
	}

}
