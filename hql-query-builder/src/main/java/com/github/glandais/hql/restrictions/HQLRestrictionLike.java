package com.github.glandais.hql.restrictions;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.HQLRestriction;
import com.github.glandais.hql.beans.HQLRestrictionValues;

public class HQLRestrictionLike implements HQLRestriction {

    private final String path;
    private final String value;
    private final HQLRestrictionLikeMatchMode matchMode;

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
