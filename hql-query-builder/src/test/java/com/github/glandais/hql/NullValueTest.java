package com.github.glandais.hql;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NullValueTest {

    @Test
    void equalsOnlyMatchesSameClass() {
        assertThat(NullValue.NULL_VALUE).isEqualTo(NullValue.NULL_VALUE);
        assertThat(NullValue.NULL_VALUE).isNotEqualTo(null);
        assertThat(NullValue.NULL_VALUE).isNotEqualTo("NullValue");
    }
}
