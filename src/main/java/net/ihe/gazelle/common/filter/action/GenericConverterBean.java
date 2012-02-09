package net.ihe.gazelle.common.filter.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.IntegerType;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Init;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Name("genericConverter")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class GenericConverterBean implements Converter, GenericConverterLocal {

	private static Logger log = LoggerFactory.getLogger(GenericConverterBean.class);

	private List<Class<?>> supportedClasses;
	private Map<String, Class<?>> supportedClassesMap;
	private Map<Class<?>, String> supportedClassesMapReversed;

	@Observer("org.jboss.seam.postInitialization")
	public void registerConverters() {
		log.info("Discovering entities to convert automatically");

		supportedClasses = Collections.synchronizedList(new ArrayList<Class<?>>());
		supportedClassesMap = Collections.synchronizedMap(new HashMap<String, Class<?>>());
		supportedClassesMapReversed = Collections.synchronizedMap(new HashMap<Class<?>, String>());

		Init.instance().getConverters().put("genericConverter", "genericConverter");

		EntityManager entityManager = (EntityManager) Component.getInstance("entityManager");
		Object delegate = entityManager.getDelegate();

		SessionFactoryImplementor factory = null;

		if (delegate instanceof Session) {
			Session session = (Session) delegate;
			SessionFactory sessionFactory = session.getSessionFactory();
			if (sessionFactory instanceof SessionFactoryImplementor) {
				factory = (SessionFactoryImplementor) sessionFactory;
			}
		}
		if (factory == null) {
			throw new IllegalArgumentException();
		}

		Map<String, ClassMetadata> allClassMetadata = factory.getAllClassMetadata();
		Collection<ClassMetadata> values = allClassMetadata.values();
		for (ClassMetadata classMetadata : values) {
			boolean idIsId = "id".equals(classMetadata.getIdentifierPropertyName());
			boolean idIsInteger = classMetadata.getIdentifierType() instanceof IntegerType;
			if (idIsId && idIsInteger) {
				Class<?> mappedClass = classMetadata.getMappedClass(EntityMode.POJO);

				String canonicalName = mappedClass.getCanonicalName();
				log.info("Discovered class " + canonicalName);

				Init.instance().getConvertersByClass().put(mappedClass, "genericConverter");
				supportedClasses.add(mappedClass);
				String hash = Integer.toHexString(canonicalName.hashCode());
				supportedClassesMap.put(hash, mappedClass);
				supportedClassesMapReversed.put(mappedClass, hash);

			}
		}

	}

	@Create
	public void init() {
		// ?
	}

	@Destroy
	public void stopProxy() {
		// ?
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}

		int i = value.indexOf('-');
		String hashClass = value.substring(0, i);
		String idString = value.substring(i + 1);

		Class<?> selectableClass = supportedClassesMap.get(hashClass);

		Integer id;
		try {
			id = new Integer(idString);
		} catch (NumberFormatException e) {
			log.error("Failed to parse id " + idString, e);
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

		String idString = id.toString();

		Class<?> valueClass = value.getClass();
		String hashClass = supportedClassesMapReversed.get(valueClass);
		if (hashClass == null) {
			for (Class<?> supportedClass : supportedClasses) {
				if (hashClass == null) {
					if (supportedClass.isAssignableFrom(valueClass)) {
						hashClass = supportedClassesMapReversed.get(supportedClass);
					}
				}
			}
		}

		return hashClass + "-" + idString;
	}
}
