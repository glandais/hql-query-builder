package net.ihe.gazelle.common.filter.hql;

public class HQLRestrictionAnd extends HQLRestrictionLogical {

	public HQLRestrictionAnd(HQLRestriction... restrictions) {
		super(restrictions);
	}

	@Override
	public String getLogicalOperator() {
		return "AND";
	}

}
