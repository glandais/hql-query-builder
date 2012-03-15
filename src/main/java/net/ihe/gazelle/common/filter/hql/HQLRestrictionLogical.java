package net.ihe.gazelle.common.filter.hql;

public abstract class HQLRestrictionLogical implements HQLRestriction {

	private HQLRestriction[] restrictions;

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
			restriction.toHQL(queryBuilder, values, sb);
		}
		sb.append(")");
	}

	public abstract String getLogicalOperator();

}
