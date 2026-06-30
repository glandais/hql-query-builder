package com.github.glandais.hql.paths;

import com.github.glandais.hql.HQLQueryBuilder;
import com.github.glandais.hql.restrictions.HQLRestrictionCompare;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HQLSafePathBasicNumberTest {

    private HQLQueryBuilder<?> queryBuilder;
    private HQLSafePathBasicNumber<Integer> path;

    @BeforeEach
    void setUp() {
        queryBuilder = mock(HQLQueryBuilder.class);
        path = new HQLSafePathBasicNumber<>(null, "this_.age", queryBuilder, Integer.class);
    }

    @Test
    void geAddsCompareRestriction() {
        path.ge(18);
        verify(queryBuilder).addRestriction(any(HQLRestrictionCompare.class));
    }

    @Test
    void leAddsCompareRestriction() {
        path.le(18);
        verify(queryBuilder).addRestriction(any(HQLRestrictionCompare.class));
    }

    @Test
    void gtAddsCompareRestriction() {
        path.gt(18);
        verify(queryBuilder).addRestriction(any(HQLRestrictionCompare.class));
    }

    @Test
    void ltAddsCompareRestriction() {
        path.lt(18);
        verify(queryBuilder).addRestriction(any(HQLRestrictionCompare.class));
    }

    @Test
    void restrictionFactoriesReturnCompareRestrictions() {
        assertThat(path.geRestriction(1)).isInstanceOf(HQLRestrictionCompare.class);
        assertThat(path.leRestriction(1)).isInstanceOf(HQLRestrictionCompare.class);
        assertThat(path.gtRestriction(1)).isInstanceOf(HQLRestrictionCompare.class);
        assertThat(path.ltRestriction(1)).isInstanceOf(HQLRestrictionCompare.class);
    }
}
