package net.ihe.gazelle.hql.providers;

import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.TreeSet;

import javax.persistence.EntityManager;

public class EntityManagerService {

	protected static EntityManagerProvider PROVIDER = null;

	private static void initProvider() {
		if (PROVIDER == null) {
			synchronized (EntityManagerService.class) {
				if (PROVIDER == null) {
					ServiceLoader<EntityManagerProvider> providers = ServiceLoader.load(EntityManagerProvider.class);
					TreeSet<EntityManagerProvider> providerSet = new TreeSet<EntityManagerProvider>(
							new Comparator<EntityManagerProvider>() {
								@Override
								public int compare(EntityManagerProvider o1, EntityManagerProvider o2) {
									return o1.getWeight() - o2.getWeight();
								}
							});
					for (EntityManagerProvider entityManagerProvider : providers) {
						providerSet.add(entityManagerProvider);
					}
					if (providerSet.size() > 0) {
						PROVIDER = providerSet.first();
					} else {
						// FIXME log error
					}
				}
			}
		}
	}

	public static EntityManager provideEntityManager() {
		initProvider();
		return PROVIDER.providerEntityManager();
	}

	private EntityManagerService() {
		super();
	}

}
