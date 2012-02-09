package net.ihe.gazelle.common.filter.hql;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

public class HQLRestrictionIn implements HQLRestriction {

	private String path;
	private Collection<?> elements;

	HQLRestrictionIn(String path, Collection<?> elements) {
		super();
		this.path = path;
		if (elements == null) {
			this.elements = CollectionUtils.EMPTY_COLLECTION;
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
