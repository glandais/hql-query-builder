package net.ihe.gazelle.hql.restrictions;

import net.ihe.gazelle.hql.HQLQueryBuilder;
import net.ihe.gazelle.hql.HQLRestriction;
import net.ihe.gazelle.hql.beans.HQLRestrictionValues;

abstract class HQLRestrictionLogical implements HQLRestriction {

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
			sb.append("(");
			restriction.toHQL(queryBuilder, values, sb);
			sb.append(")");
		}
		sb.append(")");
	}

	public abstract String getLogicalOperator();

}
