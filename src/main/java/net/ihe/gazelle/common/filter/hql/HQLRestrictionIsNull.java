package net.ihe.gazelle.common.filter.hql;

public class HQLRestrictionIsNull implements HQLRestriction {

	private String propertyName;

	HQLRestrictionIsNull(String propertyName) {
		super();
		this.propertyName = propertyName;
	}

	@Override
	public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
		sb.append(queryBuilder.getShortProperty(propertyName)).append(" is null");
	}

}
