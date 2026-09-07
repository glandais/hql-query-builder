package com.github.glandais.hql;

import com.github.glandais.hql.fixtures.EntityManagerFixtures;
import com.github.glandais.hql.fixtures.RootEntity;
import com.github.glandais.hql.paths.HQLSafePathBasic;
import com.github.glandais.hql.testsupport.StaticCacheResetExtension;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(StaticCacheResetExtension.class)
class HQLQueryBuilderQueryExecutionTest {

    private EntityManager entityManager;
    private Query query;
    private HQLQueryBuilder<RootEntity> queryBuilder;

    @BeforeEach
    void setUp() {
        entityManager = EntityManagerFixtures.mockEntityManager();
        query = mock(Query.class);
        when(entityManager.createQuery(anyString())).thenReturn(query);
        queryBuilder = new HQLQueryBuilder<>(entityManager, RootEntity.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getListReturnsResultListFromQuery() {
        RootEntity entity = new RootEntity();
        when(query.getResultList()).thenReturn((List) List.of(entity));

        assertThat(queryBuilder.getList()).containsExactly(entity);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getListBindsRestrictionParameters() {
        queryBuilder.addEq("name", "bob");
        when(query.getResultList()).thenReturn((List) List.of());

        queryBuilder.getList();

        verify(query).setParameter("restriction_0", "bob");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getListNullIfEmptyReturnsNullWhenNoResults() {
        when(query.getResultList()).thenReturn((List) List.of());

        assertThat(queryBuilder.getListNullIfEmpty()).isNull();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getListNullIfEmptyReturnsListWhenNonEmpty() {
        RootEntity entity = new RootEntity();
        when(query.getResultList()).thenReturn((List) List.of(entity));

        assertThat(queryBuilder.getListNullIfEmpty()).containsExactly(entity);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getListUsesIdSubQueryWhenEnabled() {
        when(query.getResultList()).thenReturn((List) List.of());
        queryBuilder.setComputeListUsingIdSubQuery(true);

        assertThat(queryBuilder.isComputeListUsingIdSubQuery()).isTrue();
        assertThat(queryBuilder.getList()).isEmpty();

        verify(entityManager, org.mockito.Mockito.atLeast(2)).createQuery(anyString());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getListWithOrdersUnwrapsObjectArrayRows() {
        RootEntity entity = new RootEntity();
        when(query.getResultList()).thenReturn((List) java.util.Collections.singletonList(new Object[]{entity, "sortKey"}));
        queryBuilder.addOrder("name", true);

        assertThat(queryBuilder.getList()).containsExactly(entity);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getCountSumsNumericResults() {
        when(query.getResultList()).thenReturn((List) List.of(3L));

        assertThat(queryBuilder.getCount()).isEqualTo(3);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getCountOnPathUsesProvidedPath() {
        when(query.getResultList()).thenReturn((List) List.of(2));

        assertThat(queryBuilder.getCountOnPath("name")).isEqualTo(2);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getCountDistinctOnPathUsesProvidedPath() {
        when(query.getResultList()).thenReturn((List) List.of(1));

        assertThat(queryBuilder.getCountDistinctOnPath("name")).isEqualTo(1);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getListDistinctReturnsRawResultList() {
        when(query.getResultList()).thenReturn((List) List.of("a", "b"));

        List<Object> result = (List<Object>) queryBuilder.getListDistinct("name");
        assertThat(result).containsExactly("a", "b");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getMultiSelectWithSinglePathReturnsFlatList() {
        when(query.getResultList()).thenReturn((List) List.of("a", "b"));

        List<Object> result = (List<Object>) queryBuilder.getMultiSelect("name");
        assertThat(result).containsExactly("a", "b");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getMultiSelectWithMultiplePathsReturnsRowLists() {
        when(query.getResultList()).thenReturn((List) java.util.Collections.singletonList(new Object[]{"a", 1}));

        List<Object> result = (List<Object>) queryBuilder.getMultiSelect("name", "child.label");

        assertThat(result).hasSize(1);
        assertThat((List<Object>) result.get(0)).containsExactly("a", 1);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getMultiSelectAcceptsSafePathArguments() {
        when(query.getResultList()).thenReturn((List) List.of("a"));
        HQLSafePathBasic<String> namePath = new HQLSafePathBasic<>(null, "name", queryBuilder, String.class);

        List<Object> result = (List<Object>) queryBuilder.getMultiSelect(namePath);
        assertThat(result).containsExactly("a");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void paginationSetsFirstResultAndMaxResultsOnQuery() {
        when(query.getResultList()).thenReturn((List) List.of());
        queryBuilder.setFirstResult(5);
        queryBuilder.setMaxResults(10);

        queryBuilder.getList();

        verify(query).setFirstResult(5);
        verify(query).setMaxResults(10);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getUniqueResultReturnsFirstElement() {
        RootEntity entity = new RootEntity();
        when(query.getResultList()).thenReturn((List) List.of(entity));

        assertThat(queryBuilder.getUniqueResult()).isSameAs(entity);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getUniqueResultReturnsNullWhenEmpty() {
        when(query.getResultList()).thenReturn((List) List.of());

        assertThat(queryBuilder.getUniqueResult()).isNull();
    }
}
