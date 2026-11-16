package com.github.glandais.hql.beans;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HQLStatisticTest {

    @Test
    void initializesCountsArrayWithZeroes() {
        HQLStatistic<String> statistic = new HQLStatistic<>(1, "item", 3);

        assertThat(statistic.getId()).isEqualTo(1);
        assertThat(statistic.getItem()).isEqualTo("item");
        assertThat(statistic.getCounts()).containsExactly(0, 0, 0);
    }

    @Test
    void countsArrayIsMutableInPlace() {
        HQLStatistic<String> statistic = new HQLStatistic<>(1, "item", 2);

        statistic.getCounts()[0] = 5;

        assertThat(statistic.getCounts()).containsExactly(5, 0);
    }
}
