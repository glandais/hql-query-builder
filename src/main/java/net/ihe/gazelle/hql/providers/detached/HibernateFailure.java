package net.ihe.gazelle.hql.providers.detached;

public class HibernateFailure extends Exception {

	private static final long serialVersionUID = 3513924676969487851L;

	public HibernateFailure() {
		super();
	}

	public HibernateFailure(String message) {
		super(message);
	}

	public HibernateFailure(Throwable cause) {
		super(cause);
	}

	public HibernateFailure(String message, Throwable cause) {
		super(message, cause);
	}

}
