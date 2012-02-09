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
package net.ihe.gazelle.common.filter.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;

public class MapNotifier implements Map<String, Object> {

	private HashMap<String, Object> delegator;
	private List<MapNotifierListener> listeners;

	public MapNotifier() {
		super();
	}

	public MapNotifier(HashMap<String, Object> filterValues) {
		super();
		this.delegator = filterValues;
		this.listeners = new ArrayList<MapNotifierListener>();
	}
	
	public void addListener(MapNotifierListener listener) {
		listeners.add(listener);
	}

	protected void modified() {
		for (MapNotifierListener listener : listeners) {
			listener.modified();
		}
	}
	
	public int size() {
		return delegator.size();
	}

	public boolean isEmpty() {
		return delegator.isEmpty();
	}

	public Object get(java.lang.Object key) {
		return delegator.get(key);
	}

	public boolean equals(java.lang.Object o) {
		return delegator.equals(o);
	}

	public boolean containsKey(java.lang.Object key) {
		return delegator.containsKey(key);
	}

	public Object put(String key, Object value) {
		Object result = delegator.put(key, value);
		if (!new EqualsBuilder().append(result, value).isEquals()) {
			modified();
		}
		return result;
	}

	public int hashCode() {
		return delegator.hashCode();
	}

	public java.lang.String toString() {
		return delegator.toString();
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		delegator.putAll(m);
		modified();
	}

	public Object remove(java.lang.Object key) {
		Object result = delegator.remove(key);
		modified();
		return result;
	}

	public void clear() {
		delegator.clear();
		modified();
	}

	public boolean containsValue(java.lang.Object value) {
		return delegator.containsValue(value);
	}

	public java.lang.Object clone() {
		return delegator.clone();
	}

	public Set<String> keySet() {
		return delegator.keySet();
	}

	public Collection<Object> values() {
		return delegator.values();
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return delegator.entrySet();
	}

}
