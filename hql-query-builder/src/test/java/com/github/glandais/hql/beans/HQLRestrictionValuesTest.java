package com.github.glandais.hql.beans;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HQLRestrictionValuesTest {

    @Test
    void addValueGeneratesSequentialKeysAndStoresValue() {
        HQLRestrictionValues values = new HQLRestrictionValues();

        Object firstKey = values.addValue("name", "bob");
        Object secondKey = values.addValue("age", 18);

        assertThat(firstKey).isEqualTo("restriction_0");
        assertThat(secondKey).isEqualTo("restriction_1");
        assertThat(values).containsEntry("restriction_0", "bob");
        assertThat(values).containsEntry("restriction_1", 18);
    }
}
