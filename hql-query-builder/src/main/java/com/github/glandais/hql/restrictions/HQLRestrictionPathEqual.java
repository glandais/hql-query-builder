package com.github.glandais.hql.restrictions;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.HQLRestriction;
import com.github.glandais.hql.beans.HQLRestrictionValues;

public class HQLRestrictionPathEqual implements HQLRestriction {

    private final String path1;
    private final String path2;

    public HQLRestrictionPathEqual(String path1, String path2) {
        super();
        this.path1 = path1;
        this.path2 = path2;
    }

    @Override
    public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
        sb.append(queryBuilder.getShortProperty(path1));
        sb.append(" = ");
        sb.append(queryBuilder.getShortProperty(path2));
    }

}
