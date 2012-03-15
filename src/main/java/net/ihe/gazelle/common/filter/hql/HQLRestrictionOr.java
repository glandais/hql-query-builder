package net.ihe.gazelle.common.filter.hql;

public class HQLRestrictionOr extends HQLRestrictionLogical {

	public HQLRestrictionOr(HQLRestriction... restrictions) {
		super(restrictions);
	}

	@Override
	public String getLogicalOperator() {
		return "OR";
	}

}
