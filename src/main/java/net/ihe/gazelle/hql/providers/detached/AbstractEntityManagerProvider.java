package net.ihe.gazelle.hql.providers.detached;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import net.ihe.gazelle.hql.providers.EntityManagerProvider;

import org.hibernate.ejb.Ejb3Configuration;

public abstract class AbstractEntityManagerProvider implements EntityManagerProvider {

	private EntityManagerFactory entityManagerFactory;

	// META-INF/hibernate.cfg.xml
	public abstract String getHibernateConfigPath();

	protected EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null) {
			synchronized (AbstractEntityManagerProvider.class) {
				if (entityManagerFactory == null) {
					Ejb3Configuration cfg = new Ejb3Configuration();
					cfg.configure(getHibernateConfigPath());
					entityManagerFactory = cfg.buildEntityManagerFactory();
				}
			}
		}
		return entityManagerFactory;
	}

	@Override
	public EntityManager provideEntityManager() {
		EntityManager em = HibernateActionPerformer.ENTITYMANGER_THREADLOCAL.get();
		if (em != null) {
			return em;
		} else {
			em = getEntityManagerFactory().createEntityManager();
			HibernateActionPerformer.ENTITYMANGER_THREADLOCAL.set(em);
			return em;
		}
	}

	@Override
	public int compareTo(EntityManagerProvider o) {
		return getWeight().compareTo(o.getWeight());
	}

}
