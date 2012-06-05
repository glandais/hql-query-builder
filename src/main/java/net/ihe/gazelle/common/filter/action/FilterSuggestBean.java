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
package net.ihe.gazelle.common.filter.action;

import java.io.Serializable;

import net.ihe.gazelle.common.LinkDataProvider;
import net.ihe.gazelle.common.LinkDataProviderService;
import net.ihe.gazelle.common.filter.Filter;
import net.ihe.gazelle.common.filter.criterion.Criterion;
import net.ihe.gazelle.common.filter.criterion.PropertyCriterion;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("filterSuggestBean")
@Scope(ScopeType.PAGE)
public class FilterSuggestBean implements Serializable {

	private static final long serialVersionUID = 6674342811351430146L;

	public String getSuggestionId(String fieldId) {
		return fieldId + "Suggestion";
	}

	public boolean supportsLink(Filter<?> filter, String filterId) {
		Criterion<?, ?> criterion = filter.getCriterions().get(filterId);
		Class<?> valueClass = criterion.getSelectableClass();
		if (criterion instanceof PropertyCriterion) {
			PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;
			valueClass = propertyCriterion.getRealSelectableClass();
		}
		LinkDataProvider provider = LinkDataProviderService.getProviderForClass(valueClass);
		if (provider != null) {
			return true;
		} else {
			return false;
		}
	}

}
