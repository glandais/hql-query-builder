package io.github.glandais.hql.paths;

import io.github.glandais.hql.HQLQueryBuilder;
import io.github.glandais.hql.HQLRestriction;
import io.github.glandais.hql.restrictions.HQLRestrictionLike;
import io.github.glandais.hql.restrictions.HQLRestrictionLikeMatchMode;

public class HQLSafePathBasicString<T> extends HQLSafePathBasic<T> {

    public HQLSafePathBasicString(HQLSafePathEntity parent, String path, HQLQueryBuilder<?> queryBuilder, Class<?> type) {
        super(parent, path, queryBuilder, type);
    }

    public void like(String value, HQLRestrictionLikeMatchMode matchMode) {
        queryBuilder.addRestriction(likeRestriction(value, matchMode));
    }

    public HQLRestriction likeRestriction(String value, HQLRestrictionLikeMatchMode matchMode) {
        return new HQLRestrictionLike(path, value, matchMode);
    }

    public void like(String value) {
        queryBuilder.addRestriction(likeRestriction(value));
    }

    public HQLRestriction likeRestriction(String value) {
        return new HQLRestrictionLike(path, value);
    }

}
