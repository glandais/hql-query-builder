package net.ihe.gazelle.hql.providers;

import javax.persistence.EntityManager;

public interface EntityManagerProvider {

	public EntityManager provideEntityManager();

	public int getWeight();

}
