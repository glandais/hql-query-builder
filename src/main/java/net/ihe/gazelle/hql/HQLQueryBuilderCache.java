package net.ihe.gazelle.hql;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import net.ihe.gazelle.hql.providers.EntityManagerService;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.BagType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

class HQLQueryBuilderCache {

	private Map<String, ClassMetadata> pathToClassMetadata;
	private Map<String, Class<?>> pathToClass;
	private Map<String, String> aliases;
	private Map<String, Boolean> isBagTypes;
	private SessionFactoryImplementor factory;

	public HQLQueryBuilderCache(EntityManager entityManager, Class<?> entityClass) {
		super();

		this.factory = null;

		EntityManager entityManagerForFactory;
		if (entityManager != null) {
			entityManagerForFactory = entityManager;
		} else {
			entityManagerForFactory = EntityManagerService.provideEntityManager();
		}
		Object delegate = entityManagerForFactory.getDelegate();
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

		pathToClassMetadata = Collections.synchronizedMap(new HashMap<String, ClassMetadata>());
		pathToClassMetadata.put("this_", factory.getClassMetadata(entityClass));
		pathToClass = Collections.synchronizedMap(new HashMap<String, Class<?>>());
		pathToClass.put("this_", entityClass);
		aliases = Collections.synchronizedMap(new HashMap<String, String>());
		isBagTypes = Collections.synchronizedMap(new HashMap<String, Boolean>());
	}

	public ClassMetadata getClassMetadata(String path) {
		ClassMetadata result = pathToClassMetadata.get(path);
		if (result == null) {
			buildCompleteClassMetadata(path);
			result = pathToClassMetadata.get(path);
		}
		return result;
	}

	private void buildCompleteClassMetadata(String path) {
		String[] parts = StringUtils.split(path, ".");
		String currentPath = "";

		ClassMetadata parentClassMetadata = null;
		ClassMetadata classMetadata = null;

		for (int i = 0; i < parts.length; i++) {
			if (i != 0) {
				currentPath = currentPath + ".";
			}
			String lastPart = parts[i];
			currentPath = currentPath + lastPart;

			classMetadata = pathToClassMetadata.get(currentPath);
			if (classMetadata == null) {
				synchronized (pathToClassMetadata) {
					classMetadata = pathToClassMetadata.get(currentPath);
					if (classMetadata == null) {
						classMetadata = buildClassMetadata(currentPath, parentClassMetadata, lastPart);
						pathToClassMetadata.put(currentPath, classMetadata);
					}
				}
			}
			parentClassMetadata = classMetadata;
		}
	}

	private ClassMetadata buildClassMetadata(String currentPath, ClassMetadata parentClassMetadata, String lastPart) {
		ClassMetadata classMetadata;
		Type propertyType = parentClassMetadata.getPropertyType(lastPart);

		Class<?> returnedClass = propertyType.getReturnedClass();
		if (propertyType instanceof CollectionType) {
			String role = ((CollectionType) propertyType).getRole();
			propertyType = factory.getCollectionPersister(role).getElementType();
			returnedClass = propertyType.getReturnedClass();
		}
		pathToClass.put(currentPath, returnedClass);
		classMetadata = factory.getClassMetadata(returnedClass);
		pathToClassMetadata.put(currentPath, classMetadata);
		return classMetadata;
	}

	public Class<?> getPathToClass(String path) {
		return pathToClass.get(path);
	}

	public String getAlias(String path) {
		String alias = aliases.get(path);
		if (alias == null) {
			synchronized (aliases) {
				alias = aliases.get(path);
				if (alias == null) {
					int lastIndexOf = path.lastIndexOf('.');
					String propertyName = path.substring(lastIndexOf + 1);

					alias = propertyName + "_";
					Integer j = 1;
					while (aliases.containsValue(alias)) {
						alias = propertyName + "_" + j.toString();
						j++;
					}
					aliases.put(path, alias);
				}
			}
		}
		return alias;
	}

	public Boolean isBagType(String path) {
		Boolean result = isBagTypes.get(path);
		if (result == null) {
			synchronized (isBagTypes) {
				result = isBagTypes.get(path);
				if (result == null) {
					int lastIndexOf = path.lastIndexOf('.');
					String parentPath = path.substring(0, lastIndexOf);
					String propertyName = path.substring(lastIndexOf + 1);

					ClassMetadata classMetadata = getClassMetadata(parentPath);
					Type propertyType = classMetadata.getPropertyType(propertyName);

					if (propertyType.getClass().isAssignableFrom(BagType.class)) {
						result = true;
					} else {
						result = false;
					}

					isBagTypes.put(path, result);
				}
			}

		}
		return result;
	}

}
