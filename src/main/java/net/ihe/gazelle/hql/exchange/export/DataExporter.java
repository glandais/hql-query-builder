package net.ihe.gazelle.hql.exchange.export;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.ihe.gazelle.hql.exchange.common.DataException;
import net.ihe.gazelle.hql.exchange.common.DataFormatter;
import net.ihe.gazelle.hql.paths.HQLSafePath;
import net.ihe.gazelle.hql.paths.HQLSafePathBasic;
import net.ihe.gazelle.hql.paths.HQLSafePathEntity;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataExporter {

	private static Logger log = LoggerFactory.getLogger(DataExporter.class);

	public DataExporter() {
		super();
	}

	public void writeElement(Object element, DataFormatter dataFormatter, OutputStream os) throws DataException {
		try {
			MultiKeyMap dictionnary = new MultiKeyMap();
			Map<MultiKey, Integer> internalIds = new HashMap<MultiKey, Integer>();

			write(element, dictionnary, internalIds);

			dataFormatter.writeStart(os);
			writeDictionnary(dictionnary, internalIds, dataFormatter);
			dataFormatter.writeEnd();

		} catch (DataException e) {
			throw e;
		} catch (Exception e) {
			log.error("Failed to export object", e);
			throw new DataException(e);
		}
	}

	private Integer write(Object element, MultiKeyMap dictionnary, Map<MultiKey, Integer> internalIds)
			throws DataException {
		HQLSafePathEntity<?> hqlEntity = getHQLEntity(element);

		// Lookup for unique attributes
		Set<HQLSafePath<?>> uniqueAttributes = hqlEntity.getUniqueAttributes();
		if (uniqueAttributes.size() == 0) {
			throw new DataException("No unique attributes for " + element + "...");
		}

		Object[] values = new Object[uniqueAttributes.size() + 1];
		values[0] = hqlEntity.getEntityClass().getCanonicalName();

		int i = 1;
		for (HQLSafePath<?> hqlSafePath : uniqueAttributes) {
			String propertyName = getPropertyName(hqlSafePath);
			Object value = evaluate(element, propertyName);
			if (hqlSafePath instanceof HQLSafePathBasic) {
				values[i] = value;
			} else {
				values[i] = write(value, dictionnary, internalIds);
			}
			i++;
		}

		MultiKey key = new MultiKey(values);
		if (!dictionnary.containsKey(key)) {
			dictionnary.put(key, null);
			internalIds.put(key, new Integer(internalIds.size() + 1));
			Map<String, Object> dictionnaryValues = writeAddToDictionnary(dictionnary, internalIds, hqlEntity);
			dictionnary.put(key, dictionnaryValues);
		}
		return internalIds.get(key);
	}

	protected Map<String, Object> writeAddToDictionnary(MultiKeyMap dictionnary, Map<MultiKey, Integer> internalIds,
			HQLSafePathEntity<?> hqlEntity) throws DataException {
		Map<String, Object> values = new HashMap<String, Object>();

		Object uniqueResult = hqlEntity.getQueryBuilder().getUniqueResult();
		Set<HQLSafePath<?>> allAttributes = hqlEntity.getAllAttributes();
		Set<HQLSafePath<?>> notExportedAttributes = hqlEntity.getNotExportedAttributes();
		allAttributes.removeAll(notExportedAttributes);

		for (HQLSafePath attribute : allAttributes) {
			Object value = null;

			String propertyName = getPropertyName(attribute);
			if (attribute instanceof HQLSafePathBasic) {
				value = evaluate(uniqueResult, propertyName);
			} else if (attribute instanceof HQLSafePathEntity) {

				HQLSafePathEntity hqlSafePathEntity = (HQLSafePathEntity) attribute;

				if (hqlSafePathEntity.isSingle()) {

					Object object = evaluate(uniqueResult, propertyName);
					if (object == null) {
						value = null;
					} else {
						value = write(object, dictionnary, internalIds);
					}

				} else {
					try {
						List<Integer> children = new ArrayList<Integer>();

						Collection<Object> objects = (Collection<Object>) evaluate(uniqueResult, propertyName);
						for (Object object : objects) {
							Integer childValue = null;
							if (object == null) {
								childValue = null;
							} else {
								childValue = write(object, dictionnary, internalIds);
							}
							children.add(childValue);
						}

						value = children;
					} catch (DataException e) {
						log.error("No processing " + attribute);
					}
				}
			}

			values.put(propertyName, value);
		}

		return values;
	}

	protected Object evaluate(Object element, String propertyName) throws DataException {
		Object value;
		try {
			value = PropertyUtils.getProperty(element, propertyName);
		} catch (Exception e) {
			throw new DataException("Failed to get property on path " + propertyName, e);
		}
		return value;
	}

	protected String getPropertyName(HQLSafePath<?> hqlSafePath) {
		String path = hqlSafePath.getPath();
		String propertyName = path.substring(path.lastIndexOf('.') + 1);
		return propertyName;
	}

	protected HQLSafePathEntity<?> getHQLEntity(Object element) throws DataException {
		String canonicalName = element.getClass().getCanonicalName();
		String entityName = canonicalName + "Query";
		Class<HQLSafePathEntity<?>> safePathEntityClass;
		try {
			safePathEntityClass = (Class<HQLSafePathEntity<?>>) element.getClass().forName(entityName);
		} catch (ClassNotFoundException e1) {
			throw new DataException(e1);
		}

		HQLSafePathEntity<?> safePathEntity;
		try {
			safePathEntity = safePathEntityClass.newInstance();
		} catch (Exception e) {
			throw new DataException("Failed to init", e);
		}
		return safePathEntity;
	}

	private void writeDictionnary(MultiKeyMap dictionnary, Map<MultiKey, Integer> internalIds,
			DataFormatter dataFormatter) throws DataException {
		Set entrySet = dictionnary.entrySet();
		for (Object object : entrySet) {
			Map.Entry entry = (Entry) object;
			MultiKey key = (MultiKey) entry.getKey();
			String className = (String) key.getKey(0);
			Integer internalId = internalIds.get(key);
			Map<String, Object> value = (Map<String, Object>) entry.getValue();
			writeDictionnaryEntry(className, internalId, value, dataFormatter);
		}
	}

	private void writeDictionnaryEntry(String className, Integer internalId, Map<String, Object> value,
			DataFormatter dataFormatter) throws DataException {
		dataFormatter.writeStartSubValue("entry");

		dataFormatter.writeValue("class", className);
		dataFormatter.writeValue("internalId", internalId);

		dataFormatter.writeStartSubValue("value");
		Set<Entry<String, Object>> entries = value.entrySet();
		for (Entry<String, Object> entry : entries) {
			writeDictionnaryObject(entry.getKey(), entry.getValue(), dataFormatter);
		}
		dataFormatter.writeEndSubValue();

		dataFormatter.writeEndSubValue();
	}

	private void writeDictionnaryObject(String key, Object value, DataFormatter dataFormatter) throws DataException {
		if (value instanceof List) {
			dataFormatter.writeStartSubValue(key);
			List<?> list = (List<?>) value;
			dataFormatter.writeValue("size", list.size());
			int i = 0;
			for (Object listElement : list) {
				if (listElement == null) {
					dataFormatter.writeValue("item_" + Integer.toString(i), null);
				} else {
					dataFormatter.writeValue("item_" + Integer.toString(i), listElement);
				}
				i++;
			}
			dataFormatter.writeEndSubValue();
		} else {
			dataFormatter.writeValue(key, value);
		}
	}

}
