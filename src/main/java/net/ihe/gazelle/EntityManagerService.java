package net.ihe.gazelle;

import java.util.ServiceLoader;

import javax.persistence.EntityManager;

public class EntityManagerService {

	protected static EntityManagerProvider PROVIDER = null;

	private static void initProvider() {
		if (PROVIDER == null) {
			synchronized (EntityManagerService.class) {
				if (PROVIDER == null) {
					ServiceLoader<EntityManagerProvider> providers = ServiceLoader.load(EntityManagerProvider.class);
					if (providers.iterator().hasNext()) {
						PROVIDER = providers.iterator().next();
					} else {
						// FIXME log error
					}
				}
			}
		}
	}

	public static EntityManager providerEntityManager() {
		initProvider();
		return PROVIDER.providerEntityManager();
	}

	private EntityManagerService() {
		super();
	}

}
