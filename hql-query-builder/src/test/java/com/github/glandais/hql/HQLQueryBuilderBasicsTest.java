package com.github.glandais.hql;

import com.github.glandais.hql.fixtures.EntityManagerFixtures;
import com.github.glandais.hql.fixtures.RootEntity;
import com.github.glandais.hql.restrictions.HQLRestrictions;
import com.github.glandais.hql.testsupport.StaticCacheResetExtension;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(StaticCacheResetExtension.class)
class HQLQueryBuilderBasicsTest {

    private EntityManager entityManager;
    private HQLQueryBuilder<RootEntity> queryBuilder;

    @BeforeEach
    void setUp() {
        entityManager = EntityManagerFixtures.mockEntityManager();
        queryBuilder = new HQLQueryBuilder<>(entityManager, RootEntity.class);
    }

    @Test
    void getEntityClassReturnsConstructorArgument() {
        assertThat(queryBuilder.getEntityClass()).isEqualTo(RootEntity.class);
    }

    @Test
    void getShortPropertyOnEmptyOrNullPathReturnsThis() {
        assertThat(queryBuilder.getShortProperty(null)).isEqualTo("this_");
        assertThat(queryBuilder.getShortProperty("")).isEqualTo("this_");
        assertThat(queryBuilder.getShortProperty("this")).isEqualTo("this_");
        assertThat(queryBuilder.getShortProperty("this_")).isEqualTo("this_");
    }

    @Test
    void getShortPropertyOnDirectPropertyUsesRootAlias() {
        assertThat(queryBuilder.getShortProperty("name")).isEqualTo("this_.name");
    }

    @Test
    void getShortPropertyOnNestedPropertyCreatesJoinAlias() {
        assertThat(queryBuilder.getShortProperty("child.label")).isEqualTo("child_.label");
    }

    @Test
    void getShortPropertyReusesTheSameAliasForTheSamePath() {
        queryBuilder.getShortProperty("child.label");

        assertThat(queryBuilder.getShortProperty("child.id")).isEqualTo("child_.id");
    }

    @Test
    void addEqAddsARestriction() {
        queryBuilder.addEq("name", "bob");

        assertThat(queryBuilder.getRestrictions()).hasSize(1);
    }

    @Test
    void addInAddsARestriction() {
        queryBuilder.addIn("name", java.util.List.of("a"));

        assertThat(queryBuilder.getRestrictions()).hasSize(1);
    }

    @Test
    void addLikeAddsARestriction() {
        queryBuilder.addLike("name", "bob");

        assertThat(queryBuilder.getRestrictions()).hasSize(1);
    }

    @Test
    void addRestrictionAppendsToTheList() {
        queryBuilder.addRestriction(HQLRestrictions.isNull("name"));

        assertThat(queryBuilder.getRestrictions()).hasSize(1);
    }

    @Test
    void addOrderUsesShortPropertyPath() {
        queryBuilder.addOrder("name", true);

        assertThat(queryBuilder.getOrders()).hasSize(1);
        assertThat(queryBuilder.getOrders().get(0).getPath()).isEqualTo("this_.name");
        assertThat(queryBuilder.getOrders().get(0).isAscending()).isTrue();
    }

    @Test
    void addOrderWithLowerCaseFlag() {
        queryBuilder.addOrder("name", false, true);

        assertThat(queryBuilder.getOrders().get(0).getPath()).isEqualTo("lower(this_.name)");
    }

    @Test
    void addFetchRegistersThePath() {
        Query query = mock(Query.class);
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(java.util.List.of());

        queryBuilder.addFetch("child");
        queryBuilder.getList();

        verify(entityManager).createQuery(org.mockito.ArgumentMatchers.contains("fetch"));
    }

    @Test
    void cachableDefaultsToTrueAndIsSettable() {
        assertThat(queryBuilder.isCachable()).isTrue();

        queryBuilder.setCachable(false);

        assertThat(queryBuilder.isCachable()).isFalse();
    }

    @Test
    void createRealQuerySetsTheHibernateCacheableHint() {
        Query query = mock(Query.class);
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(java.util.List.of());
        queryBuilder.setCachable(false);

        queryBuilder.getList();

        verify(query).setHint("org.hibernate.cacheable", false);
    }

    @Test
    void patchPropertyNamePrependsThisPrefix() {
        assertThat(queryBuilder.patchPropertyName("name")).isEqualTo("this_.name");
        assertThat(queryBuilder.patchPropertyName("this.name")).isEqualTo("this_.name");
        assertThat(queryBuilder.patchPropertyName("this_.name")).isEqualTo("this_.name");
    }
}
