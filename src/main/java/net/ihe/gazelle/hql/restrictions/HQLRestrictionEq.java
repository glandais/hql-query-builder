package net.ihe.gazelle.hql.restrictions;

import net.ihe.gazelle.hql.HQLQueryBuilder;
import net.ihe.gazelle.hql.HQLRestriction;
import net.ihe.gazelle.hql.NullValue;
import net.ihe.gazelle.hql.beans.HQLRestrictionValues;

public class HQLRestrictionEq implements HQLRestriction {

	private String path;
	private Object value;

	public HQLRestrictionEq(String path, Object value) {
		super();
		this.path = path;
		this.value = value;
	}

	@Override
	public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
		if (value != null && !NullValue.NULL_VALUE.equals(value)) {
			sb.append(queryBuilder.getShortProperty(path));
			sb.append(" = :").append(values.addValue(path, value));
		} else {
			sb.append(queryBuilder.getShortProperty(path)).append(" is null");
		}
	}

}
