package net.ihe.gazelle.hql.restrictions;

import net.ihe.gazelle.hql.HQLQueryBuilder;
import net.ihe.gazelle.hql.HQLRestriction;
import net.ihe.gazelle.hql.beans.HQLRestrictionValues;

public class HQLRestrictionLike implements HQLRestriction {

	private String path;
	private String value;
	private HQLRestrictionLikeMatchMode matchMode;

	public HQLRestrictionLike(String path, String value, HQLRestrictionLikeMatchMode matchMode) {
		super();
		this.path = path;
		this.value = value.toLowerCase();
		this.matchMode = matchMode;
	}

	public HQLRestrictionLike(String path, String value) {
		super();
		this.path = path;
		this.value = value.toLowerCase();
		this.matchMode = HQLRestrictionLikeMatchMode.ANYWHERE;
	}

	@Override
	public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
		sb.append("lower(");
		sb.append(queryBuilder.getShortProperty(path));
		sb.append(") like :");
		sb.append(values.addValue(path, matchMode.toHQL(value)));
	}

}
