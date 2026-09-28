package com.github.glandais.hql.paths;

import com.github.glandais.hql.HQLQueryBuilder;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HQLSafePathBasicDateTest {

    @Test
    void inheritsNumberComparisonBehaviorForDates() {
        HQLQueryBuilder<?> queryBuilder = mock(HQLQueryBuilder.class);
        HQLSafePathBasicDate<Date> path = new HQLSafePathBasicDate<>(null, "this_.createdAt", queryBuilder, Date.class);

        assertThat(path).isInstanceOf(HQLSafePathBasicNumber.class);
        assertThat(path.getType()).isEqualTo(Date.class);
    }
}
