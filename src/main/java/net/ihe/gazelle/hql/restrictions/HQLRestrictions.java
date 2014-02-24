package net.ihe.gazelle.hql.restrictions;

import java.util.Collection;

import net.ihe.gazelle.hql.HQLRestriction;

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

	public static HQLRestriction nin(String propertyName, Collection<?> elements) {
		return new HQLRestrictionNotIn(propertyName, elements);
	}

	public static HQLRestriction like(String propertyName, String value, HQLRestrictionLikeMatchMode matchMode) {
		return new HQLRestrictionLike(propertyName, value, matchMode);
	}

	public static HQLRestriction like(String propertyName, String value) {
		return new HQLRestrictionLike(propertyName, value);
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

	public static HQLRestriction or(HQLRestriction... restrictions) {
		return new HQLRestrictionOr(restrictions);
	}

	public static HQLRestriction and(HQLRestriction... restrictions) {
		return new HQLRestrictionAnd(restrictions);
	}

	public static HQLRestriction not(HQLRestriction toNegate) {
		return new HQLRestrictionNot(toNegate);
	}

}
