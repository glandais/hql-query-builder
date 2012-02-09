package net.ihe.gazelle.common.filter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;

import org.apache.commons.beanutils.PropertyUtils;
import org.jboss.seam.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericConverter implements Converter {

	private static Logger log = LoggerFactory.getLogger(GenericConverter.class);

	private Class<?> selectableClass;

	public GenericConverter(Class<?> selectableClass) {
		super();
		this.selectableClass = selectableClass;
		PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(selectableClass);
		Class<?> idType = null;
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (propertyDescriptor.getName().equals("id")) {
				idType = propertyDescriptor.getPropertyType();
				if (!idType.isAssignableFrom(Integer.class)) {
					throw new IllegalArgumentException("Illegal id type (only Integer is supported)");
				}
			}
		}
		if (idType == null) {
			throw new IllegalArgumentException("No id property fund");
		}
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		Integer id;
		try {
			id = new Integer(value);
		} catch (NumberFormatException e) {
			log.error("Failed to parse id " + value, e);
			return null;
		}
		EntityManager em = (EntityManager) Component.getInstance("entityManager");
		return em.find(selectableClass, id);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		Object id = null;
		if (value != null) {
			try {
				id = PropertyUtils.getSimpleProperty(value, "id");
			} catch (IllegalAccessException e) {
				log.error("Failed to get id", e);
				return null;
			} catch (InvocationTargetException e) {
				log.error("Failed to get id", e);
				return null;
			} catch (NoSuchMethodException e) {
				log.error("Failed to get id", e);
				return null;
			}
		}
		if (id == null) {
			return null;
		}
		return id.toString();
	}

}
