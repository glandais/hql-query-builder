package com.github.glandais.hql.beans;

import com.github.glandais.hql.HQLRestriction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HQLStatisticItemTest {

    @Test
    void constructorWithoutRestrictionLeavesItNull() {
        HQLStatisticItem item = new HQLStatisticItem("path");

        assertThat(item.getPath()).isEqualTo("path");
        assertThat(item.getRestriction()).isNull();
    }

    @Test
    void constructorWithRestrictionStoresIt() {
        HQLRestriction restriction = mock(HQLRestriction.class);

        HQLStatisticItem item = new HQLStatisticItem("path", restriction);

        assertThat(item.getRestriction()).isSameAs(restriction);
    }

    @Test
    void settersOverridePathAndRestriction() {
        HQLStatisticItem item = new HQLStatisticItem("path");
        HQLRestriction restriction = mock(HQLRestriction.class);

        item.setPath("other");
        item.setRestriction(restriction);

        assertThat(item.getPath()).isEqualTo("other");
        assertThat(item.getRestriction()).isSameAs(restriction);
    }
}
