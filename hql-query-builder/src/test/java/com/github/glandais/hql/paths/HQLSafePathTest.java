package com.github.glandais.hql.paths;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.HQLRestriction;
import com.github.glandais.hql.beans.HQLStatisticItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HQLSafePathTest {

    private HQLQueryBuilder<?> queryBuilder;

    @BeforeEach
    void setUp() {
        queryBuilder = mock(HQLQueryBuilder.class);
    }

    private HQLSafePathBasic<String> path(String path) {
        return new HQLSafePathBasic<>(null, path, queryBuilder, String.class);
    }

    @Test
    void toStringReturnsThePath() {
        assertThat(path("this_.name")).hasToString("this_.name");
    }

    @Test
    void getPathReturnsThePath() {
        assertThat(path("this_.name").getPath()).isEqualTo("this_.name");
    }

    @Test
    void getQueryBuilderReturnsTheBuilder() {
        assertThat(path("this_.name").getQueryBuilder()).isSameAs(queryBuilder);
    }

    @Test
    void equalsAndHashCodeAreBasedOnPath() {
        assertThat(path("this_.name")).isEqualTo(path("this_.name"));
        assertThat(path("this_.name")).hasSameHashCodeAs(path("this_.name"));
        assertThat(path("this_.name")).isNotEqualTo(path("this_.other"));
        assertThat(path("this_.name")).isNotEqualTo(null);
        assertThat(path("this_.name")).isNotEqualTo("this_.name");
    }

    @Test
    void compareToOrdersByPath() {
        assertThat(path("a").compareTo(path("b"))).isNegative();
        assertThat(path("b").compareTo(path("a"))).isPositive();
        assertThat(path("a").compareTo(path("a"))).isZero();
    }

    @Test
    void statisticItemWithoutRestriction() {
        HQLStatisticItem item = path("this_.name").statisticItem();

        assertThat(item.getPath()).isEqualTo("this_.name");
        assertThat(item.getRestriction()).isNull();
    }

    @Test
    void statisticItemWithRestriction() {
        HQLRestriction restriction = mock(HQLRestriction.class);

        HQLStatisticItem item = path("this_.name").statisticItem(restriction);

        assertThat(item.getRestriction()).isSameAs(restriction);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void getStatisticsDelegatesToQueryBuilder() {
        List<Object[]> expected = List.of(new Object[][]{{"a", 1}});
        when(queryBuilder.getStatistics("this_.name")).thenReturn((List) expected);

        List<Object[]> result = path("this_.name").getStatistics();

        assertThat(result).isSameAs(expected);
        verify(queryBuilder).getStatistics("this_.name");
    }
}
