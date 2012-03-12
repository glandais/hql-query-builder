package net.ihe.gazelle.common.tag;

import java.util.List;

public interface LinkDataProvider {

	List<Class<?>> getSupportedClasses();

	String getLabel(Object o, boolean detailed);

	String getLink(Object o);

}
