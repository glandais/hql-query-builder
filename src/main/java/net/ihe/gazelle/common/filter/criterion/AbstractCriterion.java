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
package net.ihe.gazelle.common.filter.criterion;

import net.ihe.gazelle.common.filter.AbstractEntity;
import net.ihe.gazelle.common.filter.NullValue;
import net.ihe.gazelle.common.filter.ValueProvider;
import net.ihe.gazelle.common.filter.hql.HQLQueryBuilder;

public abstract class AbstractCriterion<E, F> {

	private AbstractEntity<E> entity;
	private Class<F> selectableClass;
	private String keyword;
	private ValueProvider valueFixer;
	private ValueProvider valueInitiator;

	public AbstractCriterion(AbstractEntity<E> entity, Class<F> selectableClass, String keyword) {
		super();
		this.entity = entity;
		this.selectableClass = selectableClass;
		this.keyword = keyword;
	}

	public AbstractEntity<E> getEntity() {
		return entity;
	}

	public Class<F> getSelectableClass() {
		return selectableClass;
	}

	public String getKeyword() {
		return keyword;
	}

	public void filter(HQLQueryBuilder<E> queryBuilder, Object filterValue) {
		String path = getPath();
		queryBuilder.addEq(path, filterValue);
	}

	public void appendDefaultFilter(HQLQueryBuilder<E> queryBuilder) {
		// None by default
	}

	public ValueProvider getValueFixer() {
		return valueFixer;
	}

	public void setValueFixer(ValueProvider valueFixer) {
		this.valueFixer = valueFixer;
	}

	public ValueProvider getValueInitiator() {
		return valueInitiator;
	}

	public void setValueInitiator(ValueProvider valueInitiator) {
		this.valueInitiator = valueInitiator;
	}

	public final String getSelectableLabel(F instance) {
		if (NullValue.NULL_VALUE.equals(instance)) {
			return "Not defined";
		} else {
			return getSelectableLabelNotNull(instance);
		}
	}

	public abstract String getSelectableLabelNotNull(F instance);

	public abstract String getPath();

}
