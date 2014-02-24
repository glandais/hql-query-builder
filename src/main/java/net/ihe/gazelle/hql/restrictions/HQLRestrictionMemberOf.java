package net.ihe.gazelle.hql.restrictions;

import net.ihe.gazelle.hql.HQLQueryBuilder;
import net.ihe.gazelle.hql.HQLRestriction;
import net.ihe.gazelle.hql.NullValue;
import net.ihe.gazelle.hql.beans.HQLRestrictionValues;

public class HQLRestrictionMemberOf implements HQLRestriction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String path;
	private Object value;

	public HQLRestrictionMemberOf(Object value, String path) {
		super();
		this.path = path;
		this.value = value;
	}

	@Override
	public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
		if (value != null && !NullValue.NULL_VALUE.equals(value)) {
			if (!(value instanceof String)){
				sb.append(" :").append(values.addValue(path, value)).append(" MEMBER OF ").append(queryBuilder.getShortProperty(path));
			}
			else {
				sb.append(" :").append(values.addValue(path, value)).append(" IN ").append("elements(" + queryBuilder.getShortProperty(path) + ")");
			}
		}
	}

}
