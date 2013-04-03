package net.ihe.gazelle.hql.providers.detached;

import javax.persistence.EntityManager;

public interface PerformHibernateAction {

	public Object performAction(EntityManager entityManager, Object... context) throws Exception;

}
