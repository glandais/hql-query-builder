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

import java.util.HashMap;
import java.util.List;

import net.ihe.gazelle.common.filter.Filter;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ValueFormatter {

	public static final int ABBREVIATION_LENGTH = 50;

	private Filter filter;

	private String keyword;

	public ValueFormatter(Filter filter, String keyword) {
		super();
		this.filter = filter;
		this.keyword = keyword;
	}

	public String format(Object o) {
		String selectableLabel = "?";
		if (o != null) {
			Criterion criterion = (Criterion) filter.getCriterions().get(keyword);
			selectableLabel = Filter.getSelectableLabel(criterion, o);

			selectableLabel = abbreviate(selectableLabel);

			// Add count
			HashMap possibleValuesCount = filter.getPossibleValuesCount();
			if (possibleValuesCount != null) {
				List<Integer> counts = (List<Integer>) possibleValuesCount.get(keyword);
				if (counts != null) {
					List<Object> values = (List<Object>) filter.getPossibleValues().get(keyword);
					int index = values.indexOf(o);
					if (index != -1) {
						Integer count = counts.get(index);
						if (count >= 0) {
							selectableLabel = selectableLabel + " (" + count + ")";
						}
					}
				}
			}
		}
		return selectableLabel;
	}

	public static String abbreviate(String toAbbreviate) {
		toAbbreviate = StringUtils.abbreviate(toAbbreviate, ABBREVIATION_LENGTH);
		return toAbbreviate;
	}

	public String formatLabel(Object o) {
		String selectableLabel = "?";
		if (o != null) {
			Criterion criterion = (Criterion) filter.getCriterions().get(keyword);
			selectableLabel = Filter.getSelectableLabel(criterion, o);
			selectableLabel = abbreviate(selectableLabel);
		}
		return selectableLabel;
	}

	public String formatCount(Object o) {
		if (o != null) {
			HashMap possibleValuesCount = filter.getPossibleValuesCount();
			if (possibleValuesCount != null) {
				List<Integer> counts = (List<Integer>) possibleValuesCount.get(keyword);
				if (counts != null) {
					List<Object> values = (List<Object>) filter.getPossibleValues().get(keyword);
					int index = values.indexOf(o);
					if (index != -1) {
						Integer count = counts.get(index);
						if (count >= 0) {
							return Integer.toString(count);
						}
					}
				}
			}
		}
		return "";
	}

}
