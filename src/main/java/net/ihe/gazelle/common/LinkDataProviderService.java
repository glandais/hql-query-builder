package net.ihe.gazelle.common;

import java.util.List;
import java.util.ServiceLoader;

public class LinkDataProviderService {

	protected static ServiceLoader<LinkDataProvider> PROVIDERS = ServiceLoader.load(LinkDataProvider.class);

	private LinkDataProviderService() {
		super();
	}

	public static LinkDataProvider getProviderForClass(Class<?> valueClass) {
		for (LinkDataProvider dataProvider : PROVIDERS) {
			List<Class<?>> supportedClasses = dataProvider.getSupportedClasses();
			for (Class<?> supportedClass : supportedClasses) {
				if (supportedClass.isAssignableFrom(valueClass)) {
					return dataProvider;
				}
			}
		}
		return null;
	}

}
