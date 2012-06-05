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
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.model.Ordering;

@Name("dataTableStateHolder")
@Scope(ScopeType.PAGE)
public class DatatableStateHolderBean implements Serializable {

	private static final long serialVersionUID = 1869072214666544359L;

	private Map<String, Ordering> sortOrders = new HashMap<String, Ordering>();

	private Map<String, Object> columnFilterValues = new HashMap<String, Object>();

	public Map<String, Object> getColumnFilterValues() {
		return columnFilterValues;
	}

	public Map<String, Ordering> getSortOrders() {
		return sortOrders;
	}
	
	public void setAscendingOn(String column) {
		sortOrders.put(column, Ordering.ASCENDING);
	}

	public void setDescendingOn(String column) {
		sortOrders.put(column, Ordering.DESCENDING);
	}

}
