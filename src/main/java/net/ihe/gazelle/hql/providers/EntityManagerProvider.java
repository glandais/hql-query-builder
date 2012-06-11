package net.ihe.gazelle.hql.providers;

import javax.persistence.EntityManager;

public interface EntityManagerProvider extends Comparable<EntityManagerProvider> {

	public EntityManager provideEntityManager();

	public Integer getWeight();

}
