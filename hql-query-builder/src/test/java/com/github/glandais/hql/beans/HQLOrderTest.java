package com.github.glandais.hql.beans;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HQLOrderTest {

    @Test
    void ascendingOrderKeepsPathAsIs() {
        HQLOrder order = new HQLOrder("this_.name", true);

        assertThat(order.getPath()).isEqualTo("this_.name");
        assertThat(order.isAscending()).isTrue();
        assertThat(order.isLowerCase()).isFalse();
    }

    @Test
    void lowerCaseOrderWrapsPathInLowerFunction() {
        HQLOrder order = new HQLOrder("this_.name", false, true);

        assertThat(order.getPath()).isEqualTo("lower(this_.name)");
        assertThat(order.isAscending()).isFalse();
        assertThat(order.isLowerCase()).isTrue();
    }
}
