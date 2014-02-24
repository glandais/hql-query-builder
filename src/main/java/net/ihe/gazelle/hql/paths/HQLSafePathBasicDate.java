package net.ihe.gazelle.hql.paths;

import net.ihe.gazelle.hql.HQLQueryBuilder;

public class HQLSafePathBasicDate<T> extends HQLSafePathBasicNumber<T> {

	public HQLSafePathBasicDate(HQLSafePathEntity parent, String path,
			HQLQueryBuilder<?> queryBuilder, Class<?> type) {
		super(parent, path, queryBuilder, type);
	}

}
