package net.ihe.gazelle.common.filter.hql;

import java.util.Collection;

public class HQLRestrictions {

	private HQLRestrictions() {
		super();
	}

	public static HQLRestriction eq(Object propertyName, Object value) {
		return new HQLRestrictionEq(propertyName.toString(), value);
	}

	public static HQLRestriction neq(Object propertyName, Object value) {
		return new HQLRestrictionNeq(propertyName.toString(), value);
	}

	public static HQLRestriction in(Object propertyName, Collection<?> elements) {
		return new HQLRestrictionIn(propertyName.toString(), elements);
	}

	public static HQLRestriction nin(Object propertyName, Collection<?> elements) {
		return new HQLRestrictionNotIn(propertyName.toString(), elements);
	}

	public static HQLRestriction like(Object propertyName, String value, HQLRestrictionLikeMatchMode matchMode) {
		return new HQLRestrictionLike(propertyName.toString(), value, matchMode);
	}

	public static HQLRestriction like(Object propertyName, String value) {
		return new HQLRestrictionLike(propertyName.toString(), value);
	}

	public static HQLRestriction or(HQLRestriction... restrictions) {
		return new HQLRestrictionOr(restrictions);
	}

	public static HQLRestriction and(HQLRestriction... restrictions) {
		return new HQLRestrictionAnd(restrictions);
	}

	public static HQLRestriction isNotNull(Object propertyName) {
		return new HQLRestrictionIsNotNull(propertyName.toString());
	}

	public static HQLRestriction isNull(Object propertyName) {
		return new HQLRestrictionIsNull(propertyName.toString());
	}

	public static HQLRestriction ge(Object propertyName, Object value) {
		return new HQLRestrictionCompare(propertyName.toString(), value, ">=");
	}

	public static HQLRestriction le(Object propertyName, Object value) {
		return new HQLRestrictionCompare(propertyName.toString(), value, "<=");
	}

	public static HQLRestriction gt(Object propertyName, Object value) {
		return new HQLRestrictionCompare(propertyName.toString(), value, ">");
	}

	public static HQLRestriction lt(Object propertyName, Object value) {
		return new HQLRestrictionCompare(propertyName.toString(), value, "<");
	}

	public static HQLRestriction isNotEmpty(Object propertyName) {
		return new HQLRestrictionIsNotEmpty(propertyName.toString());
	}

	public static HQLRestriction isEmpty(Object propertyName) {
		return new HQLRestrictionIsEmpty(propertyName.toString());
	}

}
