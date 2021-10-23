package io.github.glandais.hql.restrictions;

import io.github.glandais.hql.HQLQueryBuilder;
import io.github.glandais.hql.HQLRestriction;
import io.github.glandais.hql.beans.HQLRestrictionValues;

import java.util.ArrayList;
import java.util.Collection;

public class HQLRestrictionIn implements HQLRestriction {

    private final Collection<?> EMPTY_COLLECTION = new ArrayList(0);

    private final String path;
    private final Collection<?> elements;

    public HQLRestrictionIn(String path, Collection<?> elements) {
        super();
        this.path = path;
        if (elements == null) {
            this.elements = EMPTY_COLLECTION;
        } else {
            this.elements = elements;
        }
    }

    public String getPath() {
        return path;
    }

    public Collection<?> getElements() {
        return elements;
    }

    @Override
    public void toHQL(HQLQueryBuilder<?> queryBuilder, HQLRestrictionValues values, StringBuilder sb) {
        if (elements.isEmpty()) {
            sb.append(queryBuilder.getShortProperty(path)).append(" is null");
        } else {
            sb.append(queryBuilder.getShortProperty(path));
            sb.append(" in (:").append(values.addValue(path, elements)).append(")");
        }
    }

}
