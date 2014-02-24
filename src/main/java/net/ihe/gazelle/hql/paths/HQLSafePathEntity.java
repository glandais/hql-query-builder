package net.ihe.gazelle.hql.paths;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ihe.gazelle.hql.HQLQueryBuilder;

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

	public Set<HQLSafePath<?>> getIdAttributes() {
		return new HashSet<HQLSafePath<?>>();
	}

	public Set<HQLSafePath<?>> getNotExportedAttributes() {
		return new HashSet<HQLSafePath<?>>();
	}

	public Set<HQLSafePath<?>> getExportableAttributes() {
		Set<HQLSafePath<?>> allAttributes = getAllAttributes();
		Set<HQLSafePath<?>> notExportedAttributes = getNotExportedAttributes();
		allAttributes.removeAll(notExportedAttributes);
		return allAttributes;
	}

}
