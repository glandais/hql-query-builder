package net.ihe.gazelle.hql;

import net.ihe.gazelle.hql.beans.HQLRestrictionValues;

public interface HQLRestriction {

	void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb);

}
