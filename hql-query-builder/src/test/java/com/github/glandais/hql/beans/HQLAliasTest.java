package com.github.glandais.hql.beans;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HQLAliasTest {

    @Test
    void storesPathAliasAndJoinType() {
        HQLAlias alias = new HQLAlias("this_.child", "child_", HQLAlias.LEFT_JOIN);

        assertThat(alias.getPath()).isEqualTo("this_.child");
        assertThat(alias.getAlias()).isEqualTo("child_");
        assertThat(alias.getJoinType()).isEqualTo(HQLAlias.LEFT_JOIN);
    }
}
