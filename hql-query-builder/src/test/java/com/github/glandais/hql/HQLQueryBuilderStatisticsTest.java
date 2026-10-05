package com.github.glandais.hql;

import com.github.glandais.hql.beans.HQLStatistic;
import com.github.glandais.hql.beans.HQLStatisticItem;
import com.github.glandais.hql.fixtures.ChildEntity;
import com.github.glandais.hql.fixtures.EntityManagerFixtures;
import com.github.glandais.hql.fixtures.RootEntity;
import com.github.glandais.hql.testsupport.StaticCacheResetExtension;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(StaticCacheResetExtension.class)
class HQLQueryBuilderStatisticsTest {

    private EntityManager entityManager;
    private HQLQueryBuilder<RootEntity> queryBuilder;

    @BeforeEach
    void setUp() {
        entityManager = EntityManagerFixtures.mockEntityManager();
        queryBuilder = new HQLQueryBuilder<>(entityManager, RootEntity.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getStatisticsOnScalarPathReturnsRawRows() {
        Query countQuery = mock(Query.class);
        when(entityManager.createQuery(anyString())).thenReturn(countQuery);
        when(countQuery.getResultList()).thenReturn((List) Collections.singletonList(new Object[]{"value", 2L}));

        List<Object[]> result = queryBuilder.getStatistics("name");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)[0]).isEqualTo("value");
        assertThat(result.get(0)[1]).isEqualTo(2L);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getStatisticsOnEntityPathResolvesInstances() {
        Query countQuery = mock(Query.class);
        Query subQuery = mock(Query.class);
        when(entityManager.createQuery(contains("count(distinct this_)"))).thenReturn(countQuery);
        when(entityManager.createQuery(contains("where this_.id in"))).thenReturn(subQuery);
        when(countQuery.getResultList()).thenReturn((List) Collections.singletonList(new Object[]{1, 4L}));

        ChildEntity child = new ChildEntity();
        child.setId(1L);
        when(subQuery.getResultList()).thenReturn((List) List.of(child));

        List<Object[]> result = queryBuilder.getStatistics("child");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)[0]).isSameAs(child);
        assertThat(result.get(0)[1]).isEqualTo(4L);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getListWithStatisticsAggregatesCountsPerItem() {
        Query listQuery = mock(Query.class);
        Query statsQuery = mock(Query.class);
        when(entityManager.createQuery(contains("select distinct this_"))).thenReturn(listQuery);
        when(entityManager.createQuery(contains("group by this_.id"))).thenReturn(statsQuery);

        RootEntity entity = new RootEntity();
        entity.setId(7L);
        when(listQuery.getResultList()).thenReturn((List) List.of(entity));
        when(statsQuery.getResultList()).thenReturn((List) Collections.singletonList(new Object[]{7, 2}));

        HQLStatisticItem item = new HQLStatisticItem("child");
        List<HQLStatistic<RootEntity>> statistics = queryBuilder.getListWithStatistics(List.of(item));

        assertThat(statistics).hasSize(1);
        assertThat(statistics.get(0).getItem()).isSameAs(entity);
        assertThat(statistics.get(0).getCounts()[0]).isEqualTo(2);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getListWithStatisticsDropsItemsWithoutGetId() {
        Query listQuery = mock(Query.class);
        when(entityManager.createQuery(anyString())).thenReturn(listQuery);
        when(listQuery.getResultList()).thenReturn((List) List.of(new Object()));

        List<HQLStatistic<RootEntity>> statistics = queryBuilder.getListWithStatistics(List.of());

        assertThat(statistics).isEmpty();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getListWithStatisticsItemsAppliesItemIdAndPathRestriction() {
        Query query = mock(Query.class);
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn((List) List.of("a", "b"));

        RootEntity entity = new RootEntity();
        entity.setId(9L);
        HQLStatistic<RootEntity> statistic = new HQLStatistic<>(9, entity, 1);
        HQLStatisticItem item = new HQLStatisticItem("name");

        List<Object> result = queryBuilder.getListWithStatisticsItems(List.of(item), statistic, 0);

        assertThat(result).containsExactly("a", "b");
    }
}
