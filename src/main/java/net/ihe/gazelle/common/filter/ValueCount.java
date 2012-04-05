package net.ihe.gazelle.common.filter;

import net.ihe.gazelle.common.filter.criterion.AbstractCriterion;

class ValueCount implements Comparable<ValueCount> {

	Object value;
	Integer count;
	AbstractCriterion<?, Object> effectiveFilterObject;

	public ValueCount(Object value, Integer count, AbstractCriterion<?, Object> effectiveFilterObject) {
		super();
		this.value = value;
		this.count = count;
		this.effectiveFilterObject = effectiveFilterObject;
	}

	private int compareObjects(AbstractCriterion<?, Object> effectiveFilterObject, Object o1, Object o2) {
		if (o1 == null || NullValue.NULL_VALUE.equals(o1)) {
			return -1;
		}
		if (o2 == null || NullValue.NULL_VALUE.equals(o2)) {
			return 1;
		}
		String l1 = effectiveFilterObject.getSelectableLabel(o1);
		if (l1 == null) {
			return -1;
		}
		String l2 = effectiveFilterObject.getSelectableLabel(o2);
		if (l2 == null) {
			return 1;
		}
		return l1.compareToIgnoreCase(l2);
	}

	@Override
	public int compareTo(ValueCount o) {
		if (o == null) {
			return 1;
		}
		return compareObjects(effectiveFilterObject, value, o.value);
	}

}
