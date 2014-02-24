package net.ihe.gazelle.hql;

import java.io.Serializable;

import net.ihe.gazelle.hql.beans.HQLRestrictionValues;

public interface HQLRestriction extends Serializable {

	void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb);

}
