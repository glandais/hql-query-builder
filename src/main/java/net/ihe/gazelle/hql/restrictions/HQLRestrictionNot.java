package net.ihe.gazelle.hql.restrictions;

import net.ihe.gazelle.hql.HQLQueryBuilder;
import net.ihe.gazelle.hql.HQLRestriction;
import net.ihe.gazelle.hql.beans.HQLRestrictionValues;

public class HQLRestrictionNot implements HQLRestriction {

	private HQLRestriction toNegate;

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
