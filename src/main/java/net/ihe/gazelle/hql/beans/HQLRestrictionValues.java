package net.ihe.gazelle.hql.beans;

import java.util.HashMap;

public class HQLRestrictionValues extends HashMap<String, Object> {

	private static final long serialVersionUID = -8657038713260676019L;

	public Object addValue(String path, Object value) {
		String key = "restriction_" + size();
		put(key, value);
		return key;
	}

}
