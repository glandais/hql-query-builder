package net.ihe.gazelle.hql.providers.detached;

import java.sql.BatchUpdateException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import net.ihe.gazelle.hql.providers.EntityManagerProvider;
import net.ihe.gazelle.services.GenericServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateActionPerformer {

	private static final Logger log = LoggerFactory.getLogger(HibernateActionPerformer.class);

	public static final ThreadLocal<EntityManager> ENTITYMANGER_THREADLOCAL = new ThreadLocal<EntityManager>();

	public static Object performHibernateAction(PerformHibernateAction action, Object... context)
			throws HibernateFailure {
		Object result = null;
		Throwable failure = null;

		transactionStart();
		// Ceinture, bretelles
		try {
			result = action.performAction(getEntityManager(), context);
		} catch (Throwable e) {
			failure = e;
			transactionError(e);
		}

		failure = transactionClose();
		if (failure != null) {
			throw new HibernateFailure("Something went wrong!", failure);
		}
		return result;
	}

	private static EntityManager getEntityManager() {
		return ENTITYMANGER_THREADLOCAL.get();
	}

	static void transactionStart() {
		ENTITYMANGER_THREADLOCAL.remove();
		EntityManager entityManager = GenericServiceLoader.getService(EntityManagerProvider.class)
				.provideEntityManager();
		ENTITYMANGER_THREADLOCAL.set(entityManager);

		if (!checkActive(entityManager)) {
			entityManager.getTransaction().begin();
		}
	}

	static void transactionError(Throwable e) {
		log.error("Caught exception, marking transaction to be rollbacked", e);
		EntityManager entityManager = getEntityManager();
		if (checkActive(entityManager)) {
			entityManager.getTransaction().setRollbackOnly();
		} else {
			log.warn("Inactive transaction as marking rollback");
		}
	}

	static Throwable transactionClose() {
		Throwable failure = null;
		EntityManager entityManager = getEntityManager();
		if (entityManager != null) {
			try {
				if (checkActive(entityManager)) {
					if (entityManager.getTransaction().getRollbackOnly()) {
						log.info("Rollbacking transaction");
						try {
							entityManager.getTransaction().rollback();
						} catch (PersistenceException e) {
							failure = e;
							log.error("Failed to rollback transaction", e);
						}
					} else {
						try {
							entityManager.getTransaction().commit();
						} catch (RollbackException e) {
							failure = e;
							log.error("Failed to commit transaction", e);
							searchBatchUpdateException(e);
						}
					}
				} else {
					log.warn("Inactive transaction!");
				}
			} catch (Throwable e) {
				log.error("Caught exception, releasing entity manager anyway", e);
				failure = e;
			}
			try {
				entityManager.close();
			} catch (IllegalStateException e) {
				log.error("Failed to close entity manager", e);
				failure = e;
			}
		}
		ENTITYMANGER_THREADLOCAL.remove();
		return failure;
	}

	private static void searchBatchUpdateException(Throwable throwable) {
		if (throwable instanceof BatchUpdateException) {
			BatchUpdateException batchUpdateException = (BatchUpdateException) throwable;
			log.error("Next exception", batchUpdateException.getNextException());
		} else {
			if (throwable.getCause() != null) {
				searchBatchUpdateException(throwable.getCause());
			}
		}
	}

	private static boolean checkActive(EntityManager entityManager) {
		return entityManager.getTransaction().isActive();
	}

}
