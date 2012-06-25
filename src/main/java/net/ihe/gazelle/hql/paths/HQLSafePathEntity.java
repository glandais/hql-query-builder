package net.ihe.gazelle.hql.paths;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.ihe.gazelle.hql.HQLQueryBuilder;
import net.ihe.gazelle.hql.exchange.common.DataException;

public abstract class HQLSafePathEntity<T> extends HQLSafePath<T> {

	public HQLSafePathEntity(String path, HQLQueryBuilder<?> queryBuilder) {
		super(path, queryBuilder);
	}

	public void addFetch() {
		queryBuilder.addFetch(path);
	}

	public abstract boolean isSingle();

	public abstract Class<?> getEntityClass();

	public List<String> getUniqueAttributeColumns() {
		return null;
	}

	public Map<String, HQLSafePath<?>> getSingleAttributes() {
		return new HashMap<String, HQLSafePath<?>>();
	}

	public Set<HQLSafePath<?>> getAllAttributes() {
		return new HashSet<HQLSafePath<?>>();
	}

	public Set<HQLSafePath<?>> getNotExportedAttributes() {
		return new HashSet<HQLSafePath<?>>();
	}

	public Set<HQLSafePath<?>> getUniqueAttributes() throws DataException {
		List<String> uniqueAttributeColumns = getUniqueAttributeColumns();
		if (uniqueAttributeColumns == null || uniqueAttributeColumns.size() == 0) {
			throw new DataException("No unique attribute for " + getEntityClass().getCanonicalName());
		}
		Map<String, HQLSafePath<?>> singleAttributes = getSingleAttributes();
		Set<HQLSafePath<?>> uniqueAttributes = new TreeSet<HQLSafePath<?>>();
		for (String uniqueAttributeColumn : uniqueAttributeColumns) {
			HQLSafePath<?> uniqueAttribute = singleAttributes.get(uniqueAttributeColumn);
			if (uniqueAttribute == null) {
				throw new DataException("Unique attribute " + uniqueAttributeColumn + " not found in "
						+ getEntityClass().getCanonicalName());
			}
			uniqueAttributes.add(uniqueAttribute);
		}
		return uniqueAttributes;
	}

}
