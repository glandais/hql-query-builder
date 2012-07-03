package net.ihe.gazelle.hql.providers.detached;

public class TransactionnalProvider {

	public static void transactionStart() {
		HibernateActionPerformer.transactionStart();
	}

	public static Throwable transactionClose() {
		return HibernateActionPerformer.transactionClose();
	}

	public static void transactionError(Throwable throwable) {
		HibernateActionPerformer.transactionError(throwable);
	}

}
