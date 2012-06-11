package net.ihe.gazelle.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericServiceLoader {

	private static final Logger log = LoggerFactory.getLogger(GenericServiceLoader.class);

	private static final Map<Class<?>, Object> services = Collections.synchronizedMap(new HashMap<Class<?>, Object>());

	public static final <T> T getService(Class<T> serviceClass) {
		T result = (T) services.get(serviceClass);
		if (result == null) {
			synchronized (serviceClass) {
				result = (T) services.get(serviceClass);
				if (result == null) {
					ServiceLoader<T> providers = ServiceLoader.load(serviceClass);
					if (Arrays.asList(serviceClass.getInterfaces()).contains(Comparable.class)) {
						TreeSet<T> providerSet = new TreeSet<T>(new Comparator<T>() {
							@Override
							public int compare(T o1, T o2) {
								Comparable c1 = (Comparable) o1;
								Comparable c2 = (Comparable) o2;
								return c1.compareTo(c2);
							}
						});
						for (T t : providers) {
							providerSet.add(t);
						}
						if (providerSet.size() > 0) {
							result = providerSet.first();
						}
					} else {
						if (providers.iterator().hasNext()) {
							result = providers.iterator().next();
						}
					}
				}
				if (result != null) {
					services.put(serviceClass, result);
				} else {
					log.error("Failed to load an implementation for the service " + serviceClass.getCanonicalName()
							+ " !");
				}
			}
		}
		return result;
	}

	private GenericServiceLoader() {
		super();
	}
}
