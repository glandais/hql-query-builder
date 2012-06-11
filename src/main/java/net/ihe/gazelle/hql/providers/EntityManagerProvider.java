package net.ihe.gazelle.hql.providers;

import javax.persistence.EntityManager;

public interface EntityManagerProvider {

	public EntityManager providerEntityManager();

	public int getWeight();

}
