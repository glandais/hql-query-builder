package io.github.glandais.hql;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


class HQLQueryBuilderCache {

    private static Map<String, EntityType<?>> metamodel;

    private final Map<String, EntityType<?>> pathToClassMetadata;
    private final Map<String, Class<?>> pathToClass;
    private final Map<String, String> aliases;
    private final Map<String, Boolean> isBagTypes;

    public HQLQueryBuilderCache(EntityManager entityManager,
                                Class<?> entityClass) {
        super();

        if (metamodel == null) {
            metamodel = Collections
                    .synchronizedMap(new HashMap<String, EntityType<?>>());
            Set<EntityType<?>> entities = entityManager.getMetamodel()
                    .getEntities();
            for (EntityType<?> entityType : entities) {
                metamodel.put(entityType.getJavaType().getCanonicalName(),
                        entityType);
            }
        }

        pathToClassMetadata = Collections
                .synchronizedMap(new HashMap<String, EntityType<?>>());
        pathToClassMetadata.put("this_",
                metamodel.get(entityClass.getCanonicalName()));
        pathToClass = Collections
                .synchronizedMap(new HashMap<String, Class<?>>());
        pathToClass.put("this_", entityClass);
        aliases = Collections.synchronizedMap(new HashMap<String, String>());
        isBagTypes = Collections
                .synchronizedMap(new HashMap<String, Boolean>());
    }

    public EntityType<?> getClassMetadata(String path) {
        EntityType<?> result = pathToClassMetadata.get(path);
        if (result == null) {
            buildCompleteClassMetadata(path);
            result = pathToClassMetadata.get(path);
        }
        return result;
    }

    private void buildCompleteClassMetadata(String path) {
        String[] parts = StringUtils.split(path, ".");
        String currentPath = "";

        EntityType<?> parentClassMetadata = null;
        EntityType<?> classMetadata = null;

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
                        classMetadata = buildClassMetadata(currentPath,
                                parentClassMetadata, lastPart);
                        pathToClassMetadata.put(currentPath, classMetadata);
                    }
                }
            }
            parentClassMetadata = classMetadata;
        }
    }

    private EntityType<?> buildClassMetadata(String currentPath,
                                             EntityType<?> parentClassMetadata, String lastPart) {
        EntityType<?> classMetadata;
        Attribute<?, ?> propertyType = parentClassMetadata
                .getDeclaredAttribute(lastPart);

        Class<?> returnedClass = propertyType.getJavaType();
        if (propertyType instanceof PluralAttribute) {
            returnedClass = ((PluralAttribute) propertyType).getElementType()
                    .getJavaType();
        }
        pathToClass.put(currentPath, returnedClass);
        classMetadata = metamodel.get(returnedClass.getCanonicalName());
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
                        alias = propertyName + "_" + j;
                        j++;
                    }
                    aliases.put(path, alias);
                }
            }
        }
        return alias;
    }

    public Boolean isBagType(String path) {
        return false;
        /*
        Boolean result = isBagTypes.get(path);
        if (result == null) {
            synchronized (isBagTypes) {
                result = isBagTypes.get(path);
                if (result == null) {
                    int lastIndexOf = path.lastIndexOf('.');
                    String parentPath = path.substring(0, lastIndexOf);
                    String propertyName = path.substring(lastIndexOf + 1);

                    EntityType<?> classMetadata = getClassMetadata(parentPath);
                    Attribute<?, ?> propertyType = classMetadata
                            .getDeclaredAttribute(propertyName);

                    //					if (propertyType.getClass().isAssignableFrom(BagType.class)) {
                    //						result = true;
                    //					} else {
                    result = false;
                    //					}

                    isBagTypes.put(path, result);
                }
            }

        }
        return result;

         */
    }

}
