package net.ihe.gazelle.hql.providers;

import javax.persistence.EntityManager;

import net.ihe.gazelle.services.GenericServiceLoader;

public class EntityManagerService {

	private EntityManagerService() {
		super();
	}

	public static EntityManager provideEntityManager() {
		return GenericServiceLoader.getService(EntityManagerProvider.class).provideEntityManager();
	}

}
