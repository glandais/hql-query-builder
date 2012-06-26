package net.ihe.gazelle.hql.exchange.export;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
			dataFormatter.writeStart(os);

			MultiKeyMap dictionnary = new MultiKeyMap();

			write("", element, dictionnary);

			writeDictionnary(dictionnary, dataFormatter);

			dataFormatter.writeEnd();
		} catch (Exception e) {
			log.error("Failed to export object", e);
			throw new DataException(e);
		}
	}

	protected MultiKey write(String path, Object element, MultiKeyMap dictionnary) throws DataException {
		System.out.println(path + " : " + element);
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

		Set<HQLSafePath<?>> uniqueAttributes = safePathEntity.getUniqueAttributes();
		Object[] values = new Object[uniqueAttributes.size() + 1];
		values[0] = safePathEntity.getEntityClass().getCanonicalName();

		int i = 1;
		for (HQLSafePath<?> hqlSafePath : uniqueAttributes) {
			values[i] = evaluate(element, hqlSafePath);
			hqlSafePath.getQueryBuilder().addEq(hqlSafePath.getPath(), values[i]);
			i++;
		}

		MultiKey multiKey = new MultiKey(values);
		// is item already known?
		if (!dictionnary.containsKey(multiKey)) {
			dictionnary.put(multiKey, null);
			Map<String, Object> dictionnaryValues = writeAddToDictionnary(path, dictionnary, safePathEntity);
			dictionnary.put(multiKey, dictionnaryValues);
		}

		return multiKey;
	}

	protected Map<String, Object> writeAddToDictionnary(String path, MultiKeyMap dictionnary,
			HQLSafePathEntity<?> safePathEntity) throws DataException {
		Map<String, Object> values = new HashMap<String, Object>();

		Object uniqueResult = safePathEntity.getQueryBuilder().getUniqueResult();
		Set<HQLSafePath<?>> allAttributes = safePathEntity.getAllAttributes();
		Set<HQLSafePath<?>> notExportedAttributes = safePathEntity.getNotExportedAttributes();
		allAttributes.removeAll(notExportedAttributes);
		for (HQLSafePath attribute : allAttributes) {
			if (attribute instanceof HQLSafePathBasic) {
				HQLSafePathBasic hqlSafePathBasic = (HQLSafePathBasic) attribute;
				values.put(attribute.getPath(), evaluate(uniqueResult, hqlSafePathBasic));
			} else if (attribute instanceof HQLSafePathEntity) {
				HQLSafePathEntity hqlSafePathEntity = (HQLSafePathEntity) attribute;
				if (hqlSafePathEntity.isSingle()) {

					Object object = evaluate(uniqueResult, attribute);
					if (object == null) {
						values.put(attribute.getPath(), null);
					} else {
						Class<HQLSafePathEntity<?>> pathClass = (Class<HQLSafePathEntity<?>>) hqlSafePathEntity
								.getClass();
						values.put(attribute.getPath(),
								write(path + "." + getPropertyName(attribute), object, dictionnary));
					}

				} else {
					try {
						List<MultiKey> children = new ArrayList<MultiKey>();

						Collection<Object> objects = (Collection<Object>) evaluate(uniqueResult, attribute);
						Class<HQLSafePathEntity<?>> pathClass = (Class<HQLSafePathEntity<?>>) hqlSafePathEntity
								.getClass();
						for (Object object : objects) {
							if (object == null) {
								children.add(null);
							} else {
								MultiKey childMultiKey = write(path + "." + getPropertyName(attribute), object,
										dictionnary);
								children.add(childMultiKey);
							}
						}

						values.put(attribute.getPath(), children);
					} catch (DataException e) {
						log.error("No processing " + attribute);
					}
				}
			}
		}

		return values;
	}

	protected Object evaluate(Object element, HQLSafePath<?> hqlSafePath) throws DataException {
		String propertyName = getPropertyName(hqlSafePath);
		Object value;
		try {
			value = PropertyUtils.getProperty(element, propertyName);
		} catch (Exception e) {
			throw new DataException("Failed to get property on path " + hqlSafePath, e);
		}
		return value;
	}

	private String getPropertyName(HQLSafePath<?> hqlSafePath) {
		String path = hqlSafePath.getPath();
		String propertyName = path.substring(path.lastIndexOf('.') + 1);
		return propertyName;
	}

	protected void writeDictionnary(MultiKeyMap dictionnary, DataFormatter dataFormatter) throws DataException {
		Set entrySet = dictionnary.entrySet();
		for (Object object : entrySet) {
			Map.Entry entry = (Entry) object;
			MultiKey key = (MultiKey) entry.getKey();
			Map<String, Object> value = (Map<String, Object>) entry.getValue();
			dataFormatter.writeStartSubValue("entry");
			writeDictionnaryMultiKey(key, dataFormatter);
			writeDictionnaryValue(value, dataFormatter);
			dataFormatter.writeEndSubValue();
		}
	}

	protected void writeDictionnaryMultiKey(MultiKey key, DataFormatter dataFormatter) throws DataException {
		Object[] keys = key.getKeys();
		int i = 0;
		for (Object object : keys) {
			if (i == 0) {
				dataFormatter.writeValue("class", object);
			} else {
				dataFormatter.writeValue("key_" + i, object);
			}
			i++;
		}
	}

	protected void writeDictionnaryValue(Map<String, Object> valueMap, DataFormatter dataFormatter)
			throws DataException {
		dataFormatter.writeStartSubValue("value");
		Set<Entry<String, Object>> entrySet = valueMap.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey();

			Object value = entry.getValue();
			if (value instanceof MultiKey) {
				dataFormatter.writeStartSubValue(key);
				dataFormatter.writeValue("type", "reference");
				MultiKey multiKey = (MultiKey) value;
				writeDictionnaryMultiKey(multiKey, dataFormatter);
				dataFormatter.writeEndSubValue();
			} else if (value instanceof List) {
				dataFormatter.writeStartSubValue(key);
				dataFormatter.writeValue("type", "referenceList");
				List list = (List) value;
				dataFormatter.writeValue("size", list.size());
				int i = 0;
				for (Object listElement : list) {
					if (listElement == null) {
						dataFormatter.writeValue("item_" + Integer.toString(i), null);
					} else {
						dataFormatter.writeStartSubValue("item_" + Integer.toString(i));
						MultiKey multiKey = (MultiKey) listElement;
						writeDictionnaryMultiKey(multiKey, dataFormatter);
						dataFormatter.writeEndSubValue();
					}
					i++;
				}
				dataFormatter.writeEndSubValue();
			} else {
				dataFormatter.writeValue(key, value);
			}

		}
		dataFormatter.writeEndSubValue();
	}

}
