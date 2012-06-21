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

		EntityManager entityManager = null;
		// Ceinture, bretelles
		try {
			ENTITYMANGER_THREADLOCAL.remove();
			entityManager = GenericServiceLoader.getService(EntityManagerProvider.class).provideEntityManager();
			ENTITYMANGER_THREADLOCAL.set(entityManager);

			if (!checkActive(entityManager)) {
				entityManager.getTransaction().begin();
			}
			try {
				result = action.performAction(entityManager, context);
			} catch (Throwable e) {
				failure = e;
				log.error("Caught exception, marking transaction to be rollbacked", e);
				if (checkActive(entityManager)) {
					entityManager.getTransaction().setRollbackOnly();
				} else {
					log.warn("Inactive transaction as marking rollback");
				}
			}
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
		if (entityManager != null) {
			try {
				entityManager.close();
			} catch (IllegalStateException e) {
				log.error("Failed to close entity manager", e);
				failure = e;
			}
		}
		ENTITYMANGER_THREADLOCAL.remove();
		if (failure != null) {
			throw new HibernateFailure("Something went wrong!", failure);
		}
		return result;
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
