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

import javax.faces.context.FacesContext;

import net.ihe.gazelle.common.filter.hql.HQLQueryBuilder;

public class FilterDataModel<F> extends HibernateDataModel<F> {

	private transient Filter<F> filter;

	public FilterDataModel(Filter<F> filter) {
		super(filter.getEntity().getEntityClass());
		setFilter(filter);
	}

	public Filter<F> getFilter() {
		return filter;
	}

	public void setFilter(Filter<F> filter) {
		this.filter = filter;
		this.filter.setFilterDataModel(this);
	}

	public final void appendFiltersPublic(FacesContext context, HQLQueryBuilder<F> queryBuilder) {
		appendFilters(context, queryBuilder);
	}

	@Override
	protected final void appendFilters(FacesContext context, HQLQueryBuilder<F> queryBuilder) {
		super.appendFilters(context, queryBuilder);
		filter.appendHibernateFilters(queryBuilder);
		appendFiltersFields(queryBuilder);
	}

	public void appendFiltersFields(HQLQueryBuilder<F> queryBuilder) {
		//
	}

	@Override
	public void resetCache() {
		super.resetCache();
		getFilter().markRefresh();
	}

}
