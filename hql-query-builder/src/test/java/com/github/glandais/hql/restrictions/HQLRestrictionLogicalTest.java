package com.github.glandais.hql.restrictions;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.beans.HQLRestrictionValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HQLRestrictionLogicalTest {

    private HQLQueryBuilder<?> queryBuilder;
    private HQLRestrictionValues values;
    private StringBuilder sb;

    @BeforeEach
    void setUp() {
        queryBuilder = mock(HQLQueryBuilder.class);
        when(queryBuilder.getShortProperty(anyString())).thenAnswer(invocation -> "this_." + invocation.getArgument(0));
        values = new HQLRestrictionValues();
        sb = new StringBuilder();
    }

    @Test
    void andJoinsRestrictionsWithAndOperator() {
        HQLRestrictions.and(HQLRestrictions.eq("a", 1), HQLRestrictions.eq("b", 2)).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("((this_.a = :restriction_0) AND (this_.b = :restriction_1))");
    }

    @Test
    void orJoinsRestrictionsWithOrOperator() {
        HQLRestrictions.or(HQLRestrictions.eq("a", 1), HQLRestrictions.eq("b", 2)).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("((this_.a = :restriction_0) OR (this_.b = :restriction_1))");
    }

    @Test
    void notNegatesTheWrappedRestriction() {
        HQLRestrictions.not(HQLRestrictions.eq("a", 1)).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("not(this_.a = :restriction_0)");
    }

    @Test
    void andWithSingleRestrictionHasNoOperator() {
        HQLRestrictions.and(HQLRestrictions.isNull("a")).toHQL(queryBuilder, values, sb);

        assertThat(sb.toString()).isEqualTo("((this_.a is null))");
    }
}
