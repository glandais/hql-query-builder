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

import java.util.List;

import javax.persistence.EntityManager;

import net.ihe.gazelle.common.filter.hql.HQLQueryBuilder;

import org.apache.commons.lang.StringUtils;

public abstract class PropertyCriterion<E, F> extends AbstractCriterion<E, F> {

	protected String associationPath;
	protected String propertyName;
	private Class<?> realSelectableClass;

	public PropertyCriterion(Class<F> selectableClass, String keyword, String associationPath, String propertyName) {
		super(selectableClass, keyword);
		this.associationPath = associationPath;
		this.propertyName = propertyName;
		this.realSelectableClass = selectableClass;
	}

	public String getPath() {
		String path;
		if (StringUtils.trimToNull(associationPath) == null || associationPath.equals("this")) {
			path = propertyName;
		} else {
			path = associationPath + "." + propertyName;
		}
		return path;
	}

	/**
	 * Override this method if you want to see the info link for the linked
	 * item.
	 * 
	 * @return
	 */
	public Class<?> getRealSelectableClass() {
		return realSelectableClass;
	}

	public Object getRealValue(F filterValue, EntityManager entityManager) {
		HQLQueryBuilder<?> builder = new HQLQueryBuilder(getRealSelectableClass());
		builder.setMaxResults(2);
		builder.addEq(propertyName, filterValue);
		List<?> list = builder.getList();
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

}
