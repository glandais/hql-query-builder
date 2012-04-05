package net.ihe.gazelle.common;

import java.util.List;

/*
 * Used for generation of links
 * 
 * @author glandais
 *
 */
public interface LinkDataProvider {

	/**
	 * 
	 * @return List of supported classes by this provider
	 */
	List<Class<?>> getSupportedClasses();

	/**
	 * 
	 * @param o
	 *            object instance supported
	 * @param detailed
	 * 
	 * @return Label to be displayed
	 */
	String getLabel(Object o, boolean detailed);

	/**
	 * @param o
	 *            object instance supported
	 * @return A permalink to the object
	 */
	String getLink(Object o);

}
