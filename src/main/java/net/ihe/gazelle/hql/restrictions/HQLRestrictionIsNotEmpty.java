package net.ihe.gazelle.hql.restrictions;

import net.ihe.gazelle.hql.HQLQueryBuilder;
import net.ihe.gazelle.hql.HQLRestriction;
import net.ihe.gazelle.hql.beans.HQLRestrictionValues;

public class HQLRestrictionIsNotEmpty implements HQLRestriction{
	
	private String propertyName;

	HQLRestrictionIsNotEmpty(String propertyName) {
		super();
		this.propertyName = propertyName;
	}

	@Override
	public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
		sb.append(queryBuilder.getShortProperty(propertyName)).append(" is not empty");
	}

}
