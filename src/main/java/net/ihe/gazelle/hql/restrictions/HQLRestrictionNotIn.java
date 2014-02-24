package net.ihe.gazelle.hql.restrictions;

import java.util.ArrayList;
import java.util.Collection;

import net.ihe.gazelle.hql.HQLQueryBuilder;
import net.ihe.gazelle.hql.HQLRestriction;
import net.ihe.gazelle.hql.beans.HQLRestrictionValues;

public class HQLRestrictionNotIn implements HQLRestriction {

	private Collection<?> EMPTY_COLLECTION = new ArrayList(0);

	private String path;
	private Collection<?> elements;

	public HQLRestrictionNotIn(String path, Collection<?> elements) {
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
	public void toHQL(HQLQueryBuilder<?> queryBuilder,
			HQLRestrictionValues values, StringBuilder sb) {
		if (elements.isEmpty()) {
			sb.append(queryBuilder.getShortProperty(path)).append(
					" is not null");
		} else {
			sb.append(queryBuilder.getShortProperty(path));
			sb.append(" not in (:").append(values.addValue(path, elements))
					.append(")");
		}
	}

}
