package net.ihe.gazelle.hql;

public class NullValue {

	public static final NullValue NULL_VALUE = new NullValue();
	public static final String NULL_VALUE_STRING = "NullValue";

	private NullValue() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

}
