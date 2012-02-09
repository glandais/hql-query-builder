package net.ihe.gazelle.common.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.el.ELException;
import javax.el.Expression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import net.ihe.gazelle.common.filter.hql.HQLQueryBuilder;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.jboss.seam.Component;
import org.richfaces.model.ExtendedFilterField;
import org.richfaces.model.FilterField;
import org.richfaces.model.Modifiable;
import org.richfaces.model.Ordering;
import org.richfaces.model.SortField2;

public class HibernateDataModel<T> extends ExtendedDataModel implements Modifiable {

	private Class<T> dataClass;

	private T dataItem;

	private SequenceRange cachedRange;

	private List<T> cachedItems;

	private List<FilterField> filterFields;

	private List<SortField2> sortFields;

	@SuppressWarnings("unchecked")
	public HibernateDataModel(Class<?> dataClass) {
		super();
		this.dataClass = (Class<T>) dataClass;
	}

	private static boolean areEqualRanges(SequenceRange range1, SequenceRange range2) {
		if (range1 == null || range2 == null) {
			return range1 == null && range2 == null;
		} else {
			return range1.getFirstRow() == range2.getFirstRow() && range1.getRows() == range2.getRows();
		}
	}

	private String getPropertyName(FacesContext facesContext, Expression expression) {
		try {
			return (String) ((ValueExpression) expression).getValue(facesContext.getELContext());
		} catch (ELException e) {
			throw new FacesException(e.getMessage(), e);
		}
	}

	@Override
	public Object getRowKey() {
		return dataItem;
	}

	@Override
	public void setRowKey(Object key) {
		this.dataItem = dataClass.cast(key);
	}

	public List<T> getAllItems(FacesContext facesContext) {
		try {
			walk(facesContext, new DataVisitor() {
				public void process(FacesContext facescontext, Object obj, Object obj1) throws IOException {
				}
			}, null, null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return cachedItems;
	}

	protected void appendFilters(FacesContext context, HQLQueryBuilder<T> queryBuilder) {
		if (filterFields != null && context != null) {
			for (FilterField filterField : filterFields) {
				String filterValue = ((ExtendedFilterField) filterField).getFilterValue();
				if (filterValue != null && filterValue.length() != 0) {
					String propertyName = getPropertyName(context, filterField.getExpression());
					if (propertyName != null) {
						queryBuilder.addLike(propertyName, filterValue);
					} else {
						System.err.println(filterField.getExpression() + " is not a valid value");
					}
				}
			}
		}
	}

	protected void appendSorts(FacesContext context, HQLQueryBuilder<T> queryBuilder) {
		if (sortFields != null && context != null) {
			for (SortField2 sortField : sortFields) {
				Ordering ordering = sortField.getOrdering();

				if (Ordering.ASCENDING.equals(ordering) || Ordering.DESCENDING.equals(ordering)) {
					String propertyName = getPropertyName(context, sortField.getExpression());
					if (propertyName != null) {
						queryBuilder.addOrder(propertyName, Ordering.ASCENDING.equals(ordering));
					} else {
						System.err.println(sortField.getExpression() + " is not a valid sort");
					}
				}
			}
		}
	}

	@Override
	public void walk(FacesContext facesContext, DataVisitor visitor, Range range, Object argument) throws IOException {

		SequenceRange jpaRange = (SequenceRange) range;

		if (this.cachedItems == null || !areEqualRanges(this.cachedRange, jpaRange)) {

			HQLQueryBuilder<T> queryBuilder = new HQLQueryBuilder<T>(getEntityManager(), dataClass);

			appendFilters(facesContext, queryBuilder);
			appendSorts(facesContext, queryBuilder);

			if (jpaRange != null) {
				int first = jpaRange.getFirstRow();
				int rows = jpaRange.getRows();

				queryBuilder.setFirstResult(first);
				if (rows > 0) {
					queryBuilder.setMaxResults(rows);
				}
			}

			this.cachedRange = jpaRange;
			this.cachedItems = queryBuilder.getList();
		}

		for (T item : this.cachedItems) {
			visitor.process(facesContext, item, argument);
		}
	}

	/**
	 * For list compatibility
	 * 
	 * @return row count
	 */
	public int size() {
		return getRowCount();
	}

	@Override
	public int getRowCount() {
		HQLQueryBuilder<T> queryBuilder = new HQLQueryBuilder<T>(getEntityManager(), dataClass);
		appendFilters(FacesContext.getCurrentInstance(), queryBuilder);
		return queryBuilder.getCount();
	}

	@Override
	public Object getRowData() {
		return this.dataItem;
	}

	@Override
	public int getRowIndex() {
		return -1;
	}

	@Override
	public Object getWrappedData() {
		return cachedItems;
	}

	@Override
	public boolean isRowAvailable() {
		return (this.dataItem != null);
	}

	@Override
	public void setRowIndex(int rowIndex) {
	}

	@Override
	public void setWrappedData(Object data) {
	}

	public void modify(List<FilterField> filterFields, List<SortField2> sortFields) {
		boolean modified = false;
		if (filterFields == null || !filterFields.equals(this.filterFields)) {
			modified = true;
		}

		if (!modified) {
			if (this.sortFields == null && sortFields != null) {
				modified = true;
			} else if (this.sortFields != null && sortFields == null) {
				modified = true;
			} else if (this.sortFields != null && sortFields != null) {
				if (this.sortFields.size() != sortFields.size()) {
					modified = true;
				} else {
					sortSortFields2(this.sortFields);
					sortSortFields2(sortFields);
					for (int i = 0; i < sortFields.size(); i++) {
						if (!modified) {
							SortField2 sortFieldNew = sortFields.get(i);
							SortField2 sortFieldOrig = this.sortFields.get(i);

							int orderingNew = -1;
							int orderingOrig = -1;

							if (sortFieldNew.getOrdering() != null) {
								orderingNew = sortFieldNew.getOrdering().ordinal();
							}
							if (sortFieldOrig.getOrdering() != null) {
								orderingOrig = sortFieldOrig.getOrdering().ordinal();
							}

							String expressionNew = null;
							String expressionOrig = null;

							if (sortFieldNew.getExpression() != null) {
								expressionNew = sortFieldNew.getExpression().getExpressionString();
							}
							if (sortFieldOrig.getExpression() != null) {
								expressionOrig = sortFieldOrig.getExpression().getExpressionString();
							}

							EqualsBuilder equalsBuilder = new EqualsBuilder();
							if (!equalsBuilder.append(orderingNew, orderingOrig).append(expressionNew, expressionOrig)
									.isEquals()) {
								modified = true;
							}
						}
					}
				}
			}
		}

		if (modified) {
			this.filterFields = filterFields;
			this.sortFields = sortFields;
			resetCache();
		}
	}

	private void sortSortFields2(List<SortField2> sortFields2) {
		Collections.sort(sortFields2, new Comparator<SortField2>() {
			@Override
			public int compare(SortField2 o1, SortField2 o2) {
				String o1e = null;
				if (o1.getExpression() != null) {
					o1e = o1.getExpression().getExpressionString();
				}
				String o2e = null;
				if (o2.getExpression() != null) {
					o2e = o2.getExpression().getExpressionString();
				}
				if (o1e == null && o2e == null) {
					return 0;
				}
				if (o1e == null) {
					return 1;
				}
				if (o2e == null) {
					return -1;
				}
				return o1e.compareTo(o2e);
			}
		});
	}

	public void resetCache() {
		this.cachedItems = null;
		this.cachedRange = null;
	}

	protected EntityManager getEntityManager() {
		return (EntityManager) Component.getInstance("entityManager");
	}

}
