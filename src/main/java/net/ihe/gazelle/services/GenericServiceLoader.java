package net.ihe.gazelle.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericServiceLoader {

	private static final Logger log = LoggerFactory.getLogger(GenericServiceLoader.class);

	private static final Map<Class<?>, Object> services = Collections.synchronizedMap(new HashMap<Class<?>, Object>());

	private static final Map<Class<?>, Boolean> isComparableService = Collections
			.synchronizedMap(new HashMap<Class<?>, Boolean>());

	private static final Map<Class<?>, List> servicesList = Collections.synchronizedMap(new HashMap<Class<?>, List>());

	public static final <T> T getService(Class<T> serviceClass) {
		T result = (T) services.get(serviceClass);
		if (result == null) {
			boolean isComparable = isComparable(serviceClass);
			List serviceList = getServicesList(serviceClass);
			if (isComparable) {
				Collections.sort(serviceList);
			}

			if (serviceList.size() > 0) {
				result = (T) serviceList.get(0);
			} else {
				log.error("Failed to load an implementation for the service " + serviceClass.getCanonicalName() + " !");
			}

			if (!isComparable) {
				services.put(serviceClass, result);
			}
		}
		return result;
	}

	private static <T> List getServicesList(Class<T> serviceClass) {
		List result = servicesList.get(serviceClass);
		if (result == null) {
			synchronized (serviceClass) {
				result = servicesList.get(serviceClass);
				if (result == null) {
					result = new ArrayList();
					ServiceLoader<T> providers = ServiceLoader.load(serviceClass);
					for (T provider : providers) {
						result.add(provider);
					}
					servicesList.put(serviceClass, result);
				}
			}
		}
		return result;
	}

	private static <T> Boolean isComparable(Class<T> serviceClass) {
		Boolean result = isComparableService.get(serviceClass);
		if (result == null) {
			synchronized (serviceClass) {
				result = isComparableService.get(serviceClass);
				if (result == null) {
					result = Arrays.asList(serviceClass.getInterfaces()).contains(Comparable.class);
					isComparableService.put(serviceClass, result);
				}
			}
		}
		return result;
	}

	private GenericServiceLoader() {
		super();
	}
}
