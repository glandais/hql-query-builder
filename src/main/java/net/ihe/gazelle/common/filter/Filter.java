/*******************************************************************************
 * Copyright 2011 IHE International (http://www.ihe.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.ihe.gazelle.common.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;

import net.ihe.gazelle.EntityManagerService;
import net.ihe.gazelle.common.filter.action.DatatableStateHolderBean;
import net.ihe.gazelle.common.filter.criterion.Criterion;
import net.ihe.gazelle.common.filter.criterion.PropertyCriterion;
import net.ihe.gazelle.common.filter.criterion.ValueFormatter;
import net.ihe.gazelle.common.filter.hql.HQLQueryBuilder;
import net.ihe.gazelle.common.filter.hql.HQLStatistic;
import net.ihe.gazelle.common.filter.hql.HQLStatisticItem;
import net.ihe.gazelle.common.filter.util.MapNotifier;
import net.ihe.gazelle.common.filter.util.MapNotifierListener;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.jboss.seam.Component;
import org.richfaces.model.Ordering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Filter<E> implements MapNotifierListener {

	private static Logger log = LoggerFactory.getLogger(Filter.class);

	protected EntityFiltrable<E> entity;

	protected Map<String, Criterion<E, ?>> criterions = new HashMap<String, Criterion<E, ?>>();

	// Value selected for each criterion
	protected MapNotifier filterValues;

	// Used for list with statistics
	private boolean refreshListStatistics;
	protected List<HQLStatisticItem> statisticItems;
	private List<HQLStatistic<E>> listWithStatistics;

	// String value for each criterion (only reliable on read)
	protected Map<String, Object> stringValues;

	// Data model calling this filter, can be null
	// Used to back call the data model on getting possible values)
	private FilterDataModel<E> filterDataModel = null;

	// cache
	private boolean refreshPossibleValues;
	private boolean refreshStringValues;

	private HashMap<String, List<Object>> possibleValues;
	private HashMap<String, List<Integer>> possibleValuesCount;

	private Map<String, Converter> converters;

	private Map<String, ValueFormatter> formatters;

	private Map<String, Suggester<E, Object>> suggesters;

	public static final <E, F> String getSelectableLabel(Criterion<E, F> effectiveFilter, F value) {
		if (NullValue.NULL_VALUE.equals(value)) {
			return "Not defined";
		} else {
			return effectiveFilter.getSelectableLabelNotNull(value);
		}
	}

	public Filter(EntityFiltrable<E> entity, List<Criterion<E, ?>> criterionsList,
			Map<String, String> requestParameterMap) {
		this(entity, criterionsList);
		if (requestParameterMap != null) {
			initFromUrl(requestParameterMap);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Filter(EntityFiltrable<E> entity, List<Criterion<E, ?>> criterionsList) {
		super();
		this.entity = entity;

		markRefresh();
		possibleValues = null;
		possibleValuesCount = null;
		stringValues = null;

		filterValues = new MapNotifier(new HashMap<String, Object>());
		filterValues.addListener(this);

		suggesters = new HashMap<String, Suggester<E, Object>>();
		formatters = new HashMap<String, ValueFormatter>();
		converters = new HashMap<String, Converter>();

		criterions = new HashMap<String, Criterion<E, ?>>();

		for (Criterion<E, ?> criterion : criterionsList) {
			String keyword = criterion.getKeyword();

			filterValues.put(keyword, null);
			suggesters.put(keyword, new Suggester(this, keyword, criterion));
			formatters.put(keyword, new ValueFormatter(this, keyword));
			criterions.put(keyword, criterion);

			Class<?> selectableClass = criterion.getSelectableClass();
			Converter converter = null;

			if (selectableClass.equals(String.class)) {
				converter = new StringConverter();
			} else {
				try {
					converter = FacesContext.getCurrentInstance().getApplication().createConverter(selectableClass);
				} catch (Exception e) {
					converter = null;
					log.error("Failed to load converter for " + selectableClass, e);
				}
			}
			// Here we delegate the converter as we want to manage null as value
			// and null as none selected
			converters.put(keyword, new ConverterDelegator(converter));

		}

		clear();
		refreshPossibleValues();
	}

	public void appendHibernateFilters(HQLQueryBuilder<E> queryBuilder) {
		appendHibernateFilters(queryBuilder, null);
	}

	public void appendHibernateFilters(HQLQueryBuilder<E> queryBuilder, String excludedKeyword) {
		log.debug("adding default filter for entity");
		getEntity().appendDefaultFilter(queryBuilder);
		for (Criterion<E, ?> effectiveFilter : criterions.values()) {
			// The value is provided
			ValueProvider valueFixer = effectiveFilter.getValueFixer();
			Object fixedValue = null;
			if (valueFixer != null) {
				try {
					fixedValue = valueFixer.getValue();
				} catch (Exception e) {
					log.error("Failed to get fix falue for criterion " + effectiveFilter.getKeyword(), e);
				}
			}
			if (fixedValue != null) {
				String path = effectiveFilter.getPath();
				queryBuilder.addEq(path, fixedValue);
			} else {
				// The value can be selected
				String keyword = effectiveFilter.getKeyword();
				if (!keyword.equals(excludedKeyword)) {
					Object filterValue = filterValues.get(keyword);
					if (filterValue != null) {
						log.debug("adding filter " + keyword + " = " + filterValue);
						String path = effectiveFilter.getPath();
						queryBuilder.addEq(path, filterValue);
					}
				}
			}
		}
	}

	public void clear() {
		filterValues.clear();
		for (Criterion<E, ?> effectiveFilter : criterions.values()) {
			if (effectiveFilter.getValueInitiator() != null) {
				Object value = null;
				try {
					value = effectiveFilter.getValueInitiator().getValue();
				} catch (Exception e) {
					log.error("Failed to get initial falue for criterion " + effectiveFilter.getKeyword(), e);
				}
				if (value != null) {
					getFilterValues().put(effectiveFilter.getKeyword(), value);
				}
			}
		}
	}

	public Map<String, Converter> getConverters() {
		return converters;
	}

	public Map<String, Criterion<E, ?>> getCriterions() {
		return criterions;
	}

	public EntityFiltrable<E> getEntity() {
		return entity;
	}

	public FilterDataModel<E> getFilterDataModel() {
		return filterDataModel;
	}

	public MapNotifier getFilterValues() {
		return filterValues;
	}

	public Map<String, ValueFormatter> getFormatters() {
		return formatters;
	}

	public List<HQLStatistic<E>> getListWithStatistics() {
		refreshListWithStatistics();
		return listWithStatistics;
	}

	public List<Object> getListWithStatisticsItems(HQLStatistic<E> item, int statisticItemIndex) {
		HQLQueryBuilder<E> queryBuilder = new HQLQueryBuilder<E>(entity.getEntityClass());
		appendHibernateFilters(queryBuilder);
		List<Object> listWithStatisticsItems = queryBuilder.getListWithStatisticsItems(statisticItems, item,
				statisticItemIndex);
		return listWithStatisticsItems;
	}

	private void refreshListWithStatistics() {
		if (refreshListStatistics) {
			HQLQueryBuilder<E> queryBuilder = new HQLQueryBuilder<E>(entity.getEntityClass());
			appendHibernateFilters(queryBuilder);
			listWithStatistics = queryBuilder.getListWithStatistics(statisticItems);
			refreshListStatistics = false;
		}
	}

	public HashMap<String, List<Object>> getPossibleValues() {
		refreshPossibleValues();
		return possibleValues;
	}

	public HashMap<String, List<Integer>> getPossibleValuesCount() {
		if (!isCountEnabled()) {
			return null;
		}
		refreshPossibleValues();
		return possibleValuesCount;
	}

	protected Session getSession() {
		EntityManager entityManager = EntityManagerService.provideEntityManager();
		if (entityManager != null) {
			Object delegate = entityManager.getDelegate();
			if (delegate instanceof Session) {
				Session session = (Session) delegate;
				return session;
			}
		}
		return null;
	}

	public Map<String, Object> getStringValues() {
		refreshStringValues();
		return stringValues;
	}

	public Map<String, Suggester<E, Object>> getSuggesters() {
		return suggesters;
	}

	@Override
	public void modified() {
		markRefresh();
		if (filterDataModel != null) {
			filterDataModel.resetCache();
		}
	}

	public void markRefresh() {
		refreshPossibleValues = true;
		refreshStringValues = true;
		refreshListStatistics = true;
	}

	@SuppressWarnings("unchecked")
	private void refreshPossibleValues() {
		if (refreshPossibleValues || possibleValues == null) {
			log.debug("refreshing filter possible values");

			EntityManager em = EntityManagerService.provideEntityManager();

			possibleValues = new HashMap<String, List<Object>>();

			if (!isPossibleFiltered()) {
				for (Criterion<E, ?> effectiveFilter : criterions.values()) {
					List<Object> listValues = new ArrayList<Object>();

					String keyword = effectiveFilter.getKeyword();
					Criterion<E, Object> effectiveFilterObject = (Criterion<E, Object>) effectiveFilter;

					log.debug("refreshing filter possible values for " + keyword);

					@SuppressWarnings("rawtypes")
					HQLQueryBuilder<?> queryBuilder = new HQLQueryBuilder(effectiveFilter.getSelectableClass());
					List<?> distincts = queryBuilder.getList();

					List<ValueCount> values = new ArrayList<ValueCount>(distincts.size() + 1);
					values.add(new ValueCount(NullValue.NULL_VALUE, 0, effectiveFilterObject));
					for (Object value : distincts) {
						values.add(new ValueCount(value, 0, effectiveFilterObject));
					}
					Collections.sort(values);
					for (ValueCount valueCount : values) {
						listValues.add(valueCount.value);
					}
					possibleValues.put(keyword, listValues);
				}
			} else {
				if (isCountEnabled()) {
					possibleValuesCount = new HashMap<String, List<Integer>>();
				}

				for (Criterion<E, ?> effectiveFilter : criterions.values()) {
					String keyword = effectiveFilter.getKeyword();

					log.debug("refreshing filter possible values for " + keyword);

					HQLQueryBuilder<E> queryBuilder = new HQLQueryBuilder<E>(entity.getEntityClass());

					effectiveFilter.appendDefaultFilter(queryBuilder);

					if (filterDataModel != null) {
						log.debug("back calling data model filter");
						filterDataModel.appendFiltersFields(queryBuilder);
					}
					if (filterValues.get(keyword) == null) {
						appendHibernateFilters(queryBuilder);
					} else {
						appendHibernateFilters(queryBuilder, keyword);
					}

					String path = effectiveFilter.getPath();
					Criterion<E, Object> effectiveFilterObject = (Criterion<E, Object>) effectiveFilter;

					List<Object> listValues = new ArrayList<Object>();

					if (isCountEnabled()) {
						log.debug("require counting each item");
						List<Object[]> statistics = queryBuilder.getStatistics(path);
						log.debug("fund " + statistics.size() + " items");

						List<ValueCount> values = new ArrayList<ValueCount>(statistics.size());

						for (Object[] result : statistics) {
							Object value = result[0];
							if (value == null) {
								value = NullValue.NULL_VALUE;
							}
							Number count = (Number) result[1];
							values.add(new ValueCount(value, count.intValue(), effectiveFilterObject));
						}

						Collections.sort(values);
						List<Integer> listCount = new ArrayList<Integer>(values.size());
						for (ValueCount valueCount : values) {
							listValues.add(valueCount.value);
							listCount.add(valueCount.count);
						}

						possibleValuesCount.put(keyword, listCount);
					} else {
						List<?> distincts = queryBuilder.getListDistinct(path);
						List<ValueCount> values = new ArrayList<ValueCount>(distincts.size());
						for (Object value : distincts) {
							if (value == null) {
								value = NullValue.NULL_VALUE;
							}
							values.add(new ValueCount(value, 0, effectiveFilterObject));
						}
						Collections.sort(values);
						for (ValueCount valueCount : values) {
							listValues.add(valueCount.value);
						}
					}

					possibleValues.put(keyword, listValues);
				}
			}

			refreshPossibleValues = false;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void refreshStringValues() {
		if (refreshStringValues) {
			stringValues = new HashMap<String, Object>();
			for (Criterion effectiveFilter : criterions.values()) {
				String keyword = effectiveFilter.getKeyword();
				Object value = filterValues.get(keyword);
				if (value != null) {
					String selectableLabel = getSelectableLabel(effectiveFilter, value);
					stringValues.put(keyword, selectableLabel);
				}
			}
			refreshStringValues = false;
		}
	}

	public void resetField(String fieldId) {
		filterValues.remove(fieldId);
		if (stringValues != null) {
			stringValues.remove(fieldId);
		}
	}

	public void setFilterDataModel(FilterDataModel<E> filterDataModel) {
		this.filterDataModel = filterDataModel;
	}

	public void setFilterValues(MapNotifier filterValues) {
		this.filterValues = filterValues;
		this.filterValues.addListener(this);
	}

	public void setStatisticItems(List<HQLStatisticItem> statisticItems) {
		this.statisticItems = statisticItems;
	}

	public void setUpdatedField(String updatedField) {
		refreshPossibleValues();
	}

	public boolean isCountEnabled() {
		return true && isPossibleFiltered();
	}

	public boolean isPossibleFiltered() {
		return true;
	}

	private void initFromUrl(Map<String, String> requestParameterMap) {
		DatatableStateHolderBean dataTableState = null;

		Object dataTableStateObject = Component.getInstance("dataTableStateHolder");
		if (dataTableStateObject instanceof DatatableStateHolderBean) {
			dataTableState = (DatatableStateHolderBean) dataTableStateObject;
		}

		Set<Entry<String, String>> entrySet = requestParameterMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String stringKey = entry.getKey();
			String stringValue = entry.getValue();
			if (stringKey.startsWith("filter.") && dataTableState != null) {
				stringKey = StringUtils.replaceOnce(stringKey, "filter.", "");
				dataTableState.getColumnFilterValues().put(stringKey, stringValue);
			} else if (stringKey.startsWith("sort.") && dataTableState != null) {
				stringKey = StringUtils.replaceOnce(stringKey, "sort.", "");
				Ordering orderingValue = Ordering.valueOf(stringValue);
				dataTableState.getSortOrders().put(stringKey, orderingValue);
			} else {
				Converter converter = converters.get(stringKey);
				if (converter != null) {
					if (stringValue.equals("null")) {
						filterValues.put(stringKey, NullValue.NULL_VALUE);
					} else {
						Object realValue = converter.getAsObject(FacesContext.getCurrentInstance(), null, stringValue);
						if (realValue == null) {
							try {
								Integer id = Integer.parseInt(stringValue);
								Criterion<E, ?> abstractCriterion = criterions.get(stringKey);
								Class<?> selectableClass = abstractCriterion.getSelectableClass();
								EntityManager entityManager = EntityManagerService.provideEntityManager();
								realValue = entityManager.find(selectableClass, id);
							} catch (NumberFormatException e) {
								realValue = null;
							}
						}
						filterValues.put(stringKey, realValue);
					}
				}
			}
		}

		modified();
	}

	public void setUrlParameters(String urlParameters) {
		//
	}

	public String getUrlParameters() {
		StringBuilder sb = new StringBuilder();

		for (Criterion<E, ?> effectiveFilter : criterions.values()) {
			String keyword = effectiveFilter.getKeyword();
			Object value = filterValues.get(keyword);
			if (value != null) {
				String id = null;
				if (NullValue.NULL_VALUE.equals(value)) {
					id = "null";
				} else {
					try {
						id = PropertyUtils.getProperty(value, "id").toString();
					} catch (Throwable e) {
						id = value.toString();
					}
				}
				if (sb.length() != 0) {
					sb.append("&");
				}
				sb.append(keyword).append("=").append(id);
			}
		}

		Object dataTableStateObject = Component.getInstance("dataTableStateHolder");
		if (dataTableStateObject instanceof DatatableStateHolderBean) {
			DatatableStateHolderBean dataTableState = (DatatableStateHolderBean) dataTableStateObject;

			Map<String, Object> columnFilterValues = dataTableState.getColumnFilterValues();
			Set<Entry<String, Object>> filterEntrySet = columnFilterValues.entrySet();
			for (Entry<String, Object> entry : filterEntrySet) {
				if (entry.getValue() != null) {
					if (sb.length() != 0) {
						sb.append("&");
					}
					sb.append("filter.").append(entry.getKey()).append("=").append(entry.getValue());
				}
			}

			Map<String, Ordering> sortOrders = dataTableState.getSortOrders();
			Set<Entry<String, Ordering>> sortEntrySet = sortOrders.entrySet();
			for (Entry<String, Ordering> entry : sortEntrySet) {
				if (entry.getValue() != Ordering.UNSORTED) {
					if (sb.length() != 0) {
						sb.append("&");
					}
					sb.append("sort.").append(entry.getKey()).append("=").append(entry.getValue());
				}
			}

		}

		return sb.toString();
	}

	public Object getRealFilterValue(String filterId) {
		Object filterValue = filterValues.get(filterId);
		if (possibleValues != null && possibleValues.get(filterId) != null && possibleValues.get(filterId).size() == 1) {
			filterValue = possibleValues.get(filterId).get(0);
		} else if (filterValue == null || NullValue.NULL_VALUE.equals(filterValue)) {
			return null;
		}
		Criterion<E, ?> criterion = criterions.get(filterId);
		if (criterion instanceof PropertyCriterion) {
			PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;
			Class realSelectableClass = propertyCriterion.getRealSelectableClass();
			if (!realSelectableClass.equals(propertyCriterion.getSelectableClass())) {
				filterValue = propertyCriterion.getRealValue(filterValue);
			}
		}
		return filterValue;
	}
}
