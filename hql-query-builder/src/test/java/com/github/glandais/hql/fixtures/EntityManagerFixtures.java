package com.github.glandais.hql.fixtures;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.Type;

import java.util.Collection;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class EntityManagerFixtures {

    private EntityManagerFixtures() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static EntityManager mockEntityManager() {
        EntityType<RootEntity> rootType = mockEntityType(RootEntity.class);
        EntityType<ChildEntity> childType = mockEntityType(ChildEntity.class);
        EntityType<ItemEntity> itemType = mockEntityType(ItemEntity.class);

        Attribute<RootEntity, ChildEntity> childAttribute = mock(Attribute.class);
        when(childAttribute.getJavaType()).thenReturn((Class) ChildEntity.class);
        when(rootType.getDeclaredAttribute("child")).thenReturn((Attribute) childAttribute);

        PluralAttribute<RootEntity, ?, ItemEntity> itemsAttribute = mock(PluralAttribute.class);
        Type<ItemEntity> itemElementType = mock(Type.class);
        when(itemElementType.getJavaType()).thenReturn(ItemEntity.class);
        when(itemsAttribute.getElementType()).thenReturn(itemElementType);
        when(itemsAttribute.getJavaType()).thenReturn((Class) Collection.class);
        when(rootType.getDeclaredAttribute("items")).thenReturn((Attribute) itemsAttribute);

        Metamodel metamodel = mock(Metamodel.class);
        when(metamodel.getEntities()).thenReturn(Set.of(rootType, childType, itemType));

        EntityManager entityManager = mock(EntityManager.class);
        when(entityManager.getMetamodel()).thenReturn(metamodel);
        return entityManager;
    }

    @SuppressWarnings("unchecked")
    private static <X> EntityType<X> mockEntityType(Class<X> javaType) {
        EntityType<X> entityType = mock(EntityType.class);
        when(entityType.getJavaType()).thenReturn(javaType);
        return entityType;
    }
}
