package net.ihe.gazelle.common.filter.hql;

public class HQLRestrictionIsNotNull implements HQLRestriction {

	private String propertyName;

	HQLRestrictionIsNotNull(String propertyName) {
		super();
		this.propertyName = propertyName;
	}

	@Override
	public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
		sb.append(queryBuilder.getShortProperty(propertyName)).append(" is not null");
	}

}
