package net.ihe.gazelle.common.filter.hql;

public class HQLRestrictionAnd implements HQLRestriction {

	private HQLRestriction restriction1;
	private HQLRestriction restriction2;

	HQLRestrictionAnd(HQLRestriction restriction1, HQLRestriction restriction2) {
		super();
		this.restriction1 = restriction1;
		this.restriction2 = restriction2;
	}

	@Override
	public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
		sb.append("(");
		restriction1.toHQL(queryBuilder, values, sb);
		sb.append(" AND ");
		restriction2.toHQL(queryBuilder, values, sb);
		sb.append(")");
	}

}
