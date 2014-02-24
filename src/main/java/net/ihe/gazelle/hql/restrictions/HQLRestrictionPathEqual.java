package net.ihe.gazelle.hql.restrictions;

import net.ihe.gazelle.hql.HQLQueryBuilder;
import net.ihe.gazelle.hql.HQLRestriction;
import net.ihe.gazelle.hql.beans.HQLRestrictionValues;

public class HQLRestrictionPathEqual implements HQLRestriction {

	private String path1;
	private String path2;

	public HQLRestrictionPathEqual(String path1, String path2) {
		super();
		this.path1 = path1;
		this.path2 = path2;
	}

	@Override
	public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
		sb.append(queryBuilder.getShortProperty(path1));
		sb.append(" = ");
		sb.append(queryBuilder.getShortProperty(path2));
	}

}
