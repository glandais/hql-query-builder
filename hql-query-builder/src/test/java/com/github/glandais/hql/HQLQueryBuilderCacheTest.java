package com.github.glandais.hql;

import com.github.glandais.hql.fixtures.ChildEntity;
import com.github.glandais.hql.fixtures.EntityManagerFixtures;
import com.github.glandais.hql.fixtures.ItemEntity;
import com.github.glandais.hql.fixtures.RootEntity;
import com.github.glandais.hql.testsupport.StaticCacheResetExtension;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(StaticCacheResetExtension.class)
class HQLQueryBuilderCacheTest {

    private HQLQueryBuilderCache cache;

    @BeforeEach
    void setUp() {
        EntityManager entityManager = EntityManagerFixtures.mockEntityManager();
        cache = new HQLQueryBuilderCache(entityManager, RootEntity.class);
    }

    @Test
    void getAliasGeneratesPropertySuffixedAlias() {
        assertThat(cache.getAlias("this_.child")).isEqualTo("child_");
    }

    @Test
    void getAliasIsStableForTheSamePath() {
        String first = cache.getAlias("this_.child");
        String second = cache.getAlias("this_.child");

        assertThat(first).isEqualTo(second);
    }

    @Test
    void getAliasDeduplicatesCollidingAliasNames() {
        String first = cache.getAlias("this_.child");
        String second = cache.getAlias("other_.child");

        assertThat(first).isEqualTo("child_");
        assertThat(second).isEqualTo("child_1");
    }

    @Test
    void getClassMetadataResolvesSingleValuedAssociation() {
        EntityType<?> classMetadata = cache.getClassMetadata("this_.child");

        assertThat(classMetadata).isNotNull();
        assertThat(classMetadata.getJavaType()).isEqualTo(ChildEntity.class);
        assertThat(cache.getPathToClass("this_.child")).isEqualTo(ChildEntity.class);
    }

    @Test
    void getClassMetadataResolvesPluralAssociationElementType() {
        EntityType<?> classMetadata = cache.getClassMetadata("this_.items");

        assertThat(classMetadata).isNotNull();
        assertThat(classMetadata.getJavaType()).isEqualTo(ItemEntity.class);
        assertThat(cache.getPathToClass("this_.items")).isEqualTo(ItemEntity.class);
    }

    @Test
    void getClassMetadataForRootPathReturnsTheRootEntityType() {
        EntityType<?> classMetadata = cache.getClassMetadata("this_");

        assertThat(classMetadata.getJavaType()).isEqualTo(RootEntity.class);
    }

    @Test
    void isBagTypeIsAlwaysFalse() {
        assertThat(cache.isBagType("this_.items")).isFalse();
        assertThat(cache.isBagType("this_.child")).isFalse();
    }
}
