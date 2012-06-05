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
import java.util.List;

import net.ihe.gazelle.common.filter.criterion.Criterion;
import net.ihe.gazelle.common.filter.criterion.ValueFormatter;

public class Suggester<E, F> {

	private Filter<E> filter;
	private String keyword;
	private Criterion<E, F> criterion;

	public Suggester(Filter<E> filter, String keyword, Criterion<E, F> criterion) {
		super();
		this.filter = filter;
		this.keyword = keyword;
		this.criterion = criterion;
	}

	@SuppressWarnings("unchecked")
	public List<F> suggest(Object suggest) {
		String input = (String) suggest;
		List<F> allValues = (List<F>) filter.getPossibleValues().get(keyword);
		List<F> values = allValues;

		// show all items if something is already selected and the label match
		// the selected item
		boolean showAllItems = true;

		if (allValues != null && input != null && input.length() > 1) {
			showAllItems = false;
		}
		F selectedValue = (F) filter.getFilterValues().get(keyword);
		if (selectedValue != null) {
			String label = Filter.getSelectableLabel(criterion, selectedValue);
			label = ValueFormatter.abbreviate(label);
			if (label != null && input != null && label.equalsIgnoreCase(input)) {
				showAllItems = true;
			}
		}

		if (!showAllItems) {
			input = input.toLowerCase();
			values = new ArrayList<F>();
			for (F e : allValues) {
				String label = Filter.getSelectableLabel(criterion, e);
				if (label != null) {
					label = label.toLowerCase();
					if (label.contains(input)) {
						values.add(e);
					}
				}
			}
		}
		return values;
	}

}
