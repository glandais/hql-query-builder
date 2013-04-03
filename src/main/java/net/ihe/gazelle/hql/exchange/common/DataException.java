package net.ihe.gazelle.hql.exchange.common;

public class DataException extends Exception {

	private static final long serialVersionUID = -2115852403502264426L;

	public DataException() {
		super();
	}

	public DataException(String message) {
		super(message);
	}

	public DataException(Throwable cause) {
		super(cause);
	}

	public DataException(String message, Throwable cause) {
		super(message, cause);
	}

}
