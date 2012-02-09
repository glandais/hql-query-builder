package net.ihe.gazelle.common.filter.hql;

import java.util.Collection;

public class HQLRestrictions {

	private HQLRestrictions() {
		super();
	}

	public static HQLRestriction eq(String propertyName, Object value) {
		return new HQLRestrictionEq(propertyName, value);
	}

	public static HQLRestriction neq(String propertyName, Object value) {
		return new HQLRestrictionNeq(propertyName, value);
	}

	public static HQLRestriction in(String propertyName, Collection<?> elements) {
		return new HQLRestrictionIn(propertyName, elements);
	}

	public static HQLRestriction like(String propertyName, String value, HQLRestrictionLikeMatchMode matchMode) {
		return new HQLRestrictionLike(propertyName, value, matchMode);
	}

	public static HQLRestriction like(String propertyName, String value) {
		return new HQLRestrictionLike(propertyName, value);
	}

	public static HQLRestriction or(HQLRestriction restriction1, HQLRestriction restriction2) {
		return new HQLRestrictionOr(restriction1, restriction2);
	}

	public static HQLRestriction and(HQLRestriction restriction1, HQLRestriction restriction2) {
		return new HQLRestrictionAnd(restriction1, restriction2);
	}

	public static HQLRestriction isNotNull(String propertyName) {
		return new HQLRestrictionIsNotNull(propertyName);
	}
	
	public static HQLRestriction isNull(String propertyName) {
        return new HQLRestrictionIsNull(propertyName);
    }

	public static HQLRestriction ge(String propertyName, Object value) {
		return new HQLRestrictionCompare(propertyName, value, ">=");
	}

	public static HQLRestriction le(String propertyName, Object value) {
		return new HQLRestrictionCompare(propertyName, value, "<=");
	}

	public static HQLRestriction gt(String propertyName, Object value) {
		return new HQLRestrictionCompare(propertyName, value, ">");
	}

	public static HQLRestriction lt(String propertyName, Object value) {
		return new HQLRestrictionCompare(propertyName, value, "<");
	}
	
	public static HQLRestriction isNotEmpty(String propertyName) {
		return new HQLRestrictionIsNotEmpty(propertyName);
	}
	
	public static HQLRestriction isEmpty(String propertyName) {
        return new HQLRestrictionIsEmpty(propertyName);
    }

}
