package net.ihe.gazelle.hql.restrictions;

import net.ihe.gazelle.hql.HQLQueryBuilder;
import net.ihe.gazelle.hql.HQLRestriction;
import net.ihe.gazelle.hql.NullValue;
import net.ihe.gazelle.hql.beans.HQLRestrictionValues;

public class HQLRestrictionCompare implements HQLRestriction {

	private String path;
	private Object value;
	private String operator;

	public HQLRestrictionCompare(String path, Object value, String operator) {
		super();
		this.path = path;
		this.value = value;
		this.operator = operator;
	}

	@Override
	public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
		if (value != null && !NullValue.NULL_VALUE.equals(value)) {
			sb.append(queryBuilder.getShortProperty(path));
			sb.append(" ").append(operator).append(" :").append(values.addValue(path, value));
		}
	}
}
