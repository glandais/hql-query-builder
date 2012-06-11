package net.ihe.gazelle;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;

public class SeamEntityManagerProvider implements EntityManagerProvider {

	@Override
	public EntityManager providerEntityManager() {
		if (Contexts.isApplicationContextActive()) {
			return (EntityManager) Component.getInstance("entityManager");
		} else {
			return null;
		}
	}

	@Override
	public int getWeight() {
		if (Contexts.isApplicationContextActive()) {
			return 0;
		} else {
			return 100;
		}
	}

}
