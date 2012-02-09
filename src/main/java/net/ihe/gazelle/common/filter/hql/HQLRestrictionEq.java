package net.ihe.gazelle.common.filter.hql;

import net.ihe.gazelle.common.filter.NullValue;

public class HQLRestrictionEq implements HQLRestriction {

	private String path;
	private Object value;

	HQLRestrictionEq(String path, Object value) {
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
