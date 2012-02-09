package net.ihe.gazelle.common.filter.hql;

public interface HQLRestriction {

	void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb);
	
}
